package org.usfirst.frc.team1786.robot;
import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj.Joystick;

public class Arm {
	double deadband;
	double wheelSpeed;
	WPI_TalonSRX leftController;
	WPI_TalonSRX rightController;
	
	/**
	 * Constructor for a robotic arm with driven wheels
	 * @param leftArmController - Motor controller for use in driving the left arm
	 * @param rightArmController - Motor controller for use in driving the right arm
	 * @param armDeadband - desired deadband radius for arm control
	 */
	public Arm(WPI_TalonSRX leftArmController, WPI_TalonSRX rightArmController, double armDeadband) {
		leftController = leftArmController;
		rightController = rightArmController;
		wheelSpeed = 1;
		deadband = armDeadband;
	}
	
	/**
	 * Constructor for a robotic arm with driven wheels
	 * @param leftArmController - Motor controller for use in driving the left arm
	 * @param rightArmController - Motor controller for use in driving the right arm
	 * @param armDeadband - desired deadband radius for arm control
	 * @param armWheelSpeed - desired regular speed preset for the arm control
	 */
	public Arm(WPI_TalonSRX leftArmController, WPI_TalonSRX rightArmController, double armDeadband, double armWheelSpeed) {
		leftController = leftArmController;
		rightController = rightArmController;
		wheelSpeed = armWheelSpeed;
		deadband = armDeadband;
	}
	
	/**
	 * change the previously set deadband
	 * @param armDeadband - the new deadband value
	 */
	public void setDeadband( double armDeadband) {
		deadband = armDeadband;
	}
	
	/**
	 * change the previously set deadband
	 * @param armWheelSpeed - the new regular wheel speed value
	 */
	public void setWheelSpeed( double armWheelSpeed) {
		wheelSpeed = armWheelSpeed;
	}
	
	/**
	 * to be run in a looping function. Drives the arm based on input
	 * @param inputJoy - wpilib joystick object to get z axis from
	 */
	public void driveArm( Joystick inputJoy) {
		
		// driveArm logic by Dylan
		double zValueRight = inputJoy.getZ();
		
		if (zValueRight < deadband) {
			rightController.set(-wheelSpeed);
			leftController.set(wheelSpeed);
		} else if (zValueRight > deadband) {
			rightController.set(-wheelSpeed);
			leftController.set(wheelSpeed);	
		} else {
			rightController.set(0);
			leftController.set(0);	
}
	}
}
