package org.sert2521.bunnybots2022.commands.auto

import edu.wpi.first.math.controller.HolonomicDriveController
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.ProfiledPIDController
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.trajectory.Trajectory
import edu.wpi.first.math.trajectory.TrajectoryGenerator
import edu.wpi.first.math.trajectory.TrapezoidProfile
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.CommandBase
import org.sert2521.bunnybots2022.Reloadable
import org.sert2521.bunnybots2022.constants
import org.sert2521.bunnybots2022.subsystems.Drivetrain
import kotlin.math.atan2

// Maybe should recalculate if way off
// Maybe should include current velocity in path generation
class DriveDynamic(private val target: Pose2d) : CommandBase(), Reloadable {
    private var trajectory: Trajectory? = null
    private var angle: Rotation2d? = null

    private lateinit var driveController: HolonomicDriveController
    private var startTime: Double = 0.0

    init {
        addRequirements(Drivetrain)

        setController()

        registerReload()
    }

    private fun setController() {
        // Maybe make into function for both autos
        driveController = HolonomicDriveController(
            PIDController(constants.autoForwardP, constants.autoForwardI, constants.autoForwardD),
            PIDController(constants.autoForwardP, constants.autoForwardI, constants.autoForwardD),
            ProfiledPIDController(constants.autoAngleP, constants.autoAngleI, constants.autoAngleD,
                TrapezoidProfile.Constraints(constants.autoMaxVel, constants.autoMaxAcc))
        )
    }

    private fun genTrajectory() {
        // Pose angles should generate smooth paths
        val diff = target.translation - Drivetrain.pose.translation
        val startPose = Pose2d(Drivetrain.pose.translation, Rotation2d(atan2(diff.y, diff.x)))
        trajectory = TrajectoryGenerator.generateTrajectory(startPose, mutableListOf(), target, constants.trajectoryConfig)
        angle = target.rotation
    }

    override fun initialize() {
        if (!Drivetrain.poseInited) {
            throw Exception("Drivetrain must be have an inited pose")
        }

        genTrajectory()

        startTime = Timer.getFPGATimestamp()
    }

    override fun execute() {
        if (angle != null) {
            val sample = trajectory?.sample((Timer.getFPGATimestamp() - startTime))
            if (sample != null) {
                Drivetrain.drive(driveController.calculate(Drivetrain.pose, sample, angle))
            }
        }
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
