package org.sert2521.bunnybots2022.commands.lift

import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.wpilibj2.command.CommandBase
import org.sert2521.bunnybots2022.Reloadable
import org.sert2521.bunnybots2022.constants
import org.sert2521.bunnybots2022.subsystems.Lift

// Could use more advanced control loop (maybe sysid)
class LiftSetHeight(private val height: Double, private val finish: Boolean) : CommandBase(), Reloadable {
    private lateinit var pid: PIDController

    init {
        addRequirements(Lift)

        registerReload()

        initPID()
    }

    private fun initPID() {
        pid = PIDController(constants.liftP, constants.liftI, constants.liftD)
        pid.setpoint = constants.liftTolerance
    }

    override fun initialize(){
        pid.reset()
    }

    override fun execute() {
        Lift.setMotor(pid.calculate(Lift.getHeight(), height) + constants.liftG)
    }

    override fun isFinished(): Boolean {
        return finish && pid.atSetpoint()
    }

    override fun end(interrupted: Boolean) {
        Lift.stop()
    }

    override fun reload() {
        initPID()
    }
}
