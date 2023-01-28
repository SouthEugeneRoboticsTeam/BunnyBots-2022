package org.sert2521.bunnybots2022.subsystems

import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.sert2521.bunnybots2022.constants
import kotlin.math.max
import kotlin.math.min

object Lift : SubsystemBase() {
    //private val motor = CANSparkMax(constants.liftMotorID, CANSparkMaxLowLevel.MotorType.kBrushless)
    private val liftUpLimitSwitch = DigitalInput(constants.liftUpSwitchID)
    private val liftDownLimitSwitch = DigitalInput(constants.liftDownSwitchID)
    var unset = true

    init {
        //motor.idleMode = CANSparkMax.IdleMode.kBrake
    }

    override fun periodic() {
        if (atTop()) {
            //motor.encoder.position = constants.liftEncoderMax / constants.liftEncoderRatio
            unset = false

            //if (motor.get() > 0) {
                //motor.stopMotor()
            //}
        } else if (atBottom()) {
            //motor.encoder.position = 0.0

            unset = false

            //if (motor.get() < 0) {
                //motor.stopMotor()
            //}
        }

        if (unset) {
            //motor.set(constants.liftCalibrateSpeed)
        }
    }

    fun getHeight(): Double {
        if (unset) {
            return constants.liftEncoderMax
        }

        // So it definitely gets to bottom
        return 0.0//motor.encoder.position * constants.liftEncoderRatio
    }

    fun setMotor(speed: Double) {
        if (unset) {
            return
        } else if(atTop() && (speed > 0.0)) {
            return
        }else if(atBottom() && (speed < 0.0)) {
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
        //motor.stopMotor()
    }
}