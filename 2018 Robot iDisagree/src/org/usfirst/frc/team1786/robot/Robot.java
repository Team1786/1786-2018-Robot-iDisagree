/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

/*----------------------------------------------------------------------------*/
/* Everyones code combined into a single file                                 */
/* Individuals should add more comments to sections of code they wrote        */
/* because they were seriously lacking in their individual sections!          */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team1786.robot;

//import org.usfirst.frc.team1786.robot.RobotUtilities;
import static org.usfirst.frc.team1786.robot.RobotConstants.*;
import static org.usfirst.frc.team1786.robot.RobotUtilities.deadbandScaled;

import org.usfirst.frc.team1786.robot.DriveTrain;
import org.usfirst.frc.team1786.robot.Arm;
import org.usfirst.frc.team1786.robot.Elevator;
import org.usfirst.frc.team1786.robot.AutonomousActions;
import org.usfirst.frc.team1786.robot.ButtonDebouncer;


import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.CameraServer;
//import edu.wpi.first.wpilibj.drive.*;
//import edu.wpi.first.wpilibj.Solenoid;

//import com.ctre.phoenix.motorcontrol.can.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends IterativeRobot {

	// Autonomous actions
	private static final String command1 = "Switch Position 1";
	private static final String command2 = "Switch Position 2";
	private static final String command3 = "Scale Position 3";
	private static final String command4 = "Move Forward";//defualt acation

	private String m_autoSelected;

	private SendableChooser<String> m_chooser = new SendableChooser<>();
	
	public String gameData; // used to get game data information at the start of a match
	
	//controllers for the robot
	Joystick joystickLeft = new Joystick(0);
	Joystick joystickRight = new Joystick(1);
	ButtonDebouncer shiftBtn = new ButtonDebouncer(joystickLeft, 5, 0.5);
	
	Compressor compressor1;// = new Compressor(0);
	
	// create robot systems
	//private RobotUtilities myRobotUtilities = new RobotUtilities();
	private DriveTrain myDriveTrain = new DriveTrain();//drive train for the robot
	private Arm myArm = new Arm();
	private Elevator myElevator = new Elevator();
	private AutonomousActions myAutonomousActions = new AutonomousActions(myDriveTrain, myArm, myElevator);
	
	//for use in testPeriodic only for testing autonomous actions 1 at a time
	int autoOrder = 1;
	boolean timerDelayed = false;
	
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		
		// add autonomous options
		m_chooser.addObject(command1, command1);// Switch Position 1
		m_chooser.addObject(command2, command2);// Switch Position 2
		m_chooser.addObject(command3, command3);// Scale Position 3
		m_chooser.addObject(command4, command4);// drive forward
		
		//initialize systems
		myDriveTrain.init();
		// if we add invert and follow code to arms we will need an init for that as well

		// initialize and turn the compressor and camera on if we are not on the test robot
		if(!TESTBOT){
			compressor1 = new Compressor(0);
			compressor1.setClosedLoopControl(true);
			
			// add the camera to the dashboard
			CameraServer myCameraServer = CameraServer.getInstance();
			myCameraServer.addAxisCamera("main", "10.17.86.209");
		}
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
		
		// Get what autonomous action was selected by the team captain
		m_autoSelected = m_chooser.getSelected();
		
		// autoSelected = SmartDashboard.getString("Auto Selector", defaultAuto);
		System.out.println("Auto selected: " + m_autoSelected);
		
		// get the game data so we know where our switch and scale is
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		
		// start the autonomous timer and send it the game data
		myAutonomousActions.autonomousInit(gameData);
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		myAutonomousActions.action1();
		/*switch (m_autoSelected) {
			case command1:
				myAutonomousActions.action1();
				break;
			case command2:
				myAutonomousActions.action2();
				break;
			case command3:
				myAutonomousActions.action3();
				break;
			case command4:
				myAutonomousActions.actionDefault();
				break;
			default:
				// Put default auto code here
				myAutonomousActions.actionDefault();
				break;
		}
		
		//check to see how far we have moved or turned
		myAutonomousActions.trackEncoder();
		myAutonomousActions.trackNavx();
		*/

	}

	@Override
	public void teleopInit() {
		// low gear by default
		myDriveTrain.shifted = false;
		// can shift by default
		myDriveTrain.shiftable = true;
	}
	
	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		
		//handle driving
		myDriveTrain.go(-joystickLeft.getY(), joystickLeft.getX(), joystickLeft.getZ());
		
		//handle elevator
		myElevator.go(-joystickRight.getY());
		
		//handle arm
		myArm.go(joystickRight.getThrottle());
		
		//switch gears
		myDriveTrain.shiftToggle(shiftBtn.get());
		
		//any buttons for elevator and arm presets
		
	}

	@Override
	public void testInit() {
	}
	
	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
		 
//		if(!timerDelayed)
//		{
//			myAutonomousActions.trackEncoder();
//			myDriveTrain.leftTalonPulse();
//			Timer.delay(10);
//			myDriveTrain.resetSensors();
//			myDriveTrain.leftTalonPulse();
//			Timer.delay(10);
//			timerDelayed = true;
//		}
//		myAutonomousActions.action1();
		// test the autonomous move code
//		autoOrder = myDriveTrain.autonomousMove(24, 1, autoOrder, myAutonomousActions.trackEncoder());
		// test autonomous turn code
		//autoOrder = myDriveTrain.autonomousTurn(90, 1, autoOrder, myAutonomousActions.trackNavx());
		// test autonomous elevator code
//		autoOrder = myElevator.autonomousRaiseToScale(2, autoOrder, 5);
//		autoOrder = myElevator.autonomousRaiseToSwitch(4, autoOrder, 2);
		// test autonomous arm code
//		autoOrder = myArm.autonomousDepositeCube(3, autoOrder);
		
		// track Encoder and Navx just so smartdash is updated.
//		myDriveTrain.leftTalonPulse();
//		myAutonomousActions.trackEncoder();
//		myAutonomousActions.trackNavx();
	}
	
}
