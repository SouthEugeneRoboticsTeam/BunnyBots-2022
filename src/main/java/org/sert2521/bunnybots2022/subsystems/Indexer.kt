package org.sert2521.bunnybots2022.subsystems

import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel
import com.revrobotics.SparkMaxRelativeEncoder
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.sert2521.bunnybots2022.Reloadable
import org.sert2521.bunnybots2022.commands.indexer.RunIndexer
import org.sert2521.bunnybots2022.constants

object Indexer : SubsystemBase(), Reloadable {
    //private val indexer = CANSparkMax(constants.indexerMotorID, CANSparkMaxLowLevel.MotorType.kBrushless)

    private lateinit var indexerPID: PIDController

    var speedSetpoint = 0.0

    init {
        //indexer.inverted = true

        setPID()

        defaultCommand = RunIndexer()
    }

    private fun setPID() {
        indexerPID = PIDController(constants.indexerP, constants.indexerI, constants.indexerD)
    }

    override fun periodic() {
        //indexer.set(indexerPID.calculate(indexer.encoder.velocity, speedSetpoint) + speedSetpoint * constants.indexerF)
    }

    fun stop() {
        speedSetpoint = 0.0
    }

    override fun reload() {
        setPID()
    }
}