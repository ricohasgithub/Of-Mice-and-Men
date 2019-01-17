package event;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/*
 * 		Rico Zhu		Jan 17th, 2019
 * 
 * 		The RumorEvent is a special type of event with a different
 * 		response. A RumorEvent tells the player that there is work
 * 		in a town (name randomly chosen)
 * 		Each RumorEvent would have an associated WorkEvent,
 * 		which represents the work that is in the contents of the story.
 * 
 * 		For example, a regular event would have multiple choices and
 * 		each event is different. A rumor event only has one choice
 * 		(Let's go - which resumes the game) and all rumor events are
 * 		essentially the same thing but with changed city names and
 * 		journy dates.
 * 
 */


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JPanel;

import main.Main;

public class RumorEvent extends JPanel {

	// The title of the event
	String title;
	// This variable stores the town that the story currently refers to
	String town;
	// This String array stores the story of the work event
	String[] story;
	// This array is used to get random town names
	String[] towns = {"SALINAS", "PASADENA", "SACREMENTO", "EL CERRITO", "SAN FRANCISCO", "LOS ANGELES", "FREMONT", "SANTA CLARA"};

	// How many days it takes to get to the town with work (work event)
	int days;

	public RumorEvent (String filename) throws IOException {
		// Create a new FileReader that reads from a file
		FileReader fr = new FileReader(filename);
		// Create a new BufferedReader that reads from a the FileReader
		BufferedReader br = new BufferedReader(fr);

		// Get the title of the event from the input file
		String title = br.readLine();

		// Get the size of the story (Number of lines)
		int storySize = Integer.parseInt(br.readLine());

		// Initialize the story array to a new String array of storySize
		story = new String[storySize];

		// Read each line of the story and load it into the story array
		for (int i=0; i<storySize; i++) {
			story[i] = br.readLine();
		}

		// Set a random town as the destination
		setRanTown();

		// Set a random travel time
		setRanDate();

	}

	public void draw (Graphics g) {
		// Change the color to white
		g.setColor(Color.WHITE);

		// This variable is used to easily track the y coordinates of each line of the story text
		int y = 200;

		// Write the story to the screen with a random town name and travel time in place
		for (int i=0; i<story.length; i++) {
			// This variable is used to temporarily store the current line of the text
			String temp = story[i];
			for (int j=0; j<temp.length(); j++) {
				// Replace the special character * with the random town name and | with the random travel time
				if (temp.charAt(j) == '*') {
					// Replace special character * with the random town name
					temp = temp.substring(0, j) + town + temp.substring(j+1, temp.length());
				} else if (temp.charAt(j) == '|') {
					// Replace special character | with the travel time
					temp = temp.substring(0, j) + days + temp.substring(j+1, temp.length());
				}
			}
			// Draw the string to the screen
			g.drawString(temp, 200, y);
			// Increment y by 50
			y += 50;
		}

	}

	// This method returns the name of the town that the player is travelling to
	public String getTownName () {
		return town;
	}

	// This method returns the number of days it takes to travel to the work destination
	public int getTravelTime () {
		return days;
	}

	// This method resets the town name and date
	public void reset () {
		// Set a random town as the destination
		setRanTown();

		// Set a random travel time
		setRanDate();
	}

	// This method randomly sets the town variable to a String in the towns array
	private void setRanTown () {
		// Store the current name of the town in a temporary String variable (Note: this is used to later check for repetitions)
		String temp = town;

		// Set the town variable to a random String variable from the towns array
		town = towns[(int)(Math.random() * towns.length)];

		// Repeatedly get new town names if the name is equal to the current name (Note: this is used to avoid getting the same town twice in a roll)
		while (town.equals(temp)) {
			town = towns[(int)(Math.random() * towns.length)];
		}
	}

	// This method set the days variable (how many days it takes until the work event appears)
	private void setRanDate () {
		// Set the days variable to a random number between 5 and 14
		days = (int) (Math.random() * 10) + 5;
	}

}
