package org.sert2521.bunnybots2022

import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandScheduler
import org.sert2521.bunnybots2022.commands.drivetrain.JoystickDrive
import org.sert2521.bunnybots2022.commands.drivetrain.test.RunTests

object Robot : TimedRobot() {
    private val commandScheduler = CommandScheduler.getInstance()

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
        currAuto?.schedule()
    }

    override fun autonomousExit() {
        currAuto?.cancel()
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