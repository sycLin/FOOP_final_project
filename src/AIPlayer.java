package daifugo;
import java.lang.*;
import java.util.*;

class AIPlayer extends Player{

	private Hand lastHand;
	private boolean lead;
	private boolean isReversed;
	private boolean isTight;
	private int myPosition;
	private boolean forcedExch;

	public AIPlayer() {
		super();
		lead = false;
		isReversed = false;
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
					for(int j = 0; j < 2; j ++) {
						tmpCards.add(myCards.get(i + j));
					}
					allHands.add(new Hand(tmpCards));
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

		// ArrayList<Card> tmpJokers = new ArrayList<Card>();
		// // Remove JOKERS from myCards first
		// // In the implementation, it doesn't need to go back.
		// for(int i = 0; i < 2; i ++) {
		// 	if(myCards.get(myCards.size() - 1).getSuit() == Card.JOKER) {
		// 		tmpJokers.add(myCards.get(myCard.size() - 1));
		// 		myCards.remove(myCards.size() - 1);
		// 	}
		// }
		// // Move ACEs and TWOs to the last.
		// for(int i = 0; i < myCards.size(); i ++) {
		// 	if(myCards.get(0).getRank() <= 2) {
		// 		myCards.add(myCards.get(i));
		// 		myCards.remove(0);
		// 	}
		// 	else {
		// 		break;
		// 	}
		// }

		// // Create four ArrayList<Card> representing four Suit.
		// ArrayList<Card> clubs = new ArrayList<Card>();
		// ArrayList<Card> diamonds = new ArrayList<Card>();
		// ArrayList<Card> hearts = new ArrayList<Card>();
		// ArrayList<Card> spades = new ArrayList<Card>();

		// // And separate myCards into four suits.
		// for(int i = 0; i < myCards.size(); i ++) {
		// 	switch(myCards.get(i).getSuit()) {
		// 		case Card.CLUB:
		// 			clubs.add(myCards.get(i));
		// 			break;
		// 		case Card.DIAMOND:
		// 			diamonds.add(myCards.get(i));
		// 			break;
		// 		case Card.HEART:
		// 			hearts.add(myCards.get(i));
		// 			break;
		// 		case Card.SPADE:
		// 			spade.add(myCards.get(i));
		// 			break;
		// 	}
		// }
		// ArrayList<ArrayList<Card>> all = new ArrayList<ArrayList<Card>>();
		// all.add(clubs);
		// all.add(diamonds);
		// all.add(hearts);
		// all.add(spades);


		// Hand tmpHand;
		// for(int i = 0; i < 4; i ++) {
		// 	ArrayList<Card> sameSuits = all.get(i);
		// 	for(int j = 0; j < sameSuits.size() - 3; j ++) {
		// 		// [STRAIGHT_FLUSH] Detection
		// 		// Case 1: WITHOUT JOKERS
		// 		tmpCards.clear();
		// 		for(int k = 0; k < 4; k ++) {
		// 			tmpCards.add(sameSuits.get(j + k));
		// 		}
		// 		tmpHand = new Hand(tmpCards);
		// 		if(tmpHand.isStraightFlush()) {
		// 			allHands.add(tmpHand);
		// 		}

		// 		// Case 2: WITH one JOKER
		// 		if(tmpJokers.size() == 1) {
		// 			// Case 2.1: [JOKER][1][2][3]
		// 			tmpCards.clear();
		// 			if(sameSuits.get(j).getRealRank() > 3) {
		// 				tmpCards.add(new Card(sameSuits.get(j).getSuit(), sameSuits.get(j).getRealRank() - 1));
		// 				for(int k = 0; k < 3; k ++) {
		// 					tmpCards.add(sameSuits.get(j + k));
		// 				}
		// 			}
		// 			tmpHand = new Hand(tmpCards);
		// 			if(tmpHand.isStraightFlush()) {
		// 				jokerAs.clear();
		// 				jokerAs.add(tmpCards.get(0));
		// 				allHands.add(tmpHand);
		// 			}
		// 			// Case 2.2: [1][JOKER][2][3]
		// 			tmpCards.clear();
		// 			tmpCards.add(new Card(sameSuits.get(j).getSuit(), sameSuits.get(j).getRealRank() + 1));
		// 			for(int k = 0; k < 3; k ++) {
		// 				tmpCards.add(sameSuits.get(j + k));
		// 			}
		// 			tmpHand = new Hand(tmpCards);
		// 			if(tmpHand.isStraightFlush()) {
		// 				allHands.add(tmpHand);
		// 			}
		// 			// Case 2.2: [1][2][JOKER][3]
		// 			tmpCards.clear();
		// 			tmpCards.add(new Card(sameSuits.get(j).getSuit(), sameSuits.get(j).getRealRank() + 1));
		// 			// Case 2.2: [1][2][3][JOKER]
		// 		}
				

				

		// }
		

		

		return allHands;
	}

	public Hand play_card(ArrayList<Card> myCards) {
		ArrayList<Card> retCards = new ArrayList<Card>();
		ArrayList<Card> jokerAs = new ArrayList<Card>();
		myCards = rearrange(myCards);
		Collections.sort(myCards);
		Hand retHand;
		byte type;
		//System.out.println("FINDING ALL HANDS");
		ArrayList<Hand> allHands = find_all_hands(myCards);
		Collections.sort(allHands);

		// DEBUGGING MODE
		System.out.print(myPosition + ":");
		print_cards(myCards);
		// for(int i = 0; i < allHands.size(); i ++) {
		// 	System.out.println(allHands.get(i).toString());
		// }

		if(lead) {
			lead = !lead;
			//int rand = (int)(Math.random() * allHands.size());

			System.out.println(allHands.get(0).toString());
			return allHands.get(0);
		}

		for(int i = 0; i < allHands.size(); i ++) {
			if(Daifugo.canBeat(allHands.get(i).beats(lastHand), lastHand, allHands.get(i))) {
				System.out.println(allHands.get(i).toString());
				System.out.println(" can beat ");
				System.out.println(lastHand.toString());
				return allHands.get(i);
			}
		}

		// DEBUGGING MODE
		// Scanner scanner = new Scanner(System.in);
		// String x = scanner.nextLine();
		System.out.println("PASS");
		return new Hand(retCards);
	}

	private int[] generate_random(int rng, int number) {
		int[] ret = new int[number];
		boolean flag;
		for(int i = 0; i < number; i ++) {
			
			while(true) {
				flag = true;
				ret[i] = (int)Math.random() * rng;
				for(int j = 0; j < i; j ++) {
					if(ret[i] == ret[j]) {
						flag = false;
					}
				}
				if(flag) {
					break;
				}
			}
		}
		return ret;
	}

	public ArrayList<Card> give_up_card(ArrayList<Card> myCards, int number) {
		ArrayList<Card> retCards = new ArrayList<Card>();
		ArrayList<Card> arragedMyCards = rearrange(myCards);
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

		// GIVE UP SMALLEST cards
		System.out.println("GIVE UP CARD");
		
		for(int i = 0; i < number; i ++) {
			retCards.add(arragedMyCards.get(i));
		}
		System.out.print("Give up ");
		for(int i = 0; i < number; i ++) {
			System.out.print(retCards.get(i).toString());
		}
		System.out.println("");
		return retCards;
	}

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

	private void print_cards(ArrayList<Card> myCards) {
		String tmpLine = "";
		for(int i = 0; i < myCards.size(); i ++) {
			tmpLine += myCards.get(i).toString();
		}
		System.out.println(tmpLine);
	}

	public void update_info(Message msg) {
		// A person played his cards.
		if((msg.getAction() & Message.ACTION_PLAYING) != 0) {
			lastHand = (Hand)msg.getContent();
			isReversed = msg.isUnderRevolution() ^ msg.isUnderJackBack();
			
			isTight = msg.isTight();
			// System.out.println("Player" + Integer.toString(msg.getPlayer()) + "'s hand was " + lastHand.toString());
			// Someone won.
			if((msg.getAction() & Message.ACTION_WINNING) != 0) {
				System.out.println("Player at position " + msg.getPlayer() + "won.");	
			}
		}

		// DONE
		else if((msg.getAction() & Message.ACTION_LEADING) != 0) {
			//System.out.println("----- Trick " + Integer.toString((int)msg.getContent()) + " -----");
			//System.out.println("The leader of this trick is position " + Integer.toString(msg.getPlayer()));
			if(myPosition == msg.getPlayer()) {
				lead = true;
			}
		}

		// DONE
		else if((msg.getAction() & Message.ACTION_EXCH_CARD) != 0) {
			if(this.get_title() == InfoCenter.NEEDY || this.get_title() == InfoCenter.EXTREME_NEEDY) {
				forcedExch = true;
				System.out.println("HAHAHA");
			}
			else {
				System.out.println("HEYHEY");
			}
		}

		else if((msg.getAction() & Message.ACTION_UPDT_POS) != 0) {
			myPosition = msg.getPlayer();
			String[] tmpNames = (String[])msg.getContent();
			
		}
		else if((msg.getAction() & Message.ACTION_UPDT_SCORE) != 0) {
			int[] scores = (int[])msg.getContent();
			System.out.println("------ SCORES ------");
			for(int i = 0; i < 4; i ++) {
				System.out.println(i + ": " + scores[i]);
			}
		}
	}
	public void enter_name() {
		name = "9527";
	}
}


