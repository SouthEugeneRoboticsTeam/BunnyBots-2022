package org.sert2521.bunnybots2022.subsystems

import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.sert2521.bunnybots2022.constants

object Lift : SubsystemBase() {
    val motor = CANSparkMax(-1, CANSparkMaxLowLevel.MotorType.kBrushless)
    val liftDownLimitSwitch = DigitalInput(-1)

    override fun periodic(){
        if(liftDownLimitSwitch.get()) {
            motor.encoder.position = 0.0
        }

        if(getHeight() >= constants.liftEncoderMax){
            if(motor.get()>0){
                motor.stopMotor()
            }
        }else if(getHeight() <=0.0){
            if(motor.get()<0){
                motor.stopMotor()
            }
        }
    }

    fun getHeight(): Double{
        return motor.encoder.position * constants.liftEncoderRatio
    }
    fun setMotor(speed:Double){
        if((getHeight() >= constants.liftEncoderMax) && (speed > 0.0)){
            return
        }else if((getHeight() <= 0.0) && (speed < 0.0)){
            return
        }
        motor.set(speed)
    }

    fun atBottom():Boolean{
        return liftDownLimitSwitch.get()
    }

    fun stop(){
        motor.stopMotor()
    }
}