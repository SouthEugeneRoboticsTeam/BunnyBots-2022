package org.sert2521.bunnybots2022.commands

import edu.wpi.first.wpilibj2.command.CommandBase
import org.sert2521.bunnybots2022.constants
import org.sert2521.bunnybots2022.subsystems.Indexer
import org.sert2521.bunnybots2022.subsystems.Intake
import org.sert2521.bunnybots2022.subsystems.Lift

class OuttakeFront : CommandBase(){
    init {
        addRequirements(Intake, Indexer)
    }

    override fun initialize() {
        Intake.pneuStop()
        Intake.intakeOn(-constants.intakeSpeed)

        Indexer.speedSetpoint = -constants.indexerSpeed
    }

    override fun end(interrupted: Boolean) {
        Intake.pneuOn()
        Intake.stop()

        Indexer.stop()
    }
}