package org.sert2521.bunnybots2022.subsystems

import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.sert2521.bunnybots2022.commands.indexer.RunIndexer
import org.sert2521.bunnybots2022.constants

object Indexer : SubsystemBase() {
    private val indexer = CANSparkMax(constants.indexerMotorID, CANSparkMaxLowLevel.MotorType.kBrushed)

    init {
        defaultCommand = RunIndexer()
    }

    fun setIndexerSpeed(speed: Double) {
        indexer.set(speed)
    }

    fun stop() {
        indexer.stopMotor()
    }
}