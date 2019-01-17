package character;

/*
 * 		Rico Zhu			Jan 10th, 2019
 * 
 * 		This class acts as a reference class for all of the player's traits
 * 		All classes will be able to access the player's traits through this class
 */

public class Traits {

	// Player traits - default is 70 each
	private static int STRENGTH = 70;
	private static int INTELLIGENCE = 70;
	private static int KARMA = 70;

	// This method returns the strength trait of the player
	public static int getStrength () {
		return STRENGTH;
	}

	// This method returns the intelligence trait of the player
	public static int getIntelligence () {
		return INTELLIGENCE;
	}

	// This method returns the karma trait of the player
	public static int getKarma () {
		return KARMA;
	}

	// This method returns the traits of the player as an 3 element array
	public static int[] getTraitsAsArray () {
		return new int[] {STRENGTH, INTELLIGENCE, KARMA};
	}

	// This method adds a value to the Strength category
	public static void applyStrengthChange (int change) {
		if (change > 0) {
			// If the total strength is increasing set strength to either current strength + change or 100 (whichever is smaller)
			STRENGTH = Math.min(100 , STRENGTH + change);
		} else {
			// If the change is negative, set strength to either current strength + change or 0 (whichever is larger)
			STRENGTH = Math.max(0, STRENGTH + change);
		}
	}

	// This method adds a value to the Intelligence category
	public static void applyIntelligenceChange (int change) {
		if (change > 0) {
			// If the total intelligence is increasing set strength to either current intelligence + change or 100 (whichever is smaller)
			INTELLIGENCE = Math.min(100 , INTELLIGENCE + change);
		} else {
			// If the change is negative, set intelligence to either current intelligence + change or 0 (whichever is larger)
			INTELLIGENCE = Math.max(0, INTELLIGENCE + change);
		}
	}

	// This method adds a value to the Karma category
	public static void applyKarmaChange (int change) {
		if (change > 0) {
			// If the total karma is increasing set karma to either current karma + change or 100 (whichever is smaller)
			KARMA = Math.min(100 , KARMA + change);
		} else {
			// If the change is negative, set karma to either current karma + change or 0 (whichever is larger)
			KARMA = Math.max(0, KARMA + change);
		}
	}

}
