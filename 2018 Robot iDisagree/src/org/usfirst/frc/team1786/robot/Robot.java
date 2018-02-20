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

import org.usfirst.frc.team1786.robot.Arm;
import org.usfirst.frc.team1786.robot.Elevator;
import org.usfirst.frc.team1786.robot.ButtonDebouncer;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.drive.*;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Compressor;

import com.ctre.phoenix.motorcontrol.can.*;


public class Robot extends IterativeRobot {

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
	}

	@Override
	public void testPeriodic() {
	}
}
