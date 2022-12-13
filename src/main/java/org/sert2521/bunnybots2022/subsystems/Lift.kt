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
    private var unset = true

    init {
        motor.idleMode = CANSparkMax.IdleMode.kBrake
    }

    override fun periodic() {
        if (atTop()) {
            motor.encoder.position = constants.liftEncoderMax
            unset = false
        }

        if(atBottom()) {
            motor.encoder.position = 0.0
            unset = false
        }

        if(getHeight() >= constants.liftEncoderMax) {
            if(motor.get()>0){
                motor.stopMotor()
            }
        }else if(getHeight()<=0.0) {
            if(motor.get()<0) {
                motor.stopMotor()
            }
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
        if((getHeight() >= constants.liftEncoderMax) && (speed > 0.0)) {
            return
        }else if((getHeight() <= 0.0) && (speed < 0.0)) {
            return
        }
        //motor.set(speed)
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