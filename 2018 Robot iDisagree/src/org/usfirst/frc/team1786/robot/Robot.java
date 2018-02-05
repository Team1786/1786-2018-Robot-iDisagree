/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team1786.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.drive.*;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Compressor;

import com.ctre.phoenix.motorcontrol.can.*;
import com.kauailabs.navx.frc.AHRS;


public class Robot extends IterativeRobot {

	Joystick gamepad = new Joystick(0);
	
	/* CONSTANT VALUES */
	final int SHIFTER = 3;
	final int ACCDRIVE = 6;
	
	
	WPI_TalonSRX talonL1 = new WPI_TalonSRX(1); // left Side talons
	WPI_TalonSRX talonL2 = new WPI_TalonSRX(2);
	WPI_TalonSRX talonL3 = new WPI_TalonSRX(3);
	WPI_TalonSRX talonR4 = new WPI_TalonSRX(4); // right Side Talons
	WPI_TalonSRX talonR5 = new WPI_TalonSRX(5);
	WPI_TalonSRX talonR6 = new WPI_TalonSRX(6);
	
	DifferentialDrive myRobot = new DifferentialDrive(talonL1, talonR4);
	
	Compressor compressor = new Compressor(); // only one compressor in system
	Solenoid shifter = new Solenoid(0);
	
	private int maxPeakAmp = 60; // defines the max amp that can be given to a CIM motor during its peak
	private int maxCountAmp = 40; // defines the max amp that can be given to a CIM motor after its peak
	private int peakTimeDuration = 10000; // defines how long the peak will last in milliseconds
	
	private Double driveDeadband = .05; // defines the deadzone of the myRobot object
	private Double shiftLimit = 0.2; // defines upper maximum rate of movement that shifting is allowed in
	
	boolean accurateDrive;
	boolean shifted;
	
	private void dashboardUpdate() {
		
		// put amp info on dashboard
		double talon1Current = talonL1.getOutputCurrent();
		double talon2Current = talonL2.getOutputCurrent();
		double talon3Current = talonL3.getOutputCurrent();
		double talon4Current = talonR4.getOutputCurrent();
		double talon5Current = talonR5.getOutputCurrent();
		double talon6Current = talonR6.getOutputCurrent();
		double compressorCurrent = compressor.getCompressorCurrent(); 
		SmartDashboard.putNumber("Talon1 Amps", talon1Current); // displays all the talon amp values
		SmartDashboard.putNumber("Talon2 Amps", talon2Current);
		SmartDashboard.putNumber("Talon3 Amps", talon3Current);
		SmartDashboard.putNumber("Talon4 Amps", talon4Current);
		SmartDashboard.putNumber("Talon5 Amps", talon5Current);
		SmartDashboard.putNumber("Talon6 Amps", talon6Current);
		SmartDashboard.putNumber("compressor amps", compressorCurrent); //display compressor amp usage
		
		// put left joystick info on dashboard
		SmartDashboard.putNumber("drive Y value", gamepad.getY());
		SmartDashboard.putNumber("drive X value", gamepad.getX());
		SmartDashboard.putNumber("drive Z value", gamepad.getZ());
		SmartDashboard.putNumber("drive twist value", gamepad.getTwist());
		SmartDashboard.putBoolean("is shift button pressed", gamepad.getRawButton(SHIFTER));
		SmartDashboard.putBoolean("is acc drive button pressed", gamepad.getRawButton(ACCDRIVE));
		
		// compressor and solenoid information (bools)
		SmartDashboard.putBoolean("solenoid drive shifter ID 0 state", shifter.get());
		SmartDashboard.putBoolean("low pressure switch state", compressor.getPressureSwitchValue());
	}
	
	@Override
	public void robotInit() {
		// configure talon followers.
		talonL2.follow(talonL1); 
		talonL3.follow(talonL1);
		talonR5.follow(talonR4);
		talonR6.follow(talonR4);

		//we can just set motor safety for the differentialDrive object
		myRobot.setDeadband(driveDeadband);
		myRobot.setSafetyEnabled(true);
		myRobot.setExpiration(100);
		
		// Configure talon amp limits
		talonL1.configPeakCurrentDuration(peakTimeDuration, 0); // sets the duration of the peak
		talonL1.configPeakCurrentLimit(maxPeakAmp, 0); // "Configure the peak current limit to the threshold necessary to exceed to activate current limiting"
		talonL1.configContinuousCurrentLimit(maxCountAmp, 0); // sets the max current for the time after the peak
		talonL1.enableCurrentLimit(true);
		talonR4.configPeakCurrentDuration(peakTimeDuration, 0); // same as the other one
		talonR4.configPeakCurrentLimit(maxPeakAmp, 0);
		talonR4.configContinuousCurrentLimit(maxCountAmp, 0);
		talonR4.enableCurrentLimit(true);
		
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

	@Override
	public void teleopInit() {
		accurateDrive = false;
		shifted = false;
	}
	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		
		//get joystick axis values for use later.
		Double driveX = gamepad.getX();// puts the left joysticks X value into a variable
		Double driveZ = gamepad.getZ();// puts the left joysticks Z value into a variable
		Double driveY = -gamepad.getY(); // inverts the y value so that foward is foward	
		Double driveTwist = gamepad.getTwist();
		
		/* BEGIN DRIVE CODE */
		
		//myRobot.arcadeDrive(driveY, driveZ, true); // allows the robot to drive with squared inputs using the y and z values from the left joystick
		if(gamepad.getRawButtonPressed(ACCDRIVE) && accurateDrive) {
			accurateDrive = false;
		} else if (gamepad.getRawButtonPressed(ACCDRIVE) && !accurateDrive) {
			accurateDrive = true;
		}
		
		if(accurateDrive) {
			myRobot.curvatureDrive(driveY, driveZ, true);
		} else {
			myRobot.curvatureDrive(driveY, driveZ, false);
		};
		/* END DRIVE CODE */
		
		/* BEGIN PNEUMATICS CODE */
		//run the compressor if the pressure is low
		// TODO add compressor current limiting and calibrate
		if(!compressor.getPressureSwitchValue()) {
			compressor.setClosedLoopControl(true);
		} else {
				compressor.setClosedLoopControl(false);
		}
		
		//run the shifter pnuematic cylinder
		//shifts when the shifting button is pressed and pressure is sufficient for shifting
		// TODO add timer to wait for movement between shifts 
		if(gamepad.getRawButtonPressed(SHIFTER) && shifted) {
			shifter.set(true);
		} else if
		(gamepad.getRawButtonPressed(SHIFTER) && !shifted) {
			shifter.set(false);
		}
		
		//prevent shifting if moving at high speeds
		// TODO calibrate shiftLimit Value
		if (shifted && (driveY < shiftLimit && driveZ < shiftLimit)) {
			shifter.set(true);
		} else {
			shifter.set(false);
		}
		/* END PNUEMATICS CODE */
		
		//put data on dashboard
		dashboardUpdate();
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
		
		// Fill the compressor during autonomous
		if(compressor.getPressureSwitchValue() == false)
		{
			compressor.setClosedLoopControl(true);
		} else {
			compressor.setClosedLoopControl(false);
		}
			
	}
}	
