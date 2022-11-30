package org.sert2521.bunnybots2022.commands

import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.CommandBase
import org.sert2521.bunnybots2022.constants
import org.sert2521.bunnybots2022.subsystems.Intake

class IntakeCommand : CommandBase() {
    private var startTime =  0.0

    override fun initialize() {
        Intake.pneuStop()
        Intake.intakeOn(constants.intakeSpeed)

        startTime = Timer.getFPGATimestamp()
    }

    override fun execute() {
        // Double check actually helps
        if (Timer.getFPGATimestamp() - startTime > constants.intakeForceOpenTime * 1000) {
            Intake.pneuNeither()
        }
    }

    override fun end(interrupted: Boolean) {
        Intake.pneuOn()
        Intake.stop()
    }
}