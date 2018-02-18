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
	// don't need to do anything in auto right now
	}

	// dylan's deadband code
	public static double deadbandScaled(double inputAxis, double deadband) {
		double value = inputAxis;

		if (value > 0 && value > deadband)
		{
			double scaledValue = (value - deadband) / ((1 - deadband) / (1));
			return scaledValue;
		}
		else if (value < 0 && value < -deadband)
		{
			double scaledValue = (value + deadband) / ((1 - deadband) / (1));
			return scaledValue;
		}
		else
		{
			double scaledValue = 0;
			return scaledValue;
		}
	}
}
