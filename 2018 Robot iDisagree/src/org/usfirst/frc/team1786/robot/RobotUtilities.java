package org.usfirst.frc.team1786.robot;

import static org.usfirst.frc.team1786.robot.RobotConstants.*;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.can.*;
/*
 * A utility class for some common utilities we need
 *
 * I'm cheating since java doesn't support top-level static classes.
 * However, this class is final ( can't be extended )
 * 			its members are static
 * 			its constructor is private
 *
 * that makes it pretty similar to a top-level static class
 */

public final class RobotUtilities {

	private RobotUtilities() {
	// don't need to do anything in auto right now
	}

	//add a curve t
	public static double exponentialModify(double joyY) {
		double scale = 5; //scale the curve
		return Math.tan(joyY*Math.atan(scale))/scale;
	}
	
	// dylan's deadband code
	public static double deadbandScaled(double inputAxis, double deadband) {
		double value = inputAxis;

		if (value > 0 && value > deadband)
		{
			double scaledValue = (value - deadband) / ((1 - deadband) / (1));
			return scaledValue;
		}
		else if (value < 0 && value < -deadband)
		{
			double scaledValue = (value + deadband) / ((1 - deadband) / (1));
			return scaledValue;
		}
		else
		{
			double scaledValue = 0;
			return scaledValue;
		}
	}
	
	// Philip's utilities
	// Current limiting
	public static void currentLimiting(WPI_TalonSRX talon, int contCurrent, int peakCurrent, int peakDuration) {
		talon.configContinuousCurrentLimit(contCurrent, 0);
		talon.configPeakCurrentLimit(peakCurrent, 0);
		talon.configPeakCurrentDuration(peakDuration, 0);
		talon.enableCurrentLimit(true);
		
	}
	
	
	//Talon Display
	public static void displayTalon(WPI_TalonSRX talon, String label) {
		
		SmartDashboard.putNumber(label + " Current", talon.getOutputCurrent());
		SmartDashboard.putNumber(label + " Temperature", talon.getTemperature());
		
	}
	
	
	//Joystick Display
	private void displayJoystick(Joystick joystick, String label) {
		SmartDashboard.putNumber(label + "Y", joystick.getY());
		SmartDashboard.putNumber(label + "Z", joystick.getZ());
	}
	
	
}
