package org.sert2521.bunnybots2022.subsystems

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.sert2521.bunnybots2022.constants
import kotlin.math.atan2

object Vision : SubsystemBase() {
    private val visionTable = NetworkTableInstance.getDefault().getTable("Vision")
    private val isTargetEntry = visionTable.getEntry("Is Target")
    private val targetPosEntry = visionTable.getEntry("Position")
    private val targetAngleEntry = visionTable.getEntry("Rotation")

    var targetPose: Pose2d? = null
        private set

    // Runs before command periodic
    override fun periodic() {
        targetPose = if (isTargetEntry.getBoolean(false)) {
            val rawPos = targetPosEntry.getDoubleArray(DoubleArray(0))
            val rawRot = targetAngleEntry.getDoubleArray(DoubleArray(0))
            // Drivetrain.pose.translation may be from the previous periodic
            val seenTrans = (Translation2d(rawPos[0], rawPos[2]).rotateBy(constants.visionOffset.rotation) + constants.visionOffset.translation)
            val seenRot = Rotation2d(atan2(rawRot[1], rawRot[0])) + constants.visionOffset.rotation
            Pose2d(seenTrans.rotateBy(Drivetrain.pose.rotation) + Drivetrain.pose.translation, seenRot)
        } else {
            null
        }
    }
}