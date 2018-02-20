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

Master Branch

*/

package org.usfirst.frc.team1786.robot;

import org.usfirst.frc.team1786.robot.RobotUtilities;
import org.usfirst.frc.team1786.robot.ButtonDebouncer;

import org.usfirst.frc.team1786.robot.Arm;
import org.usfirst.frc.team1786.robot.Elevator;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.drive.*;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Compressor;

import com.ctre.phoenix.motorcontrol.can.*;


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

	Joystick joystickLeft = new Joystick(0);
	Joystick joystickRight = new Joystick(1);
		
	// robot modules
	DifferentialDrive drivetrain = new DifferentialDrive(talonL1, talonR4);
	Arm arm = new Arm(leftArmTalon, rightArmTalon, 0.2);
	Elevator elevator = new Elevator(elevatorTalon1, 0.2);
	
	ButtonDebouncer shiftBtn = new ButtonDebouncer(joystickLeft, SHIFTER, 0.5);
	
	Compressor compressor = new Compressor();
	Solenoid shifter = new Solenoid(0);
	
	private int maxPeakAmp = 60; //defines the max amp that can be given to a moter during its peak
	private int maxCountAmp = 40; //defines the max amp that can be given to a moter after its peak
	private int peakTimeDuration = 10000; //defines how long the peak will last in milliseconds	
	
	boolean shifted;
	
	@Override
	public void robotInit() {
	}

	@Override
	public void autonomousInit() {
	}

	@Override
	public void autonomousPeriodic() {
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
		
		double yValueLeftScaled = RobotUtilities.deadbandScaled(yValueLeft, 0.15);
		double zValueLeftScaled = RobotUtilities.deadbandScaled(yValueLeft, 0.15);
		
		// run the moudles
		drivetrain.arcadeDrive(yValueLeftScaled, zValueLeftScaled);
		
		arm.driveArm(yValueRight, true);
		
		elevator.driveElevator(throttleValueLeft, true);
		
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
