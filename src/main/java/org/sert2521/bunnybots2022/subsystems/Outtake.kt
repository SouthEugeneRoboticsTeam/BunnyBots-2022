package org.sert2521.bunnybots2022.subsystems

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.sert2521.bunnybots2022.constants

// This behaviour (using periodic to control motors) is not consistent with other subsystems
// I am testing different styles for bunnybots
object Outtake : SubsystemBase() {
    private val flapMotor = TalonSRX(constants.outtakeFlapMotorID)
    private val indexingMotor = CANSparkMax(constants.outtakeIndexingMotorID, CANSparkMaxLowLevel.MotorType.kBrushless)

    private val atOpen = DigitalInput(constants.outtakeAtTopPing)
    private val atClosed = DigitalInput(constants.outtakeAtBottomPin)

    var outtaking = false

    override fun periodic() {
        if (outtaking) {
            if (!atOpen.get()) {
                flapMotor.set(ControlMode.PercentOutput, constants.outtakeFlapSpeed)
                indexingMotor.set(0.0)
            } else {
                flapMotor.set(ControlMode.PercentOutput, 0.0)
                indexingMotor.set(constants.outtakeIndexFastSpeed)
            }
        } else {
            if (!atClosed.get()) {
                flapMotor.set(ControlMode.PercentOutput, -constants.outtakeFlapSpeed)
                indexingMotor.set(0.0)
            } else {
                flapMotor.set(ControlMode.PercentOutput, 0.0)
                indexingMotor.set(constants.outtakeIndexDefaultSpeed)
            }
        }
    }
}