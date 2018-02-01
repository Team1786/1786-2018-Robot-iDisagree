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
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.buttons.JoystickButton;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends IterativeRobot {
	private int currentLimit1 = 40;
	private int currentLimit2 = 60;
	private int currentDur = 10000;
	private int upShiftButtonNum = 6;
	private int downShiftButtonNum = 5;

	
	private static final String kDefaultAuto = "Default";
	private static final String kCustomAuto = "My Auto";
	private String m_autoSelected;
	private SendableChooser<String> m_chooser = new SendableChooser<>();

	Joystick joystickLeft = new Joystick(0);
	Joystick joystickRight = new  Joystick(1);
	JoystickButton upShiftButton = new JoystickButton(joystickLeft,upShiftButtonNum);
	JoystickButton downShiftButton = new JoystickButton(joystickLeft,downShiftButtonNum);
	
	WPI_TalonSRX talonL1 = new WPI_TalonSRX(1);
	WPI_TalonSRX talonL2 = new WPI_TalonSRX(2);
	//WPI_TalonSRX talonL3 = new WPI_TalonSRX(3);
	WPI_TalonSRX talonR4 = new WPI_TalonSRX(3);
	WPI_TalonSRX talonR5 = new WPI_TalonSRX(4);
	//WPI_TalonSRX talonR6 = new WPI_TalonSRX(6);
	
	Compressor compressor1 = new Compressor(1);
	Solenoid solenoid1 = new Solenoid(0);
	
	
	DifferentialDrive myRobot = new DifferentialDrive (talonL1, talonR4);
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		m_chooser.addDefault("Default Auto", kDefaultAuto);
		m_chooser.addObject("My Auto", kCustomAuto);
		SmartDashboard.putData("Auto choices", m_chooser);
		
		//limiting the current all the time
		talonL1.configContinuousCurrentLimit(currentLimit1,0);
		talonL2.configContinuousCurrentLimit(currentLimit1,0);
		//talonL3.configContinuousCurrentLimit(currentLimit1,0);
		talonR4.configContinuousCurrentLimit(currentLimit1,0);
		talonR5.configContinuousCurrentLimit(currentLimit1,0);
		//talonR6.configContinuousCurrentLimit(currentLimit1,0);
		
		//time when peak is active
		talonL1.configPeakCurrentDuration(currentDur,0);
		talonL2.configPeakCurrentDuration(currentDur,0);
		//talonL3.configPeakCurrentDuration(currentDur,0);
		talonR4.configPeakCurrentDuration(currentDur,0);
		talonR5.configPeakCurrentDuration(currentDur,0);
		//talonR6.configPeakCurrentDuration(currentDur,0);
		
		//only when the above is active
		talonL1.configPeakCurrentLimit(currentLimit2,0);
		talonL2.configPeakCurrentLimit(currentLimit2,0);
		//talonL3.configPeakCurrentLimit(currentLimit2,0);
		talonR4.configPeakCurrentLimit(currentLimit2,0);
		talonR5.configPeakCurrentLimit(currentLimit2,0);
		//talonR6.configPeakCurrentLimit(currentLimit2,0);
		
		//does it do what it do
		talonL1.enableCurrentLimit(true);
		talonL2.enableCurrentLimit(true);
		//talonL3.enableCurrentLimit(true);
		talonR4.enableCurrentLimit(true);
		talonR5.enableCurrentLimit(true);
		//talonR6.enableCurrentLimit(true);
		
		talonL2.follow(talonL1);
		//talonL3.follow(talonL1);
		talonR5.follow(talonR4);
		//talonR6.follow(talonR4);
	
		SmartDashboard.putString("upShiftButtonState", "false");
	
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
		SmartDashboard.putNumber("joystickY", joystickLeft.getY());
		SmartDashboard.putNumber("joystickZ", joystickLeft.getZ());
		
		//the tempature
		SmartDashboard.putNumber("talonL1Temp", talonL1.getTemperature());
		SmartDashboard.putNumber("talonL2Temp", talonL2.getTemperature());
		//SmartDashboard.putNumber("talonL3Temp", talonL3.getTemperature());
		SmartDashboard.putNumber("talonR3Temp", talonR4.getTemperature());
		SmartDashboard.putNumber("talonR5Temp", talonR5.getTemperature());
		//SmartDashboard.putNumber("talonR6Temp", talonR6.getTemperature());
		
		//the output current
		SmartDashboard.putNumber("talonL1Current", talonL1.getOutputCurrent());
		SmartDashboard.putNumber("talonL2TCurrent", talonL2.getOutputCurrent());
		//SmartDashboard.putNumber("talonL3Current", talonL3.getOutputCurrent());
		SmartDashboard.putNumber("talonR3Current", talonR4.getOutputCurrent());
		SmartDashboard.putNumber("talonR5Current", talonR5.getOutputCurrent());
		//SmartDashboard.putNumber("talonR6Current", talonR6.getOutputCurrent());
		
		
		if ( upShiftButton.get() == false ){
			
			SmartDashboard.putString("upShiftButtonState", "True");
		
			solenoid1.set(true);
		}
		if ( downShiftButton.get() == false ){
			
			solenoid1.set(false);
		}
		
		//while (compressor1.getPressureSwitchValue() == false)
		///{
			//compressor1.setClosedLoopControl(true);
			//compressor1.start();
		//}
			//compressor1.setClosedLoopControl(false);
		
		myRobot.arcadeDrive(-joystickLeft.getY(), joystickLeft.getZ(), false);
		
	
	}
	
	

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
