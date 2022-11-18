package org.sert2521.bunnybots2022.subsystems

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.TalonFX
import com.ctre.phoenix.sensors.CANCoder
import com.kauailabs.navx.frc.AHRS
import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.SimpleMotorFeedforward
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.math.kinematics.SwerveDriveKinematics
import edu.wpi.first.math.kinematics.SwerveModuleState
import edu.wpi.first.wpilibj.MotorSafety
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.photonvision.PhotonCamera
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
                   var state: SwerveModuleState) : MotorSafety() {
    init {
        anglePID.enableContinuousInput(-PI, PI)
    }

    private fun getVelocity(): Double {
        return powerMotor.selectedSensorVelocity * constants.powerEncoderMultiplier
    }

    private fun getAngle(): Rotation2d {
        return Rotation2d(angleEncoder.absolutePosition * constants.angleEncoderMultiplier - angleOffset)
    }

    // Should be called in periodic
    fun updateState() {
        state = SwerveModuleState(getVelocity(), getAngle())
    }

    fun set(wanted: SwerveModuleState) {
        // Using state because it should be updated and getVelocity and getAngle (probably) spend time over CAN
        val optimized = SwerveModuleState.optimize(wanted, state.angle)

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
    private var camera = PhotonCamera("Vision")

    private val kinematics: SwerveDriveKinematics
    private var modules: Array<SwerveModule>
    private val poseEstimator: SwerveDrivePoseEstimator

    var poseInited = false
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
            SwerveModuleState())
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

        poseEstimator.update(-imu.rotation2d, *states.toTypedArray())

        val target = camera.latestResult.targets.find { it.fiducialId == constants.visionTargetID }
        if (target != null) {
            val measurement = constants.targetPose.transformBy(target.bestCameraToTarget.inverse()).transformBy(constants.cameraTrans)

            val deltaTime = Timer.getFPGATimestamp() - camera.latestResult.latencyMillis
            if (poseInited) {
                poseEstimator.addVisionMeasurement(measurement.toPose2d(), deltaTime)
            } else {
                // Equivalent to setting pose, but with latency
                poseEstimator.addVisionMeasurement(measurement.toPose2d(), deltaTime, constants.startGlobalDeviations)
                poseEstimator.setVisionMeasurementStdDevs(constants.globalDeviations)
                poseInited = true
            }
        }
    }

    // Getting poseMeters does not calculations
    var pose: Pose2d
        get() = poseEstimator.estimatedPosition
        set(value) {
            poseEstimator.resetPosition(value, -imu.rotation2d)
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