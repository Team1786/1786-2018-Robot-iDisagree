package org.usfirst.frc.team1786.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;

public class Debouncer {

	Timer timer;
	Joystick joystick;
	int buttonNum;
	double period;
	double latest = 0;
	double now;
	
	// initalize a debouncer which only allows state changes of a button every waitPeriod amount of time
	public Debouncer(Joystick joy, int button, double waitPeriod) {
		joystick = joy;
		buttonNum = button;
		period = waitPeriod;
	}
	
	public void setDebouncePeriod(double waitPeriod) {
		period = waitPeriod;
	}
	
	public boolean get() {
		now = Timer.getFPGATimestamp();
		if(joystick.getRawButton(buttonNum)) {
			if ((now - latest) > period) {
				latest = now;
				return true;
			}
		};
		return false;
	}
		

};
