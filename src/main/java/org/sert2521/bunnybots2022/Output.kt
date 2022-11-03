package org.sert2521.bunnybots2022

import edu.wpi.first.wpilibj.smartdashboard.Field2d
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.sert2521.bunnybots2022.subsystems.Drivetrain

object Output {
    private val field = Field2d()
    private val values = mutableListOf<Pair<String, () -> Double>>()

    init {
        values.add(Pair("Acceleration") { Drivetrain.getAccelSqr() })

        SmartDashboard.putData(field)

        update()
    }

    fun update() {
        field.robotPose = Drivetrain.pose

        for (value in values) {
            SmartDashboard.putNumber("Output/${value.first}", value.second())
        }
    }
}