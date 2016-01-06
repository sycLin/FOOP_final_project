package daifugo;
import java.lang.*;
import java.util.*;

class Deck {

	/**
	 * to store the content of the deck
	 */
	private ArrayList<Card> cards;

	/**
	 * which card index to return next
	 */
	private int current_index;

	/**
	 * to initialize the instance variables
	 */
	public Deck() {
		current_index = 0;
		cards = new ArrayList<Card>();
	}

	/**
	 * to open a new deck of 54 cards
	 */
	public void open() {
		// first, remove all cards in the current deck
		cards.clear();
		// reset current_index to 0
		current_index = 0;
		// add 52 cards
		for(int i=1; i<=13; i++) {
			cards.add(new Card(Card.CLUB, (byte)i));
			cards.add(new Card(Card.DIAMOND, (byte)i));
			cards.add(new Card(Card.HEART, (byte)i));
			cards.add(new Card(Card.SPADE, (byte)i));
		}
		// add 2 jokers
		cards.add(new Card(Card.JOKER, (byte)0));
		cards.add(new Card(Card.JOKER, (byte)0));
	}

	/**
	 * to shuffle the deck
	 */
	public void shuffle() {
		Collections.shuffle(cards);
	}

	/**
	 * to get the next card in deck
	 * @return the card got from the deck
	 */
	public Card getNextCard() {
		// check if no more cards in this deck
		if(remainingCount() <= 0) {
			shuffle();
			current_index = 0;
		}
		// return the desired card and increase the counter
		return cards.get(current_index++);
	}

	/**
	 * to know if there are still cards in the deck
	 * @return a integer of how many cards left in deck
	 */
	private int remainingCount() {
		// current_index is actually the "next card index" to return
		// please notice this.
		return(cards.size() - current_index);
	}
}

