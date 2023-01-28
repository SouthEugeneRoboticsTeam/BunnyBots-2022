package org.sert2521.bunnybots2022.subsystems

import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel
import edu.wpi.first.wpilibj.DoubleSolenoid
import edu.wpi.first.wpilibj.PneumaticsModuleType
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.sert2521.bunnybots2022.constants

object Intake : SubsystemBase() {
    //private val intakeMotor = CANSparkMax(constants.intakeMotor, CANSparkMaxLowLevel.MotorType.kBrushless)
    //private val intakePneu = DoubleSolenoid(PneumaticsModuleType.CTREPCM, constants.intakeSolenoid.first, constants.intakeSolenoid.second)

    init {
        //intakeMotor.inverted = true
    }

    fun intakeOn(speed: Double)  {
        //intakeMotor.set(speed)
    }

    fun stop() {
        //intakeMotor.stopMotor()
    }

    fun pneuOn() {
       //intakePneu.set(DoubleSolenoid.Value.kReverse)
    }

    fun pneuNeither() {
        //intakePneu.set(DoubleSolenoid.Value.kOff)
    }

    fun pneuStop() {
        //intakePneu.set(DoubleSolenoid.Value.kForward)
    }
}