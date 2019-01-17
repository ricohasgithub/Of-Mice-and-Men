package event;

import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import character.Resources;

public class WorkEvent {

	// The title of the event
	String title;
	// This String array stores the story of the work event
	String[] story;
	
	// This variable stores how much money the player earns from working
	int pay;

	public WorkEvent (String filename) throws IOException {
		// Create a new FileReader that reads from a file
		FileReader fr = new FileReader(filename);
		// Create a new BufferedReader that reads from a the FileReader
		BufferedReader br = new BufferedReader(fr);

		// Get the title of the event from the input file
		String title = br.readLine();

		// Get the size of the story (Number of lines)
		int storySize = Integer.parseInt(br.readLine());

		// Initialize the story array to a new String array with size storySize
		story = new String[storySize];
		
		// Load the story line by line into the story array
		for (int i=0; i<storySize; i++) {
			story[i] = br.readLine();
		}
		
		// Set the payment
		pay = Integer.parseInt(br.readLine());
		
	}
	
	public void draw (Graphics g) {
		// Change font color to white
		g.setColor(Color.WHITE);
		
		// Draw each line of the story
		for (int i=0; i<story.length; i++) {
			g.drawString(story[i], 200, (i * 50) + 200);
		}
	}
	
	// This method adds the earned money to the player's balance
	public void addPay () {
		Resources.applyMoneyChange(pay);
	}

}
