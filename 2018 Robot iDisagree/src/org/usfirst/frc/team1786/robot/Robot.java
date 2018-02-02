/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team1786.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.drive.*;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.buttons.*;
import edu.wpi.first.wpilibj.PWMTalonSRX;
import com.ctre.phoenix.motorcontrol.can.*;

public class Robot extends IterativeRobot {

	// left Side
	WPI_TalonSRX talonL1 = new WPI_TalonSRX(1);
	WPI_TalonSRX talonL2 = new WPI_TalonSRX(2);
	WPI_TalonSRX talonL3 = new WPI_TalonSRX(3);
	
	// right Side
	WPI_TalonSRX talonR4 = new WPI_TalonSRX(4);
	WPI_TalonSRX talonR5 = new WPI_TalonSRX(5);
	WPI_TalonSRX talonR6 = new WPI_TalonSRX(6);
	
	Joystick joystickLeft = new Joystick(0);
	Joystick joystickRight = new Joystick(1);
	
	// buttons on top of Joystick
	JoystickButton shiftUp = new JoystickButton(joystickLeft, 5);
	JoystickButton shiftDown = new JoystickButton(joystickLeft, 6);
	
	Compressor robotCompressor = new Compressor();
	
	DifferentialDrive myRobot = new DifferentialDrive(talonL1, talonR4);
	
	private int maxPeakAmp = 60; // defines the max amp that can be given to a moter during its peak
	private int maxCountAmp = 40; // defines the max amp that can be given to a moter after its peak
	private int peakTimeDuration = 10000; // defines how long the peak will last in milliseconds
	
	private Double driveDeadband = .05; // defines the deadzone
	
	private void dashboardUpdate() {
		
		// put talon amp info on dashboard
		double talon1Current = talonL1.getOutputCurrent(); // defines the talons AMP values
		double talon2Current = talonL2.getOutputCurrent();
		double talon3Current = talonL3.getOutputCurrent();
		double talon4Current = talonR4.getOutputCurrent();
		double talon5Current = talonR5.getOutputCurrent();
		double talon6Current = talonR6.getOutputCurrent();
		SmartDashboard.putNumber("Talon1 Amps", talon1Current); // displays all the talon AMP values
		SmartDashboard.putNumber("Talon2 Amps", talon2Current);
		SmartDashboard.putNumber("Talon3 Amps", talon3Current);
		SmartDashboard.putNumber("Talon4 Amps", talon4Current);
		SmartDashboard.putNumber("Talon5 Amps", talon5Current);
		SmartDashboard.putNumber("Talon6 Amps", talon6Current);
		
		// put left joystick info on dashboard
		SmartDashboard.putNumber("drive Y value", joystickLeft.getY()); // displays the y value on computer
		SmartDashboard.putNumber("drive X value", joystickLeft.getX()); // displays the x value on computer
		SmartDashboard.putNumber("drive Z value", joystickLeft.getZ()); // displays the z value on computer
	}
	
	@Override
	public void robotInit() {
		
		myRobot.setDeadband(driveDeadband); // sets the deadzone
		
		// configure talon slaves.
		talonL2.follow(talonL1); 
		talonL3.follow(talonL1);
		talonR5.follow(talonR4);
		talonR6.follow(talonR4);
		
		// Configure left drive side amp limits
		talonL1.configPeakCurrentDuration(peakTimeDuration, 0); // sets the duration of the peak
		talonL1.configPeakCurrentLimit(maxPeakAmp, 0); // sets the max current of the peak
		talonL1.configContinuousCurrentLimit(maxCountAmp, 0); // sets the max current for the time after the peak
		
		// Configure right drive side amp limits
		talonR4.configPeakCurrentDuration(peakTimeDuration, 0); // same as the other one
		talonR4.configPeakCurrentLimit(maxPeakAmp, 0);
		talonR4.configContinuousCurrentLimit(maxCountAmp, 0);		
		
	}
	@Override
	public void autonomousInit() {
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		Double driveX = joystickLeft.getX();// puts the left joysticks X value into a variable
		Double driveZ = joystickLeft.getZ();// puts the left joysticks Z value into a variable
		Double driveY = -(joystickLeft.getY()); // inverts the y value so that foward is foward	
		
		myRobot.arcadeDrive(driveY, driveX, true); // allows the robot to drive with squared inputs using the y and z values from the left joystick
		
		//put data on dashboard
		dashboardUpdate();
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
		
		// Fill the compressor during autonomous
		while(robotCompressor.getPressureSwitchValue() == false)
		{
			robotCompressor.setClosedLoopControl(true);
		}
			robotCompressor.setClosedLoopControl(false);
		}
			
	
}
