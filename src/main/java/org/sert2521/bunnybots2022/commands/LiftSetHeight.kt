package org.sert2521.bunnybots2022.commands

import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.wpilibj2.command.CommandBase
import org.sert2521.bunnybots2022.constants
import org.sert2521.bunnybots2022.subsystems.Lift

class LiftSetHeight(private val height: Double) : CommandBase() {
    private val pid = PIDController(constants.liftCommandP, constants.liftCommandI, constants.liftCommandD)
    init {
        addRequirements(Lift)
    }

    override fun initialize(){
        pid.reset()
    }

    override fun execute() {
        Lift.setMotor(pid.calculate(Lift.getHeight(), height))
    }

    override fun end(interrupted: Boolean) {
        Lift.stop()
    }
}
