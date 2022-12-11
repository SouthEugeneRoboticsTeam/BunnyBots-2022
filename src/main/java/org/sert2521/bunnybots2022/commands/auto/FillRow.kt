package org.sert2521.bunnybots2022.commands.auto

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup
import org.sert2521.bunnybots2022.commands.lift.LiftSetHeight
import org.sert2521.bunnybots2022.constants
import org.sert2521.bunnybots2022.outtake.OuttakeTubes

// Start at constants.startFillPose from the table with lift at top with outtake open
class FillRow : SequentialCommandGroup() {
    init {
        addCommands(
            constants.toTouching.deadlineWith(LiftSetHeight(constants.liftTopHeight, false)),
            OuttakeTubes(1).deadlineWith(LiftSetHeight(constants.liftTopHeight, false)),
            constants.fromTouching.deadlineWith(LiftSetHeight(constants.liftTopHeight, false)),
            OuttakeTubes(1).deadlineWith(LiftSetHeight(constants.liftMiddleHeight, false)),
            LiftSetHeight(constants.liftMiddleHeight, true),
            constants.toTouching.deadlineWith(LiftSetHeight(constants.liftMiddleHeight, false)),
            OuttakeTubes(1).deadlineWith(LiftSetHeight(constants.liftMiddleHeight, false)),
        )
    }
}