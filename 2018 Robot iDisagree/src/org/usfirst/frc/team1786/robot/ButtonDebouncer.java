package org.usfirst.frc.team1786.robot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;

/**
 * class to allow for the debouncing of buttons. Make a new instance for EACH button 
 * to be debounced
 */
public class ButtonDebouncer {
	Joystick joystick;
	
	int button;
	double period;
	
	double latest;
	double now;
	
	/**
	 * constructor for buttonDebouncer
	 * @param joy - wpilib joystick object
	 * @param btnChannel - desired btn channel to debounce
	 * @param waitPeriod - how long to wait in between presses to debounce in seconds
	 */
	public ButtonDebouncer(Joystick joy, int btnChannel, double waitPeriod) {
		button = btnChannel;
		period = waitPeriod;
		
		latest = 0;
	}
	
	/**
	 * change the wait period between presses
	 * @param waitperiod - how many seconds
	 */
	public void setPeriod(double waitperiod) {
		period = waitperiod;
	}
	
	/** 
	 * @return - whether button has been pressed or not
	 */
	public boolean get() {
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
