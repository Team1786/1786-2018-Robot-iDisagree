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
import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.drive.*;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
// welcome to my branch boys
public class Robot extends IterativeRobot {
	private static final String kDefaultAuto = "Default";
	private static final String kCustomAuto = "My Auto";
	private String m_autoSelected;
	private SendableChooser<String> m_chooser = new SendableChooser<>();

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	WPI_TalonSRX talonL1 = new WPI_TalonSRX(1);
	WPI_TalonSRX talonL2 = new WPI_TalonSRX(2);
	WPI_TalonSRX talonL3 = new WPI_TalonSRX(3);
	WPI_TalonSRX talonR4 = new WPI_TalonSRX(4);
	WPI_TalonSRX talonR5 = new WPI_TalonSRX(5);
	WPI_TalonSRX talonR6 = new WPI_TalonSRX(6);
	WPI_TalonSRX rightArmTalon = new WPI_TalonSRX(7);
	WPI_TalonSRX leftArmTalon = new WPI_TalonSRX(8);
	WPI_TalonSRX elevatorTalon1 = new WPI_TalonSRX(9);
	WPI_TalonSRX elevatorTalon2 = new WPI_TalonSRX(10);
	
	Joystick joystickLeft = new Joystick(0);
	Joystick joystickRight = new Joystick(1);
		
	DifferentialDrive myRobot = new DifferentialDrive(talonL1, talonR4);
	
	Compressor compressor = new Compressor();
	Solenoid shifter = new Solenoid(0);
	
	private int maxPeakAmp = 60; //defines the max amp that can be given to a moter during its peak
	private int maxCountAmp = 40; //defines the max amp that can be given to a moter after its peak
	private int peakTimeDuration = 10000; //defines how long the peak will last in milliseconds
	
	// joystick button channel 3
	final int SHIFTER = 3;
	boolean shifted;
	boolean shiftBtn;
	double latest;
	double period;
	
	public void debounceButton(Joystick joy, int BtnChannel, int waitPeriod ) 
	{
		
		boolean BtnState = joy.getRawButtonPressed(BtnChannel);
		Timer lastButtonClick = new Timer();
		double timeSence = lastButtonClick.get();
		int toMilli = 1000;
		long waitInMilli = waitPeriod * toMilli;
		
		if (joy.getRawButtonPressed(BtnChannel)) 
		{
			lastButtonClick.reset();
			lastButtonClick.start();
		}
		if (BtnState && timeSence < waitPeriod) 
		{	
			try 
			{
				Thread.currentThread().sleep(waitInMilli);
			}
			catch(InterruptedException ie){
				
			}
		}
	
		
	}
	
	public double YdeadbandScaler(Joystick joy, double deadband) 
	{
		double joystickY = joy.getY();
		
		
		if (joystickY > 0 && joystickY > deadband) 
		{
			double scaledYValue = (joystickY - deadband) / ((1-deadband)/(1));
			return scaledYValue;
		}
		else if (joystickY < 0 && joystickY < -deadband) 
		{
			double scaledYValue = -(joystickY + deadband) / ((1+deadband)/(1));
			return scaledYValue;
		}
		else 
		{
			double scaledYValue = 0;
			return scaledYValue;
		}	
		
	}
	
	public double ZdeadbandScaler(Joystick joy, double deadband) 
	{
		double joystickZ = joy.getZ();
		
		
		if (joystickZ > 0 && joystickZ > deadband) 
		{
			double scaledZValue = (joystickZ - deadband) / ((1-deadband)/(1));
			return scaledZValue;
		}
		else if (joystickZ < 0 && joystickZ < -deadband) 
		{
			double scaledZValue = -(joystickZ + deadband) / ((1+deadband)/(1));
			return scaledZValue;
		}
		else 
		{
			double scaledZValue = 0;
			return scaledZValue;
		}	
		
	}
	
	
	
	
	@Override
	public void robotInit() {
		m_chooser.addDefault("Default Auto", kDefaultAuto);
		m_chooser.addObject("My Auto", kCustomAuto);
		SmartDashboard.putData("Auto choices", m_chooser);
		
		
		talonL2.follow(talonL1); //tells the following talons to follow their leading talons
		talonL3.follow(talonL1);
		talonR5.follow(talonR4);
		talonR6.follow(talonR4);
		elevatorTalon2.follow(elevatorTalon1);
		
		talonL1.setInverted(true);
		talonR4.setInverted(true);
		
		Double deadband = .05; //defines the deadzone
		
		myRobot.setDeadband(deadband); //sets the deadzone
		
		talonL1.configPeakCurrentDuration(peakTimeDuration, 0); //sets the duration of the peak
		talonL1.configPeakCurrentLimit(maxPeakAmp, 0); //sets the max current of the peak
		talonL1.configContinuousCurrentLimit(maxCountAmp, 0); //sets the max current for the time after the peak
		
		talonR4.configPeakCurrentDuration(peakTimeDuration, 0); //same as the other one
		talonR4.configPeakCurrentLimit(maxPeakAmp, 0);
		talonR4.configContinuousCurrentLimit(maxCountAmp, 0);
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

	@Override
	public void teleopInit() {
		shifted = false;
		shiftBtn = false;
		latest = 0;
	}
	
	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		
		Double dispYValueLeft = joystickLeft.getY(); //puts the left joysticks Y value into a variable
		Double xValueLeft = joystickLeft.getX();//puts the left joysticks X value into a variable
		Double zValueLeft = joystickLeft.getZ();//puts the left joysticks Z value into a variable
		Double yValueRight = joystickRight.getY();
		Double zValueRight = joystickRight.getZ();
		SmartDashboard.putNumber("Y value", dispYValueLeft); //displays the y value on computer
		SmartDashboard.putNumber("X value", xValueLeft); //displays the x value on computer
		SmartDashboard.putNumber("Z value", zValueLeft); //displays the z value on computer
		
		Double driveYValue = -(joystickLeft.getY()); //inverts the y value so that foward is foward
		
		double talon1 = talonL1.getOutputCurrent(); //defines the talons AMP values
		double talon2 = talonL2.getOutputCurrent();
		double talon3 = talonL3.getOutputCurrent();
		double talon4 = talonR4.getOutputCurrent();
		double talon5 = talonR5.getOutputCurrent();
		double talon6 = talonR6.getOutputCurrent();
		SmartDashboard.putNumber("Talon1", talon1); //displays all the talon AMP values
		SmartDashboard.putNumber("Talon2", talon2);
		SmartDashboard.putNumber("Talon3", talon3);
		SmartDashboard.putNumber("Talon4", talon4);
		SmartDashboard.putNumber("Talon5", talon5);
		SmartDashboard.putNumber("Talon6", talon6);
		
		
		
		myRobot.arcadeDrive(driveYValue, zValueLeft, true); //allows the robot to drive with scaling using the y and z values from the left joystick
		
		double armWheelSpeed = zValueRight;
		double elevatorSpeed = yValueRight;
		double armDeadband = .15;
		double elevatorDeadband = .15;
		
		
		
		if (zValueRight < armDeadband) {
			rightArmTalon.set(-armWheelSpeed);
			leftArmTalon.set(armWheelSpeed);
		} else if (zValueRight > armDeadband) {
			rightArmTalon.set(-armWheelSpeed);
			leftArmTalon.set(armWheelSpeed);	
		} else {
			rightArmTalon.set(0);
			leftArmTalon.set(0);	
		}
		
		
		elevatorTalon1.set(YdeadbandScaler(joystickRight, elevatorDeadband));
		
		double period = 0.5; //seconds 
		double now = Timer.getFPGATimestamp();
		if (joystickLeft.getRawButton(SHIFTER)) {
			if ((now - latest) > period) {
				latest = now;
				shiftBtn = true;
				System.out.println("We just shifted!");
			} else {
				shiftBtn = false;
			}
		}
		
		//shifting code
		if (shiftBtn && shifted) {
			shifted = false;
		} else if (shiftBtn && !shifted) {
			shifted = true;
		}
		
		if (shifted) {
			shifter.set(true);
		} else {
			shifter.set(false);
		}
		
	}
	
	

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
		
		double y = joystickLeft.getY();
		double dead = .15;
		double scaled = YdeadbandScaler(joystickLeft, dead);
		SmartDashboard.putNumber("origY", y);
		SmartDashboard.putNumber("scaledY", scaled);
		
		
	}
}
