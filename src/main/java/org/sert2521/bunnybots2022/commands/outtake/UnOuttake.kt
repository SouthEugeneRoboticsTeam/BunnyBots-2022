package org.sert2521.bunnybots2022.commands.outtake

import edu.wpi.first.wpilibj2.command.CommandBase
import org.sert2521.bunnybots2022.constants
import org.sert2521.bunnybots2022.subsystems.Outtake

class UnOuttake : CommandBase() {
    init {
        addRequirements(Outtake)
    }

    override fun initialize() {
        Outtake.openingFlap = false
        Outtake.speedSetpoint = -constants.outtakeDefaultSpeed
    }

    override fun end(interrupted: Boolean) {
        Outtake.stop()
    }
}