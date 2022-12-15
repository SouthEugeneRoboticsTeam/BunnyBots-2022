package org.sert2521.bunnybots2022.subsystems

import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.sert2521.bunnybots2022.constants

object Lift : SubsystemBase() {
    private val motor = CANSparkMax(constants.liftMotorID, CANSparkMaxLowLevel.MotorType.kBrushless)
    private val liftUpLimitSwitch = DigitalInput(constants.liftUpSwitchID)
    private val liftDownLimitSwitch = DigitalInput(constants.liftDownSwitchID)
    var unset = true
        private set

    init {
        motor.idleMode = CANSparkMax.IdleMode.kBrake
    }

    override fun periodic() {
        if (!unset && getHeight() >= constants.liftTopHeight) {
            if (motor.get() > 0) {
                motor.stopMotor()
            }
        } else if (atBottom()) {
            motor.encoder.position = constants.liftBottomHeight
            unset = false

            if (motor.get() < 0) {
                motor.stopMotor()
            }
        }

        if (unset) {
            motor.set(constants.liftCalibrateSpeed)
        }
    }

    fun getHeight(): Double {
        if (unset) {
            return constants.liftEncoderMax
        }

        return motor.encoder.position * constants.liftEncoderRatio
    }

    fun setHeight(place: Double) {
        motor.encoder.position = place / constants.liftEncoderRatio
    }

    fun setMotor(speed: Double) {
        if (unset) {
            return
        } else if((getHeight() >= constants.liftEncoderMax) && (speed > 0.0)) {
            return
        }else if((getHeight() <= 0.0) && (speed < 0.0)) {
            return
        }

        motor.set(speed)
    }

    fun atBottom(): Boolean {
        return !liftDownLimitSwitch.get()
    }

    fun atTop(): Boolean {
        return !liftUpLimitSwitch.get()
    }

    fun stop() {
        motor.stopMotor()
    }
}