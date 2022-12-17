package org.sert2521.bunnybots2022

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj.Joystick
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.InstantCommand
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup
import edu.wpi.first.wpilibj2.command.button.JoystickButton
import org.sert2521.bunnybots2022.commands.OuttakeFront
import org.sert2521.bunnybots2022.commands.intake.IntakeCommand
import org.sert2521.bunnybots2022.commands.lift.LiftSetHeight
import org.sert2521.bunnybots2022.commands.outtake.OuttakeTubes
import org.sert2521.bunnybots2022.commands.outtake.UnOuttake
import org.sert2521.bunnybots2022.subsystems.Drivetrain
import org.sert2521.bunnybots2022.subsystems.Lift

object Input {
    private val driverController = XboxController(0)
    private val gunnerController = Joystick(1)

    private var prevNext = false
    private var currNext = false

    private val autoChooser = SendableChooser<Command?>()

    private val buttonReset = JoystickButton(driverController, 2)

    private val buttonIntake = JoystickButton(gunnerController, 14)
    private val outtakeFront = JoystickButton(gunnerController, 15)
    private val outtakeFrontDesperate = JoystickButton(gunnerController, 16)

    private val liftBottom = JoystickButton(gunnerController, 8)
    private val recalibLift = JoystickButton(gunnerController, 10)
    private val liftTop = JoystickButton(gunnerController, 9)

    private val outtake = JoystickButton(gunnerController, 13)

    init {
        autoChooser.setDefaultOption("Nothing", null)
        SmartDashboard.putData("Input/Auto", autoChooser)

        // Figure how this relates to vision
        buttonReset.whenHeld(InstantCommand({ Drivetrain.setNewPose(Pose2d()) }))

        buttonIntake.whenHeld(IntakeCommand())

        liftBottom.whenPressed(LiftSetHeight(constants.liftBottomHeight, false))

        recalibLift.whenPressed(InstantCommand({ Lift.unset = true }))

        liftTop.whenPressed(LiftSetHeight(constants.liftTopHeight, false))

        outtake.whenHeld(OuttakeTubes(null))

        outtakeFront.whenHeld(OuttakeFront())

        outtakeFrontDesperate.whenHeld(ParallelCommandGroup(OuttakeFront(), UnOuttake()))
    }

    fun update() {
        // Remove
        prevNext = currNext
        currNext = driverController.aButton
    }

    fun getY(): Double {
        return driverController.leftY
    }

    fun getX(): Double {
        return -driverController.leftX
    }

    fun getRot(): Double {
        return driverController.rightX
    }

    fun getNext(): Boolean {
        return !prevNext && currNext
    }

    fun setRumble(value: Double) {
        driverController.setRumble(GenericHID.RumbleType.kRightRumble, value)
        driverController.setRumble(GenericHID.RumbleType.kLeftRumble, value)
    }

    fun getAuto(): Command? {
        return autoChooser.selected
    }
}