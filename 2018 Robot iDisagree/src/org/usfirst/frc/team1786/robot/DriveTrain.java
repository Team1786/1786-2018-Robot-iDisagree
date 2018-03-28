/*
 * Drive train system for team 1786 2018 robot
 */

package org.usfirst.frc.team1786.robot;

//import org.usfirst.frc.team1786.robot.RobotConstants;
import static org.usfirst.frc.team1786.robot.RobotConstants.*;
import static org.usfirst.frc.team1786.robot.RobotUtilities.*;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
//import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
//import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;

public class DriveTrain implements PIDOutput{
	
	//Wroble Drive variables
	// for smart dashboard
	boolean isTurning;
	boolean isSteering;
	final double masterThrottle = 1;
	final double refreshPeriod = 0.5;
	final int interval = 9;
	
	// boolean for tracking shifted state
	boolean shifted;
	boolean shiftable;
	
	// drive train talons
	WPI_TalonSRX talonL1 = new WPI_TalonSRX(numTalonL1);
	WPI_TalonSRX talonL2 = new WPI_TalonSRX(numTalonL2);
	WPI_TalonSRX talonL3 = new WPI_TalonSRX(numTalonL3);
	WPI_TalonSRX talonR1 = new WPI_TalonSRX(numTalonR1);
	WPI_TalonSRX talonR2 = new WPI_TalonSRX(numTalonR2);
	WPI_TalonSRX talonR3 = new WPI_TalonSRX(numTalonR3);
	
	/**
	 * solenoid used for drivetrain gear shifting
	 */
	Solenoid solenoid1;// = new Solenoid(0);

	DifferentialDrive myRobot = new DifferentialDrive(talonL1, talonR1);
		
	// NavX MXP 
	AHRS navx;
	PIDController turnController;
	double rotateToAngleRate;
	
	// throttle and turn speed for teleop controls
	double throttle = 0;
	double turn = 0;
	// current speed being sent use to limit throttle increases
	double sendSpeed = 0;
	
	public DriveTrain()
	{
		/* this code causes the robot to crash...
		// check to see if we are running it on our test robot or our production robot
		// Test robot only has 4 drive talons while the production robot has 6 drive talons
		if(TESTBOT)
		{
			// Assign talon IDs for the test robot
			talonL1 = new WPI_TalonSRX(1);
			talonL2 = new WPI_TalonSRX(2);
			talonR1 = new WPI_TalonSRX(3);
			talonR2 = new WPI_TalonSRX(4);
		}
		else
		{
			// Assign talon IDs for the production robot
			talonL1 = new WPI_TalonSRX(1);
			talonL2 = new WPI_TalonSRX(2);
			talonL3 = new WPI_TalonSRX(3);
			talonR1 = new WPI_TalonSRX(4);
			talonR2 = new WPI_TalonSRX(5);
			talonR3 = new WPI_TalonSRX(6);
		}
		*/
	}
	
	public void init()
	{
		// check to see if we are running it on our test robot or our production robot
		// Test robot only has 4 drive talons while the production robot has 6 drive talons
		if(TESTBOT)
		{
			initTestRobot();
		}
		else
		{
			initProductionRobot();
		}
		
		// enable motor safety by default
		myRobot.setSafetyEnabled(true);
		
		//set up turning auto objects
		navx = new AHRS(SPI.Port.kMXP);
		navx.reset();
		turnController = new PIDController(kTurnP,kTurnI,kTurnD,kTurnF,navx, this);
		turnController.setInputRange(-180.0f, 180.0f);
		turnController.setOutputRange(-1.0, 1.0);
		turnController.setAbsoluteTolerance(kTurnToleranceDegrees);
		turnController.setContinuous(true);
		turnController.disable(); // Don't forget this!!
		
		// set min and peak output values for the move forward auto pid loop
		talonR1.configNominalOutputForward(0, 0);
		talonR1.configNominalOutputReverse(0, 0);
		talonR1.configPeakOutputForward(1, 0);
		talonR1.configPeakOutputReverse(-1, 0);
		
		talonR1.configAllowableClosedloopError(0, 0, 0);
		
		talonR1.config_kP(0, kMoveP, 0);
		talonR1.config_kI(0, kMoveI, 0);
		talonR1.config_kD(0, kMoveD, 0);
		talonR1.config_kF(0, kMoveF, 0);
		
		resetSensors();
		/*
		 * lets grab the 360 degree position of the MagEncoder's absolute
		 * position, and intitally set the relative sensor to match.
		 */
		int absolutePosition = talonR1.getSensorCollection().getPulseWidthPosition();
		/* mask out overflows, keep bottom 12 bits */
		absolutePosition &= 0xFFF;
		/* set the quadrature (relative) sensor to match absolute */
		talonR1.setSelectedSensorPosition(absolutePosition, 0, 0);
		// setup and zero out encoders, etc.	
		
	}
	
	private void initTestRobot()
	{
		//tells the following talons to follow their leading talons
		talonL2.follow(talonL1); 
		talonR2.follow(talonR1);
		
		
		RobotUtilities.currentLimiting(talonL1,driveMaxPeakAmp,driveMaxCountAmp,drivePeakTimeDuration);
		RobotUtilities.currentLimiting(talonL2,driveMaxPeakAmp,driveMaxCountAmp,drivePeakTimeDuration);
		RobotUtilities.currentLimiting(talonR1,driveMaxPeakAmp,driveMaxCountAmp,drivePeakTimeDuration);
		RobotUtilities.currentLimiting(talonR2,driveMaxPeakAmp,driveMaxCountAmp,drivePeakTimeDuration);
	}


	private void initProductionRobot()
	{
		//tells the following talons to follow their leading talons
		talonL2.follow(talonL1); 
		talonL3.follow(talonL1);
		talonR2.follow(talonR1);
		talonR3.follow(talonR1);
				
		// top motor on each side needs to be inverted because of the way they interact with the gear box
		talonL1.setInverted(true);
		talonR1.setInverted(true);
		
		RobotUtilities.currentLimiting(talonL1,driveMaxPeakAmp,driveMaxCountAmp,drivePeakTimeDuration);
		RobotUtilities.currentLimiting(talonL2,driveMaxPeakAmp,driveMaxCountAmp,drivePeakTimeDuration);
		RobotUtilities.currentLimiting(talonL3,driveMaxPeakAmp,driveMaxCountAmp,drivePeakTimeDuration);
		RobotUtilities.currentLimiting(talonR1,driveMaxPeakAmp,driveMaxCountAmp,drivePeakTimeDuration);
		RobotUtilities.currentLimiting(talonR2,driveMaxPeakAmp,driveMaxCountAmp,drivePeakTimeDuration);
		RobotUtilities.currentLimiting(talonR3,driveMaxPeakAmp,driveMaxCountAmp,drivePeakTimeDuration);
		
		solenoid1 = new Solenoid(0);
		
	}
	
	/**
	 * move the drive train according to axis 
	 * @param y - [-1,1] axis
	 * @param x - [-1,1] axis
	 * @param z - [-1,1] axis
	 */
	public void go(double y, double x, double z)
	{
		double currentAngle = navx.getAngle();
		SmartDashboard.putNumber("current navx reading -- auto --", currentAngle);
		
		throttle = y;
		turn = z;
		
		// apply curve to joystick input to give better control at lower speed
		throttle = exponentialModify(throttle);
		turn = exponentialModify(turn);
		
		// limit how fast we can attempt to accelerate (NOT WORKING)
//		throttle = throttleSpeedIncrease(throttle);
		
		// force low gear when turing
		if (Math.abs(turn) > deadband) {
			solenoid1.set(false);
			shiftable = false;
		} else {
			// if we aren't turning, switch back to last used state
			shiftable = true;
			solenoid1.set(shifted);
		}
		
		switch(myDriveSystem) {
			case ARCADE_DRIVE_SQUARED:
				// Aracade drive
				myRobot.setSafetyEnabled(true);
				myRobot.arcadeDrive(-throttle, turn);
				break;
			case ARCADE_DRIVE:
				// Aracade drive, don't square inputs
				myRobot.setSafetyEnabled(true);
				myRobot.arcadeDrive(-throttle, turn, false);
				break;
			case CURVATURE_DRIVE_SQUARED:
				//Curvature Drive
				myRobot.setSafetyEnabled(true);
				myRobot.curvatureDrive(-throttle, turn, true);
				break;
			// Wroble drive with x turn
			case WROBLE_DRIVE:
				//wroble drive
				myRobot.setSafetyEnabled(false);
				wrobleDrive(x, -throttle, z);
				break;
			// Wroble drive with z turn
			case WROBLE_DRIVE_TURN_INVERTED:
				myRobot.setSafetyEnabled(false);
				wrobleDrive(z, -throttle, x);
				break;
		}
	}
	
	private void wrobleDrive(double x, double y, double z) {
		

		//SmartDashboard.putBoolean("is turning", isTurning);
		//SmartDashboard.putBoolean("isSteering", isSteering);

		if (z < -0.4 || z > 0.4) {
			// twist

			isTurning = true;
			talonL1.set(exponentialModify(z * masterThrottle));
			talonR1.set(exponentialModify(z * masterThrottle));
		} else {
			isTurning = false;
			double power = Math.sqrt((x * x) + (y * y));
			//SmartDashboard.putNumber("rawY", y);
			//SmartDashboard.putNumber("rawX", x);
			// dead zone for all non twisting movement; was = 0.2
			if (power > 0.25) {
				isSteering = true;

				if (power > 1)
					power = 1;
				// philip's modifier function
				// now controller in the Go function as to if we are doing this
				//power = exponentialModify(power);
				if (y > 0)
					power = -power;
				power *= masterThrottle;
				//SmartDashboard.putNumber("power", power);
				// getting scale
				double scale = 1 - Math.abs(x);

				if (x < 0) {
					// left

					talonL1.set(-power * scale);
					talonR1.set(power);
				} else {
					// right

					talonL1.set(-power);
					talonR1.set(power * scale);
				}

			} else {
				isSteering = false;
				talonL1.set(0);
				talonR1.set(0);
			}
		}
	}
	
	/**
	 * pass through for whether to shift or not.
	 * Will only shift if drivetrain.shiftable = true
	 * @param button - true if you want to toggle it.
	 * 				   you need to debounce this 
	 * 				   state change
	 */
	public void shiftToggle(boolean button) {
		if(shiftable) {
			if (button && shifted) {
				shifted = false;
			} else if (button && !shifted) {
				shifted = true;
			}
			
			solenoid1.set(shifted);
		}
	}
	
	/**
	 * move the robot to a given position autonomously, must be called periodically.
	 * @param distance - distance in inches you'd like to move
	 * @param order
	 * @param autoOrder
	 * @return
	 */
	public int autonomousMove(double distance, int order, int autoOrder) {		
		//check to see what command we are on. If they don't match, do nothing. 
		if (order == autoOrder) {
			// disable safety before we start
			myRobot.setSafetyEnabled(false);
			
			// get how many wheel rotations we need, using 2(pi)r formula
			double targetRotations = distance / (6 * Math.PI);
			double targetPosition = targetRotations * 4096;
			
			//active the pid loop
			talonR1.set(ControlMode.Position, targetPosition);
			
			//match the left talon to the right without the permanant follow mode
			talonL1.set(talonR1.getMotorOutputPercent());
			
			// deadzone of 200 quadrature ticks (out of total 4096 per rotation)
			if(rightTalonEncoderData() >= (targetPosition - 200) && rightTalonEncoderData() <= (targetPosition + 200)) {
				talonR1.set(ControlMode.PercentOutput, 0);
				talonL1.set(0);
				
				// exit routine and re-enable motor safety (auto turn uses motor safety)
				myRobot.setSafetyEnabled(true);
				autoOrder++;
			}
		}
		return autoOrder;
	}
	
	/**
	 * turn to a given angle autonomously, must be called periodically
	 * @param direction - [-180f,180f] the range must be that
	 * @param order
	 * @param autoOrder
	 * @return
	 */
	public int autonomousTurn(double direction, int order, int autoOrder) {
		if (order == autoOrder) {
			// init check
			if(!turnController.isEnabled()) {
				navx.reset();
				turnController.setSetpoint(direction);
				rotateToAngleRate = 0;
				turnController.enable();
			}
			double currentAngle = navx.getAngle();
			
			leftTalonPulse(); // get encoder data for fun
			// turn based on the outputed rotateToAngleRate
			// given by the turn controller
			go(0.0, 0.0, rotateToAngleRate * 0.98);
			
			//are we done yet? if so, turn it off and move on to the next action
			// check if the angle is within a tolerance, say 5 degress +-
			// allowing for a little over and under shoot
			if(currentAngle >= (direction - 2) && currentAngle <= (direction + 2)) {
				turnController.disable();
				autoOrder++;
			}
			
		} // upon action completion, increment autoOrder++
		return autoOrder;
	}
	
	public void resetSensors()
	{
		// configure encoder
		talonL1.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
		talonR1.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
		// reset encoder
		talonL1.getSensorCollection().setPulseWidthPosition(0, 100000);
		talonR1.getSensorCollection().setPulseWidthPosition(0, 100000);
		navx.zeroYaw();
	}
	
	public double leftTalonEncoderData()
	{
		return talonL1.getSensorCollection().getPulseWidthPosition();
	}
	
	public void leftTalonPulse()
	{
		SmartDashboard.putNumber("Right Position: ", talonR1.getSensorCollection().getPulseWidthPosition() );
	}
	
	public double rightTalonEncoderData()
	{
		return talonR1.getSensorCollection().getPulseWidthPosition();
	}
	
	public double getNavXAngle()
	{
		return navx.getAngle();
	}
	
	// NOT FUNCTIONAL
	private double throttleSpeedIncrease(double targetSpeed)
	{
		// amount to increase speed by. at .02 it will take 1 second to reach max speed
		double maxIncrease = .01; //at .01 it will take 2 second to reach max speed
		double extra = 0; //speed won't increase by exactly maxIncrease so lets allow slightly more by rounding up
		
		// Set targetSpeed to 3 decimal place precision
		// not the best method to use but being off by 1/1000 every so often won't matter
		targetSpeed = (double)Math.round(targetSpeed * 1000d) / 1000d;
		
		if(sendSpeed == targetSpeed)
		{
			return sendSpeed;
		}
		else if(sendSpeed < targetSpeed)
		{
			extra = targetSpeed-(((int)(targetSpeed/maxIncrease))*maxIncrease);
			// make extra increase can only be half of the maxIncrease
			if(extra > maxIncrease/2)
			{
				extra = maxIncrease/2;
			}
			sendSpeed = sendSpeed + maxIncrease + extra;
			return sendSpeed;
		}
		else //drop speed as fast as you want???
		{
			sendSpeed = targetSpeed;
			return sendSpeed;
		}
	}
	
	public void pidWrite(double output) {
		rotateToAngleRate = output;
	}
}
