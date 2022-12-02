package org.sert2521.bunnybots2022.commands

import edu.wpi.first.wpilibj2.command.CommandBase
import org.sert2521.bunnybots2022.constants
import org.sert2521.bunnybots2022.subsystems.Intake

class IntakeCommand : CommandBase() {
    override fun initialize() {
        Intake.pneuStop()
        Intake.intakeOn(constants.intakeSpeed)
    }

    override fun end(interrupted: Boolean) {
        Intake.pneuOn()
        Intake.stop()
    }
}