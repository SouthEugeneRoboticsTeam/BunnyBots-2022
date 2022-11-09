package org.sert2521.bunnybots2022

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.button.JoystickButton
import org.sert2521.bunnybots2022.commands.IntakeCommand
import org.sert2521.bunnybots2022.subsystems.Drivetrain

object Input {
    private val controller = XboxController(0)
    private var prevNext = false
    private var currNext = false

    private val autoChooser = SendableChooser<Command?>()

    private val buttonIntake = JoystickButton(controller, -1)

    init {
        autoChooser.setDefaultOption("Nothing", null)
        SmartDashboard.putData("Input/Auto", autoChooser)
        buttonIntake.whileHeld(IntakeCommand())
    }

    fun update() {
        prevNext = currNext
        currNext = controller.aButton

        if (controller.bButton) {
            Drivetrain.pose = Pose2d(Drivetrain.pose.translation, Rotation2d(0.0))
        }
    }

    fun getY(): Double {
        return controller.leftY
    }

    fun getX(): Double {
        return -controller.leftX
    }

    fun getRot(): Double {
        return controller.rightX
    }

    fun getNext(): Boolean {
        return !prevNext && currNext
    }

    fun setRumble(value: Double) {
        controller.setRumble(GenericHID.RumbleType.kRightRumble, value)
        controller.setRumble(GenericHID.RumbleType.kLeftRumble, value)
    }

    fun getAuto(): Command? {
        return autoChooser.selected
    }
}