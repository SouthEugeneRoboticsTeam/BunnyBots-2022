package org.sert2521.bunnybots2022

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj.Joystick
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.button.JoystickButton
import org.sert2521.bunnybots2022.commands.lift.LiftSetHeight
import org.sert2521.bunnybots2022.commands.intake.IntakeCommand
import org.sert2521.bunnybots2022.commands.indexer.RunIndexer
import org.sert2521.bunnybots2022.subsystems.Drivetrain

object Input {
    private val xboxController = XboxController(0)
    private val gunnerController = Joystick(1)

    private var prevNext = false
    private var currNext = false

    private val autoChooser = SendableChooser<Command?>()

    private val buttonIntake = JoystickButton(xboxController, 4)
    private val buttonIndex = JoystickButton(xboxController,5)

    private val liftBottom = JoystickButton(gunnerController, 1)
    private val liftMiddle = JoystickButton(gunnerController, 2)
    private val liftTop = JoystickButton(gunnerController, 3)

    init {
        autoChooser.setDefaultOption("Nothing", null)
        SmartDashboard.putData("Input/Auto", autoChooser)

        buttonIntake.whileHeld(IntakeCommand())
        buttonIndex.whenHeld(RunIndexer())

        liftBottom.whenPressed(LiftSetHeight(constants.liftBottomHeight))
        liftMiddle.whenPressed(LiftSetHeight(constants.liftMiddleHeight))
        liftTop.whenPressed(LiftSetHeight(constants.liftTopHeight))
    }

    fun update() {
        prevNext = currNext
        currNext = xboxController.aButton

        if (xboxController.bButton) {
            // Remove
            Drivetrain.pose = Pose2d(Drivetrain.pose.translation, Rotation2d(0.0))
        }
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