package org.sert2521.bunnybots2022

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandScheduler
import org.sert2521.bunnybots2022.commands.auto.DriveDynamic
import org.sert2521.bunnybots2022.commands.drivetrain.JoystickDrive
import org.sert2521.bunnybots2022.commands.drivetrain.test.RunTests
import org.sert2521.bunnybots2022.commands.outtake.IndexOuttake
import org.sert2521.bunnybots2022.subsystems.Intake

object Robot : TimedRobot() {
    private val commandScheduler = CommandScheduler.getInstance()

    private val driveDynamic = DriveDynamic(Pose2d(0.0, -1.0, Rotation2d(0.0)))
    private val joystickDrive = JoystickDrive(true)
    private val indexOuttake = IndexOuttake()
    private val runTests = RunTests()

    private var currAuto: Command? = null

    override fun robotInit() {
        Intake.pneuOn()
    }

    override fun robotPeriodic() {
        Input.update()

        if (constants.tuning) {
            Tuning.update()
        }

        Output.update()

        commandScheduler.run()
    }

    override fun teleopInit() {
        joystickDrive.schedule()
        indexOuttake.schedule()
    }

    override fun autonomousInit() {
        currAuto = Input.getAuto()
        driveDynamic?.schedule()
    }

    override fun testInit() {
        runTests.schedule()
    }

    override fun testExit() {
        runTests.cancel()
    }

    override fun disabledInit() {
        commandScheduler.cancelAll()
        Input.setRumble(0.0)
    }
}