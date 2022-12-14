package org.sert2521.bunnybots2022.commands.outtake

import edu.wpi.first.wpilibj2.command.CommandBase
import org.sert2521.bunnybots2022.constants
import org.sert2521.bunnybots2022.subsystems.Outtake

class OuttakeTubes(private val amount: Int?) : CommandBase() {
    private var startPlace = 0.0

    init {
        addRequirements(Outtake)
    }

    override fun initialize() {
        startPlace = Outtake.getSpinAmount()

        Outtake.openingFlap = true
        Outtake.stop()
    }

    override fun execute() {
        if (Outtake.isOpen()) {
            Outtake.speedSetpoint = constants.outtakeExhaleSpeed
        } else {
            Outtake.stop()
        }
    }

    override fun isFinished(): Boolean {
        return if (amount != null) {
            startPlace + amount * constants.outtakeOneDistance > Outtake.getSpinAmount()
        } else {
            false
        }
    }

    override fun end(interrupted: Boolean) {
        Outtake.stop()
    }
}