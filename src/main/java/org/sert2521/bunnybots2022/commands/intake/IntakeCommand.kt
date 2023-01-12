package org.sert2521.bunnybots2022.commands.intake

import edu.wpi.first.wpilibj2.command.CommandBase
import org.sert2521.bunnybots2022.constants
import org.sert2521.bunnybots2022.subsystems.Intake
import org.sert2521.bunnybots2022.subsystems.Outtake

class IntakeCommand : CommandBase() {
    init {
        addRequirements(Intake)
    }

    override fun initialize() {
        Intake.pneuStop()
        Intake.intakeOn(constants.intakeSpeed)
    }

    override fun end(interrupted: Boolean) {
        Intake.pneuOn()
        Intake.stop()
    }
}