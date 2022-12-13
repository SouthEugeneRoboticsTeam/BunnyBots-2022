package org.sert2521.bunnybots2022.commands.indexer

import edu.wpi.first.wpilibj2.command.CommandBase
import org.sert2521.bunnybots2022.constants
import org.sert2521.bunnybots2022.subsystems.Indexer
import org.sert2521.bunnybots2022.subsystems.Lift

class RunIndexer : CommandBase(){
    init {
        addRequirements(Indexer)
    }

    override fun execute() {
        if (Lift.atBottom()) {
            Indexer.setIndexerSpeed(constants.indexerSpeed)
        } else {
            Indexer.stop()
        }
    }

    override fun end(interrupted: Boolean) {
        Indexer.stop()
    }
}