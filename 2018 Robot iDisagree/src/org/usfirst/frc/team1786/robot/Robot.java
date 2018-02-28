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

import java.awt.geom.Line2D;
import java.lang.invoke.ConstantCallSite;

import org.usfirst.frc.team1786.robot.Arm;
import org.usfirst.frc.team1786.robot.Elevator;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.drive.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Timer.StaticInterface;
import edu.wpi.first.wpilibj.buttons.Button;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.*;

import com.kauailabs.navx.frc.AHRS;

public class Robot extends IterativeRobot {

	// button mapping
	final int SHIFTER = 3;
	final int ARMRELEASE = 4;
	final int REV = 1;
	final int WROBLE = 12;
	final int CURVE = 10;
	
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
	
	Double yValueLeft;
	Double xValueLeft;
	Double zValueLeft;
	Double throttleValueLeft;
	
	Double yValueRight;
	Double xValueRight;
	Double zValueRight;
	Double throttleValueRight;
	
	/* START OF PREF ITEMS */
	// setup preferences area of smartDashboard
	// variables under this heading may be changes by the dashboard
	// these values will be saved in a text file on the roborio, and are persistent from boot to boot
	Preferences prefs;
	private int maxPeakAmpDrivetrain = 60; //defines the max amp that can be given to a moter during its peak
	private int maxCountAmpDrivetrain = 40; //defines the max amp that can be given to a moter after its peak
	private int peakTimeDurationDrivetrain = 10000; //defines how long the peak will last in milliseconds	
	
	double armDeadband = 0.2;
	double elevatorDeadband = 0.2;
	/* END OF PREF ITEMS*/
		
	// robot modules
	DifferentialDrive drivetrain = new DifferentialDrive(talonL1, talonR4);
	Arm arm = new Arm(leftArmTalon, rightArmTalon, armDeadband);
	Elevator elevator = new Elevator(elevatorTalon1, elevatorDeadband);
	
	// buttons which will need debouncing for toggled use
	ButtonDebouncer shiftBtn = new ButtonDebouncer(joystickLeft, SHIFTER, 0.5);
	ButtonDebouncer armReleaseBtn = new ButtonDebouncer(joystickLeft, ARMRELEASE, 0.5);
	ButtonDebouncer wrobleDriveBtn = new ButtonDebouncer(joystickLeft, WROBLE, 0.5);
	ButtonDebouncer curveDriveBtn = new ButtonDebouncer(joystickLeft, CURVE, 0.5);
	ButtonDebouncer reverseDriveBtn = new ButtonDebouncer(joystickLeft, REV, 0.5);
	
	// pneumatics
	// note: compressor appears to use 10 amps in usage
	Compressor compressor = new Compressor();
	Solenoid shifter = new Solenoid(0);
	Solenoid armReleaser = new Solenoid(1);
	
	boolean shifted;
	boolean armReleased;
	boolean useWrobleDrive;
	boolean useCurvatureDrive;
	boolean reversed;
	
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
		
		// limit current of drive Talons
		limitTalonCurrent(talonL1, maxPeakAmpDrivetrain, peakTimeDurationDrivetrain, maxCountAmpDrivetrain);
		limitTalonCurrent(talonL2, maxPeakAmpDrivetrain, peakTimeDurationDrivetrain, maxCountAmpDrivetrain);
		limitTalonCurrent(talonL3, maxPeakAmpDrivetrain, peakTimeDurationDrivetrain, maxCountAmpDrivetrain);
		limitTalonCurrent(talonR4, maxPeakAmpDrivetrain, peakTimeDurationDrivetrain, maxCountAmpDrivetrain);
		limitTalonCurrent(talonR5, maxPeakAmpDrivetrain, peakTimeDurationDrivetrain, maxCountAmpDrivetrain);
		limitTalonCurrent(talonR6, maxPeakAmpDrivetrain, peakTimeDurationDrivetrain, maxCountAmpDrivetrain);
		
		//configure encoders for drivetrain
		// Will's implementation
		talonL1.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
		talonR4.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
		
		// configure encoders for elevator
		elevatorTalon1.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
	}

	// current limiting talons and display a single talon on the dashboard
	// phillip's logic
	private void limitTalonCurrent(WPI_TalonSRX talon, int peakLimit, int PeakDuration, int ContLimit)
	{
		
		talon.enableCurrentLimit(true);
		talon.configPeakCurrentLimit(peakLimit, 0);
		talon.configPeakCurrentDuration(PeakDuration, 0);
		talon.configContinuousCurrentLimit(ContLimit, 0);
	}
	
	private void displayTalon(WPI_TalonSRX talon, String label)
	{
		SmartDashboard.putNumber(label+" Output Current: ", talon.getOutputCurrent());
		SmartDashboard.putNumber(label+" Output Temperature: ", talon.getTemperature());
		
	}
	
	// update the smart dashboard
	public void DashboardUpdate() {
		// raw joystick data		
		SmartDashboard.putNumber("yLeft", yValueLeft);
		SmartDashboard.putNumber("xLeft", xValueLeft);
		SmartDashboard.putNumber("zLeft", zValueLeft);
		SmartDashboard.putNumber("throttleLeft", throttleValueLeft);
		SmartDashboard.putNumber("yRight", yValueRight);
		SmartDashboard.putNumber("zRight", zValueRight);
		SmartDashboard.putNumber("throttleRight", throttleValueRight);
		
		// current data on drive talons
		displayTalon(talonL1, "Talon L1");
		displayTalon(talonL2, "Talon L2");
		displayTalon(talonL3, "Talon L3");
		displayTalon(talonR4, "Talon R4");
		displayTalon(talonR5, "Talon R5");
		displayTalon(talonR6, "Talon R6");
		
		// current data on other systems
		displayTalon(rightArmTalon, "rightArmTalon");
		displayTalon(leftArmTalon, "leftArmTalon");
		displayTalon(elevatorTalon1, "elevatorTalon");

	}
	
	// Will's drive code, it uses two axes to determine the amount and type of rotation
	public void WrobleDrive(double speed, double rotation, double inPlaceRotation) {
		double y = speed;
		double x = rotation;
		double z = inPlaceRotation;
		
		SmartDashboard.putBoolean("is turning", isTurning);
		SmartDashboard.putBoolean("isSteering", isSteering);
		
		if(z < -0.4 || z > 0.4)
		{
			//twist
			
			isTurning = true;
			talonL1.set(RobotUtilities.exponentialModify(z*speed, 5));
			talonR4.set(RobotUtilities.exponentialModify(z*speed, 5));
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
				power = RobotUtilities.exponentialModify(power, 3);
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
					talonR4.set(power);
				}
				else
				{
					//right
					
					talonL1.set(-power);
					talonR4.set(power*scale);
				}
				
			}
			else
			{
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
		//default positons
		shifted = false;
		useWrobleDrive = true;
		useCurvatureDrive = false;
		reversed = false;
	}
	
	@Override
	public void teleopPeriodic() {

		// get input and scale some of it
		// code from Dylan
		yValueLeft = joystickLeft.getY();
		xValueLeft = joystickLeft.getX();
		zValueLeft = joystickLeft.getZ();
		throttleValueLeft = joystickLeft.getThrottle();
		
		yValueRight = joystickRight.getY();
		xValueRight = joystickRight.getY();
		zValueRight = joystickRight.getZ();
		throttleValueRight = joystickRight.getThrottle();
		
		// run the modules
		arm.driveArm(yValueRight, true);
		
		elevator.driveElevator(throttleValueRight, true);
		
		// drive code toggling
		boolean wrobleDriveBtnState = wrobleDriveBtn.get();
		boolean curveDriveBtnState = curveDriveBtn.get();
		boolean reverseDriveBtnState = curveDriveBtn.get();
		
		// switch between reversed driving and regular
		if (reverseDriveBtnState && reversed) {
			reversed = false;
		} else if (reverseDriveBtnState && !reversed) {
			reversed = true;
		}
		
		// switch between using wroble's drive code and the regular one
		if (wrobleDriveBtnState && useWrobleDrive) {
			useWrobleDrive = false;
		} else if (wrobleDriveBtnState && !useWrobleDrive) {
			useWrobleDrive = true;
		}
		
		// switch between using curvature drive and the regular one
		if (curveDriveBtnState && useCurvatureDrive) {
			useCurvatureDrive = false;
		} else if (curveDriveBtnState && !useCurvatureDrive) {
			useCurvatureDrive = true;
		}
		
		if (reversed) {
			//L1 and R4 are masters and need the opposition inversion of the rest
			talonL1.setInverted(false);
			talonL2.setInverted(true);
			talonL3.setInverted(true);
			
			talonR4.setInverted(false);
			talonR5.setInverted(true);
			talonR6.setInverted(true);
		} else {
			//L1 and R4 are masters and need the opposition inversion of the rest
			talonL1.setInverted(true);
			talonL2.setInverted(false);
			talonL3.setInverted(false);
			
			talonR4.setInverted(true);
			talonR5.setInverted(false);
			talonR6.setInverted(false);
		}
		
		if (useWrobleDrive) {
			// WrobleDrive doesn't use the drivetrain object
			drivetrain.setSafetyEnabled(false);
			WrobleDrive(yValueLeft, xValueLeft, zValueLeft);
		} else {
			// arcade and curvature drive use the drivetrain object
			drivetrain.setSafetyEnabled(true);
			if (useCurvatureDrive) {
				// quick turn when thumb button is held
				if (joystickRight.getRawButton(2)) {
					drivetrain.curvatureDrive(RobotUtilities.exponentialModify(RobotUtilities.deadbandScaled(-yValueLeft, 0.2), 2),
								              RobotUtilities.exponentialModify(RobotUtilities.deadbandScaled(zValueRight, 0.1), 2), true);
				} else {
					// second axis controls rate of turning
					drivetrain.curvatureDrive(RobotUtilities.exponentialModify(RobotUtilities.deadbandScaled(-yValueLeft, 0.2), 2),
											  RobotUtilities.exponentialModify(RobotUtilities.deadbandScaled(xValueRight, 0.2), 2), false);
				}
			} else {
				// arcade drive with one joystick and squared inputs
				drivetrain.arcadeDrive(RobotUtilities.deadbandScaled(-yValueLeft, 0.2),
									   RobotUtilities.deadbandScaled(zValueLeft, 0.2), true);
			}
		}
		
		// shifting code
		boolean shiftBtnState = shiftBtn.get();
		if (shiftBtnState == true && shifted) {
			shifted = false;
		} else if (shiftBtnState == true && !shifted) {
			shifted = true;
		}
		
		if(shifted == true) {
			shifter.set(true);
		} else { 
			shifter.set(false);
		}
		
		// update the dashboard
		DashboardUpdate();
	}
	
	@Override
	public void testPeriodic() {

	}
}
