package org.sert2521.bunnybots2022.commands.lift

import edu.wpi.first.wpilibj2.command.CommandBase
import org.sert2521.bunnybots2022.constants
import org.sert2521.bunnybots2022.subsystems.Lift

class LiftCalibrate : CommandBase() {
    init {
        addRequirements(Lift)
    }

    override fun initialize() {
        Lift.stop()
        Lift.setMotor(constants.liftCalibrateSpeed)
    }

    override fun isFinished(): Boolean {
        return Lift.atBottom()
    }

    override fun end(interrupted: Boolean) {
        Lift.stop()
    }
}
