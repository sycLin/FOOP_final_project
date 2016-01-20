package daifugo;
import java.lang.*;
import java.util.*;

class AIPlayer extends Player{

	private Hand lastHand;
	private boolean lead;
	private int myPosition;
	private boolean forcedExch;

	public AIPlayer() {
		super();
		lead = false;
		forcedExch = false;
	}

	private ArrayList<Hand> find_all_hands(ArrayList<Card> myCards) {
		ArrayList<Hand> allHands = new ArrayList<Hand>();
		ArrayList<Card> tmpCards = new ArrayList<Card>();
		ArrayList<Card> jokerAs = new ArrayList<Card>();

		Collections.sort(myCards);
		for(int i = 0; i < myCards.size(); i ++) {

			if(i < myCards.size() - 3) {
				// [FOUR_OF_A_KIND] without JOKER
				if(myCards.get(i).getRank() == myCards.get(i + 1).getRank() &&
				   myCards.get(i + 1).getRank() == myCards.get(i + 2).getRank() &&
				   myCards.get(i + 2).getRank() == myCards.get(i + 3).getRank()) {
					tmpCards.clear();
					for(int j = 0; j < 4; j ++) {
						tmpCards.add(myCards.get(i + j));
					}
					allHands.add(new Hand(tmpCards));
				}
			} 
			if(i < myCards.size() - 2) {
				
				if(myCards.get(i).getRank() == myCards.get(i + 1).getRank() &&
				   myCards.get(i + 1).getRank() == myCards.get(i + 2).getRank()) {
					// [THREE_OF_A_KIND] without JOKER
					tmpCards.clear();
					for(int j = 0; j < 3; j ++) {
						tmpCards.add(myCards.get(i + j));
					}
					allHands.add(new Hand(tmpCards));

					// Then add THREE_OF_A_KIND plus Joker, (if there is any)
					// as another form of FOUR_OF_A_KIND to all hands
					if(myCards.get(myCards.size() - 1).getSuit() == Card.JOKER) {
						jokerAs.clear();
						tmpCards.add(myCards.get(myCards.size() - 1));
						jokerAs.add(myCards.get(i));
						allHands.add(new Hand(tmpCards, jokerAs));
					}
				}

				// [THREE_OF_A_KIND] with two JOKERs
				if(myCards.get(myCards.size() - 1).getSuit() == Card.JOKER &&
				   myCards.get(myCards.size() - 2).getSuit() == Card.JOKER &&
				   myCards.get(i).getSuit() != Card.JOKER) {
					tmpCards.clear();
					tmpCards.add(myCards.get(i));
					tmpCards.add(myCards.get(myCards.size() - 1));
					tmpCards.add(myCards.get(myCards.size() - 2));

					jokerAs.clear();
					jokerAs.add(myCards.get(i));
					jokerAs.add(myCards.get(i));
					allHands.add(new Hand(tmpCards, jokerAs));
				}
			}
			if(i < myCards.size() - 1) {
				// PAIR
				if(myCards.get(i).getRank() == myCards.get(i + 1).getRank()) {
					// First add [PAIR] to all hands
					tmpCards.clear();
					if(myCards.get(i).getSuit() != Card.JOKER) {
						for(int j = 0; j < 2; j ++) {
							tmpCards.add(myCards.get(i + j));
						}
						allHands.add(new Hand(tmpCards));	
					}
					
					// Then add PAIR plus JOKER as [THREE_OF_A_KIND]
					if(myCards.get(i).getSuit() != Card.JOKER && 
					   myCards.get(myCards.size() - 1).getSuit() == Card.JOKER) {
						jokerAs.clear();
						jokerAs.add(myCards.get(i));
						tmpCards.add(myCards.get(myCards.size() - 1));
						allHands.add(new Hand(tmpCards, jokerAs));
					}
					// [PAIR] plus two JOKERs as [FOUR_OF_A_KIND]
					if(myCards.get(i).getSuit() != Card.JOKER &&
					   myCards.get(myCards.size() - 1).getSuit() == Card.JOKER &&
					   myCards.get(myCards.size() - 2).getSuit() == Card.JOKER) {
					   	tmpCards.clear();
					   	for(int j = 0; j < 2; j ++) {
							tmpCards.add(myCards.get(i + j));
						}
						tmpCards.add(myCards.get(myCards.size() - 1));
						tmpCards.add(myCards.get(myCards.size() - 2));
						jokerAs.clear();
						jokerAs.add(myCards.get(i));
						jokerAs.add(myCards.get(i));
						allHands.add(new Hand(tmpCards, jokerAs));
					}
					// Two JOKERs as [PAIR]
					if(myCards.get(i).getSuit() == Card.JOKER) {
						jokerAs.clear();
						tmpCards.clear();
						tmpCards.add(myCards.get(i));
						tmpCards.add(myCards.get(i + 1));
						for(int j = 1; j <= 13; j ++) {
							jokerAs.add(new Card(Card.HEART, (byte)j));
							jokerAs.add(new Card(Card.SPADE, (byte)j));
							allHands.add(new Hand(tmpCards, jokerAs));
						}
					}

				}
			}
			
			// [SINGLE] without JOKER
			if(myCards.get(i).getSuit() != Card.JOKER) {
				tmpCards.clear();
				tmpCards.add(myCards.get(i));
				allHands.add(new Hand(tmpCards));
			}

			// JOKER as [SINGLE]
			else {
				
				for(int j = 1; j <= 13; j ++) {
					tmpCards.clear();
					tmpCards.add(myCards.get(i));
					jokerAs.clear();
					jokerAs.add(new Card(Card.CLUB, (byte)j));
					allHands.add(new Hand(tmpCards, jokerAs));
				}
			}

			// [PAIR] with one JOKER
			if(myCards.get(myCards.size() - 1).getSuit() == Card.JOKER &&
			   myCards.get(i).getSuit() != Card.JOKER) {
				tmpCards.clear();
				tmpCards.add(myCards.get(i));
				tmpCards.add(myCards.get(myCards.size() - 1));

				jokerAs.clear();
				jokerAs.add(myCards.get(i));
				allHands.add(new Hand(tmpCards, jokerAs));
			}
		}

		return allHands;
	}

	public Hand play_card(ArrayList<Card> myCards) {
		ArrayList<Card> retCards = new ArrayList<Card>();
		ArrayList<Card> jokerAs = new ArrayList<Card>();
		myCards = rearrange(myCards);
		Collections.sort(myCards);
		Hand retHand;
		byte type;
		
		// Find all possible hands
		ArrayList<Hand> allHands = find_all_hands(myCards);
		Collections.sort(allHands);

		// DEBUGGING MODE
		// System.out.print(myPosition + ":");
		// print_cards(myCards);
		
		if(lead) {
			lead = !lead;
			for(int i = 0; i < allHands.size(); i ++) {
				byte tmp = allHands.get(i).getContent().get(0).getRank();
				if(tmp == (byte)7 || tmp == (byte)10) {
					System.out.println(allHands.get(i).toString());
					return allHands.get(i);
				}
			}
			System.out.println(allHands.get(0).toString());
			return allHands.get(0);
		}

		for(int i = 0; i < allHands.size(); i ++) {
			if(Daifugo.canBeat(allHands.get(i).beats(lastHand), lastHand, allHands.get(i))) {
				// System.out.println(allHands.get(i).toString());
				// System.out.println(" can beat ");
				// System.out.println(lastHand.toString());
				return allHands.get(i);
			}
		}

		// PASS
		return new Hand(retCards);
	}


	public ArrayList<Card> give_up_card(ArrayList<Card> myCards, int number) {
		ArrayList<Card> retCards = new ArrayList<Card>();
		ArrayList<Card> arragedMyCards = rearrange(myCards);

		// NEEDY OR EXTREME_NEEDY need to give up the biggest cards at the start of the round.
		if(forcedExch) {
			forcedExch = false;

			// GIVE UP BIGGEST cards
			for(int i = 0; i < number; i ++) {
				retCards.add(arragedMyCards.get(myCards.size() - i - 1));
			}
			
			return retCards;
		}

		if(myCards.size() <= number) {
			return myCards;
		}

		
		
		// GIVE UP SEVEN & TEN FIRST
		for(int i = 0; i < myCards.size() && number > 0; i ++) {
			byte tmp = arragedMyCards.get(i).getRank();

			if(tmp == (byte)7 || tmp == (byte)10) {
				number --;
				retCards.add(arragedMyCards.get(i));
			}
		}

		// GIVE UP SMALLEST cards
		for(int i = 0; i < number; i ++) {
			retCards.add(arragedMyCards.get(i));
		}
		
		for(int i = 0; i < number; i ++) {
			System.out.print(retCards.get(i).toString());
		}
		System.out.println("");
		return retCards;
	}

	/**
	 *	Sort the cards according to the "Real" rank.
	 * @param myCards the cards to be sorted
	 * @return an arraylist of cards that is sorted
	 */
	private ArrayList<Card> rearrange(ArrayList<Card> myCards) {
		ArrayList<Card> retCards = new ArrayList<Card>();
		Collections.sort(myCards);
		for(int i = 0; i < myCards.size(); i ++) {
			if(myCards.get(i).getRank() > 2 && myCards.get(i).getSuit() != Card.JOKER) {
				retCards.add(myCards.get(i));
			}
		}
		for(int i = 0; i < myCards.size(); i ++) {
			if(myCards.get(i).getRank() <= 2 || myCards.get(i).getSuit() == Card.JOKER) {
				retCards.add(myCards.get(i));
			}	
		}
		return retCards;
	}

	// private void print_cards(ArrayList<Card> myCards) {
	// 	String tmpLine = "";
	// 	for(int i = 0; i < myCards.size(); i ++) {
	// 		tmpLine += myCards.get(i).toString();
	// 	}
	// 	System.out.println(tmpLine);
	// }

	public void update_info(Message msg) {
		// A person played his cards, record his cards.
		if((msg.getAction() & Message.ACTION_PLAYING) != 0) {
			lastHand = (Hand)msg.getContent();
		}

		// This AI leads this trick, can't PASS, can play any legal hand.
		else if((msg.getAction() & Message.ACTION_LEADING) != 0) {
			if(myPosition == msg.getPlayer()) {
				lead = true;
			}
		}

		// If NEEDY or EXTREME_NEEDY, they are forced to give the biggest cards.
		else if((msg.getAction() & Message.ACTION_EXCH_CARD) != 0) {
			if(this.get_title() == InfoCenter.NEEDY || this.get_title() == InfoCenter.EXTREME_NEEDY) {
				forcedExch = true;
			}
		}

		// Update my position (position changes in every round)
		else if((msg.getAction() & Message.ACTION_UPDT_POS) != 0) {
			myPosition = msg.getPlayer();
		}

	}
	public void enter_name() {
		name = "AI";
	}
}


