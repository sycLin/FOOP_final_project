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
	 * to store the length of the hand
	 */
	private int length;
	/**
	 * to store the length of the hand
	 */
	private ArrayList<Card> joker;
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
		// 1) judge type
		judgeType(_cards);
		// 2) determine the power
		// 3) save the content
		length = _cards.size();
		content = new ArrayList<Card>(_cards);
	}

	/**
	 * to create a Hand instance from a set of cards, and a set of cards that jokers represent
	 * @param _cards an ArrayList of cards to create hand
	 * @param _jokers an ArrayList of cards that jokers represent
	 */
	public Hand(ArrayList<Card> _cards, ArrayList<Card> _jokers) {
		joker = new ArrayList<Card>(_jokers);
		int jokerSize = _jokers.size();
		for (int i = 0; i < _cards.size(); i++) {
			if (_cards.get(i).getRank() == 0) {
				_cards.remove(i);
				if (jokerSize >= 0)
					_cards.add(i, _jokers.get(--jokerSize));
				else 
					System.out.println("Wrong parameters");
			}
		}
		// 1) judge type
		judgeType(_cards);
		// 2) determine the power
		// 3) save the content
		length = _cards.size();
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

	public void judgeType(ArrayList<Card> _cards) {
		// first sort the hand
		Collections.sort(_cards);

		// determine the type
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
			boolean sfFlag = isStraightFlush(_cards);
		} else {
			type = UNKNOWN;
		}
	}

	/**
	 * to know if tight with the given hand
	 * @param another_hand the given hand
	 * @return true if tight; false, otherwise.
	 */
	public boolean isTight(Hand another_hand) {
		// lengths not equal, no tight
		if (getLength() != another_hand.getLength())
			return false;

		// types not equal, or one of them is UNKNOWN, no tight
		if (getType() != another_hand.getType())
			return false;
		else
			if (getType() == UNKNOWN || another_hand.getType() == UNKNOWN)
				return false;

		// compare the suit
		for (int i = 0; i < getLength(); i++)
			if (getContent().get(i).getSuit() != another_hand.getContent().get(i).getSuit())
				return false;

		return true;
	}

	public boolean isStraightFlush(ArrayList<Card> cards) {
		// size < 4, GG
		if (cards.size() < 4) return false;

		// suit not consistent, GG
		byte suit = cards.get(0).getSuit();
		for (int i = 1; i < cards.size(); i++)
			if (cards.get(i).getSuit() != suit)
				return false;

		// rank not continuous, GG
		ArrayList<Byte> rankList = new ArrayList<Byte>();
		ArrayList<Byte> tmpList = new ArrayList<Byte>();
		for (int i = 0; i < cards.size(); i++) {
			if (cards.get(i).getRank() <= 2) {
				byte tmp = cards.get(i).getRank();
				tmp += 13;
				tmpList.add(tmp);
			}
			else
				rankList.add(cards.get(i).getRank());
		}
		for (int i = 0; i < tmpList.size(); i++)
			rankList.add(tmpList.get(i));

		for (int i = 0; i < rankList.size()-1; i++)
			if ((rankList.get(i+1) - rankList.get(i)) != 1)
				return false;

		// suit consistent, rank continuous
		return true;
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
	 * to get the length of this hand
	 * @return a integer representing the length of this hand
	 */
	public int getLength() {
		int ret = length;
		return ret;
	}

	/**
	 * to get the content of this hand
	 * @return the content
	 */
	public ArrayList<Card> getContent() {
		return this.content;
	}

	/**
	 * to get the jokers of this hand
	 * @return the jokers
	 */
	public ArrayList<Card> getJokerContent() {
		return this.joker;
	}

	/**
	 * to get all the effects this hand has
	 * @return array of integers indicating the count of a specific effect.
	 */
	public int[] getEffects() {
		return null;
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
}