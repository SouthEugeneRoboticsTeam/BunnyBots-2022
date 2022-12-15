package org.sert2521.bunnybots2022.commands.auto

import edu.wpi.first.math.controller.HolonomicDriveController
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.ProfiledPIDController
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.trajectory.Trajectory
import edu.wpi.first.math.trajectory.TrapezoidProfile
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.CommandBase
import org.sert2521.bunnybots2022.Reloadable
import org.sert2521.bunnybots2022.constants
import org.sert2521.bunnybots2022.subsystems.Drivetrain

class DrivePath(private val trajectory: Trajectory, private val angle: Rotation2d) : CommandBase(), Reloadable {
    private lateinit var driveController: HolonomicDriveController
    private var startTime = 0.0

    init {
        addRequirements(Drivetrain)

        // Null pointer exception?
        //setController()

        registerReload()
    }

    private fun setController() {
        // Maybe make into function for both autos
        driveController = HolonomicDriveController(
            PIDController(constants.autoForwardP, constants.autoForwardI, constants.autoForwardD),
            PIDController(constants.autoForwardP, constants.autoForwardI, constants.autoForwardD),
            ProfiledPIDController(constants.autoAngleP, constants.autoAngleI, constants.autoAngleD,
                TrapezoidProfile.Constraints(constants.autoMaxVel, constants.autoMaxAcc)))
    }

    override fun initialize() {
        startTime = Timer.getFPGATimestamp()
    }

    override fun execute() {
        Drivetrain.drive(driveController.calculate(Drivetrain.pose, trajectory.sample((Timer.getFPGATimestamp() - startTime) / 1000), angle))
    }

    override fun isFinished(): Boolean {
        return driveController.atReference()
    }

    override fun end(interrupted: Boolean) {
        Drivetrain.stop()
    }

    override fun reload() {
        setController()
    }
}
