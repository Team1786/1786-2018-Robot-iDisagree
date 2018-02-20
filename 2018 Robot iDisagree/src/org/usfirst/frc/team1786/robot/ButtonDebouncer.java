package org.usfirst.frc.team1786.robot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;

public class ButtonDebouncer {
	Joystick joystick;
	
	int button;
	double period;
	
	double latest;
	double now;
	
	
	public ButtonDebouncer(Joystick joy, int btnChannel, double waitPeriod) {
		button = btnChannel;
		period = waitPeriod;
		
		latest = 0;
	}
	
	public void setPeriod(double waitperiod) {
		period = waitperiod;
	}
	
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
