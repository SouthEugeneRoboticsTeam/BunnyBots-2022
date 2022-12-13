package org.sert2521.bunnybots2022.subsystems

import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.sert2521.bunnybots2022.constants

// This behaviour (using periodic to control motors) is not consistent with other subsystems
// I am testing different styles for bunnybots
object Outtake : SubsystemBase() {
    private val flapMotor = CANSparkMax(constants.outtakeFlapMotorID, CANSparkMaxLowLevel.MotorType.kBrushed)
    private val indexingMotor = CANSparkMax(constants.outtakeIndexingMotorID, CANSparkMaxLowLevel.MotorType.kBrushed)

    private val atOpen = DigitalInput(constants.outtakeAtTopPin)
    private val atClosed = DigitalInput(constants.outtakeAtBottomPin)

    var openingFlap = false

    init {
        flapMotor.inverted = true
        indexingMotor.inverted = true
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
    }

    fun setOuttakeSpeed(speed: Double) {
        indexingMotor.set(speed)
    }

    fun getSpinAmount(): Double {
        return indexingMotor.encoder.position * constants.outtakeConversionFactor
    }

    fun isOpen(): Boolean {
        return !atOpen.get()
    }

    fun isClosed(): Boolean {
        return !atClosed.get()
    }

    fun stop() {
        indexingMotor.stopMotor()
    }
}