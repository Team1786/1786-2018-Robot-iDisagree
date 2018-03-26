package org.usfirst.frc.team1786.robot;

import static org.usfirst.frc.team1786.robot.RobotUtilities.*;
import static org.usfirst.frc.team1786.robot.RobotConstants.*;

import edu.wpi.first.wpilibj.Timer;

import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj.Joystick;

import static org.usfirst.frc.team1786.robot.RobotConstants.autoSpeedForElevator;
import static org.usfirst.frc.team1786.robot.RobotConstants.autoTimeToSwitchHeight;

import java.lang.Math;

/*
 * A class for controlling team 1786's power-cube holding arms.
 */
public class Arm {

	WPI_TalonSRX rightArmTalon = new WPI_TalonSRX(7);
	WPI_TalonSRX leftArmTalon = new WPI_TalonSRX(8);
	
	// Timer used in autonomous
	private Timer timer = new Timer();

	/**
	 * Constructor for a robotic arm with driven wheels
	 * @param leftArmController - Motor controller for use in driving the left arm
	 * @param rightArmController - Motor controller for use in driving the right arm
	 * @param armDeadband - desired deadband radius for arm control
	 */
	public Arm() {
		
	}
	
	public void go(double speed)
	{
		// should probably use follow and invert but this is fine for now
		rightArmTalon.set(deadbandScaled(speed, armDeadband));
		leftArmTalon.set(-deadbandScaled(speed, armDeadband));
	}
	
	public int autonomousDepositeCube(int order, int autoOrder)
	{
		if (order == autoOrder) {
			if(timer.get() == 0){// this might not work may need to find a different method of starting the timer probably use a boolean to track if it was started.
				timer.start();
			}
			if(timer.get() < autoTimeForArm) {
				//think these are going in the correct direction...
				rightArmTalon.set(-autoSpeedForArm);
				leftArmTalon.set(autoSpeedForArm);
			}
			else {
				rightArmTalon.set(0);
				leftArmTalon.set(0);
				autoOrder++;
				timer.stop();
				timer.reset();
			}
		}
		
		return autoOrder;
	}
	
}
