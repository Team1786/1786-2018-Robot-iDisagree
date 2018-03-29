package org.usfirst.frc.team1786.robot;

import static org.usfirst.frc.team1786.robot.RobotConstants.*;
import static org.usfirst.frc.team1786.robot.RobotUtilities.*;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Elevator {
	
	WPI_TalonSRX elevatorTalon1 = new WPI_TalonSRX(numElevatorTalon1);

	// Timer used in autonomous
	private Timer timer = new Timer();
	
	public Elevator() {
		
	}
	
	/**
	 * move the elevator directly with axis input
	 * @param y - [-1,1] ranged input
	 */
	public void go(double y)
	{
		elevatorTalon1.set(deadbandScaled(-y, elevatorDeadband));
	}

	// this might be able to be modified so that it can be used with a button during teleop time.
	public int autonomousRaiseToSwitch(int order, int autoOrder)
	{
		if (order == autoOrder) {
			if(timer.get() == 0){// this might not work may need to find a different method of starting the timer probably use a boolean to track if it was started.
				timer.start();
			}
			if(timer.get() < autoTimeToSwitchHeight) {
				elevatorTalon1.set(-autoSpeedForElevator);
			}
			else {
				elevatorTalon1.set(0);
				autoOrder++;
				timer.stop();
				timer.reset();
			}
		}
			
		return autoOrder;
	}
	
	public int autonomousRaiseToScale(int order, int autoOrder)
	{
		if (order == autoOrder) {
			if(timer.get() == 0){// this might not work may need to find a different method of starting the timer probably use a boolean to track if it was started.
				timer.start();
			}
			if(timer.get() < autoTimeToScaleHeight) {
				elevatorTalon1.set(autoSpeedForElevator);
			}
			else {
				elevatorTalon1.set(0);
				autoOrder++;
				timer.stop();
				timer.reset();
			}
		}
		return autoOrder;
	}
		
	// for testing on test robot
	//speed is set low because we have no limit switches on the test robot elevator
	public int autonomousRaiseToSwitch(int order, int autoOrder, double timeRun)
	{
		if (order == autoOrder) {
			if(timer.get() == 0){// this might not work may need to find a different method of starting the timer probably use a boolean to track if it was started.
				timer.start();
			}
			if(timer.get() < timeRun) {
				elevatorTalon1.set(.1);
			}
			else {
				elevatorTalon1.set(0);
				autoOrder++;
				timer.stop();
				timer.reset();
			}
		}
			
		return autoOrder;
	}
	
	public int autonomousRaiseToScale(int order, int autoOrder, double timeRun)
	{
		if (order == autoOrder) {
				
			if(timer.get() == 0){// this might not work may need to find a different method of starting the timer probably use a boolean to track if it was started.
				timer.start();
			}
				
			if(timer.get() < timeRun) {
				elevatorTalon1.set(.1);
			}
			else {
				elevatorTalon1.set(0);
				autoOrder++;
				timer.stop();
				timer.reset();
			}
		}	
		return autoOrder;
	}
		
} //elevator class end
