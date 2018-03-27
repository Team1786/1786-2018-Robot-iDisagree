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

/** 
 * 
 * a utility class for common robot utilities like deadband scaling and vector modification
 *
 */
public final class RobotUtilities {

	private RobotUtilities() {
	// don't need to do anything in auto right now
	}

	/**
	 * scaled input by a power of 5, works with numbers between [-1,1]
	 * @param joyY - wpilib joystick object
	 * @return returns scaled value
	 */
	public static double exponentialModify(double joyY) {
		double scale = 5; //scale the curve
		return Math.tan(joyY*Math.atan(scale))/scale;
	}
	
	/**
	 * @author dylan hackel
	 * @param inputAxis - axis which needs the deadband
	 * @param deadband - the radius
	 * @return scaled value after deadband application
	 */
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
	
	/**
	 * set the current limiting functions of a wpi_talonsrx motorcontroller object
	 * @param talon - wpi_talonSRX motorcontroller object
	 * @param contCurrent - defines the max amp that can be given to a motor after its peak
	 * @param peakCurrent - defines the max amp that can be given to a motor during its peak
	 * @param peakDuration - defines how long the peak will last in milliseconds
	 */
	public static void currentLimiting(WPI_TalonSRX talon, int contCurrent, int peakCurrent, int peakDuration) {
		talon.configContinuousCurrentLimit(contCurrent, 0);
		talon.configPeakCurrentLimit(peakCurrent, 0);
		talon.configPeakCurrentDuration(peakDuration, 0);
		talon.enableCurrentLimit(true);
		
	}
	
	
	/**
	 * display info about given talon on smart dashboard
	 * @param talon - wpi_talonsrx motor controller to get info from
	 * @param label - desired label on smart dashboard
	 */
	public static void displayTalon(WPI_TalonSRX talon, String label) {
		
		SmartDashboard.putNumber(label + " Current", talon.getOutputCurrent());
		SmartDashboard.putNumber(label + " Temperature", talon.getTemperature());
		
	}
	
	
	/** 
	 * display info about given joystick on smart dashboard
	 * @param joystick - wpilib joystick object
	 * @param label - desired label to be put on smart dasboard
	 */
	private void displayJoystick(Joystick joystick, String label) {
		SmartDashboard.putNumber(label + "Y", joystick.getY());
		SmartDashboard.putNumber(label + "Z", joystick.getZ());
	}
	
	
}
