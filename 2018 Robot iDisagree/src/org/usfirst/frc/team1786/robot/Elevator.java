package org.usfirst.frc.team1786.robot;

import java.lang.Math;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Elevator {
	double deadband;
	
	// default speed
	double speed = 1;
	
	WPI_TalonSRX motorController;
	
	// constructor with custom lift speed
	public Elevator(WPI_TalonSRX liftController, double liftDeadband, double liftSpeed) {
		motorController = liftController;
		deadband = liftDeadband;
		speed = liftSpeed;
	}
	
	// constructor without liftspeed (use default)
	public Elevator(WPI_TalonSRX liftController, double liftDeadband) {
		motorController = liftController;
		deadband = liftDeadband;
	}
	
	public void setDeadband(double liftDeadband) {
		deadband = liftDeadband;
	}
	
	public void setLiftSpeed(double liftSpeed) {
		speed = liftSpeed;
	}
	
	/**
	 * to be run in a looping function. Drives the arm based on input
	 * @param inputJoy - wpilib joystick axis to get movement from
	 * @param constSpeed - bool for whether to run at const speed or not
	 */
	public void driveElevator(double axis, boolean constSpeed) {
		double value = RobotUtilities.deadbandScaled(axis, deadband);
		
		if (constSpeed) {
			if (Math.abs(value) > 0) {
				// signum returns -1,0,1 based on sign of parameter
				motorController.set(speed * Math.signum(value));
			}
		// if constSpeed isn't true, just run at the throttle speed directly
		} else {
			motorController.set(value);
		}

	}
}
