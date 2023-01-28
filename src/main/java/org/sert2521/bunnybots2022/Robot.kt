package org.sert2521.bunnybots2022

import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandScheduler
import edu.wpi.first.wpilibj2.command.InstantCommand
import edu.wpi.first.wpilibj2.command.WaitCommand
import org.sert2521.bunnybots2022.commands.auto.DriveDynamic
import org.sert2521.bunnybots2022.commands.auto.DrivePath
import org.sert2521.bunnybots2022.commands.auto.FillRow
import org.sert2521.bunnybots2022.commands.drivetrain.JoystickDrive
import org.sert2521.bunnybots2022.commands.drivetrain.test.RunTests
import org.sert2521.bunnybots2022.commands.indexer.RunIndexer
import org.sert2521.bunnybots2022.commands.lift.LiftSetHeight
import org.sert2521.bunnybots2022.commands.outtake.HoldOpenOuttake
import org.sert2521.bunnybots2022.commands.outtake.IndexOuttake
import org.sert2521.bunnybots2022.subsystems.Drivetrain
import org.sert2521.bunnybots2022.subsystems.Indexer
import org.sert2521.bunnybots2022.subsystems.Intake
import org.sert2521.bunnybots2022.subsystems.Lift
import kotlin.math.PI

object Robot : TimedRobot() {
    private val commandScheduler = CommandScheduler.getInstance()

    //private val driveDynamic = DriveDynamic(Pose2d(0.0, -1.0, Rotation2d(0.0)))
    /*private val fillRow = DrivePath(Pose2d(0.0, 0.0, Rotation2d(PI / 2)),
        Pose2d(0.0, (284 / 39.94) - (constants.halfSideLength * 3.6),
            Rotation2d(PI / 2)),
        constants.trajectoryConfigFast, Rotation2d(0.0)).andThen(FillRow())*/
    private val joystickDrive = JoystickDrive(true)
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
    }

    override fun teleopExit() {
        //Drivetrain.setMode(false)
    }

    override fun autonomousInit() {
        //currAuto = Input.getAuto()
        //Drivetrain.setNewPose(Pose2d())
        //Input.getAuto()?.schedule()
        //fillRow.schedule()
    }

    override fun testInit() {
        //runTests.schedule()
    }

    override fun testExit() {
        //runTests.cancel()
    }

    override fun disabledInit() {
        commandScheduler.cancelAll()
        Input.setRumble(0.0)
        //WaitCommand(1.5).andThen(InstantCommand({ Drivetrain.setMode(true) })).schedule()
    }

    override fun disabledExit() {
        Drivetrain.setMode(false)
    }
}