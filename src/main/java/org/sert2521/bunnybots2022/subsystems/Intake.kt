package org.sert2521.bunnybots2022.subsystems

import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel
import edu.wpi.first.wpilibj.PneumaticsModuleType
import edu.wpi.first.wpilibj.Solenoid
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.sert2521.bunnybots2022.constants

object Intake : SubsystemBase() {
    private val intakeMotor = CANSparkMax(constants.sparks, CANSparkMaxLowLevel.MotorType.kBrushless)
    private val intakePneu = Solenoid(PneumaticsModuleType.CTREPCM, 1)

    fun intakeOn(speed: Double)  {
        intakeMotor.set(speed)
    }

    fun stop() {
        intakeMotor.stopMotor()
    }

    fun pneuOn() {
       intakePneu.set(true)
    }

    fun pneuStop() {
        intakePneu.set(false)
    }

    fun pneuCheck() :Boolean {
        return intakePneu.get()
    }
}