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

data class SwerveModuleData(val position: Translation2d, val powerMotorID: Int, val angleMotorID: Int, val angleEncoderID: Int, val angleOffset: Double, val inverted: Boolean)

object TunableConstants {
    // Re run sysid
    val swervePowerS = TunableNumber("Swerve Power S", 1.20983)
    val swervePowerV = TunableNumber("Swerve Power V", 4.601311978)
    val swervePowerA = TunableNumber("Swerve Power A", 0.159883071)

    val swervePowerP = TunableNumber("Swerve Power P", 0.0)//2.730003958)
    val swervePowerI = TunableNumber("Swerve Power I", 0.0)
    val swervePowerD = TunableNumber("Swerve Power D", 0.0)

    val swerveAngleP = TunableNumber("Swerve Angle P", 0.3)
    val swerveAngleI = TunableNumber("Swerve Angle I", 0.0)
    val swerveAngleD = TunableNumber("Swerve Angle D", 0.0)

    val autoForwardP = TunableNumber("Auto Forward P", 0.8)
    val autoForwardI = TunableNumber("Auto Forward I", 0.0)
    val autoForwardD = TunableNumber("Auto Forward D", 0.0)

    val autoAngleP = TunableNumber("Auto Angle P", -0.5)
    val autoAngleI = TunableNumber("Auto Angle I", 0.0)
    val autoAngleD = TunableNumber("Auto Angle D", 0.0)

    val autoAngleMaxVel = TunableNumber("Auto Angle Max Vel", 1.0)
    val autoAngleMaxAcc = TunableNumber("Auto Angle Max Acc", 2.0)

    val liftP = TunableNumber("Lift P", 6.0)
    val liftI = TunableNumber("Lift I", 0.1)
    val liftD = TunableNumber("Lift D", 0.0)
    val liftG = TunableNumber("Lift G", 0.0)
    val liftTolerance = TunableNumber("Lift Tolerance", 0.0)

    val indexerP = TunableNumber("Indexer P", 0.00025)
    val indexerI = TunableNumber("Indexer I", 0.00001)
    val indexerD = TunableNumber("Indexer D", 0.0)
    val indexerF = TunableNumber("Indexer F", 0.00025)

    val outtakeP = TunableNumber("Outtake P", 0.0075)
    val outtakeI = TunableNumber("Outtake I", 0.0005)
    val outtakeD = TunableNumber("Outtake D", 0.0)
    val outtakeF = TunableNumber("Outtake F", 0.0125)
}

// Maybe separate into true constants and tunable constants to make clear what needs to be reloaded
class Constants {
    // (diagonal length / 4) * sqrt of 2
    val halfSideLength = (0.885 / 4) * 1.41421356237
    val swerveModuleData = mutableListOf(
        SwerveModuleData(Translation2d(halfSideLength, -halfSideLength), 5, 2, 17, 5.36 - PI, false),
        SwerveModuleData(Translation2d(-halfSideLength, -halfSideLength), 6, 10, 16, 0.29, false),
        SwerveModuleData(Translation2d(halfSideLength, halfSideLength), 7, 1, 15, 4.82, true),
        SwerveModuleData(Translation2d(-halfSideLength, halfSideLength), 8, 11, 14, 4.86, true))

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

    val indexerP = TunableConstants.indexerP.value
    val indexerI = TunableConstants.indexerI.value
    val indexerD = TunableConstants.indexerD.value
    val indexerF = TunableConstants.indexerF.value

    val outtakeP = TunableConstants.outtakeP.value
    val outtakeI = TunableConstants.outtakeI.value
    val outtakeD = TunableConstants.outtakeD.value
    val outtakeF = TunableConstants.outtakeF.value

    // PI * diameter / (gear ratio * counts per rev)
    val powerEncoderMultiplierPosition = (PI * 0.1016 / (8.14 * 2048))
    // Divided by ten to convert timescale
    val powerEncoderMultiplierVelocity = (PI * 0.1016 / (8.14 * 2048 / 10))

    // Degrees to radians
    val angleEncoderMultiplier = 0.01745329251

    val powerDeadband = 0.1
    val rotDeadband = 0.1
    val joystickDeadband = 0.1

    val driveSpeed = 1.0
    val rotSpeed = 1.0

    val joystickChangeSpeed = 0.4

    val rumbleFactor = 0.4

    val camName = "AprilTag"
    val targetTimeout = 0.7
    val tagPose = Pose3d(0.0, 0.0, 0.0, Rotation3d(0.0, 0.0, 0.0))
    val cameraTrans = Transform3d(Translation3d(halfSideLength, halfSideLength, 0.0), Rotation3d(0.0, 0.0, PI))

    val stateDeviations: Matrix<N3, N1> = MatBuilder(Nat.N3(), Nat.N1()).fill(0.01, 0.01, 0.025)
    val localDeviations: Matrix<N1, N1> = MatBuilder(Nat.N1(), Nat.N1()).fill(0.02)
    val globalDeviations: Matrix<N3, N1> = MatBuilder(Nat.N3(), Nat.N1()).fill(0.0, 0.0, 2.5)
    val startGlobalDeviations: Matrix<N3, N1> = MatBuilder(Nat.N3(), Nat.N1()).fill(0.0, 0.0, 0.0)

    val trajectoryConfig = TrajectoryConfig(1.0, 1.5)
    val trajectoryConfigFast = TrajectoryConfig(1.75, 2.5)
    val startFillPose = Translation2d(0.0, -1.0)
    val touchingFillPose = Translation2d(0.0, -halfSideLength)

    val drivetrainOptimized = true
    val maxLiftSlow = 0.30

    val tuning = false

    val liftEncoderMax = 0.78
    val liftEncoderRatio = liftEncoderMax / 0.789219796657562

    val liftCalibrateSpeed = -0.5

    // Bottom a little lower, so it definitely hits the limit switch
    val liftBottomHeight = -0.025
    val liftTopHeight = 0.77

    val liftMotorID = 9
    val liftUpSwitchID = 7
    val liftDownSwitchID = 6

    val intakeMotor = 4
    val intakeSolenoid = Pair(7, 1)

    val outtakeFlapMotorID = 12
    val outtakeIndexingMotorID = 3
    val outtakeAtTopPin = 8
    val outtakeAtBottomPin = 9

    val outtakeFlapSpeed = 0.3
    val outtakeConversionFactor = (0.24 - 0.07) / (1112.886962890625 - 1116.98681640625)
    // No idea what units 40 and 80 are in
    val outtakeDefaultSpeed = 40.0
    val outtakeExhaleSpeed = 40.0
    val outtakeHoldSpeed = 10.0
    val outtakeOneDistance = 0.18

    val indexerMotorID = 13
    // No idea what this is in either
    val indexerSpeed = 2000.0

    val intakeSpeed = 0.7
}

var constants = Constants()