package org.usfirst.frc.team1786.robot;



public final class RobotConstants {

	//***Leave this as the top Constant***
	// running on the test robot? Test robot only has for motors production has 6 for drive train
	final static boolean TESTBOT = true;
	
	//Select witch drive code to use
	// this should be switched to an mChooser on smart dashboard
	enum DriveType
	{
		WROBLE_DRIVE, WROBLE_DRIVE_TURN_INVERTED, ARCADE_DRIVE_SQUARED, ARCADE_DRIVE, CURVATURE_DRIVE_SQUARED
	}
	
	final static DriveType myDriveSystem = DriveType.ARCADE_DRIVE_SQUARED;
	
	// Default Autonomous drive action constants
	final static double defaultAutonomousDriveSeconds = 3;
	final static double defaultAutonomousDriveSpeed = .5;
	
	// throttle and turn speed for autonomous controls
	final static double autoTurnSpeed = 1;
	final static double autoDriveSpeed = 1;
	
	// Autonomous Elevator action constants
	final static double autoTimeToSwitchHeight = 1.5;
	final static double autoTimeToScaleHeight = 3;
	final static double autoSpeedForElevator = .5;
	
	// Autonomous Arm action constants
	final static double autoSpeedForArm = .5;
	final static double autoTimeForArm = 3;
	
	// joystick dead bands
	final static double armDeadband = .25;
	final static double elevatorDeadband = .15;
	final static double deadband = .15; //defines the drive dead zone
	
	// Current limiting
	final static int driveMaxPeakAmp = 50; //defines the max amp that can be given to a motor during its peak
	final static int driveMaxCountAmp = 30; //defines the max amp that can be given to a motor after its peak
	final static int drivePeakTimeDuration = 4000; //defines how long the peak will last in milliseconds (4 seconds)
	
	// Talon numbers
	// implement so that we can quickly change which talon number is assigned to a talon
	// Spare talons should be pre-configured with different numbers than what is being used on the robot
	final static int numTalonL1 = 1;
	final static int numTalonL2 = 2;
	//final static int numTalonL3 = 3; //use to test robot
	//final static int numTalonR1 = 4; //use to test robot
	//final static int numTalonR2 = 5; //use to test robot
	final static int numTalonL3 = 5; //use to live robot
	final static int numTalonR1 = 3; //use to live robot
	final static int numTalonR2 = 4; //use to live robot
	final static int numTalonR3 = 6;
	final static int numRightArmTalon = 7;
	final static int numLeftArmTalon = 8;
	final static int numElevatorTalon1 = 9;
	
}
