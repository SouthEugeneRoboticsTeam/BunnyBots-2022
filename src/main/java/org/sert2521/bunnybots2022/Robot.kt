package org.sert2521.bunnybots2022

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.wpilibj.Compressor
import edu.wpi.first.wpilibj.PneumaticsModuleType
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandScheduler
import org.sert2521.bunnybots2022.commands.auto.DriveDynamic
import org.sert2521.bunnybots2022.commands.drivetrain.JoystickDrive
import org.sert2521.bunnybots2022.commands.drivetrain.test.RunTests

object Robot : TimedRobot() {
    private val commandScheduler = CommandScheduler.getInstance()

    private val driveDynamic = DriveDynamic(Pose2d(0.0, -1.0, Rotation2d(0.0)))
    private val joystickDrive = JoystickDrive(true)
    private val runTests = RunTests()

    private var currAuto: Command? = null

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
    }

    override fun teleopExit() {
        joystickDrive.cancel()
    }

    override fun autonomousInit() {
        currAuto = Input.getAuto()
        driveDynamic?.schedule()
    }

    override fun autonomousExit() {
        driveDynamic?.cancel()
    }

    override fun testInit() {
        runTests.schedule()
    }

    override fun testExit() {
        runTests.cancel()
    }

    override fun disabledInit() {
        Input.setRumble(0.0)
    }
}