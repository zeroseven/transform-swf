package com.flagstone.transform.movie.button;

public final class Button {

	public enum State {
		/** The button is up. */
		UP(1),	
		/** The mouse is over the active area of the button. */
		OVER(2),
		/** The button is being clicked. */
		DOWN(4),
		/** The active area of the button. */
		ACTIVE(8);
		
		private int value;
		
		private State(int value) {
			this.value = value;
		}
		
		int getValue() {
			return value;
		}
	};

}
