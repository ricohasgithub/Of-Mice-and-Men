package character;

/*
 * 		Rico Zhu			Jan 10th, 2019
 * 
 * 		This class acts as a reference class for all of the player's resources
 * 		All classes will be able to access the player's resources through this class
 */

public class Resources {

	// Player Currency - default is $150
	private static int MONEY = 150;

	// Inventory is the player's inventory (size 5) - this stores the player's items
	private static String[] Inventory = new String[5];

	// This method returns the amount of money the player has
	public static int getMoney () {
		return MONEY;
	}

	// This method checks to see if the player could afford a certain item
	public static boolean canAffordItem (int price) {
		return MONEY >= price;
	}
	
	// This method decreases a player's total money. Similar to applyMoneyChange except that you always lose money.
	public static void loseMoney (int change) {
		// Set money to be money + change
		MONEY += change;
		System.out.println(MONEY);
		// If the amount of money become negative, set it to 0
		if (MONEY < 0) {
			MONEY = 0;
		}
	}

	// This method applies a change to the player's money total
	public static void applyMoneyChange (int change) {
		if ((MONEY + change) > 0) {
			// Check to see if the change would be in bounds
			MONEY += change;
		}
	}

	// This method adds an item (String) to the player's inventory if it's not full
	public static void addItem (String item) {
		if (getOpenInventorySlot() != -1) {
			// If there is an open spot, add the item into that index
			Inventory[getOpenInventorySlot()] = item;
		}
	}

	// This method checks to see if the player has a specific item in their inventory
	public static boolean hasItem (String item) {
		// Iterate through the inventory of the player and check to see if each index contains the item
		for (int i=0; i<Inventory.length; i++) {
			if (Inventory[i] != null && Inventory[i].equals(item)) {
				// Item found
				return true;
			}
		}
		// No item found, return false
		return false;
	}

	// This method removes an item from the player's inventory
	public static void removeItem (String item) {
		// Iterate through the inventory of the player and check to see if each index contains the item
		for (int i=0; i<Inventory.length; i++) {
			if (Inventory[i] != null && Inventory[i].equals(item)) {
				// Item found, remove item (replace by null)
				Inventory[i] = null;
			}
		}
	}

	// This method returns the index of an empty element in the inventory, if it is full it returns -1
	public static int getOpenInventorySlot () {
		// Iterate through the entire inventory and check if each index is empty
		for (int i=0; i<Inventory.length; i++) {
			if (Inventory[i] == null) {
				// Empty index found, inventory is not full - return the index number
				return i;
			}
		}
		// All indexes occupied, return -1
		return -1;
	}

	// This method checks to see if the player is bankrupt
	public static boolean isBankrupt () {
		return MONEY <= 0;
	}

	// This method checks to see if the player is rich enough to win the game
	public static boolean isRich () {
		return MONEY >= 500;
	}

}
