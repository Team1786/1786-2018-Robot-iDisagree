/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team1786.robot;

import edu.wpi.first.wpilibj.AnalogTrigger;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj.drive.*;
//import edu.wpi.first.wpilibj.buttons.JoystickButton;
//import edu.wpi.first.wpilibj.buttons.Trigger;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Compressor;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */

public class Robot extends IterativeRobot {
	private static final String kDefaultAuto = "Default";
	private static final String kCustomAuto = "My Auto";
	private String m_autoSelected;
	private SendableChooser<String> m_chooser = new SendableChooser<>();
	
	//Declaring the current power
	
	private int continuousAmps = 40;
	private int peakCurrent = 60;
	private int timeMs = 0;
	private int currentDuration = 10000;
	
	//Declaring the talons
	
	WPI_TalonSRX talonL1 = new WPI_TalonSRX(1);
	WPI_TalonSRX talonL2 = new WPI_TalonSRX(2);
	WPI_TalonSRX talonL3 = new WPI_TalonSRX(3);
	WPI_TalonSRX talonR4 = new WPI_TalonSRX(4);
	WPI_TalonSRX talonR5 = new WPI_TalonSRX(5);
	WPI_TalonSRX talonR6 = new WPI_TalonSRX(6);
	
	//Declaring the drive, joysticks, and solenoids
	
	DifferentialDrive myRobot = new DifferentialDrive(talonL1, talonR4);

	Joystick joystickRight = new Joystick(1); //set ID 1 in DriverStation
	Joystick joystickLeft = new Joystick(0); //set ID 2 in Driver Station
	
	Solenoid solenoid = new Solenoid(1);
	
	//Declaring buttons
	
	boolean buttonstate = false;
	//boolean button1 = true;
	AnalogTrigger trigger0 = new AnalogTrigger(0);
	
	/*
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	
	//Declaring compressor
	Compressor robotCompressor = new Compressor(1);
	
	
	
	@Override
	public void robotInit() {
		m_chooser.addDefault("Default Auto", kDefaultAuto);
		m_chooser.addObject("My Auto", kCustomAuto);
		SmartDashboard.putData("Auto choices", m_chooser);
		
		//talonL2 and L3 slave energy off talonL1
		//talonR5 and R6 slave energy off talonR4
		talonL2.follow(talonL1);
		talonL3.follow(talonL1);
		talonR5.follow(talonR4);
		talonR6.follow(talonR4);
		
		//this code tells each talon to use a certain amount of enrgy and no timeout time
		talonL1.configContinuousCurrentLimit(continuousAmps, timeMs);
		talonR4.configContinuousCurrentLimit(continuousAmps, timeMs);
		talonL2.configContinuousCurrentLimit(continuousAmps, timeMs);
		talonL3.configContinuousCurrentLimit(continuousAmps, timeMs);
		talonR5.configContinuousCurrentLimit(continuousAmps, timeMs);
		talonR6.configContinuousCurrentLimit(continuousAmps, timeMs);
		
		//the peak current states the maximum amount of energy that can be drawn from each talon with no timeout
		talonL1.configPeakCurrentLimit(peakCurrent,timeMs);
		talonR4.configPeakCurrentLimit(peakCurrent,timeMs);
		talonL3.configPeakCurrentLimit(peakCurrent,timeMs);
		talonL2.configPeakCurrentLimit(peakCurrent,timeMs);
		talonR5.configPeakCurrentLimit(peakCurrent,timeMs);
		talonR6.configPeakCurrentLimit(peakCurrent,timeMs);
		
		//this code states how long each talon can stay at peak current(amps) in milliseconds with no timeout
		talonL1.configPeakCurrentDuration(currentDuration, timeMs);
		talonL2.configPeakCurrentDuration(currentDuration, timeMs);
		talonL3.configPeakCurrentDuration(currentDuration, timeMs);
		talonR4.configPeakCurrentDuration(currentDuration, timeMs);
		talonR5.configPeakCurrentDuration(currentDuration, timeMs);
		talonR6.configPeakCurrentDuration(currentDuration, timeMs);
		
		//States that each talon can draw amps from the energy source
		talonL1.enableCurrentLimit(true);
		talonL2.enableCurrentLimit(true);
		talonL3.enableCurrentLimit(true);
		talonR4.enableCurrentLimit(true);
		talonR5.enableCurrentLimit(true);
		talonR6.enableCurrentLimit(true);
		
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * <p>You can add additional auto modes by adding additional comparisons to
	 * the switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	
	
	
	
	
	
	@Override
	public void autonomousInit() {
		m_autoSelected = m_chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + m_autoSelected);
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		switch (m_autoSelected) {
			case kCustomAuto:
				// Put custom auto code here
				break;
			case kDefaultAuto:
			default:
				// Put default auto code here
				break;
		}
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		
		myRobot.arcadeDrive(joystickLeft.getY(), joystickLeft.getZ(), true);
			//SmartDashboard.putNumber("". );
		
		/*if( joystickRight.getTrigger())
		{
			solenoid.set(true);
			
			This part is what switches the gears when the trigger is pressed
		}*/
		
		buttonstate = trigger0.getTriggerState();
		
		if (buttonstate)
		{
			solenoid.set(true);
		}

		
	}

	/**
	 * This function is called periodically during test mode.
	 */
	

	@Override
	public void testPeriodic() {
		//charges the robot and turns it off when done
		while(robotCompressor.getPressureSwitchValue() == false) {
			
			robotCompressor.setClosedLoopControl(true);
		}
		robotCompressor.setClosedLoopControl(false);
	}
}

