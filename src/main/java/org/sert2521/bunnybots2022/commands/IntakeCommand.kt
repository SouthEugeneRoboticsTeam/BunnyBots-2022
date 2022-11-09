package org.sert2521.bunnybots2022.commands

import edu.wpi.first.wpilibj2.command.CommandBase
import org.sert2521.bunnybots2022.subsystems.Intake

class IntakeCommand : CommandBase() {
    override fun initialize() {
        Intake.pneuOn()
        Intake.intakeOn(0.2)
    }

    override fun end(interrupted: Boolean) {
        Intake.pneuStop()
        Intake.stop()

    }
}