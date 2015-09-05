package com.github.mashlol.Events;

public class Event {

	private String message;
	private int scalar;
	private boolean up;
	private int frequency;
	
	public Event(String message, int scalar, boolean up, int freq) {
		this.message = message;
		this.scalar = scalar;
		this.up = up;
		frequency = freq;
	}
	
	public String getMessage() {
		return message;
	}
	
	public int getScalar() {
		return scalar;
	}
	
	public boolean getUp () {
		return up;
	}
	
	public int getFrequency() {
		return frequency;
	}
	
}
