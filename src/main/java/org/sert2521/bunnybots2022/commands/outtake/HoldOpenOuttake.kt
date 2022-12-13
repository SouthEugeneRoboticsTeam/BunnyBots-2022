package org.sert2521.bunnybots2022.commands.outtake

import edu.wpi.first.wpilibj2.command.CommandBase
import org.sert2521.bunnybots2022.subsystems.Outtake

class HoldOpenOuttake : CommandBase() {
    init {
        addRequirements(Outtake)
    }

    override fun initialize() {
        Outtake.openingFlap = true
        Outtake.stop()
    }
}