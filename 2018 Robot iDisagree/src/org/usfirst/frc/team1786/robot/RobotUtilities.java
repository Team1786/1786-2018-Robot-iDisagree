package org.usfirst.frc.team1786.robot;
import edu.wpi.first.wpilibj.Joystick;

/* 
 * A utility class for some common utilities we need
 * 
 * I'm cheating since java doesn't support top-level static classes.
 * However, this class is final ( can't be extended )
 * 			its members are static
 * 			its constructor is private
 * 
 * that makes it pretty similar to a top-level static class
 */

public final class RobotUtilities {
	
	private RobotUtilities() {
	
	}
	
	// dylan's deadband code
	public static double YdeadbandScaled(Joystick joy, double deadband) {
		double joystickY = joy.getY();
		
		if (joystickY > 0 && joystickY > deadband) 
		{
			double scaledYValue = (joystickY - deadband) / ((1-deadband)/(1));
			return scaledYValue;
		}
		else if (joystickY < 0 && joystickY < -deadband) 
		{
			double scaledYValue = (joystickY + deadband) / ((1-deadband)/(1));
			return scaledYValue;
		}
		else 
		{
			double scaledYValue = 0;
			return scaledYValue;
		}	
	}
	
	// dylan's deadband code
	public static double ZdeadbandScaled(Joystick joy, double deadband) {
		double joystickZ = joy.getZ();
		
		if (joystickZ > 0 && joystickZ > deadband) 
		{
			double scaledZValue = (joystickZ - deadband) / ((1-deadband)/(1));
			return scaledZValue;
		}
		else if (joystickZ < 0 && joystickZ < -deadband) 
		{
			double scaledZValue = (joystickZ + deadband) / ((1-deadband)/(1));
			return scaledZValue;
		}
		else 
		{
			double scaledZValue = 0;
			return scaledZValue;
		}	
		
	}
}
