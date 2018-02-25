/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/


/*

 _____  __ __  ____      __  ______  ____   ___   ____    ____  _
|     ||  |  ||    \    /  ]|      ||    | /   \ |    \  /    || |
|   __||  |  ||  _  |  /  / |      | |  | |     ||  _  ||  o  || |
|  |_  |  |  ||  |  | /  /  |_|  |_| |  | |  O  ||  |  ||     || |___
|   _] |  :  ||  |  |/   \_   |  |   |  | |     ||  |  ||  _  ||     |
|  |   |     ||  |  |\     |  |  |   |  | |     ||  |  ||  |  ||     |
|__|    \__,_||__|__| \____|  |__|  |____| \___/ |__|__||__|__||_____|

    __   __    __       __
   /  ] /   \ |   \    /  _]
  /  / |     ||    \  /  [_
 /  /  |  O  ||  D  ||    _]
/   \_ |     ||     ||   [_
\     ||     ||     ||     |
 \____| \___/ |_____||_____|

Code Written by the programming class

*/

package org.usfirst.frc.team1786.robot;

import org.usfirst.frc.team1786.robot.RobotUtilities;
import org.usfirst.frc.team1786.robot.ButtonDebouncer;
import org.usfirst.frc.team1786.robot.Arm;
import org.usfirst.frc.team1786.robot.Elevator;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.drive.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Compressor;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.*;

import com.kauailabs.navx.frc.AHRS;


public class Robot extends IterativeRobot {

	/* button mapping */
	final int SHIFTER = 3;
	
	// Code from Dylan
	WPI_TalonSRX talonL1 = new WPI_TalonSRX(1);
	WPI_TalonSRX talonL2 = new WPI_TalonSRX(2);
	WPI_TalonSRX talonL3 = new WPI_TalonSRX(3);
	WPI_TalonSRX talonR4 = new WPI_TalonSRX(4);
	WPI_TalonSRX talonR5 = new WPI_TalonSRX(5);
	WPI_TalonSRX talonR6 = new WPI_TalonSRX(6);
	WPI_TalonSRX rightArmTalon = new WPI_TalonSRX(7);
	WPI_TalonSRX leftArmTalon = new WPI_TalonSRX(8);
	WPI_TalonSRX elevatorTalon1 = new WPI_TalonSRX(9);

	// joysticks
	Joystick joystickLeft = new Joystick(0);
	Joystick joystickRight = new Joystick(1);
		
	// robot modules
	DifferentialDrive drivetrain = new DifferentialDrive(talonL1, talonR4);
	Arm arm = new Arm(leftArmTalon, rightArmTalon, 0.2);
	Elevator elevator = new Elevator(elevatorTalon1, 0.2);
	
	// buttons which will need debouncing for toggled use
	ButtonDebouncer shiftBtn = new ButtonDebouncer(joystickLeft, SHIFTER, 0.5);
	
	// pneumatics
	Compressor compressor = new Compressor();
	Solenoid shifter = new Solenoid(0);
	
	// variable values for current limitings
	private int maxPeakAmp = 60; //defines the max amp that can be given to a moter during its peak
	private int maxCountAmp = 40; //defines the max amp that can be given to a moter after its peak
	private int peakTimeDuration = 10000; //defines how long the peak will last in milliseconds	
	
	boolean shifted;
	boolean isTurning;
	boolean isSteering;
	
	@Override
	public void robotInit() {
		talonL2.follow(talonL1); //tells the following talons to follow their leading talons
		talonL3.follow(talonL1);
		talonR5.follow(talonR4);
		talonR6.follow(talonR4);

		// talons running on the top gearbox position need inverting according to gear arrangement
		talonL1.setInverted(true);
		talonR4.setInverted(true);

		talonL1.configPeakCurrentDuration(peakTimeDuration, 0); //sets the duration of the peak
		talonL1.configPeakCurrentLimit(maxPeakAmp, 0); //sets the max current of the peak
		talonL1.configContinuousCurrentLimit(maxCountAmp, 0); //sets the max current for the time after the peak
		
		talonR4.configPeakCurrentDuration(peakTimeDuration, 0); //same as the other one
		talonR4.configPeakCurrentLimit(maxPeakAmp, 0);
		talonR4.configContinuousCurrentLimit(maxCountAmp, 0);
		
		//configure encoders
		// Will's implementation
		talonL1.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
		talonR4.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
	}

	// display a single talon on the dashboard
	// will's code
	private void displayTalon(WPI_TalonSRX talon, String label)
	{
		SmartDashboard.putNumber(label+" Output Current: ", talon.getOutputCurrent());
		SmartDashboard.putNumber(label+" Output Temperature: ", talon.getTemperature());
		
	}
	// update the smart dashboard
	public void DashboardUpdate() {
		// raw joystick data
		Double yLeft = joystickLeft.getY();
		Double xLeft = joystickLeft.getX();
		Double zLeft = joystickLeft.getZ();
		Double throttleLeft = joystickLeft.getThrottle();
		Double yRight = joystickRight.getY();
		Double zRight = joystickRight.getZ();
		Double throttleRight = joystickRight.getThrottle();
		
		SmartDashboard.putNumber("yLeft", yLeft);
		SmartDashboard.putNumber("xLeft", xLeft);
		SmartDashboard.putNumber("zLeft", zLeft);
		SmartDashboard.putNumber("throttleLeft", throttleLeft);
		SmartDashboard.putNumber("yRight", yRight);
		SmartDashboard.putNumber("zRight", zRight);
		SmartDashboard.putNumber("throttleRight", throttleRight);
		
		// current data on drive talons
		displayTalon(talonL1, "Talon L1");
		displayTalon(talonL2, "Talon L2");
		displayTalon(talonL3, "Talon L3");
		displayTalon(talonR4, "Talon R4");
		displayTalon(talonR5, "Talon R5");
		displayTalon(talonR6, "Talon R6");
		
		// current data on other talons
		displayTalon(rightArmTalon, "rightArmTalon");
		displayTalon(leftArmTalon, "leftArmTalon");
		displayTalon(elevatorTalon1, "elevatorTalon");

	}
	
	// Will's drive code, it uses two axes to determine the amount and type of rotation
	public void WrobleDrive(double speed, double rotation, double inPlaceRotation) {
		double y = speed;
		double x = rotation;
		double z = inPlaceRotation;
		
		// accurate in place twisting
		if(z < -0.4 || z > 0.4) {
			// is twisting
			isTurning = true;
			talonL1.set(z * speed);
			talonR4.set(z * speed);
		} else {
			//scaled "arcade" style movement
			isTurning = false;
			
			// Implement the distance formula for Joystick power calculation
			double power = Math.sqrt((x * x) + (y * y));
			
			// deadzone for all non in place twisting movement, was = 0.2
			if(power > 0.05) {
				isSteering = true;
				
				// prevent power from getting greater than 1
				if(power > 1) {
					power = 1;
				}
				
				// philip's modifier function applied to power
				power = RobotUtilities.exponentialModify(power, 3);
				
				// deadzone is implemented to prevent accidental backwards movement
				if(y < -0.25)
					power =- power;

				// scale the power according to the desired speed
				power *= speed;
				
				SmartDashboard.putNumber("drivetrain power", power);
				
				// the value of scale must be equal to or less than x
				double scale = 1-Math.abs(x);
				
				// philip's function used for turning scale
				scale = RobotUtilities.exponentialModify(scale, 5);
				
				// apply power and scale it accordingly
				if(x < 0) {
					
					//left
					talonL1.set(-power*scale);
					talonR4.set(power);
				} else {
					
					//right
					talonL1.set(-power);
					talonR4.set(power*scale);
				}	
			} else {
				// stay still if we don't need to move
				isSteering = false;
				talonL1.set(0);
				talonR4.set(0);
			}
		}
	}
	
	@Override
	public void autonomousInit() {
	}

	@Override
	public void autonomousPeriodic() {
	}

	@Override
	public void teleopInit() {
		//set the solenoid in a default position
		shifted = false;
		//make sure latest is zero
		shiftBtn.setLatest();
	}
	
	@Override
	public void teleopPeriodic() {

		// get input and scale some of it
		// code from Dylan
		Double yValueLeft = joystickLeft.getY();
		Double xValueLeft = joystickLeft.getX();
		Double zValueLeft = joystickLeft.getZ();
		Double throttleValueLeft = joystickLeft.getThrottle();
		
		Double yValueRight = joystickRight.getY();
		Double zValueRight = joystickRight.getZ();
		Double throttleValueRight = joystickRight.getThrottle();
		
		double yValueLeftScaled = RobotUtilities.deadbandScaled(yValueLeft, 0.15);
		double zValueLeftScaled = RobotUtilities.deadbandScaled(yValueLeft, 0.15);
		
		// run the modules
//		drivetrain.arcadeDrive(yValueLeftScaled, zValueLeftScaled);
		WrobleDrive(yValueLeft, xValueLeft, zValueLeft);
		
		arm.driveArm(yValueRight, true);
		
		elevator.driveElevator(throttleValueRight, true);
		
		// shifting code
		if (shiftBtn.get() == true && shifted) {
			shifted = false;
		} else if (shiftBtn.get() == true && !shifted) {
			shifted = true;
		}
		
		if(shifted == true) {
			shifter.set(true);
		} else { 
			shifter.set(false);
		}
		
		
	}

	@Override
	public void testPeriodic() {
	}
}
