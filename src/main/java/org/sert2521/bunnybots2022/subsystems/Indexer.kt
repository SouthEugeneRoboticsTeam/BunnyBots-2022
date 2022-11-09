package org.sert2521.bunnybots2022.subsystems

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX
import edu.wpi.first.wpilibj2.command.SubsystemBase

object Indexer : SubsystemBase() {
    //CODE HERE
    private val indexer = WPI_TalonSRX(-1)

    fun setIndexerSpeed(speed: Double) {
        indexer.set(speed)
    }

    fun stop() {
        indexer.stopMotor()
    }
}