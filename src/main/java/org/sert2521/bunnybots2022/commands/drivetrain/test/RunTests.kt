package org.sert2521.bunnybots2022.commands.drivetrain.test

import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup
import org.sert2521.bunnybots2022.Input
import kotlin.math.PI

// Should not be run on ground
class RunTests : SequentialCommandGroup(
    EnterBrakePos().withInterrupt(Input::getNext),
    Drive(ChassisSpeeds(0.25, 0.0, 0.0)).withTimeout(1.0),
    Drive(ChassisSpeeds(0.0, 0.25, 0.0)).withTimeout(1.0),
    Drive(ChassisSpeeds(-0.25, 0.0, 0.0)).withTimeout(1.0),
    Drive(ChassisSpeeds(0.0, -0.25, 0.0)).withTimeout(1.0),
    Drive(ChassisSpeeds(0.25, 0.0, 0.0)).withInterrupt(Input::getNext),
    Drive(ChassisSpeeds(0.0, 0.0, PI)).withTimeout(1.0),
    Drive(ChassisSpeeds(0.0, 0.0, -PI)).withTimeout(1.0),
    Drive(ChassisSpeeds(0.0, 0.0, PI)).withInterrupt(Input::getNext),
    EnterBrakePos()
)
