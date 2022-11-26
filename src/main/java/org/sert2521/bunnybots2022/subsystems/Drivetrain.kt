package org.sert2521.bunnybots2022.subsystems

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.TalonFX
import com.ctre.phoenix.sensors.CANCoder
import com.kauailabs.navx.frc.AHRS
import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel
import edu.wpi.first.math.MatBuilder
import edu.wpi.first.math.Nat
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.SimpleMotorFeedforward
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator
import edu.wpi.first.math.geometry.*
import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.math.kinematics.SwerveDriveKinematics
import edu.wpi.first.math.kinematics.SwerveModuleState
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.MotorSafety
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.sert2521.bunnybots2022.Reloadable
import org.sert2521.bunnybots2022.SwerveModuleData
import org.sert2521.bunnybots2022.constants
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.pow

class SwerveModule(val powerMotor: TalonFX,
                   private val powerFeedforward: SimpleMotorFeedforward,
                   private val powerPID: PIDController,
                   val angleMotor: CANSparkMax,
                   private val angleEncoder: CANCoder,
                   private val angleOffset: Double,
                   private val anglePID: PIDController,
                   private val centerRotation: Rotation2d,
                   var state: SwerveModuleState,
                   shouldOptimize: Boolean) : MotorSafety() {
    var doesOptimize = shouldOptimize
        private set

    init {
        if (doesOptimize) {
            anglePID.enableContinuousInput(-PI, PI)
        } else {
            anglePID.enableContinuousInput(-PI * 2, PI * 2)
        }
    }

    private fun getVelocity(): Double {
        return powerMotor.selectedSensorVelocity * constants.powerEncoderMultiplier
    }

    private fun getAngle(): Rotation2d {
        return Rotation2d(angleEncoder.absolutePosition * constants.angleEncoderMultiplier - angleOffset)
    }

    fun setOptimize(value: Boolean) {
        doesOptimize = value

        if (doesOptimize) {
            anglePID.enableContinuousInput(-PI, PI)
        } else {
            anglePID.enableContinuousInput(-PI * 2, PI * 2)
        }
    }

    // Should be called in periodic
    fun updateState() {
        state = SwerveModuleState(getVelocity(), getAngle())
    }

    fun set(wanted: SwerveModuleState) {
        // Using state because it should be updated and getVelocity and getAngle (probably) spend time over CAN
        val optimized = if (doesOptimize) {
            SwerveModuleState.optimize(wanted, state.angle)
        } else {
            wanted
        }

        val feedforward = powerFeedforward.calculate(optimized.speedMetersPerSecond)
        val pid = powerPID.calculate(state.speedMetersPerSecond, optimized.speedMetersPerSecond)
        // Figure out voltage stuff
        powerMotor.set(ControlMode.PercentOutput, (feedforward + pid) / 12.0)
        angleMotor.set(anglePID.calculate(state.angle.radians, optimized.angle.radians))
    }

    fun enterBrakePos() {
        set(SwerveModuleState(0.0, centerRotation))
    }

    override fun stopMotor() {
        powerMotor.set(ControlMode.PercentOutput, 0.0)
        angleMotor.stopMotor()
    }

    override fun getDescription(): String {
        return "Swerve Module"
    }
}

object Drivetrain : SubsystemBase(), Reloadable {
    private val imu = AHRS()

    private val visionTable = NetworkTableInstance.getDefault().getTable("Vision")
    private val isTargetEntry = visionTable.getEntry("Is Target")
    private val targetLastUpdate = visionTable.getEntry("Last Update")
    private val targetPoseEntry = visionTable.getEntry("Position")
    private val targetAngleEntry = visionTable.getEntry("Rotation")

    private val kinematics: SwerveDriveKinematics
    private var modules: Array<SwerveModule>
    private val poseEstimator: SwerveDrivePoseEstimator

    var prevLastUpdate = 0L

    var pose = Pose2d()
        private set
    var poseInited = false
        private set

    var doesOptimize = constants.drivetrainOptimized
        private set

    init {
        val modulePositions = mutableListOf<Translation2d>()
        val modulesList = mutableListOf<SwerveModule>()

        for (moduleData in constants.swerveModuleData) {
            val powerMotor = TalonFX(moduleData.powerMotorID)
            powerMotor.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor)
            powerMotor.setNeutralMode(NeutralMode.Brake)

            val angleMotor = CANSparkMax(moduleData.angleMotorID, CANSparkMaxLowLevel.MotorType.kBrushless)
            angleMotor.idleMode = CANSparkMax.IdleMode.kBrake
            angleMotor.inverted = true

            modulePositions.add(moduleData.position)
            modulesList.add(createModule(powerMotor, angleMotor, moduleData))
        }

        modules = modulesList.toTypedArray()

        kinematics = SwerveDriveKinematics(*modulePositions.toTypedArray())
        poseEstimator = SwerveDrivePoseEstimator(-imu.rotation2d, Pose2d(), kinematics, constants.stateDeviations, constants.localDeviations, constants.globalDeviations)

        registerReload()
    }

    private fun createModule(powerMotor: TalonFX, angleMotor: CANSparkMax, moduleData: SwerveModuleData): SwerveModule {
        return SwerveModule(powerMotor,
            SimpleMotorFeedforward(constants.swervePowerS, constants.swervePowerV, constants.swervePowerA),
            PIDController(constants.swervePowerP, constants.swervePowerI, constants.swervePowerD),
            angleMotor,
            CANCoder(moduleData.angleEncoderID),
            moduleData.angleOffset,
            PIDController(constants.swerveAngleP, constants.swerveAngleI, constants.swerveAngleD),
            Rotation2d(atan2(moduleData.position.y, moduleData.position.x)),
            SwerveModuleState(),
            doesOptimize)
    }

    override fun reload() {
        val modulesList = mutableListOf<SwerveModule>()

        for (i in constants.swerveModuleData.indices) {
            val moduleData = constants.swerveModuleData[i]
            val module = modules[i]

            modulesList.add(createModule(module.powerMotor, module.angleMotor, moduleData))
        }

        modules = modulesList.toTypedArray()
    }

    override fun periodic() {
        val states = mutableListOf<SwerveModuleState>()

        for (module in modules) {
            module.updateState()
            states.add(module.state)
        }

        val position = targetPoseEntry.getDoubleArray(DoubleArray(0))
        val rotation = targetAngleEntry.getDoubleArray(DoubleArray(0))
        val lastUpdate = targetLastUpdate.getNumber(0).toLong()
        if (isTargetEntry.getBoolean(false) && lastUpdate != prevLastUpdate) {
            val translation = Translation3d(position[0], position[1], position[2])
            val measurement = Transform3d(translation, Rotation3d(MatBuilder(Nat.N3(), Nat.N3()).fill(*rotation)))
            val visionEstimate = constants.targetPose.transformBy(measurement.inverse()).transformBy(constants.cameraTrans).toPose2d()

            val deltaTime = Timer.getFPGATimestamp() - lastUpdate
            if (deltaTime < constants.targetTimeout * 1000) {
                if (poseInited) {
                    poseEstimator.addVisionMeasurement(visionEstimate, deltaTime)
                } else {
                    // Equivalent to setting pose, but with latency
                    poseEstimator.addVisionMeasurement(visionEstimate, deltaTime, constants.startGlobalDeviations)
                    poseEstimator.setVisionMeasurementStdDevs(constants.globalDeviations)
                    poseInited = true
                }
            }

            prevLastUpdate = lastUpdate
        }

        pose = poseEstimator.update(-imu.rotation2d, *states.toTypedArray())
    }

    fun setOptimize(value: Boolean) {
        doesOptimize = value

        for (module in modules) {
            module.setOptimize(doesOptimize)
        }
    }

    fun getAccelSqr(): Double {
        return (imu.worldLinearAccelY.pow(2) + imu.worldLinearAccelX.pow(2)).toDouble()
    }

    private fun feed() {
        for (module in modules) {
            module.feed()
        }
    }

    fun drive(chassisSpeeds: ChassisSpeeds) {
        // Maybe desaturate wheel speeds
        val wantedStates = kinematics.toSwerveModuleStates(chassisSpeeds)

        for (i in wantedStates.indices) {
            modules[i].set(wantedStates[i])
        }

        feed()
    }

    fun enterBrakePos() {
        for (module in modules) {
            module.enterBrakePos()
        }

        feed()
    }

    fun stop() {
        for (module in modules) {
            module.stopMotor()
        }

        feed()
    }
}