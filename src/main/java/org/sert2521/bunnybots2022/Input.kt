package org.sert2521.bunnybots2022

import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj.Joystick
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.button.JoystickButton
import org.sert2521.bunnybots2022.commands.intake.IntakeCommand
import org.sert2521.bunnybots2022.commands.outtake.HoldOpenOuttake
import org.sert2521.bunnybots2022.commands.outtake.IndexOuttake
import org.sert2521.bunnybots2022.commands.outtake.OuttakeTubes

object Input {
    private val xboxController = XboxController(0)
    private val gunnerController = Joystick(1)

    private var prevNext = false
    private var currNext = false

    private val autoChooser = SendableChooser<Command?>()

    private val buttonIntake = JoystickButton(gunnerController, 4)

    private val liftBottom = JoystickButton(gunnerController, 3)
    private val liftMiddle = JoystickButton(gunnerController, 2)
    private val liftTop = JoystickButton(gunnerController, 1)

    private val outtake = JoystickButton(gunnerController, 0)

    private var liftUp = false

    init {
        autoChooser.setDefaultOption("Nothing", null)
        SmartDashboard.putData("Input/Auto", autoChooser)

        buttonIntake.whenHeld(IntakeCommand())

        liftBottom.whenPressed {
            //LiftSetHeight(constants.liftTopHeight, false).schedule()
            HoldOpenOuttake().schedule()
            liftUp = true
            setOf()
        }

        liftMiddle.whenPressed {
            //LiftSetHeight(constants.liftMiddleHeight, false).schedule()
            HoldOpenOuttake().schedule()
            liftUp = true
            setOf()
        }

        liftTop.whenPressed {
            //LiftSetHeight(constants.liftBottomHeight, false).schedule()
            IndexOuttake().schedule()
            liftUp = false
            setOf()
        }

        outtake.whenHeld {
            if (liftUp) {
                OuttakeTubes(null).schedule()
            }

            setOf()
        }
    }

    fun update() {
        // Remove
        prevNext = currNext
        currNext = xboxController.aButton
    }

    fun getY(): Double {
        return xboxController.leftY
    }

    fun getX(): Double {
        return -xboxController.leftX
    }

    fun getRot(): Double {
        return xboxController.rightX
    }

    fun getNext(): Boolean {
        return !prevNext && currNext
    }

    fun setRumble(value: Double) {
        xboxController.setRumble(GenericHID.RumbleType.kRightRumble, value)
        xboxController.setRumble(GenericHID.RumbleType.kLeftRumble, value)
    }

    fun getAuto(): Command? {
        return autoChooser.selected
    }
}