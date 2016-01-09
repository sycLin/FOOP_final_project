package daifugo;
import java.lang.*;
import java.util.*;

class Hand {
	// ----- constants ----- //
	public static final byte SINGLE = 1;
	public static final byte PAIR = 2;
	public static final byte THREE_OF_A_KIND = 3;
	public static final byte FOUR_OF_A_KIND = 4;
	public static final byte STRAIGHT_FLUSH = 5;
	public static final byte UNKNOWN = -1;

	// ----- fields ----- //
	/**
	 * to store the content of the hand (as ArrayList of Card class)
	 */
	private ArrayList<Card> content;
	/**
	 * to store the type of this hand (as one of the constants)
	 */
	private byte type;
	/**
	 * to store the power of this hand (with regard to the type)
	 */
	private int power;

	// ----- actions ----- //
	
	/**
	 * to create a Hand instance from a set of cards
	 * @param _cards an ArrayList of cards to create hand
	 */
	public Hand(ArrayList<Card> _cards) {
		// TODO
		// 1) determine the type
		if(_cards.size() == 1)
			type = SINGLE;
		else if(_cards.size() == 2 && _cards.get(0).getRank() == _cards.get(1).getRank())
			type = PAIR;
		else if(_cards.size() == 3 
			&& _cards.get(0).getRank() == _cards.get(1).getRank() 
			&& _cards.get(0).getRank() == _cards.get(2).getRank())
			type = THREE_OF_A_KIND;
		else if(_cards.size() == 4 
			&& _cards.get(0).getRank() == _cards.get(1).getRank() 
			&& _cards.get(0).getRank() == _cards.get(2).getRank()
			&& _cards.get(0).getRank() == _cards.get(3).getRank())
			type = FOUR_OF_A_KIND;
		else if(_cards.size() >= 4) {
			// check if straight flush
			// first, sort the hand
			Collections.sort(_cards);
			// TODO ...
		} else {
			type = UNKNOWN;
		}
		// 2) determine the power
		// 3) save the content
		content = new ArrayList<Card>(_cards);
	}



	/**
	 * to check whether this hand can be played on another
	 * @param another_hand a Hand instance for checking
	 * @return true if can be played on another_hand
	 */
	public boolean beats(Hand another_hand) {
		System.err.println("beats() function not implemented yet...");
		return false;
	}

	/**
	 * to get the type of this hand
	 * @return a byte representing the type of this hand
	 */
	public byte getType() {
		byte ret = type;
		return ret;
	}

	/**
	 * to represent the hand with a string
	 * @return a string with information on this hand
	 */
	public String toString() {
		// print the type of the hand first
		String ret = "[";
		switch(type) {
			case SINGLE:
				ret += "Single";
				break;
			case PAIR:
				ret += "Pair";
				break;
			case THREE_OF_A_KIND:
				ret += "Three of a kind";
				break;
			case FOUR_OF_A_KIND:
				ret += "Four of a kind";
				break;
			case STRAIGHT_FLUSH:
				ret += "Straight flush";
				break;
			default:
		}
		ret += "]\n";
		// then print the content of the hand
		for(int i=0; i<content.size(); i++) {
			ret += (content.get(i) + " ");
		}
		return ret;
	}

	public ArrayList<Card> getContent() {
		return this.content;
	}

	public boolean isTight(Hand another_hand) {

	}

	public int numberOfThis(int rank) {

	}
}