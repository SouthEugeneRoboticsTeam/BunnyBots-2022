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
import edu.wpi.first.math.geometry.*
import edu.wpi.first.math.kinematics.*
import edu.wpi.first.wpilibj.MotorSafety
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.photonvision.PhotonCamera
import org.photonvision.targeting.PhotonPipelineResult
import org.sert2521.bunnybots2022.Input
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
                   private val inverted: Boolean,
                   var state: SwerveModuleState,
                   shouldOptimize: Boolean) : MotorSafety() {
    var doesOptimize = shouldOptimize
        private set

    var position: SwerveModulePosition

    init {
        if (doesOptimize) {
            anglePID.enableContinuousInput(-PI, PI)
        } else {
            anglePID.enableContinuousInput(-PI * 2, PI * 2)
        }

        position = SwerveModulePosition(getDistance(), getAngle())
    }

    private fun getDistance(): Double {
        return if (!inverted) {
            powerMotor.selectedSensorPosition * constants.powerEncoderMultiplierPosition
        } else {
            -powerMotor.selectedSensorPosition * constants.powerEncoderMultiplierPosition
        }
    }

    private fun getVelocity(): Double {
        return if (!inverted) {
            powerMotor.selectedSensorVelocity * constants.powerEncoderMultiplierVelocity
        } else {
            -powerMotor.selectedSensorVelocity * constants.powerEncoderMultiplierVelocity
        }
    }

    private fun getAngle(): Rotation2d {
        return if (inverted) {
            Rotation2d(-(angleEncoder.absolutePosition * constants.angleEncoderMultiplier - angleOffset))
        } else {
            Rotation2d(angleEncoder.absolutePosition * constants.angleEncoderMultiplier - angleOffset)
        }
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
        val angle = getAngle()
        state = SwerveModuleState(getVelocity(), angle)
        position = SwerveModulePosition(getDistance(), angle)
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
        // Could multiply by cos angle
        // Why isn't motor.inverted working
        if (!inverted) {
            powerMotor.set(ControlMode.PercentOutput, (feedforward + pid) / 12.0)
        } else {
            powerMotor.set(ControlMode.PercentOutput, -(feedforward + pid) / 12.0)
        }
        angleMotor.set(anglePID.calculate(state.angle.radians, optimized.angle.radians))
    }

    fun enterBrakePos() {
        set(SwerveModuleState(0.0, centerRotation))
    }

    fun setMotorMode(coast: Boolean) {
        if (coast) {
            powerMotor.setNeutralMode(NeutralMode.Coast)
            angleMotor.idleMode = CANSparkMax.IdleMode.kCoast
        } else {
            powerMotor.setNeutralMode(NeutralMode.Brake)
            angleMotor.idleMode = CANSparkMax.IdleMode.kBrake
        }
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

    private val cam = PhotonCamera(constants.camName)
    private var prevRes: PhotonPipelineResult? = null

    private val kinematics: SwerveDriveKinematics
    private var modules: Array<SwerveModule>
    private val odometry: SwerveDriveOdometry
    private val poseEstimator: SwerveDrivePoseEstimator

    var pose = Pose2d()

    var odometryPose = Pose2d()
        private set
        get() = odometry.poseMeters

    // False is broken
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

        val positions = mutableListOf<SwerveModulePosition>()

        for (module in modules) {
            module.updateState()
            positions.add(module.position)
        }

        val positionsArray = positions.toTypedArray()

        kinematics = SwerveDriveKinematics(*modulePositions.toTypedArray())
        poseEstimator = SwerveDrivePoseEstimator(kinematics, imu.rotation2d, positionsArray, Pose2d(), constants.stateDeviations, constants.globalDeviations)
        odometry = SwerveDriveOdometry(kinematics, imu.rotation2d, positionsArray)

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
            moduleData.inverted,
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
        val res = cam.latestResult
        if (res != prevRes && !Input.camOff) {
            if (res.hasTargets()) {
                val camToTargetTrans = res.bestTarget.bestCameraToTarget
                val camPose = constants.tagPose.transformBy(camToTargetTrans.inverse())
                poseEstimator.addVisionMeasurement(camPose.transformBy(constants.cameraTrans).toPose2d(), res.timestampSeconds)
            }

            prevRes = res
        }

        val positions = mutableListOf<SwerveModulePosition>()

        for (module in modules) {
            module.updateState()
            val pos = module.position
            pos.angle += Rotation2d(PI / 2)
            positions.add(pos)
        }

        val positionsArray = positions.toTypedArray()

        pose = poseEstimator.update(imu.rotation2d, positionsArray)
        odometry.update(imu.rotation2d, positionsArray)
    }

    fun setOptimize(value: Boolean) {
        doesOptimize = value

        for (module in modules) {
            module.setOptimize(doesOptimize)
        }
    }

    fun setNewPose(newPose: Pose2d) {
        pose = newPose

        val positions = mutableListOf<SwerveModulePosition>()

        for (module in modules) {
            module.updateState()
            positions.add(module.position)
        }

        val positionsArray = positions.toTypedArray()

        odometry.resetPosition(imu.rotation2d, positionsArray, pose)
        poseEstimator.resetPosition(imu.rotation2d, positionsArray, pose)
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

    fun setMode(coast: Boolean) {
        for (module in modules) {
            module.setMotorMode(coast)
        }
    }

    fun stop() {
        for (module in modules) {
            module.stopMotor()
        }

        feed()
    }
}