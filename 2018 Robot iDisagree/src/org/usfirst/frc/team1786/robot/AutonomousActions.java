package org.usfirst.frc.team1786.robot;

import static org.usfirst.frc.team1786.robot.RobotConstants.*;
import org.usfirst.frc.team1786.robot.DriveTrain;
import org.usfirst.frc.team1786.robot.Elevator;
import org.usfirst.frc.team1786.robot.Arm;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class  AutonomousActions {
	
	private Timer timer = new Timer();//timer for tracking seconds since the start of autonomous
	String gameData;
	DriveTrain myDriveTrain;
	Arm myArm;
	Elevator myElevator;
	
	//Autonomous Variables
	int autoOrder = 1; // keeps track of what action within a command we are on. 
	
	public AutonomousActions(DriveTrain aDriveTrain, Arm anArm, Elevator anElevator){
		myDriveTrain = aDriveTrain;
		myArm = anArm;
		myElevator = anElevator;
		
	}
	
	
	// start the timer and get the game data 
	public void autonomousInit(String data)
	{
		timer.start();
		gameData = data;
		myDriveTrain.resetSensors();
	}
	
	// go to switch from center
	public void action1()
	{
		if (gameData.charAt(0) == 'L') // left side is our switch
		{
//			DropPickup();
			autoOrder = myDriveTrain.autonomousMove(24.0, 1, autoOrder);
			autoOrder = myArm.autonomousGetCube(2, autoOrder);
			autoOrder = myDriveTrain.autonomousMove(36.0, 3, autoOrder);
			autoOrder = myElevator.autonomousRaiseToSwitch(4, autoOrder);
			autoOrder = myDriveTrain.autonomousTurn(-90.0f, 5, autoOrder);
			autoOrder = myDriveTrain.autonomousMove(72.0, 6, autoOrder);
			autoOrder = myDriveTrain.autonomousTurn(90.0f, 7, autoOrder);
			autoOrder = myDriveTrain.autonomousMove(80, 8, autoOrder);
			autoOrder = myArm.autonomousDepositeCube(9, autoOrder); 
			// attempt to line us up to get another cube??? Would need vision processing to get another cube
			
		} 
		else // right side is our switch
		{
//			DropPickup();
			autoOrder = myDriveTrain.autonomousMove(24.0, 1, autoOrder);
			autoOrder = myArm.autonomousGetCube(2, autoOrder);
			autoOrder = myDriveTrain.autonomousMove(36.0, 3, autoOrder);
			autoOrder = myElevator.autonomousRaiseToSwitch(4, autoOrder);
			autoOrder = myDriveTrain.autonomousTurn(90.0f, 5, autoOrder);
			autoOrder = myDriveTrain.autonomousMove(96.0, 6, autoOrder);
			autoOrder = myDriveTrain.autonomousTurn(-90.0f, 7, autoOrder);
			autoOrder = myDriveTrain.autonomousMove(80, 8, autoOrder);
			autoOrder = myArm.autonomousDepositeCube(9, autoOrder); 
			// attempt to line us up to get another cube??? Would need vision processing to get another cube
			
		}
	}
	//action 2 is currently set for testing. only moves 24 inches
	public void action2()
	{
		if (gameData.charAt(0) == 'L') // left side is our switch
		{
//			DropPickup();
//			autoOrder = myDriveTrain.autonomousMove(24, 1, autoOrder, distanceInches);
//			autoOrder = myDriveTrain.autonomousTurn(90, 2, autoOrder, rawNavxData);
//			autoOrder = myDriveTrain.autonomousMove(24, 3, autoOrder, distanceInches);
//			autoOrder = myElevator.autonomousRaiseToSwitch(6, autoOrder, .1);
//			autoOrder = myArm.autonomousDepositeCube(7, autoOrder); 
			// attempt to line us up to get another cube??? Would need vision processing to get another cube
			
		} 
		else // right side is our switch 
		{
//			DropPickup();
//			autoOrder = myDriveTrain.autonomousMove(24, 1, autoOrder, distanceInches);
//			autoOrder = myDriveTrain.autonomousTurn(-90, 2, autoOrder, rawNavxData);
//			autoOrder = myDriveTrain.autonomousMove(24, 3, autoOrder, distanceInches);
//			autoOrder = myElevator.autonomousRaiseToSwitch(4, autoOrder, .1);
//			autoOrder = myArm.autonomousDepositeCube(5, autoOrder); 
			// attempt to line us up to get another cube??? Would need vision processing to get another cube
		}
	}
	
	// action3 is currently set for testing. only moves 36 inches
	public void action3()
	{
		if (gameData.charAt(1) == 'L') // left side is our scale
		{
//			DropPickup();
//			autoOrder = myDriveTrain.autonomousMove(36, 1, autoOrder, distanceInches);
//			autoOrder = myDriveTrain.autonomousTurn(90, 2, autoOrder, rawNavxData);
//			autoOrder = myDriveTrain.autonomousMove(36, 3, autoOrder, distanceInches);
//			autoOrder = myElevator.autonomousRaiseToScale(4, autoOrder, .25);
//			autoOrder = myArm.autonomousDepositeCube(5, autoOrder); 
			// attempt to line us up to get another cube??? Would need vision processing to get another cube
			
		} 
		else // right side is our scale
		{
//			DropPickup();
//			autoOrder = myDriveTrain.autonomousMove(36, 1, autoOrder, distanceInches);
//			autoOrder = myDriveTrain.autonomousTurn(-90, 2, autoOrder, rawNavxData);
//			autoOrder = myDriveTrain.autonomousMove(36, 3, autoOrder, distanceInches);
//			autoOrder = myElevator.autonomousRaiseToScale(4, autoOrder, .25);
//			autoOrder = myArm.autonomousDepositeCube(5, autoOrder); 
			// attempt to line us up to get another cube??? Would need vision processing to get another cube
			
		}
	}
	
	// alternate baseline code
	public void action4()
	{
		autoOrder = myDriveTrain.autonomousMove(102.0, 1, autoOrder);
	}
	
	public void actionDefault()
	{
		//drive forward at half speed for 3 seconds or what ever it is set to in RobotConstants
		if (timer.get() < defaultAutonomousDriveSeconds) {
			myDriveTrain.go(defaultAutonomousDriveSpeed, 0, 0);
		} else {
			myDriveTrain.go(0, 0, 0);
		}
	}

//	private void DropPickup() {
//		// drop pickup
//	}
	
	public double trackEncoder() {
		// 4096 = a rotation
		// a rotation = 4pi inches moved

		double rawEncoderData = myDriveTrain.rightTalonEncoderData();
		double rotations = rawEncoderData / 4096;
		double distanceInches = 18.84955 * rotations;
		
		SmartDashboard.putNumber("Rotations: ", rotations);
		SmartDashboard.putNumber("Distance in Inches: ", distanceInches);
		SmartDashboard.putNumber("Raw Encoder Output: ", rawEncoderData);
		
		return distanceInches;

	}
	
//	public double trackNavx() {
//		// do meme here
//		rawNavxData = myDriveTrain.getNavXAngle();
//		if (rawNavxData > 180) {
//			rawNavxData = (360 - rawNavxData) * -1;
//		}
//		SmartDashboard.putNumber("NAVX", rawNavxData);
//		return rawNavxData;
//
//	}
	
}
