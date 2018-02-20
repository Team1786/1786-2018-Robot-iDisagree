package org.usfirst.frc.team1786.robot;

import org.usfirst.frc.team1786.robot.RobotUtilities;

import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj.Joystick;

import java.lang.Math;

/*
 * A class for controlling team 1786's power-cube holding arms.
 */
public class Arm {

	double deadband;
	double wheelSpeed;
	WPI_TalonSRX leftController;
	WPI_TalonSRX rightController;

	/**
	 * Constructor for a robotic arm with driven wheels
	 * @param leftArmController - Motor controller for use in driving the left arm
	 * @param rightArmController - Motor controller for use in driving the right arm
	 * @param armDeadband - desired deadband radius for arm control
	 */
	public Arm(WPI_TalonSRX leftArmController, WPI_TalonSRX rightArmController, double armDeadband) {
		leftController = leftArmController;
		rightController = rightArmController;
		wheelSpeed = 1;
		deadband = armDeadband;
	}

	/**
	 * Constructor for a robotic arm with driven wheels
	 * @param leftArmController - Motor controller for use in driving the left arm
	 * @param rightArmController - Motor controller for use in driving the right arm
	 * @param armDeadband - desired deadband radius for arm control (don't scale it please)
	 * @param armWheelSpeed - desired regular speed preset for the arm control
	 */
	public Arm(WPI_TalonSRX leftArmController, WPI_TalonSRX rightArmController, double armDeadband, double armWheelSpeed) {
		leftController = leftArmController;
		rightController = rightArmController;
		wheelSpeed = armWheelSpeed;
		deadband = armDeadband;
	}

	/**
	 * change the previously set deadband
	 * @param armDeadband - the new deadband value
	 */
	public void setDeadband( double armDeadband) {
		deadband = armDeadband;
	}

	/**
	 * change the previously set deadband
	 * @param armWheelSpeed - the new regular wheel speed value
	 */
	public void setWheelSpeed( double armWheelSpeed) {
		wheelSpeed = armWheelSpeed;
	}

	/**
	 * to be run in a looping function. Drives the arm based on input
	 * @param inputJoy - wpilib joystick object to get z axis from
	 */
	public void driveArm(double axis) {
		// driveArm logic by Dylan
		double value = RobotUtilities.deadbandScaled(axis, deadband);

		rightController.set(value);
	}
	
	// run the constant wheel speed in the direction of the joystick
	// if it is moved past deadband radius
	public void driveArm(double axis, boolean constSpeed) {
		double value = RobotUtilities.deadbandScaled(axis, deadband);
		
		if (Math.abs(value) > 0) {

			rightController.set(wheelSpeed * Math.signum(value));
			leftController.set(wheelSpeed * Math.signum(value));
		}
	}
}
