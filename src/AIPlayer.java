package daifugo;
import java.lang.*;
import java.util.*;

class AIPlayer extends Player{
	AIPlayer() {
		super();
	}

	private Message status;
	public void update_info(Message msg) {
		status = msg;
	}
	public ArrayList<Card> play_card(ArrayList<Card> myCards) {
		// New trick, randomly choose one kind of hand
		if(status.isNewTrick) {
			int rand = (int)(Math.random() * 5) + 1;
			return find_hand(myCards, (byte)rand);
		}
		else {
			return find_hand(myCards, status.lastHand.getType());
		}
	}

	/**
	 *	Randomly give up cards.
	 */
	public ArrayList<Card> give_up_card(ArrayList<Card> myCards, int number) {
		int total = myCards.size();
		if(total <= number) {
			return myCards;
		}
		int[] randoms = generate_random(total, number);
		ArrayList<Card> ret = new ArrayList<Card>();
		for(int i = 0; i < number; i ++) {
			ret.add(myCards.get(randoms[i]));
		}
	}

	// Randomly choose a hand with the type that can beat 
	// the last hand
	private ArrayList<Card> find_hand(ArrayList<Card> myCards, byte type) {
		boolean isReversed = false;
		ArrayList<Card> retHand = new ArrayList<Card>();
		if(status.isUnderJackBack) {
			isReversed = !isReversed;
		}
		if(status.isUnderRevolution) {
			isReversed = !isReversed;
		}

		switch(type) {
			case Hand.SINGLE:

				break;
			case Hand.PAIR:
				break;
			case Hand.THREE_OF_A_KIND:
				break;
			case Hand.FOUR_OF_A_KIND:
				break;
			case Hand.STRAIGHT_FLUSH:
				break;
		}
	}

	public abstract ArrayList<Card> sort(ArrayList<Card> myCards) {
		
	}

	/** 
	 *	generate number distinct randoms in the range 0 ~ total - 1
	 */
	private int[] generate_random(int total, int number) {
		int[] randoms = new int[number];
		for(int i = 0; i < number; i ++) {
			while(true) {
				boolean flag = true;
				randoms[i] = (int)(Math.random() * number);
				for(int j = 0; j < i; j ++) {
					if(randoms[i] == randoms[j]) {
						flag = false;
					}
				}
				if(flag)
					break;
			}
		}
		return randoms;
	}
}