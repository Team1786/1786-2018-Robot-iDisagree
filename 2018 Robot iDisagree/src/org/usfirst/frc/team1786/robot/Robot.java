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
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
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

	private int contCurrent = 40;
	private int peakDuration = 10000;
	private int peakCurrent = 60;
	private double joystickDeadband = 0.05;

	
	//Declare Joysticks
	Joystick joystickLeft = new Joystick(0);
	Joystick joystickRight = new Joystick(1);

	
	//Declaring Talons
	WPI_TalonSRX talonL1 = new WPI_TalonSRX(1);
	WPI_TalonSRX talonL2 = new WPI_TalonSRX(2);
	WPI_TalonSRX talonL3 = new WPI_TalonSRX(3);
	WPI_TalonSRX talonR4 = new WPI_TalonSRX(3);
	WPI_TalonSRX talonR5 = new WPI_TalonSRX(4);
	WPI_TalonSRX talonR6 = new WPI_TalonSRX(6);
	
	//Declaring Solenoids & Compressors
	Solenoid solenGear = new Solenoid(1);
	Compressor compressor = new Compressor(1);
	
	
	DifferentialDrive myRobot = new DifferentialDrive(talonL1, talonR4);
	
	
	//Current Limiting
	private void currentLimiting(WPI_TalonSRX talon) {
		talon.configContinuousCurrentLimit(contCurrent, 0);
		talon.configPeakCurrentLimit(peakCurrent, 0);
		talon.configPeakCurrentDuration(peakDuration, 0);
		talon.enableCurrentLimit(true);
		
	}
	
	
	//Talon Display
	private void displayTalon(WPI_TalonSRX talon, String label) {
		
		SmartDashboard.putNumber(label + " Current", talon.getOutputCurrent());
		SmartDashboard.putNumber(label + " Temperature", talon.getTemperature());
		
	}
	
	
	//Joystick Display
	private void displayJoystick(Joystick joystick, String label) {
		SmartDashboard.putNumber(label + "Y", joystick.getY());
		SmartDashboard.putNumber(label + "Z", joystick.getZ());
	}
	
	
	//Fill Compressor
	private void fillCompressor(Compressor compressor) {
	while(compressor.getPressureSwitchValue())
	{
		compressor.setClosedLoopControl(true);
	}
		compressor.setClosedLoopControl(false);
	}
	
	
	//Shift down
	private void DownShift(Joystick joystick) {
		@SuppressWarnings("unused")
		boolean btnState = joystick.getRawButton(4);
		
		if (btnState = true) {
			solenGear.set(false);
		} else {
			
		}
		
	}
		
	
	//Shift up
		private void UpShift(Joystick joystick) {
			@SuppressWarnings("unused")
			boolean btnState = joystick.getRawButton(5);
			
			if (btnState = false) {
				solenGear.set(true);
			} else {
				
			}
			
		}
	
	/*Z-Drive Exponential
	private double sqrZ(Joystick joystick) {
		return joystick.getZ()/Math.abs(joystick.getZ())* Math.sqrt(Math.abs(joystick.getZ()));
	}*/
	
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		m_chooser.addDefault("Default Auto", kDefaultAuto);
		m_chooser.addObject("My Auto", kCustomAuto);
		SmartDashboard.putData("Auto choices", m_chooser);
		
		//Talon follows
		talonL2.follow(talonL1);
		talonL3.follow(talonL1);
		talonR5.follow(talonR4);
		talonR6.follow(talonR4);
		
		//apply deadband
		myRobot.setDeadband(joystickDeadband);
		
		//Current Limiter
		currentLimiting(talonL1);
		currentLimiting(talonL2);
		currentLimiting(talonL3);
		currentLimiting(talonR4);
		currentLimiting(talonR5);
		currentLimiting(talonR6);
		
		
		
		
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
		
		displayTalon(talonL1, "TalonL1");
		displayTalon(talonL2, "TalonL2");
		displayTalon(talonL3, "TalonL3");
		displayTalon(talonR4, "TalonR4");
		displayTalon(talonR5, "TalonR5");
		displayTalon(talonR6, "TalonR6");
		
		displayJoystick(joystickLeft, "LeftJoystick");
		
		//driving thingy
		myRobot.arcadeDrive(-joystickLeft.getY(), joystickLeft.getZ(), true);
		
		DownShift(joystickLeft);
		UpShift(joystickLeft);
		
		
		
		
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
		
		fillCompressor(compressor);
		
		
		
	}
}
