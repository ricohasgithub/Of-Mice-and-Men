package event;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

import javax.swing.JPanel;

import character.*;
import main.Main;

/*
 * 		Rico Zhu			Jan 10th, 2019
 * 
 * 		This class acts as a single event
 * 		This class has the methods of:
 * 			- A draw method that draws the current event
 * 			- A method that loads the event from a file (initializes the title, story and choices)
 * 			- A method that will return the probability of this event happening
 */

public class Event extends JPanel implements KeyListener {

	// The title of the event
	public String title;
	// The story of the event (each line is a row index of the array)
	String[] story;
	// The choices available as strings
	String[] choices;

	// This boolean array keeps track of which choices are visible
	boolean[] choiceVisibility;

	// This HashMap maps each choice from the choices array to an array of Strings (each array indicates a story)
	HashMap<String, String[]> outcomes;

	// This array stores the Biases (Bias[0] = Strength Bias, Bias[1] = Intelligence Bias, Bias[2] = Karma Bias)
	int[] bias;
	// This array stores the minimum levels of each trait necessary for a special choice to pop up
	int[] specTraits;
	// This 2D array stores the impacts on the player's traits and resources
	// 		rows: 0 = choice 0, 1 = choice 1, 2 = choice 2, 3 = choice 3
	// 		columns: 0 = strength, 1 = intelligence, 2 = karma, 3 = money
	int[][] impacts;
	// Rarity of the event occuring (used to determine event probability)
	double rarity;

	// This string stores the name of the special item needed to activate the fourth choice option (Note: Not all events will have one)
	String specItem;

	// This variable keeps track of whether the player has made a choice yet (is used to draw the results screen of picking a choice)
	public boolean chose;

	// This String variable stores the choice that the player chose
	String playerChoice;

	public Event (String title, String[] story, int[] bias, double rarity, String[] choices, HashMap<String, String[]> outcomes, int[][] impacts, int[] specTraits, String specItem) {
		// Set the variables in the class to the arguments that are passed in
		this.title = title;
		this.story = story;
		this.choices = choices;
		this.outcomes = outcomes;
		this.bias = bias;
		this.rarity = rarity;
		this.impacts = impacts;
		this.specTraits = specTraits;
		this.specItem = specItem;

		// Add the listener
		this.addKeyListener(this);
		// Need this to set the focus of the keyboard
		setFocusable(true);

		// Initialize the choiceVisibility array (all false)
		choiceVisibility = new boolean[choices.length];

		// The player has not made a choice yet, so set the chose variable to false
		chose = false;
	}

	// Method for drawing the screen
	public void draw (Graphics g) {
		// Change the visibility of certain choices
		setChoiceVisibility();

		// Change the color to white
		g.setColor(Color.WHITE);

		// Draw title
		g.drawString(title, 350, 50);

		// This variable keeps track of where to draw the story and choices
		int y = 100;

		// Draw contents
		for (int i=0; i<story.length; i++) {
			g.drawString(story[i], 50, y);
			y += 50;
		}

		// Draw how to play string
		g.drawString("(Press 1 for choice 1, 2 for Choice 2 .etc)", 50, y);

		// Increment the y coordinate
		y += 50;

		// Draw the choices
		for (int i=0; i<choices.length; i++) {
			// If the choice is visible (player has the traits/resources to use choice) - draw it and increment y
			if (choiceVisibility[i]) {
				g.drawString(choices[i], 50, y);
				y += 50;
			}
		}
		
	}

	// This method draws the result screen as a result of the player picking a choice
	public void drawResultsScreen (Graphics g) {
		// Change the color to white
		g.setColor(Color.WHITE);
		
		// Get the outcome story as a result of the player's choice
		String[] playerOutcome = outcomes.get(playerChoice);

		// Iterate through each line and draw each String
		for (int i=0; i<playerOutcome.length; i++) {
			g.drawString(playerOutcome[i], 50, (i * 50) + 50);
		}
		
		g.drawString("Press Space to Continue", 100, playerOutcome.length * 50 + 50);

	}

	// Method that sets certain choices to be invisible when drawn
	public void setChoiceVisibility () {
		// Set the first 2 choice' visibilities to true
		choiceVisibility[0] = true;
		choiceVisibility[1] = true;

		// Check choice 3 visibility (needs to pass trait threshold needed to activate)

		// Get the traits of the player as an array
		int[] traits = Traits.getTraitsAsArray();

		// Iterate through each element of the player's actual traits and check to see if it is higher than the requirement
		for (int i=0; i<traits.length; i++) {
			if (traits[i] < specTraits[i]) {
				// If one trait doesn't pass the required threshold, break and set choice 3 to visible
				choiceVisibility[2] = false;
				break;
			} else {
				// Otherwise, set it to be true for now
				choiceVisibility[2] = true;
			}
		}

		// Check choice 4 visibility (needs to have certain item to activate)
		if (Resources.hasItem(specItem)) {
			// Required item found in inventory - set choice to visible
			choiceVisibility[3] = true;
		}

	}
	
	// This method returns the probability of an event occuring based on the player's traits and resources
	public double getEventProbability () {
		// Theis variable stores the probability of the event
		double prob = rarity;
		// The probability of an event is calculated by multiplying the biases to the player's traits
		prob += (((double)Traits.getStrength())/100 * (double)bias[0]) + (((double)Traits.getIntelligence())/100 * (double)bias[1]) 
				+ (((double)Traits.getKarma())/100 * (double)bias[2]);
		return prob;
	}

	public void keyPressed(KeyEvent e) {
		// Do Nothing
	}

	// This method determines what happens after the player makes a choice by pressing a key (1,2,3,4)
	public void keyReleased(KeyEvent e) {
		// Get the action command as a string
		char ac = e.getKeyChar();
		
		if (ac == '1' && Main.inEvent && !chose && choiceVisibility[0] == true) {
			// Picked choice 1 while the player is in an event and the choice isn't hidden

			// Update chose and playerChoice
			chose = true;
			playerChoice = choices[0];

			// Apply trait changes
			Traits.applyStrengthChange(impacts[0][0]);
			Traits.applyIntelligenceChange(impacts[0][1]);
			Traits.applyKarmaChange(impacts[0][2]);

			// Apply money change
			Resources.loseMoney(impacts[0][3]);
			System.out.println(title);
		} else if (ac == '2' && Main.inEvent && !chose && choiceVisibility[1] == true) {
			// Picked choice 2 while the player is in an event and the choice isn't hidden
			
			// Update chose and playerChoice
			chose = true;
			playerChoice = choices[1];

			// Apply trait changes
			Traits.applyStrengthChange(impacts[1][0]);
			Traits.applyIntelligenceChange(impacts[1][1]);
			Traits.applyKarmaChange(impacts[1][2]);

			// Apply money change
			Resources.loseMoney(impacts[1][3]);
			System.out.println(title);
		} else if (ac == '3' && Main.inEvent && !chose && choiceVisibility[2] == true) {
			// Picked choice 3 while the player is in an event and the choice isn't hidden

			// Update chose and playerChoice
			chose = true;
			playerChoice = choices[2];

			// Apply trait changes
			Traits.applyStrengthChange(impacts[2][0]);
			Traits.applyIntelligenceChange(impacts[2][1]);
			Traits.applyKarmaChange(impacts[2][2]);

			// Apply money change
			Resources.loseMoney(impacts[2][3]);
			System.out.println(title);
		} else if (ac == '4' && Main.inEvent && !chose && choiceVisibility[3] == true) {
			// Picked choice 4 while the player is in an event and the choice isn't hidden - Used special item

			// Update chose and playerChoice
			chose = true;
			playerChoice = choices[3];

			// Remove used item from inventory
			Resources.removeItem(specItem);

			// Apply trait changes
			Traits.applyStrengthChange(impacts[3][0]);
			Traits.applyIntelligenceChange(impacts[3][1]);
			Traits.applyKarmaChange(impacts[3][2]);

			// Apply money change
			Resources.loseMoney(impacts[3][3]);
			System.out.println(title);
		} else if (ac == ' ' && Main.inEvent && chose) {
			// Reset chose
			chose = false;
			// Reset Main.inEvent
			Main.inEvent = false;
			// Reset playerChoice to empty string
			playerChoice = "";
			// Reset choice visibilities
			for (int i=0; i<choiceVisibility.length; i++) {
				choiceVisibility[i] = false;
			}
		}

	}

	public void keyTyped(KeyEvent e) {
		// Do Nothing
	}

	// Method to add delays for smoother animations
	private void delay(int mili) {
		try {
			Thread.sleep(mili);
		} catch (InterruptedException e) {
			System.out.println("ERROR IN SLEEPING");
		}
	}

}
