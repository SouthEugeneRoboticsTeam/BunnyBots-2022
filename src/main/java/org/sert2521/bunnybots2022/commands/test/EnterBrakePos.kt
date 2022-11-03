package org.sert2521.bunnybots2022.commands.test

import edu.wpi.first.wpilibj2.command.CommandBase
import org.sert2521.bunnybots2022.subsystems.Drivetrain

class EnterBrakePos : CommandBase() {
    init {
        addRequirements(Drivetrain)
    }

    override fun execute() {
        Drivetrain.enterBrakePos()
    }

    override fun end(interrupted: Boolean) {
        Drivetrain.stop()
    }
}
