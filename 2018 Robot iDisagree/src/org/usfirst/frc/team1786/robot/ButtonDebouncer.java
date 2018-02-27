package org.usfirst.frc.team1786.robot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;

public class ButtonDebouncer {
	Joystick joystick;
	int button;
	
	double period;
	double latest;
	double now;
	
	/**
	 * constructor. Stops a button from changing state too often.
	 * @param joy - wpilib joystick object
	 * @param btnChannel - channel of button on wpilib joystick
	 * @param waitPeriod - how long to wait in between state changes
	 */
	public ButtonDebouncer(Joystick joy, int btnChannel, double waitPeriod) {
		button = btnChannel;
		period = waitPeriod;
	}
	
	/**
	 * constructor. Stops a button from changing state too often.
	 * @param waitPeriod - new wait period to use in between state changes
	 */
	public void setPeriod(double waitPeriod) {
		period = waitPeriod;
	}
	
	/**
	 * utility function to manually reset the "latest" variable
	 */
	public void setLatest() {
		latest = 0;
	}
	
	/**
	 * get the state of the button. This is essentially a wrapper around
	 * joystick.getRawButton(). You will only see a state change every period seconds.
	 */
	public boolean get() {
		// get a time stamp from the roborio
		now = Timer.getFPGATimestamp();
		if (joystick.getRawButton(button)) {
			if ((now - latest) > period) {
				latest = now;
				return true;
			}
		}
		return false;
	}
}
