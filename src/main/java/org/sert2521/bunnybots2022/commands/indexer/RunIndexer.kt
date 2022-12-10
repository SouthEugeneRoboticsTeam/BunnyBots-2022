package org.sert2521.bunnybots2022.commands.indexer

import edu.wpi.first.wpilibj2.command.CommandBase
import org.sert2521.bunnybots2022.subsystems.Indexer

class RunIndexer : CommandBase(){
    init {
        addRequirements(Indexer)
    }

    override fun initialize() {
        Indexer.setIndexerSpeed(0.5)
    }

    override fun end(interrupted: Boolean) {
        Indexer.stop()
    }
}