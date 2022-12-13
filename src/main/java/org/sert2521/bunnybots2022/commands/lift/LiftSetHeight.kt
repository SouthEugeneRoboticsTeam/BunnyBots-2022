package org.sert2521.bunnybots2022.commands.lift

import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.wpilibj2.command.CommandBase
import org.sert2521.bunnybots2022.Reloadable
import org.sert2521.bunnybots2022.TunableConstants
import org.sert2521.bunnybots2022.subsystems.Lift

// Could use more advanced control loop (maybe sysid)
class LiftSetHeight(private val height: Double, private val finish: Boolean) : CommandBase(), Reloadable {
    private lateinit var pid: PIDController
    private var liftG: Double = 0.0

    init {
        addRequirements(Lift)

        registerReload()

        initPID()
    }

    private fun initPID() {
        pid = PIDController(TunableConstants.liftP.value, TunableConstants.liftI.value, TunableConstants.liftD.value)
        liftG = TunableConstants.liftG.value
    }

    override fun initialize(){
        pid.reset()
    }

    override fun execute() {
        Lift.setMotor(pid.calculate(Lift.getHeight(), height))
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
