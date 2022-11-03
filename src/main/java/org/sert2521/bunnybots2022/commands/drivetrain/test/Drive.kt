package org.sert2521.bunnybots2022.commands.drivetrain.test

import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.wpilibj2.command.CommandBase
import org.sert2521.bunnybots2022.subsystems.Drivetrain

class Drive(private val chassisSpeeds: ChassisSpeeds) : CommandBase() {
    init {
        addRequirements(Drivetrain)
    }

    override fun execute() {
        Drivetrain.drive(chassisSpeeds)
    }

    override fun end(interrupted: Boolean) {
        Drivetrain.stop()
    }
}
