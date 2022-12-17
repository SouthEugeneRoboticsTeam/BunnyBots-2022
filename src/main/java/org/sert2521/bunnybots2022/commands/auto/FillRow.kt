package org.sert2521.bunnybots2022.commands.auto

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.wpilibj2.command.InstantCommand
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup
import org.sert2521.bunnybots2022.commands.lift.LiftSetHeight
import org.sert2521.bunnybots2022.commands.outtake.HoldOpenOuttake
import org.sert2521.bunnybots2022.constants
import org.sert2521.bunnybots2022.commands.outtake.OuttakeTubes
import org.sert2521.bunnybots2022.subsystems.Drivetrain
import kotlin.math.PI

// Start at constants.startFillPose from the table with lift at top with outtake open
// Have to add conversion to teleop (maybe onTeleop has a LiftSetHeight(Lift.getHeight(), false))
class FillRow : SequentialCommandGroup() {
    init {
        val toTouching1 = DrivePath(Pose2d(0.0, -constants.halfSideLength * 1.5, Rotation2d(PI / 2)),
            Pose2d(0.0, -constants.halfSideLength, Rotation2d(PI / 2)),
            constants.trajectoryConfig,
            Rotation2d(0.0))

        val fromTouching = DrivePath(Pose2d(0.0, -constants.halfSideLength, Rotation2d(-PI / 2)),
            Pose2d(0.0, -constants.halfSideLength * 1.5, Rotation2d(-PI / 2)),
            constants.trajectoryConfig,
            Rotation2d(0.0))

        val toTouching2 = DrivePath(Pose2d(0.0, -constants.halfSideLength * 1.5, Rotation2d(PI / 2)),
            Pose2d(0.0, -constants.halfSideLength, Rotation2d(PI / 2)),
            constants.trajectoryConfig,
            Rotation2d(0.0))

        addCommands(
            InstantCommand({ Drivetrain.setNewPose(Pose2d(0.0, -constants.halfSideLength * 1.5, Rotation2d(0.0))) }),
            toTouching1.deadlineWith(LiftSetHeight(constants.liftBottomHeight, false), HoldOpenOuttake()),
            OuttakeTubes(1).deadlineWith(LiftSetHeight(constants.liftBottomHeight, false)),
            fromTouching.deadlineWith(LiftSetHeight(constants.liftTopHeight, false), OuttakeTubes(1).andThen(HoldOpenOuttake())),
            LiftSetHeight(constants.liftTopHeight, true).deadlineWith(HoldOpenOuttake()),
            toTouching2.deadlineWith(LiftSetHeight(constants.liftTopHeight, false), HoldOpenOuttake()),
            OuttakeTubes(null).deadlineWith(LiftSetHeight(constants.liftTopHeight, false)),
        )
    }
}
