package org.sert2521.bunnybots2022.subsystems

import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel
import com.revrobotics.SparkMaxRelativeEncoder
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.sert2521.bunnybots2022.Reloadable
import org.sert2521.bunnybots2022.commands.outtake.IndexOuttake
import org.sert2521.bunnybots2022.constants

// This behaviour (using periodic to control motors) is not consistent with other subsystems
// I am testing different styles for bunnybots
object Outtake : SubsystemBase(), Reloadable {
    private val flapMotor = CANSparkMax(constants.outtakeFlapMotorID, CANSparkMaxLowLevel.MotorType.kBrushed)
    private val indexingMotor = CANSparkMax(constants.outtakeIndexingMotorID, CANSparkMaxLowLevel.MotorType.kBrushed)
    private val indexEncoder = indexingMotor.getEncoder(SparkMaxRelativeEncoder.Type.kQuadrature, 4096)

    private lateinit var indexerPID: PIDController

    private val atOpen = DigitalInput(constants.outtakeAtTopPin)
    private val atClosed = DigitalInput(constants.outtakeAtBottomPin)

    var openingFlap = false
    var speedSetpoint = 0.0

    init {
        flapMotor.inverted = true
        indexingMotor.inverted = true

        setPID()

        defaultCommand = IndexOuttake()
    }

    private fun setPID() {
        indexerPID = PIDController(constants.outtakeP, constants.outtakeI, constants.outtakeD)
    }

    override fun periodic() {
        if (openingFlap) {
            if (!isOpen()) {
                flapMotor.set(constants.outtakeFlapSpeed)
            } else {
                flapMotor.stopMotor()
            }
        } else {
            if (!isClosed()) {
                flapMotor.set(-constants.outtakeFlapSpeed)
            } else {
                flapMotor.stopMotor()
            }
        }

        indexingMotor.set(indexerPID.calculate(getSpinSpeed(), speedSetpoint) + speedSetpoint * constants.outtakeF)
    }

    fun getSpinAmount(): Double {
        return indexEncoder.position * constants.outtakeConversionFactor
    }

    private fun getSpinSpeed(): Double {
        return indexEncoder.velocity * constants.outtakeConversionFactor
    }

    fun isOpen(): Boolean {
        return !atOpen.get()
    }

    fun isClosed(): Boolean {
        return !atClosed.get()
    }

    fun stop() {
        speedSetpoint = 0.0
    }

    override fun reload() {
        setPID()
    }
}