package org.usfirst.frc.team1786.robot;

import edu.wpi.first.wpilibj.livewindow.LiveWindow;

public final class RobotConstants {

	//***Leave this as the top Constant***
	/** running on the test robot? Test robot only has for motors, production has 6 for drive train */
	final static boolean TESTBOT = false;
	
	//Select witch drive code to use
	// this should be switched to an mChooser on smart dashboard
	enum DriveType
	{
		WROBLE_DRIVE, WROBLE_DRIVE_TURN_INVERTED, ARCADE_DRIVE_SQUARED, ARCADE_DRIVE, CURVATURE_DRIVE_SQUARED
	}
	
	/** the current drive system being used */
	final static DriveType myDriveSystem = DriveType.ARCADE_DRIVE_SQUARED;
	
	// Default Autonomous drive action constants
	final static double defaultAutonomousDriveSeconds = 3;
	final static double defaultAutonomousDriveSpeed = .5;
	
	/** throttle for autonomous controls */
	final static double autoTurnSpeed = 1;
	
	/** turn speed for autonomous controls */
	final static double autoDriveSpeed = 1;
	
	// Autonomous Elevator action constants
	final static double autoTimeToSwitchHeight = 4;
	final static double autoTimeToScaleHeight = 8;
	final static double autoSpeedForElevator = .5;
	
	// Autonomous Arm action constants
	final static double autoSpeedForArm = .7;
	final static double autoTimeForArm = 3;
	
	// joystick dead bands
	final static double armDeadband = .5;
	final static double elevatorDeadband = .15;
	
	/** deadzone for the drivetrain */
	final static double deadband = .15;
	
	// Current limiting
	/** defines the max amp that can be given to a motor during its peak */
	final static int driveMaxPeakAmp = 50;
	
	/** defines the max amp that can be given to a motor after its peak */
	final static int driveMaxCountAmp = 30;
	
	/** defines how long the peak will last in milliseconds (4 seconds) */
	final static int drivePeakTimeDuration = 4000;
	
	// Talon numbers
	// implement so that we can quickly change which talon number is assigned to a talon
	// Spare talons should be pre-configured with different numbers than what is being used on the robot
	final static int numTalonL1 = 1;
	final static int numTalonL2 = 2;
	final static int numTalonL3 = 3; //use to test robot
	final static int numTalonR1 = 4; //use to test robot
	final static int numTalonR2 = 5; //use to test robot
//	final static int numTalonL3 = 5; //use to live robot
//	final static int numTalonR1 = 3; //use to live robot
//	final static int numTalonR2 = 4; //use to live robot
	final static int numTalonR3 = 6;
	final static int numRightArmTalon = 9;
	final static int numLeftArmTalon = 8;
	final static int numElevatorTalon1 = 7;
	
	// used to tune autonomous turning routine
    static final double kTurnP = 0.15; // tune until you have a little oscillation (TEST RBT: 0.08)
    static final double kTurnI = 0.00; // tune if the system consistently over or under shoots setpoint (TEST RBT: 0.00)
    static final double kTurnD = 0.05; // dampen the oscillation a bit(TEST RBT: 0.07)
    static final double kTurnF = 0.00; // (TEST RBT: 0.00)
    
	// used to tune autonomous moving routine
    static final double kMoveP = 0.15; // tune until you have a little oscillation
    static final double kMoveI = 0.00; // tune if the system consistently over or under shoots setpoint
    static final double kMoveD = 0.00; // dampen the oscillation a bit
    static final double kMoveF = 0.00; // 
    
    static final double kMoveToleranceDegrees = 2.0f;
    static final double kTurnToleranceDegrees = 2.0f;
}
