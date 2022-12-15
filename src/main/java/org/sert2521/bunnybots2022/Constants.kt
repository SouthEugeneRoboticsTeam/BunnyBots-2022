package org.sert2521.bunnybots2022

import edu.wpi.first.math.MatBuilder
import edu.wpi.first.math.Matrix
import edu.wpi.first.math.Nat
import edu.wpi.first.math.geometry.*
import edu.wpi.first.math.numbers.N1
import edu.wpi.first.math.numbers.N3
import edu.wpi.first.math.trajectory.TrajectoryConfig
import edu.wpi.first.math.trajectory.TrajectoryGenerator
import org.sert2521.bunnybots2022.commands.auto.DrivePath
import kotlin.math.PI

data class SwerveModuleData(val position: Translation2d, val powerMotorID: Int, val angleMotorID: Int, val angleEncoderID: Int, val angleOffset: Double)

object TunableConstants {
    val swervePowerS = TunableNumber("Swerve Power S", 0.60983)
    val swervePowerV = TunableNumber("Swerve Power V", 2.601311978)
    val swervePowerA = TunableNumber("Swerve Power A", 0.159883071)

    val swervePowerP = TunableNumber("Swerve Power P", 2.730003958)
    val swervePowerI = TunableNumber("Swerve Power I", 0.0)
    val swervePowerD = TunableNumber("Swerve Power D", 0.0)

    val swerveAngleP = TunableNumber("Swerve Angle P", 0.3)
    val swerveAngleI = TunableNumber("Swerve Angle I", 0.0)
    val swerveAngleD = TunableNumber("Swerve Angle D", 0.0)

    val autoForwardP = TunableNumber("Auto Forward P", 0.0)
    val autoForwardI = TunableNumber("Auto Forward I", 0.0)
    val autoForwardD = TunableNumber("Auto Forward D", 0.0)

    val autoAngleP = TunableNumber("Auto Angle P", 0.5)
    val autoAngleI = TunableNumber("Auto Angle I", 0.0)
    val autoAngleD = TunableNumber("Auto Angle D", 0.0)

    val autoAngleMaxVel = TunableNumber("Auto Angle Max Vel", 0.5)
    val autoAngleMaxAcc = TunableNumber("Auto Angle Max Acc", 0.5)

    val liftP = TunableNumber("Lift P", 6.0)
    val liftI = TunableNumber("Lift I", 0.0)
    val liftD = TunableNumber("Lift D", 0.0)
    val liftG = TunableNumber("Lift G", 0.0)
    val liftTolerance = TunableNumber("Lift Tolerance", 0.0)

    val outtakeP = TunableNumber("Outtake P", 0.01)
    val outtakeI = TunableNumber("Outtake I", 0.0)
    val outtakeD = TunableNumber("Outtake D", 0.0)
    val outtakeF = TunableNumber("Outtake F", 0.01)
}

// Maybe separate into true constants and tunable constants to make clear what needs to be reloaded
class Constants {
    // (diagonal length / 4) * sqrt of 2
    private val halfSideLength = (0.885 / 4) * 1.41421356237
    val swerveModuleData = mutableListOf(
        SwerveModuleData(Translation2d(halfSideLength, -halfSideLength), 5, 2, 17, 5.26 - (PI)),
        SwerveModuleData(Translation2d(-halfSideLength, -halfSideLength), 6, 10, 16, 0.29),
        SwerveModuleData(Translation2d(halfSideLength, halfSideLength), 7, 1, 15, 4.77),
        SwerveModuleData(Translation2d(-halfSideLength, halfSideLength), 8, 11, 14, 4.76))

    val swervePowerS = TunableConstants.swervePowerS.value
    val swervePowerV = TunableConstants.swervePowerV.value
    val swervePowerA = TunableConstants.swervePowerA.value

    val swervePowerP = TunableConstants.swervePowerP.value
    val swervePowerI = TunableConstants.swervePowerI.value
    val swervePowerD = TunableConstants.swervePowerD.value

    val swerveAngleP = TunableConstants.swerveAngleP.value
    val swerveAngleI = TunableConstants.swerveAngleI.value
    val swerveAngleD = TunableConstants.swerveAngleD.value

    val autoForwardP = TunableConstants.autoForwardP.value
    val autoForwardI = TunableConstants.autoForwardI.value
    val autoForwardD = TunableConstants.autoForwardD.value

    val autoAngleP = TunableConstants.autoAngleP.value
    val autoAngleI = TunableConstants.autoAngleI.value
    val autoAngleD = TunableConstants.autoAngleD.value

    val autoMaxVel = TunableConstants.autoAngleMaxVel.value
    val autoMaxAcc = TunableConstants.autoAngleMaxAcc.value

    val liftP = TunableConstants.liftP.value
    val liftI = TunableConstants.liftI.value
    val liftD = TunableConstants.liftD.value
    val liftG = TunableConstants.liftG.value
    val liftTolerance = TunableConstants.liftTolerance.value

    val outtakeP = TunableConstants.outtakeP.value
    val outtakeI = TunableConstants.outtakeI.value
    val outtakeD = TunableConstants.outtakeD.value
    val outtakeF = TunableConstants.outtakeF.value

    // Figure out the divide by 10
    // PI * diameter / (gear ratio * counts per rev)
    val powerEncoderMultiplier = (PI * 0.1016 / (8.14 * 2048 / 10))

    // Degrees to radians
    val angleEncoderMultiplier = 0.01745329251

    val powerDeadband = 0.05
    val rotDeadband = 0.1
    val joystickDeadband = 0.1

    val driveSpeed = 1.0
    val rotSpeed = 2.0

    val joystickChangeSpeed = 0.4

    val rumbleFactor = 0.2

    val targetTimeout = 0.7
    val tagPose = Pose3d(1.0, 0.0, 0.0, Rotation3d(0.0, 0.0, 0.0))
    val cameraTrans = Transform3d(Translation3d(halfSideLength, 0.0, 0.0), Rotation3d(0.0, 0.0, 0.0))

    val stateDeviations: Matrix<N3, N1> = MatBuilder(Nat.N3(), Nat.N1()).fill(0.05, 0.05, 0.01)
    val localDeviations: Matrix<N1, N1> = MatBuilder(Nat.N1(), Nat.N1()).fill(0.02)
    val globalDeviations: Matrix<N3, N1> = MatBuilder(Nat.N3(), Nat.N1()).fill(0.01, 0.01, 0.05)
    val startGlobalDeviations: Matrix<N3, N1> = MatBuilder(Nat.N3(), Nat.N1()).fill(0.0, 0.0, 0.0)

    val trajectoryConfig = TrajectoryConfig(0.5, 0.5)
    private val startFillPose = Pose2d(-1.0, 0.0, Rotation2d(0.0))
    private val touchingFillPose = Pose2d(-halfSideLength / 0.9, 0.0, Rotation2d(0.0))
    val toTouching = DrivePath(TrajectoryGenerator.generateTrajectory(startFillPose, mutableListOf(), touchingFillPose, trajectoryConfig), Rotation2d(0.0))
    val fromTouching = DrivePath(TrajectoryGenerator.generateTrajectory(touchingFillPose, mutableListOf(), startFillPose, trajectoryConfig), Rotation2d(0.0))

    val drivetrainOptimized = true
    val maxLiftSlow = 0.75

    val tuning = false

    val liftEncoderMax = 0.84
    val liftEncoderRatio = liftEncoderMax / 0.789219796657562

    val liftCalibrateSpeed = -0.25

    val liftBottomHeight = 0.0
    val liftMiddleHeight = 0.12
    val liftTopHeight = 0.83

    val intakeMotor = 4
    val intakeSolenoid = Pair(7, 1)

    val outtakeFlapMotorID = 12
    val outtakeIndexingMotorID = 3
    val outtakeAtTopPin = 8
    val outtakeAtBottomPin = 9

    val outtakeFlapSpeed = 0.3
    val outtakeConversionFactor = (0.24 - 0.07) / (1112.886962890625 - 1116.98681640625)
    val outtakeDefaultSpeed = 40.0
    val outtakeExhaleSpeed = 80.0
    val outtakeOneDistance = 0.0

    val indexerMotorID = 13
    val indexerSpeed = 0.0//.8

    val liftMotorID = 9
    val liftUpSwitchID = 7
    val liftDownSwitchID = 6

    val intakeSpeed = 0.0//7
}

var constants = Constants()