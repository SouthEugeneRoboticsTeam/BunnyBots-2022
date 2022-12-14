package org.sert2521.bunnybots2022.commands.outtake

import edu.wpi.first.wpilibj2.command.CommandBase
import org.sert2521.bunnybots2022.constants
import org.sert2521.bunnybots2022.subsystems.Outtake

class IndexOuttake : CommandBase() {
    init {
        addRequirements(Outtake)
    }

    override fun initialize() {
        Outtake.openingFlap = false
        Outtake.stop()
    }

    override fun execute() {
        if (Outtake.isClosed()) {
            Outtake.speedSetpoint = constants.outtakeDefaultSpeed
        } else {
            Outtake.stop()
        }
    }

    override fun end(interrupted: Boolean) {
        Outtake.stop()
    }
}