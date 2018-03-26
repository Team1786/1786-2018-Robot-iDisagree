/*
 * Drive train system for team 1786 2018 robot
 */

package org.usfirst.frc.team1786.robot;

//import org.usfirst.frc.team1786.robot.RobotConstants;
import static org.usfirst.frc.team1786.robot.RobotConstants.*;
import static org.usfirst.frc.team1786.robot.RobotUtilities.*;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
//import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
//import edu.wpi.first.wpilibj.DriverStation;
//import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;

public class DriveTrain {
	
	//Wroble Drive variables
	// for smart dashboard
	boolean isTurning;
	boolean isSteering;
	final double masterThrottle = 1;
	final double refreshPeriod = 0.5;
	final int interval = 9;
	
	// drive train talons
	WPI_TalonSRX talonL1 = new WPI_TalonSRX(numTalonL1);
	WPI_TalonSRX talonL2 = new WPI_TalonSRX(numTalonL2);
	WPI_TalonSRX talonL3 = new WPI_TalonSRX(numTalonL3);
	WPI_TalonSRX talonR1 = new WPI_TalonSRX(numTalonR1);
	WPI_TalonSRX talonR2 = new WPI_TalonSRX(numTalonR2);
	WPI_TalonSRX talonR3 = new WPI_TalonSRX(numTalonR3);
	
	//for shifting gears
	Solenoid solenoid1;// = new Solenoid(0);
	
	DifferentialDrive myRobot = new DifferentialDrive(talonL1, talonR1);
	
	// NavX MXP 
	AHRS navx = new AHRS(SPI.Port.kMXP);
	
	// throttle and turn speed for teleop controls
	double throttle = 0;
	double turn = 0;
	// current speed being sent use to limit throttle increases
	double sendSpeed = 0;
	
	// turn speed for autonomous controls
	double turnSpeed = autoTurnSpeed;
	
	// something for turning in autonomous
	int iTurn = 1;
	
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
		resetSensors();
		
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
	
	public void go(double y, double x, double z)
	{
		throttle = y;
		turn = z;
		
		// apply curve to joystick input to give better control at lower speed
		throttle = exponentialModify(throttle);
		turn = exponentialModify(turn);
		
		// limit how fast we can attempt to accelerate
		throttle = throttleSpeedIncrease(throttle);
				
		switch(myDriveSystem) {
			case ARCADE_DRIVE_SQUARED:
				// Aracade drive
				myRobot.arcadeDrive(-throttle, turn);
				break;
			case ARCADE_DRIVE:
				// Aracade drive, don't square inputs
				myRobot.arcadeDrive(-throttle, turn, false);
				break;
			case CURVATURE_DRIVE_SQUARED:
				//Curvature Drive
				myRobot.curvatureDrive(-throttle, turn, true);
				break;
			// Wroble drive with x turn
			case WROBLE_DRIVE:
				//wroble drive
				wrobleDrive(x, -throttle, z);
				break;
			// Wroble drive with z turn
			case WROBLE_DRIVE_TURN_INVERTED:
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
	
	public int autonomousMove(double distance, int order, int autoOrder, double distanceInches) {

		//check to see what command we are on. If they don't match, do nothing. 
		if (order == autoOrder) {
			
			// some sort of error correction code should go here encase each side is moving at a different speed.
			talonL1.set(autoDriveSpeed);
			talonR1.set(-autoDriveSpeed);
			if (distanceInches >= distance) {
				talonL1.set(0);
				talonR1.set(0);
				autoOrder++;//we are done, increment autoOrder so that the next command can run. 
				navx.zeroYaw(); //set yaw to zero so we can begin our turn
			}
		}
		return autoOrder;
	}
	

	
	public int autonomousTurn(double direction, int order, int autoOrder, double rawNavxData) {
		if (order == autoOrder) {
			
			// if direction is negative we turn in the oposite direction
			if (direction < 0) {
				direction *= -1;
				turnSpeed *= -1;
			}

			talonL1.set(-turnSpeed);
			talonR1.set(turnSpeed);

			// checks to see if it should slow down
			if (rawNavxData > ((direction / interval) * iTurn)) {
				iTurn++;
				// error += rawNavxData - ((direction/interval)*i);
				double scale = ((direction / interval) * iTurn) / rawNavxData;
				turnSpeed *= scale;
				if (iTurn == interval) {
					talonL1.set(0);
					talonR1.set(0);

					turnSpeed = 1;
					iTurn = 1;
					autoOrder++;
					talonL1.getSensorCollection().setPulseWidthPosition(0, 100000);

				}
			}
		}
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
		SmartDashboard.putNumber("Left Position: ",talonL1.getSensorCollection().getPulseWidthPosition() );
	}
	
	public double rightTalonEncoderData()
	{
		return talonR1.getSensorCollection().getPulseWidthPosition();
	}
	
	public double getNavXAngle()
	{
		return navx.getAngle();
	}
	
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
	

}
