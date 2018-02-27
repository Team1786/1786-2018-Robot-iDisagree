/*----------------------------------------------------------------------------*/
  /* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

// Will
package org.usfirst.frc.team1786.robot;
 

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.drive.*;
import edu.wpi.first.wpilibj.PWMTalonSRX;
import edu.wpi.first.wpilibj.SPI;

import java.lang.Math;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.wpilibj.Spark;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends IterativeRobot {
	
	
	private String m_autoSelected;
	private SendableChooser<String> m_chooser = new SendableChooser<>();
	
	AHRS navx = new AHRS(SPI.Port.kMXP);
	
	Joystick stick1 = new Joystick(0);
	Joystick stick2 = new Joystick(1);
	
	//Current limit 120 amps
	WPI_TalonSRX talonL1 = new WPI_TalonSRX(1);
	WPI_TalonSRX talonL2 = new WPI_TalonSRX(2);
	//WPI_TalonSRX talonL3 = new WPI_TalonSRX(3);
	
	WPI_TalonSRX talonR1 = new WPI_TalonSRX(3);
	WPI_TalonSRX talonR2 = new WPI_TalonSRX(4);
	//WPI_TalonSRX talonR3 = new WPI_TalonSRX(6);
	
	//for testing
	Spark spark1 = new Spark(0);
	Spark spark2= new Spark(1);
	
	WPI_TalonSRX testTalon = new WPI_TalonSRX(5);
	
	DifferentialDrive myRobot = new DifferentialDrive(talonL1, talonR1);
	
	//for encoder
	double rotations = 0;
	double distanceInches = 0;
	double rawEncoderData = 0;
	
	// 1 = full speed .5 is testing speed
	final double speed = 1;
	
	//for smart dashboard
	boolean isTurning;
	boolean isSteering;
	
	//for autonomous
	final String command1 = "Swith Position 2";
	final String command2 = "Scale Position 1";
	final String command3 = "Scale Position 3";
	final String command4 = "Move Forward";
			
	
	

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		m_chooser.addDefault("Default Auto", command4);
		
		m_chooser.addObject(command1, command1);
		m_chooser.addObject(command2, command2);
		m_chooser.addObject(command3, command3);
		m_chooser.addObject(command4, command4);
		
		//configure encoder
		testTalon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
		//reset encoder
		testTalon.getSensorCollection().setPulseWidthPosition(0, 100000);
		
		
		SmartDashboard.putData("Auto choices", m_chooser);
		
			
		//create slaves
		talonL2.follow(talonL1);
		//talonL3.follow(talonL1);
		
		talonR2.follow(talonR1);
		//talonR3.follow(talonR1);
		
		//reverse masters
		
		//talonR1.setInverted(true);
		//talonL1.setInverted(true);
		
		//current limiting
		
		this.limitCurrent(talonL1);
		this.limitCurrent(talonR1);
		
		//follow for testTalon
		testTalon.follow(talonL1);
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
		int distanceMoved = talonL1.getSensorCollection().getPulseWidthPosition();
		
		m_autoSelected = m_chooser.getSelected();
		
		System.out.println("Auto selected: " + m_autoSelected);
		
		switch (m_autoSelected) {
			
			case command1:
				//command1
				break;
			case command2:
				//command2
				break;
			case command3:
				//command3
				break;
			case command4:
				//command4
				break;
			default:
				//command4
				break;
			
		}
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		this.trackEncoder();
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		
		//display Data
		
		this.displayTalon(talonL1, "talonL1");
		this.displayTalon(talonR1, "talonR1");
		this.displayTalon(talonL2, "talonL2");
		this.displayTalon(talonR2, "talonR2");
		
		//drive
		
		this.drive();
		//this.inferiourDrive();
		
		//pickup
		  
		this.pickup();
		
		//encoder
		
		this.trackEncoder();
		
	}
	private void pickup()
	{
		double y = stick2.getY();
		
		if(y!=0)
		{
			SmartDashboard.putNumber("ThrottleForPickup: ", y);
			spark1.set(y);
			spark2.set(-y);
		}
		
	}
	private void drive()
	{
		double x = stick1.getX();
		double y = stick1.getY();
		double z = stick1.getZ();
		
		SmartDashboard.putBoolean("is turning", isTurning);
		SmartDashboard.putBoolean("isSteering", isSteering);
		
		if(z < -0.4 || z > 0.4)
		{
			//twist
			
			isTurning = true;
			talonL1.set(exponentialModify(z*speed, 5));
			talonR1.set(exponentialModify(z*speed, 5));
		}
		else
		{
			isTurning = false;
			double power = Math.sqrt((x*x)+(y*y));
			SmartDashboard.putNumber("rawY", y);
			SmartDashboard.putNumber("rawX", x); 
			//dead zone for all non twisting movement; was = 0.2
			if(power > 0.25)
			{
				isSteering = true;
				if(power>1)
					power=1;
				//philip's modifier function
				power = exponentialModify(power, 3);
				if(y<0)
					power=-power;
				power *= speed;
				SmartDashboard.putNumber("power", power); 
				//getting scale
				double scale = 1-Math.abs(x);
				
				if(x<0)
				{
					//left
					
					talonL1.set(-power*scale);
					talonR1.set(power);
				}
				else
				{
					//right
					
					talonL1.set(-power);
					talonR1.set(power*scale);
				}
				
			}
			else
			{
				isSteering = false;
				talonL1.set(0);
				talonR1.set(0);
			}
			//nothing
		}
	}
	private void trackEncoder()
	{
		//4096 = a rotation
		//a rotation = 4pi inches moved
		
		rawEncoderData = testTalon.getSensorCollection().getPulseWidthPosition();
		double remainder = rawEncoderData % 4096;
		
		SmartDashboard.putNumber("Rotations: ", rotations);
		SmartDashboard.putNumber("Distance in Inches: ", distanceInches);
		SmartDashboard.putNumber("Raw Encoder Output: ", rawEncoderData);
		
		if(remainder < 200)
		{
			rotations += 1+(remainder/4096);
			distanceInches += 12.56631*(1+(remainder/4096));  
			
		}
		
	}
	private void limitCurrent(WPI_TalonSRX talon)
	{
		
		talon.enableCurrentLimit(true);
		talon.configPeakCurrentLimit(60, 0);
		talon.configPeakCurrentDuration(10000,0);
		talon.configContinuousCurrentLimit(40, 0);
	}
	private void displayTalon(WPI_TalonSRX talon, String label)
	{
		
		SmartDashboard.putNumber(label+" Output Current: ", talon.getOutputCurrent());
		SmartDashboard.putNumber(label+" Output Temperature: ", talon.getTemperature());
		
	}
	private void Move(double distance)
	{
		distance += distanceInches;
		double difference = distance;
		
		
		double speed = 1;
		
		talonL1.set(speed);
		talonR1.set(speed);
		while (Math.abs(difference) > 1)
		{
			difference = distanceInches - distance;
			
			if(difference < 0)
			{
				
				speed *= 0.5 * -1;
				
				distance = (difference*-1)+distanceInches;
				
				talonL1.set(speed);
				talonR1.set(speed);
			}
		}
		talonL1.set(0);
		talonR1.set(0);
		
	}
	private double exponentialModify(double power, double scale) 
	{
			
		return Math.tan(power*Math.atan(scale))/scale;
	}
	private void Turn(String direction)
	{
		if(direction == "left")
		{
			//while loop
		}
		else if(direction == "right")
		{
			//while loop
		}
	}
	private void inferiourDrive ()
	{
		double y = stick1.getY();
		double z = stick1.getZ();  
		
		myRobot.arcadeDrive(-y, z, true);
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
//willw
