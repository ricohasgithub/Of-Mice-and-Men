package main;

import javax.sound.sampled.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import javax.swing.JPanel;

import character.Resources;
import character.Traits;

import java.util.HashMap;

import event.Event;
import event.RumorEvent;
import event.WorkEvent;

/*
 * 		Rico Zhu		Jan 17th, 2019
 * 		
 * 		Of mice and men is a Oregon-trail style video game. The player plays as
 * 		George Milton, and they have to go around the state of California looking
 * 		work. Along the way, the player will encounter events which will affect their
 * 		traits (Strength, Intelligence and Karma) as well as how much money they have.
 * 
 * 		Win and Lose Conditions:
 * 		If the player has $500, then they win the game. However, if they become bankrupt
 * 		(have $0) or run out of strength, then they lose.
 * 
 * 		Main Class:
 * 		This class contains the main method which will generate the JPanel. The game will
 * 		start by displaying a title page with some 8-bit music, and when the player presses
 * 		and releases the space bar, the title page will disappear and an introduction/tutorial
 * 		page will show up.
 * 
 * 		Once the player dismisses the introduction/tutorial page (by pressing and releasing
 * 		the space bar), they will be brought to a work rumor (or rumor) event. This event
 * 		will inform the player on how much time it will take to get to their work place.
 * 
 * 		After the player dismisses the screen, they wil be brought to the playing screen with 
 * 		a walking man on top of a scrolling highway and background (desert).
 * 
 * 		The walking screen will be default display until an "event" pops up. When an event
 * 		pops up, the screen will turn black and text will appear indicating what the event
 * 		is, the story and choices for the player.
 * 
 * 		Once the player makes a choice, appropriate changes to the player's traits and
 * 		resources.
 * 
 * 		Once the player reaches the work location, they will be given some money. After they
 * 		dismiss the work event screen, they will be prompted by another rumor event.
 * 
 */

public class Main extends JPanel implements ActionListener, KeyListener {

	// Passive player walking animation (gif)
	ImageIcon passivePlayerWalk;

	// Default highway pixel image where the player will be walking on
	Image highway;
	// Default highway background
	Image defBackground;
	// Desert background
	Image desertBackground;
	// Mountain background
	Image mountainBackground;
	// Title text as .png image
	Image titleText;
	// Title background image
	Image titleBackground;

	// These coordinates are used to keep track of the scrolling background
	int bx = 0;
	int by = 0;

	// These coordinates are used to keep track of the scrolling highway
	int hx = 0;
	int hy = 275;

	// This variable keeps track of the date
	int days = 0;
	// Regular Event Cooldown Counter
	int cooldown = 0;
	// Work Travel Counter (This variable tracks how many days there are until a work event pops up
	int workCooldown = 0;
	
	// This String variable is used to store the current work destination
	String town;
	
	// This long variable is used to store the starting time of the program and also used to determine dates
	long initSystemTime;
	// This long variable is used to determine how much time had passed since the program started
	long elapsedTime;
	// This long variable is used to determine the difference betweeen the curretn system time and the last day
	long lastDayTime;

	// This boolean variable tracks whether the player is in the info title screen
	boolean inTitleScreen;
	// This boolean variable tracks whether the player is in the introduction/tutorial screen
	boolean inIntroScreen;
	// This boolean variable tracks whether the player is in a work rumor event
	boolean inRumorEvent;
	// This boolean variable tracks whether the player is in a work event
	boolean inWorkEvent;
	
	// This boolean variable keeps track of whether the player is currently in an event (the variable is public and static because an event would need access to change this)
	public static boolean inEvent;

	// This String array is used to store the file paths of the events
	String[] eventsFilePaths;
	// This array is used to store all possible events
	Event[] events;
	// This event is used to store the current event
	Event currentEvent;

	// This event is used to represent all work rumor events
	RumorEvent rumor;
	// This event is used to represent all work events
	WorkEvent work;

	// The following variables are variables used in the initialization of events (made global to increase reusability)
	String title;
	String[] story;
	int[] bias;
	double rarity;
	String[] choices;
	HashMap<String, String[]> outcomes;
	int[][] impacts;
	int[] specTraits;
	String specItem;

	// Custom 8-bit wonder font (Title Size)
	Font customTitleFont;

	// Custom 8-bit wonder font (Regular Size)
	Font customFont;

	public Main () throws IOException {

		// Turn off layout for buttons
		this.setLayout(null);

		// Add the listener
		this.addKeyListener(this);
		// Need this to set the focus of the keyboard
		setFocusable(true);

		// Initialize images and default walking gif
		passivePlayerWalk = new ImageIcon("images/Player_Walk.gif");

		try {
			highway = ImageIO.read(new File("images/Highway.png"));
			desertBackground = ImageIO.read(new File("images/DesertBackground.png"));
			mountainBackground = ImageIO.read(new File("images/MountainBackground.png"));
			titleText = ImageIO.read(new File("images/Title.png"));
			titleBackground = ImageIO.read(new File("images/TitlePage.png"));
		} catch (IOException e) {
			System.out.println("File not found");
			System.exit(-1);
		}

		// Scale the images
		highway = highway.getScaledInstance(1000, 100, Image.SCALE_SMOOTH);
		desertBackground = desertBackground.getScaledInstance(1000, 300, Image.SCALE_SMOOTH);
		mountainBackground = mountainBackground.getScaledInstance(1000, 300, Image.SCALE_SMOOTH);
		titleText = titleText.getScaledInstance(500, 250, Image.SCALE_SMOOTH);
		titleBackground = titleBackground.getScaledInstance(1000, 700, Image.SCALE_SMOOTH);

		// Change the default scrolling background image to desert
		defBackground = desertBackground;

		// Set background color to black
		setBackground(Color.BLACK);

		// Load the events into an event array
		loadEvents("Events.txt");

		// Load the work rumor event
		rumor = new RumorEvent("files/WorkRumor.txt");

		// Load the work event
		work = new WorkEvent("files/WorkEvent.txt");

		// Set the event cooldown counter to 1 day
		cooldown = 2;

		// Create a custom 8-bit style font
		try {
			// Create the regular font to use
			//customFont = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/8-BIT WONDER.ttf")).deriveFont(16f);
			customFont = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/8-bit pusab.ttf")).deriveFont(16f);
			// Create the custom title font as a larger version of the regular font
			customTitleFont = customFont.deriveFont(24f);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			// Register the font
			ge.registerFont(customFont);
		} catch (IOException e) {
			e.printStackTrace();
		} catch(FontFormatException e) {
			e.printStackTrace();
		}

		// Set the font
		this.setFont(customFont);

		// Play background music
		try {
			// Read the music.wav file
			File file = new File("sounds/DefMusic.wav");
			AudioInputStream stream;
			AudioFormat format;
			DataLine.Info info;
			Clip clip;
			// Stream the audio file
			stream = AudioSystem.getAudioInputStream(file);
			format = stream.getFormat();
			info = new DataLine.Info(Clip.class, format);
			clip = (Clip) AudioSystem.getLine(info);
			clip.open(stream);
			clip.start();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// Start the program in the title screen - set inTitleScreen to true
		inTitleScreen = true;

		// Initialize inEvent to false
		inEvent = false;

	}

	public void paintComponent (Graphics g) {
		// Call parent constructor
		super.paintComponent(g);

		// Check to see if the player has won or lost
		if (playerWon()) {
			// If the player won - draw the victory screen
			drawVictoryScreen(g);
		} else if (playerLost()) {
			// If the player lost - draw the loss screen
			drawLossScreen(g);
		} else {
			// Otherwise, continue to game, check to see what action to perform
			if (inTitleScreen) {
				// If the player is still in the title screen (has not pressed space yet)
				drawTitleScreen(g);
			} else if (inIntroScreen) {
				// If the player is still in the introduction/tutorial screen (has not pressed space yet)
				drawIntroScreen(g);
			} else {
				// Otherwise, draw the regular screen

				if (inRumorEvent) {
					// Check to see if the player is in a work rumor event
					rumor.draw(g);
				} else if (workCooldown == 0) {
					// Check to see if the player has reached the destination (Note: this comes before the regular events because work events have a higher priority)
					
					if (inWorkEvent) {
						// Currently in work event, draw screen
						work.draw(g);
					} else {
						// Just started work event, draw screen and set inWorkEvent to true
						inWorkEvent = true;
						work.draw(g);
					}
					
					work.draw(g);
				} else {
					// Draw an event if necessary (event cooldown counter reaches 0 and is not currently in an event)
					if (cooldown == 0 && !inEvent) {
						// The player is moving into an event, so set the inEvent variable to true
						inEvent = true;
						// Initialize a random event
						drawRanEvent(g);
						currentEvent.chose = false;
						// Reset event cooldown counter
						resetCooldown();
					} else if (inEvent) {
						// If the player is currently in an event, continuously draw the event that the player is currently in
						if (!currentEvent.chose) {
							// The player had not made a choice yet, draw the choice screens
							currentEvent.draw(g);
						} else {
							// They player had made a choice, draw the outcome screen
							currentEvent.drawResultsScreen(g);
						}
					} else {
						// Otherwise, draw the regular screen

						// Update the elapsed time (elpasedTime = the current time - the starting time), update the current day and reduce event cooldown
						updateTime();

						// Draw the top walking screen (moving figure on a scrolling highway against a scrolling background)
						drawTopScreen(g);

						// Draw the bottom info bar
						drawBottomScreen(g);
					}
				}
			}
		}
	}

	// This method checks and returns a boolean variable indicating whether the player has lost (has $0 or 0 strength)
	private boolean playerLost () {
		// If the player is bankrupt (has $0) or runs out of strength, then they lost
		return (Resources.isBankrupt() || Traits.getStrength() == 0);
	}

	// This method checks and returns a boolean variable indicating whether the player has won (has $500)
	private boolean playerWon () {
		return Resources.isRich();
	}

	// This method draws the victory screen
	private void drawVictoryScreen (Graphics g) {
		g.drawString("You Won!", 100, 100);
	}

	// This method draws the loss screen
	private void drawLossScreen (Graphics g) {
		g.drawString("You Lost!", 100, 100);
	}

	// This method randomly picks an event and draws it
	private void drawRanEvent (Graphics g) {
		// Get a random event to draw
		currentEvent = getRanEvent();
		// Draw the event
		currentEvent.draw(g);
	}

	// This method returns a random event
	private Event getRanEvent () {
		Event ranEvent = events[(int) (Math.random() * events.length)];
		// Repeatedly get random events until a random event with a probability over a threshold (at least 0.4) is selected
		while (ranEvent.getEventProbability() < 0.5) {
			// Change ranEvent to a new event
			ranEvent = events[(int) (Math.random() * events.length)];
		}
		return ranEvent;
	}

	// This method updates the elapsedTime, days variable and event cooldown counter
	private void updateTime () {
		// Update the elapsed time (elpasedTime = the current time - the starting time)
		elapsedTime = System.currentTimeMillis() - initSystemTime;

		// Check to see if a day had passed since last checking (3 seconds or more had passed) and update days variable accordingly

		// The following calculation checks to see if 3 seconds had passed since the last recorded day
		if ((initSystemTime + elapsedTime) - lastDayTime >= 3000) {
			// Increment days
			days++;
			// Decrement event cooldown counter
			cooldown--;
			// Decrement workCooldown counter
			workCooldown--;
			// Update the last day to the current day
			lastDayTime = initSystemTime + elapsedTime;
		}

	}

	// This method draws the bottom info bar
	private void drawBottomScreen (Graphics g) {
		// Set the font color to white (against black background)
		g.setColor(Color.WHITE);

		// Set the font for the title
		g.setFont(customTitleFont);

		// Draw the stats title string
		g.drawString("Stats", 30, 410);

		// Set the font for regular text
		g.setFont(customFont);

		// Display Date
		g.drawString("Day " + days, 230, 410);

		// Get the player's traits and money values
		int str = Traits.getStrength();
		int itl = Traits.getIntelligence();
		int kar = Traits.getKarma();
		int mon = Resources.getMoney();

		// Draw the trait strings
		g.drawString("Strength", 30, 450);
		g.drawString("Intelligence", 30, 480);
		g.drawString("Karma", 30, 510);

		// Draw the trait value bars
		g.fillRect(230, 430, str, 20);
		g.fillRect(230, 460, itl, 20);
		g.fillRect(230, 490, kar, 20);

		// Draw the amount of money
		g.drawString("You have " + mon + " dollars", 30, 560);
		
		// Draw how many days it takes to reach the work destination
		
		if (workCooldown == 1) {
			// Check to see if there is only one day left, if so, draw day instead of days
			g.drawString(workCooldown + " day until you reach " + town, 30, 610);
		} else {
			// Draw String with plural days
			g.drawString(workCooldown + " days until you reach " + town, 30, 610);
		}

	}

	// This method draws the top walking screen (moving figure on a scrolling highway against a scrolling background)
	private void drawTopScreen (Graphics g) {
		// Draw the scrolling background
		g.drawImage(defBackground, bx, by, null);
		g.drawImage(defBackground, bx + 1000, by, null);

		// Move the background
		moveBackground();

		// Draw the scrolling highway
		g.drawImage(highway, hx, hy, null);
		g.drawImage(highway, hx + 1000, hy, null);

		// Move the highway
		moveHighway();

		// Draw the walking person
		passivePlayerWalk.paintIcon(this, g, 100, 200);
	}

	// This method draws the introduction screen
	private void drawIntroScreen (Graphics g) {
		// Set introduction screen text properties
		g.setColor(Color.WHITE);
		g.setFont(customFont);
		// The y variable is used to easily modify the location of the intro passage
		int y = 50;
		// Draw the info text/tutorial summary
		g.drawString("Howdy Partner!", 350, y);
		// Increment text spacing
		y += 50;
		g.drawString("It's the year 1932, and the great-depression is in full-swing. ", 50, y);
		y += 50;
		g.drawString("You are George Milton, hardworking laborer from California.", 50, y);
		y += 75;
		g.drawString("In this game, you will have to go around the state of California", 50, y);
		y += 50;
		g.drawString("looking for work, tryin' to make a fortune of $500.", 50, y);
		y += 50;
		g.drawString("While travelling, you'll have to make decisions, which", 50, y);
		y += 50;
		g.drawString("will impact your character traits, which are: Strength, ", 50, y);
		y += 50;
		g.drawString("Intelligence and Karma, as well as how much money you have. ", 50, y);
		y += 50;
		g.drawString("Watch out though, if your strength trait hits 0 or ", 50, y);
		y += 50;
		g.drawString("if you run out of money, you lose the game!", 50, y);
		y += 75;
		g.drawString("So best of luck and rock on! (Press space to continue)", 50, y);
	}

	// This method draws the title screen
	private void drawTitleScreen (Graphics g) {
		// Set title page info text properties
		g.setColor(Color.WHITE);
		g.setFont(customTitleFont);
		// Draw the background title page image
		g.drawImage(titleBackground, 0, 0, null);
		// Add extra title image
		g.drawImage(titleText, 60, 50, null);
		// Draw the info text on how to start the game
		g.drawString("Press Space to Start", 95, 350);
	}

	// This method loads and creates events from a directory of text files
	private void loadEvents (String filename) throws IOException {
		// Create a new FileReader that reads from a file
		FileReader fr = new FileReader(filename);
		// Create a new BufferedReader that reads from a the FileReader
		BufferedReader br = new BufferedReader(fr);
		// Read the current line
		String currLine = br.readLine();
		// The first line of the events file paths file will be the number of events - initialize the events and events paths array
		int numEvents = Integer.parseInt(currLine);

		// Initialize eventsFilePaths and events arrays to be length numEvents
		eventsFilePaths = new String[numEvents];
		events = new Event[numEvents];

		// Read each line in the events file paths file
		for (int cf=0; cf<numEvents; cf++) {
			eventsFilePaths[cf] = br.readLine();
		}

		for (int cf=0; cf<numEvents; cf++) {
			// Read from each file and load the events array
			fr = new FileReader(eventsFilePaths[cf]);
			br = new BufferedReader(fr);

			// Load the file's contents into events array
			createNewEvent(br, eventsFilePaths[cf], cf);
		}

	}

	// This method creates a new event in the events array at a given index

	/*
	 * 		Each event file is formatted like this
	 * Title
	 * Main Content
	 * 		Number of lines in content paragraph
	 * 			N lines of content
	 * Bias
	 * 		Strength Bias
	 * 		Intelligence Bias
	 * 		Karma Bias
	 * 		Event Rarity
	 * Choices
	 * 		Choice 1
	 * 		...
	 * Text Responses to Choices
	 * 		Number of Lines for Choice 1
	 * 			N lines of content
	 * 	 	...
	 * Impacts on Traits and Resources
	 * 		Choice 1
	 *			Strength Impact
	 *			Intelligence Impact
	 *			Karma Impact
	 *			Money Impact
	 *		...
	 * Required Traits for Choice 3 to appear
	 * 		Mininum Strength Requirement
	 * 		Mininum Intelligence Requirement
	 * 		Mininum Karma Requirement
	 * Item Required for Choice 4 to appear
	 * 		Item Name (String)
	 *
	 */

	private void createNewEvent (BufferedReader br, String filename, int index) throws IOException {
		// Read the title - Line 1
		title = br.readLine();

		// Get the number of lines in the Main Content field
		int numLines = Integer.parseInt(br.readLine());
		// Initialize the story array
		story = new String[numLines];
		// Load the Main Contents field line by line
		for (int i=0; i<numLines; i++) {
			story[i] = br.readLine();
		}

		// Initialize the bias array
		bias = new int[3];
		// Get the biases
		for (int i=0; i<bias.length; i++) {
			// Parse and load each bias value into the bias array
			bias[i] = Integer.parseInt(br.readLine());
		}

		// Load the event rarity value
		rarity = Double.parseDouble(br.readLine());

		// Get the number of choices (reuse numLines variable)
		numLines = Integer.parseInt(br.readLine());
		// Initialize the choice array and load the choice options
		choices = new String[numLines];
		for (int i=0; i<numLines; i++) {
			choices[i] = br.readLine();
		}

		// Initialize the outcomes HashMap
		outcomes = new HashMap<String, String[]>();

		// For each of the choices, load a string array of outcome summary (Note that numLines is still set the the number of choices)
		for (int i=0; i<numLines; i++) {
			// Get how many lines the current choice response outcome passage
			int numLinesChoice = Integer.parseInt(br.readLine());
			// Initialize the String array that will be used in the outcome HasMap
			String[] choicePassage = new String[numLinesChoice];
			// Iterate through the response outcome line and load each String/line into the choicePassage String array
			for (int j=0; j<numLinesChoice; j++) {
				choicePassage[j] = br.readLine();
			}
			// Add the choice and its corresponding response outcome passage
			outcomes.put(choices[i], choicePassage);
		}

		// Initialize the impacts D matrix with dimensions (number of choices) x 4 (Note that there 4 four choices, 3 for the traits and one for money)
		impacts = new int[choices.length][4];

		// Get and store the impacts - first iterate through each of the choices and then iterate through each impact
		for (int r=0; r<choices.length; r++) {
			// Iterate through the impacts and load the data into the impacts 2D matrix
			for (int c=0; c<4; c++) {
				impacts[r][c] = Integer.parseInt(br.readLine());
			}
		}

		// Initialize and load the specTraits array (there are 3 elements)
		specTraits = new int[3];
		for (int i=0; i<specTraits.length; i++) {
			specTraits[i] = Integer.parseInt(br.readLine());
		}

		// Read the specItem String
		specItem = br.readLine();

		// Create a new event with the previously loaded data
		events[index] = new Event(title, story, bias, rarity, choices, outcomes, impacts, specTraits, specItem);
		// Adds the event to be detectable from the keyboard
		this.addKeyListener(events[index]);
	}

	// This method is used to scroll the background, creating the walking effect
	private void moveBackground () {
		// Shift both of the images left
		this.bx -= 1;

		// If the second image has completely cascaded over, loop the first image back
		if (bx <= -1000) {
			bx = 0;
		}
	}

	// This method is used to scroll the highway, creating the walking effect
	private void moveHighway () {
		// Shift both of the images left
		this.hx -= 5;

		// If the second image has completely cascaded over, loop the first image back
		if (hx <= -1000) {
			hx = 0;
		}
	}

	// This variable gets sets the event cooldown counter to a random number between 2 and 4 if the player is not currently in an event
	private void resetCooldown () {
		// Reset the cooldown if the player is not currently in an event
		cooldown = (int) (Math.random() * 3) + 2;
	}

	// Method to add delays for smoother animations
	private void delay(int mili) {
		try {
			Thread.sleep(mili);
		} catch (InterruptedException e) {
			System.out.println("ERROR IN SLEEPING");
		}
	}

	public void keyPressed(KeyEvent k) {

	}

	public void keyReleased(KeyEvent k) {
		// Get the value of the released key
		char ac = k.getKeyChar();

		if (ac == ' ' && inTitleScreen) {
			// If the player is still in the title screen and they pressed space - move onto regular screen

			// Set the inTitleScreen variable to false to indicate that the player is moving out of the title screen
			inTitleScreen = false;

			// Start the introduction screen
			inIntroScreen = true;

		} else if (ac == ' ' && inIntroScreen) {
			// If the player is still in the introduction screen and they pressed space - move onto game screen

			// Set inIntroScreen to false
			inIntroScreen = false;

			// Initialize the starting rumor event
			inRumorEvent = true;

			// Initialize all of the time variables as day 1 starts now

			// Start the timer and get the current time
			initSystemTime = System.currentTimeMillis();
			// Set the elapsed time to 0 since it had just been initialized
			elapsedTime = 0;
			// Set the time of the last day to the initialization time
			lastDayTime = initSystemTime;
		} else if (ac == ' ' && inRumorEvent) {
			// If the key is a space character and the player is currently in a work rumor event, set inRumorEvent to false
			inRumorEvent = false;
			// Initialize the workCooldown and town variables
			workCooldown = rumor.getTravelTime();
			town = rumor.getTownName();
		} else if (ac == ' ' && inWorkEvent) {
			// If the key is a space character and the player is currently in a work event, set inWorkEvent to false and inRumorEvent to true (start a new work rumor)
			inWorkEvent = false;
			inRumorEvent = true;
			// Add the money earned to the player's balance
			work.addPay();
			// Reset the rumor event
			rumor.reset();
		}

		// Redraw the frame
		repaint();
	}

	public void keyTyped(KeyEvent k) {

	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) throws IOException {
		JFrame frame = new JFrame("Of Mice and Men");
		frame.getContentPane().add(new Main());
		frame.setSize(1000, 700);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
