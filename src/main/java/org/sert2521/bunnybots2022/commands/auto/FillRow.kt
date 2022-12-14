package org.sert2521.bunnybots2022.commands.auto

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup
import org.sert2521.bunnybots2022.commands.lift.LiftSetHeight
import org.sert2521.bunnybots2022.constants
import org.sert2521.bunnybots2022.commands.outtake.OuttakeTubes

// Start at constants.startFillPose from the table with lift at top with outtake open
// Have to add conversion to teleop (maybe onTeleop has a LiftSetHeight(Lift.getHeight(), false))
class FillRow : SequentialCommandGroup() {
    init {
        addCommands(
            constants.toTouching.deadlineWith(LiftSetHeight(constants.liftMiddleHeight, false)),
            OuttakeTubes(1).deadlineWith(LiftSetHeight(constants.liftMiddleHeight, false)),
            constants.fromTouching.deadlineWith(LiftSetHeight(constants.liftMiddleHeight, false)),
            LiftSetHeight(constants.liftTopHeight, true),
            constants.toTouching.deadlineWith(LiftSetHeight(constants.liftTopHeight, false)),
            OuttakeTubes(null).deadlineWith(LiftSetHeight(constants.liftTopHeight, false)),
        )
    }
}
