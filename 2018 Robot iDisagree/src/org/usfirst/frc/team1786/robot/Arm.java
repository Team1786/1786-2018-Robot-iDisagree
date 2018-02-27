package org.usfirst.frc.team1786.robot;

import org.usfirst.frc.team1786.robot.RobotUtilities;

import com.ctre.phoenix.motorcontrol.can.*;

import java.lang.Math;

/*
 * A class for controlling team 1786's power-cube holding arms.
 */
public class Arm {

	double deadband;
	// default wheel speed
	double wheelSpeed = 1;
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
	 * @param inputJoy - wpilib joystick axis to get movement from
	 * @param constSpeed - bool for whether to run at const speed or not
	 */
	public void driveArm(double axis, boolean constSpeed) {
		double value = RobotUtilities.deadbandScaled(axis, deadband);
		
		if (constSpeed) {
			// signum returns -1,0,1 based on sign of parameter
			if (Math.abs(value) > 0) {
				rightController.set(wheelSpeed * Math.signum(value));
				leftController.set(wheelSpeed * Math.signum(value));
			}
		// if constSpeed isn't true, just run at the throttle speed directly
		} else {
			rightController.set(value);
			leftController.set(value);
		}

	}
}
