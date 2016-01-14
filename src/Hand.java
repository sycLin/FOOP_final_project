package daifugo;
import java.lang.*;
import java.util.*;

class Hand {
	// ----- constants ----- //
	public static final byte PASS = 0;
	public static final byte SINGLE = 1;
	public static final byte PAIR = 2;
	public static final byte THREE_OF_A_KIND = 3;
	public static final byte FOUR_OF_A_KIND = 4;
	public static final byte STRAIGHT_FLUSH = 5;
	public static final byte UNKNOWN = -1;

	// ----- fields ----- //
	/**
	 * to store the length of the hand
	 */
	private int length;
	/**
	 * to store the content of the hand (as ArrayList of Card class)
	 */
	private ArrayList<Card> content;
	/**
	 * to store the content of the hand (as ArrayList of Card class)
	 */
	private ArrayList<Card> contentWithJoker;
	/**
	 * to store the content of the joker (as ArrayList of Card class)
	 */
	private ArrayList<Card> joker;
	/**
	 * to store the type of this hand (as one of the constants)
	 */
	private byte type;
	/**
	 * to store the power of this hand (with regard to the type)
	 */
	private byte power;

	// ----- actions ----- //
	
	/**
	 * to create a Hand instance from a set of cards
	 * @param _cards an ArrayList of cards to create hand
	 */
	public Hand(ArrayList<Card> _cards) {
		// 1) save the content
		length = _cards.size();
		content = new ArrayList<Card>(_cards);
		Collections.sort(content);
		joker = new ArrayList<Card>();
		// 2) judge type
		judgeType(_cards);
		// 3) add contentWithJoker
		contentWithJoker = new ArrayList<Card>(_cards);
		// 4) count power
		power = countPower(_cards);
	}

	/**
	 * to create a Hand instance from a set of cards, and a set of cards that jokers represent
	 * @param _cards an ArrayList of cards to create hand
	 * @param _jokers an ArrayList of cards that jokers represent
	 */
	public Hand(ArrayList<Card> _cards, ArrayList<Card> _jokers) {
		// 1) save the content
		length = _cards.size();
		content = new ArrayList<Card>(_cards);
		Collections.sort(content);
		joker = new ArrayList<Card>(_jokers);
		Collections.sort(joker);
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
		// 2) judge type
		judgeType(_cards);
		// 3) add contentWithJoker
		contentWithJoker = new ArrayList<Card>(_cards);
		// 4) count power
		power = countPower(_cards);
	}

	/**
	 * to check whether this hand can be played on another
	 * @param another_hand a Hand instance for checking
	 * @return true if can be played on another_hand
	 */
	public boolean beats(Hand another_hand) {
		// lengths not equal, cannot beat
		if (getLength() != another_hand.getLength())
			return false;

		// types not equal, or one of them is UNKNOWN, cannot beat
		if (getType() != another_hand.getType())
			return false;
		else
			if (getType() == UNKNOWN || another_hand.getType() == UNKNOWN)
				return false;

		// compare the power
		if (getPower() > another_hand.getPower())
			return true;
		else if (getPower() < another_hand.getPower())
			return false;
		else {
			if (hasJoker() && !(another_hand.hasJoker())) {
				return true;
			}
			else{
				return false;
			}
		}
	}

	public void judgeType(ArrayList<Card> _cards) {
		// first sort the hand
		Collections.sort(_cards);

		// determine the type
		if (_cards.size() == 0)
			type = PASS;
		else if (_cards.size() == 1)
			type = SINGLE;
		else if (_cards.size() == 2 && _cards.get(0).getRank() == _cards.get(1).getRank())
			type = PAIR;
		else if (_cards.size() == 3 
			&& _cards.get(0).getRank() == _cards.get(1).getRank() 
			&& _cards.get(0).getRank() == _cards.get(2).getRank())
			type = THREE_OF_A_KIND;
		else if (_cards.size() == 4 
			&& _cards.get(0).getRank() == _cards.get(1).getRank() 
			&& _cards.get(0).getRank() == _cards.get(2).getRank()
			&& _cards.get(0).getRank() == _cards.get(3).getRank())
			type = FOUR_OF_A_KIND;
		else if (_cards.size() >= 4) {
			// check if straight flush
			if (isStraightFlush(_cards))
				type = STRAIGHT_FLUSH;
			else
				type = UNKNOWN;
		} else {
			type = UNKNOWN;
		}
	}

	public byte countPower(ArrayList<Card> cards) {
		byte ret = 0;
		byte tmp = 0;
		for (int i = 0; i < cards.size(); i++) {
			tmp = cards.get(i).getRank();
			if (cards.get(i).getRank() <= 2)
				tmp += 13;
			ret += tmp;
		}
		return ret;
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
		ArrayList<Card> first;
		ArrayList<Card> second;
		if (hasJoker())
			first = getJokerContent();
		else 
			first = getContent();
		if (another_hand.hasJoker())
			second = another_hand.getJokerContent();
		else 
			second = another_hand.getContent();
		for (int i = 0; i < getLength(); i++)
			if (first.get(i).getSuit() != second.get(i).getSuit())
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

	public boolean hasJoker() {
		if (getJoker().size() > 0)
			return true;
		else 
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
	 * to get the joker of this hand
	 * @return the joker
	 */
	public ArrayList<Card> getJoker() {
		return this.joker;
	}

	/**
	 * to get the content of this hand (with jokers inserting)
	 * @return the contentWithJoker
	 */
	public ArrayList<Card> getJokerContent() {
		return this.contentWithJoker;
	}

	/**
	 * to get the jokers of this hand
	 * @return the jokers
	 */
	public byte getPower() {
		return this.power;
	}

	/**
	 * to get all the effects this hand has
	 * @return array of integers indicating the count of a specific effect.
	 */
	public Map<String, Integer> getEffects() {
		Map<String, Integer> effectMap = new HashMap<String, Integer>();	// Map is abstract
		int skipfive = 0;
		int giveSeven = 0;
		int endEight = 0;
		int abandonTen = 0;
		int jackBack = 0;
		ArrayList<Card> cards = getJokerContent();
		for (int i = 0; i < cards.size(); i++) {
			if (cards.get(i).getRank() == 5)
				skipfive++;
			else if (cards.get(i).getRank() == 7)
				giveSeven++;
			else if (cards.get(i).getRank() == 8)
				endEight++;
			else if (cards.get(i).getRank() == 10)
				abandonTen++;
			else if (cards.get(i).getRank() == 11)
				jackBack++;
		}
		effectMap.put("SkipFive", skipfive);
		effectMap.put("GiveSeven", giveSeven);
		effectMap.put("EndEight", endEight);
		effectMap.put("AbandonTen", abandonTen);
		effectMap.put("JackBack", jackBack);
		return effectMap;
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
			case PASS:
				ret += "Pass";
				break;
			default:
				ret += "unknown";
				break;
		}
		ret += "]\n";
		// then print the content of the hand
		for (int i = 0; i < contentWithJoker.size(); i++) {
			ret += (contentWithJoker.get(i) + " ");
		}
		return ret;
	}
}