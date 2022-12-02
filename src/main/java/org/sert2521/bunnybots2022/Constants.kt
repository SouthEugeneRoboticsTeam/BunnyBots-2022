package org.sert2521.bunnybots2022

import edu.wpi.first.math.MatBuilder
import edu.wpi.first.math.Matrix
import edu.wpi.first.math.Nat
import edu.wpi.first.math.geometry.*
import edu.wpi.first.math.numbers.N1
import edu.wpi.first.math.numbers.N3
import edu.wpi.first.math.trajectory.TrajectoryConfig
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
}

// Maybe separate into true constants and tunable constants to make clear what needs to be reloaded
class Constants {
    // (diagonal length / 4) * sqrt of 2
    private val halfSideLength = (0.885 / 4) * 1.41421356237
    val swerveModuleData = mutableListOf(
        SwerveModuleData(Translation2d(halfSideLength, -halfSideLength), 5, 2, 17, 5.26 - (PI / 2)),
        SwerveModuleData(Translation2d(-halfSideLength, -halfSideLength), 6, 10, 16, 0.29 + (PI / 2)),
        SwerveModuleData(Translation2d(halfSideLength, halfSideLength), 7, 1, 15, 4.77 + (PI / 2)),
        SwerveModuleData(Translation2d(-halfSideLength, halfSideLength), 8, 11, 14, 4.76 + (PI / 2)))

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

    val joystickChangeSpeed = 0.1

    val rumbleFactor = 0.2

    val targetTimeout = 0
    val tagPose = Pose3d()
    val cameraTrans = Transform3d()

    val stateDeviations: Matrix<N3, N1> = MatBuilder(Nat.N3(), Nat.N1()).fill(0.05, 0.05, 0.01)
    val localDeviations: Matrix<N1, N1> = MatBuilder(Nat.N1(), Nat.N1()).fill(0.02)
    val globalDeviations: Matrix<N3, N1> = MatBuilder(Nat.N3(), Nat.N1()).fill(0.01, 0.01, 0.05)
    val startGlobalDeviations: Matrix<N3, N1> = MatBuilder(Nat.N3(), Nat.N1()).fill(0.0, 0.0, 0.0)

    val trajectoryConfig = TrajectoryConfig(0.5, 0.5)

    val drivetrainOptimized = false

    val tuning = false
}

var constants = Constants()