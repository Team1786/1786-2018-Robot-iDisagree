package org.usfirst.frc.team1786.robot;

import org.omg.CORBA.Current;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Autonomous {
	Robot robot;
	
	double driveDistanceReadingL = 0;
	double driveDistanceReadingR = 0;
	double driveEncoderDataL = 0;
	double driveEncoderDataR = 0;
	double driveRotationsR = 0;
	double driveRotationsL = 0;
	
	public Autonomous(Robot robot) {
		this.robot = robot;
	}
	
	public void trackDriveEncoder() {
		// 4096 is a rotation
		// wheels are 6" diameter
		// C = 2piR, C = 2pi3 = 6pi inches per rotation
		double latestDataL;
		double nowDataL;
		double latestDataR;
		double nowDataR;
		
		double diffL;
		double diffR;
		
		double rotationsL = 0;
		double rotationsR = 0;
		double distanceL = 0;
		double distanceR = 0;
		
		// useful constants
		double singleRotation = 4096;
		double wheelCircum = 2 * Math.PI * 3;
		
		// fetch old data and new updates
		latestDataL = driveEncoderDataL;
		nowDataL = robot.talonL1.getSelectedSensorPosition(0);
		// compare, this gives us what has changed since we last
		// called the function. Data is still in raw ticks
		diffL = latestDataL - nowDataL;
		// calculate rotations since last reading, not rounded down
		rotationsL = diffL / singleRotation;
		// calculate distance covered since last reading, not rounded down
		distanceL = rotationsL * wheelCircum;
		
		driveEncoderDataL = nowDataL;
		driveRotationsL += rotationsL;
		driveDistanceReadingL += distanceL;
		
		// fetch old data and new updates
		latestDataR = driveEncoderDataL;
		nowDataR = robot.talonL1.getSelectedSensorPosition(0);
		// compare, this gives us what has changed since we last
		// called the function. Data is still in raw ticks
		diffR = latestDataR - nowDataR;
		// calculate rotations since last reading, not rounded down
		rotationsR = diffR / singleRotation;
		// calculate distance covered since last reading, not rounded down
		distanceR = rotationsR * wheelCircum;
		
		driveEncoderDataR = nowDataR;
		driveRotationsR += rotationsR;
		driveDistanceReadingR += distanceR;
		
	}
	
	/**
	 *  get encoder data from any encoder equiped talon
	 * @param WPI_TalonSRX motorController
	 * @return returns raw data from generic encoder
	 */
	public double trackEncoder(WPI_TalonSRX motorController) {}
	
	/**
	 *  get encoder data from any encoder equiped talon
	 * @param TalonSRX motorController
	 * @return returns raw data from generic encoder
	 */
	public double trackEncoder(TalonSRX motorController) {}
	
	public double rotationCalculator(double rawTicks) {}
	
	public double distanceCalculator(double rotations) {}
	
	/**
	 * move a set distance
	 * @param distance - distance in inches
	 */
	public void move(double distance) {
		double startPoint = driveDistanceReadingL;
		double distanceMoved;
		double error;
		double scaledValue;
		double kP = 5;
		
		while(driveDistanceReadingL < startPoint + distance) {
			trackDriveEncoder();
			
			// want: +90 in
			// start point: 60in
			// relative movement: 60+ 90 = 120 in
			
			// forward command is positive error
			// backward command is negative error
			error = distance - (driveDistanceReadingL - startPoint);
			
			scaledValue = (error * kP);
			
			robot.talonL1.set(scaledValue);
		}
	}
	
	
}


