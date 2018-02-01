/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

// Will
package org.usfirst.frc.team1786.robot;
 

import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.drive.*;
import edu.wpi.first.wpilibj.PWMTalonSRX;
import java.lang.Math;

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
	
	private static final String kDefaultAuto = "Default";
	private static final String kCustomAuto = "My Auto";
	private String m_autoSelected;
	private SendableChooser<String> m_chooser = new SendableChooser<>();
	
	Joystick stick1 = new Joystick(1);
	Joystick stick2 = new Joystick(2);
	
	//Current limit 120 amps
	WPI_TalonSRX robotLeft = new WPI_TalonSRX(1);
	WPI_TalonSRX robotLeftSlave1 = new WPI_TalonSRX(2);
	//WPI_TalonSRX robotLeftSlave2 = new WPI_TalonSRX(3);
	
	WPI_TalonSRX robotRight = new WPI_TalonSRX(3);
	WPI_TalonSRX robotRightSlave1 = new WPI_TalonSRX(4);
	//WPI_TalonSRX robotRightSlave2 = new WPI_TalonSRX(6);
	
	Spark spark1 = new Spark(0);
	Spark spark2= new Spark(1);
	
	double speed;
	boolean isTurning;
	boolean isSteering;
	
	//current limiting
	
	

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		m_chooser.addDefault("Default Auto", kDefaultAuto);
		m_chooser.addObject("My Auto", kCustomAuto);
		SmartDashboard.putData("Auto choices", m_chooser);
		//change speed here
		speed = 0.5;
		
		//initialize current limiting
		robotLeft.enableCurrentLimit(true);
		robotRight.enableCurrentLimit(true);
		
		
		
		
		
		
		robotLeftSlave1.follow(robotLeft);
		//robotLeftSlave2.follow(robotLeft);
		
		robotRightSlave1.follow(robotRight);
		//robotRightSlave2.follow(robotRight);
		
		//current limiting
		robotLeft.configContinuousCurrentLimit(120, 1);
		robotRight.configContinuousCurrentLimit(120, 1);
		
		
		
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
		
		//display Data;
		
		this.displayTalon(robotLeft, "robotLeft");
		this.displayTalon(robotRight, "robotRight");
		this.displayTalon(robotLeftSlave1, "robotLeftSlave1");
		this.displayTalon(robotRightSlave1, "robotRightSave1");
		
		//limit current
		
		this.limitCurrent(robotLeft);
		this.limitCurrent(robotRight);
		
		//drive
		
		this.drive();
		
		//pickup
		
		this.pickup();
		
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
			robotLeft.set(z*speed);
			robotRight.set(z*speed);
		}
		else
		{
			isTurning = false;
			double power = Math.sqrt((x*x)+(y*y));
			SmartDashboard.putNumber("power", power);
			if(power > 0.2)
			{
				isSteering = true;
				power *= speed;
				if(power>1)
					power=1;
				
				if(y<-0.25)
					power=-power;
					
				double scale = 1-Math.abs(x);
				if(x<0)
				{
					//left
					
					robotLeft.set(-power*scale);
					robotRight.set(power);
				}
				else
				{
					//right
					
					robotLeft.set(-power);
					robotRight.set(power*scale);
				}
				
			}
			else
			{
				isSteering = false;
				robotLeft.set(0);
				robotRight.set(0);
			}
			//nothing
		}
	}
	private void limitCurrent(WPI_TalonSRX talon)
	{
		talon.configPeakCurrentLimit(60, 0);
		talon.configPeakCurrentDuration(10000,0);
		talon.configContinuousCurrentLimit(40, 0);
	}
	private void displayTalon(WPI_TalonSRX talon, String label)
	{
		
		SmartDashboard.putNumber(label+" Output Current: ", talon.getOutputCurrent());
		SmartDashboard.putNumber(label+" Output Temperature: ", talon.getTemperature());
		
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
