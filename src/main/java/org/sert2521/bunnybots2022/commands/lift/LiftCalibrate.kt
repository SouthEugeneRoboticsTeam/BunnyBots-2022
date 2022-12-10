package org.sert2521.bunnybots2022.commands.lift

import edu.wpi.first.wpilibj2.command.CommandBase
import org.sert2521.bunnybots2022.subsystems.Lift

class LiftCalibrate : CommandBase() {
    init {
        addRequirements(Lift)
    }

    override fun initialize() {
        Lift.setMotor(0.1)
    }

    override fun isFinished(): Boolean {
        return Lift.atBottom()
    }

    override fun end(interrupted: Boolean) {
        Lift.stop()
    }
}
