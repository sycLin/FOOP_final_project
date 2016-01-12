package daifugo;
import java.lang.*;
import java.util.*;

final class Card implements Comparable<Card> {
	
	// ----- constants ----- //
	public static final byte CLUB = 4;
	public static final byte DIAMOND = 3;
	public static final byte HEART = 2;
	public static final byte SPADE = 1;
	public static final byte JOKER = 0;

	// ----- fields ----- //
	
	private byte suit;
	private byte rank;


	// ----- actions ----- //

	/**
	 * to create the card from given suit and rank
	 * @param s the suit to create card
	 * @param r the rank to create card
	 */
	public Card(byte s, byte r) {
		suit = s;
		rank = r;
	}
	public Card(String info) {
		switch(info.charAt(0)) {
			case 'C': 
				suit = CLUB;
				break;
			case 'D':
				suit = DIAMOND;
				break;
			case 'H':
				suit = HEART;
				break;
			case 'S':
				suit = SPADE;
				break;
		}
		switch(info.charAt(1)) {
			
			case 'A':
				rank = (byte)1;
				break;
			case 'K':
				rank = (byte)13;
				break;
			case 'Q':
				rank = (byte)12;
				break;
			case 'J':
				rank = (byte)11;
				break;
			case 'T':
				rank = (byte)10;
				break;
			// 2 ~ 9
			default: 
				rank = (byte)(info.charAt(1) - '0');
		}
	}

	/**
	 * to get the suit of the card
	 * @return the suit of the card
	 */
	public byte getSuit() {
		byte ret = suit;
		return ret;
	}

	/**
	 * to get the rank of the card
	 * @return the rank of the card
	 */
	public byte getRank() {
		byte ret = rank;
		return rank;
	}

	public int compareTo(Card another_card) {
		// handle Joker cases
		if(getRank() == 0)
			return 1; // greater than
		if(another_card.getRank() == 0)
			return -1; // less than
		// handle normal cases
		if(getRank() > another_card.getRank()) return 1;
		if(getRank() < another_card.getRank()) return -1;
		return 0;
	}

	/**
	 * to represent the card with a string
	 * @return a string to represent the card
	 */
	public String toString() {
		String ret = "[";
		switch(suit) {
			case CLUB:
				ret += "C";
				break;
			case DIAMOND:
				ret += "D";
				break;
			case HEART:
				ret += "H";
				break;
			case SPADE:
				ret += "S";
				break;
			case JOKER:
				ret += "*";
				break;
			default: // unknown suit...
				ret += "X";
		}
		switch(rank) {
			case 1:
				ret += "A";
				break;
			case 13:
				ret += "K";
				break;
			case 12:
				ret += "Q";
				break;
			case 11:
				ret += "J";
				break;
			case 10:
				ret += "T";
				break;
			default:
				ret += rank;
		}
		return ret+"]";
	}
}