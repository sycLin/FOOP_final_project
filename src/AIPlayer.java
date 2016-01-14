package daifugo;
import java.lang.*;
import java.util.*;

class AIPlayer extends Player{
	public AIPlayer() {
		super();
	}
	public Hand play_card(ArrayList<Card> myCards) {
		ArrayList<Card> retCards = new ArrayList<Card>();
		ArrayList<Card> jokerAs = new ArrayList<Card>();
		Scanner scanner = new Scanner(System.in);
		Collections.sort(myCards);
		Hand retHand;
		
		while(true) {
			System.out.println("Your cards:");
			printCards(myCards);
			System.out.println("Please enter the cards you want to play.");
			String input = scanner.nextLine();
			String[] cardIndices = input.split(" ");
			int jokerNum = 0;
			// PASS
			if(cardIndices.length == 0) {
				retHand = new Hand(retCards);
				break;
			}
			try {	
				for(int i = 0; i < cardIndices.length; i ++) {
					Card tmp = myCards.get(Integer.parseInt(cardIndices[i]));
					retCards.add(tmp);
					jokerNum += (tmp.getRank() == 0) ? 1 : 0;
				}
				for(int i = 0; i < retCards.size(); i ++) {
					System.out.println(retCards.get(i).toString());
				}
			}
			catch(Exception ex) {
				System.out.println("Please enter integer within 0 ~ " + Integer.toString(myCards.size() - 1));

				continue;
			}
			if(jokerNum > 0) {
				while(true) {
					System.out.println("You have " + jokerNum + " jokers.");
					System.out.println("What do you want your jokers be?");
					input = scanner.nextLine();
					String[] cards = input.split(" ");
					if(cards.length != jokerNum) {
						System.out.println("Number not matched. Please enter again.");
						continue;
					}
					try {
						for(int i = 0; i < jokerNum; i ++) {
							jokerAs.add(new Card(cards[i]));
						}
					}
					catch(Exception ex) {
						System.out.println("Wrong card format, please enter again.");
						continue;
					}
					break;
				}
				retHand = new Hand(retCards, jokerAs);
			}
			// There is no Joker.
			else {
				retHand = new Hand(retCards);	
			}
			if(retHand.getType() == Hand.UNKNOWN) {
				System.out.println("Type unknown");
				retCards.clear();
				continue;
			}
			break;
		}
		return retHand;
	}
	public ArrayList<Card> give_up_card(ArrayList<Card> myCards, int number) {
		if(myCards.size() <= number) {
			return myCards;
		}
		ArrayList<Card> retCards = new ArrayList<Card>();
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please give up " + number + " cards.");
		Collections.sort(myCards);

		while(true) {
			System.out.println("Your cards:");
			printCards(myCards);
			System.out.println("Please enter the cards you want to give up.");
			String input = scanner.nextLine();
			String[] cardIndices = input.split(" ");
			if(cardIndices.length != number) {
				System.out.println("Please enter " + number + " cards.");
				continue;
			}
			try {	
				for(int i = 0; i < cardIndices.length; i ++) {
					retCards.add(myCards.get(Integer.parseInt(cardIndices[i])));	
				}
			}
			catch(Exception ex) {
				System.out.println("Please enter integers within 0 ~ " + Integer.toString(myCards.size() - 1));
				continue;
			}
			break;
		}
		return retCards;
	}
	public void update_info(Message msg) {
		if(msg.getType() == Message.ERROR) {
			Hand lastHand = (Hand)msg.getContent();
			if((msg.getAction() & Message.ACTION_CANT_BEAT) != 0) {
				System.out.println("Your hand couldn't beat the last hand, please play your cards again.");
			}
			else if((msg.getAction() & Message.ACTION_WRONG_TYPE) != 0) {
				System.out.println("You played wrong type of hand, please play your cards again.");
			}
			System.out.println("The last hand was " + lastHand.toString());
		}
		else if(msg.getType() == Message.BASIC) {
			// The KING lost
			if((msg.getAction() & Message.ACTION_LOSING) != 0) {
				ArrayList<Card> kingCards = (ArrayList<Card>)msg.getContent();
				System.out.println("The KING lost, here are his remains.");
				printCards(kingCards);
			}
			// A person played his cards.
			else if((msg.getAction() & Message.ACTION_PLAYING) != 0) {
				Hand lastHand = (Hand)msg.getContent();
				System.out.println("Player" + Integer.toString(msg.getPlayer()) + "'s hand was " + lastHand.toString());
				// Someone won.
				if((msg.getAction() & Message.ACTION_WINNING) != 0) {
					System.out.println("Player at position " + msg.getPlayer() + "won.");	
				}
			}

			else if((msg.getAction() & Message.ACTION_NEW_ROUND) != 0) {
				System.out.println("-----------");
				System.out.println("| Round " + Integer.toString((int)msg.getContent()) + " |");
				System.out.println("-----------");
			}
			
			else if((msg.getAction() & Message.ACTION_PASSING) != 0) {
				Hand lastHand = (Hand)msg.getContent();
				System.out.println("Player" + Integer.toString(msg.getPlayer()) + " passed");
				System.out.println("The last hand was " + lastHand.toString());
			}
			else if((msg.getAction() & Message.ACTION_LEADING) != 0) {
				System.out.println("----- Trick " + Integer.toString((int)msg.getContent()) + " -----");
				System.out.println("The leader of this trick is position " + Integer.toString(msg.getPlayer()));
			}
			else if((msg.getAction() & Message.ACTION_EXCH_CARD) != 0) {
				switch(this.get_title()) {
					case InfoCenter.GRAND_MILLIONAIRE:
						System.out.println("You are the GRAND MILLIONAIRE, now it's your turn to give up two cards.");
						break;
					case InfoCenter.MILLIONAIRE:
						System.out.println("You are the MILLIONAIRE, now it's your turn to give up one cards.");	
						break;
					case InfoCenter.NEEDY:
						System.out.println("You are the NEEDY, now it's your turn to give up one cards.");		
						break;
					case InfoCenter.EXTREME_NEEDY:
						System.out.println("You are the EXTREME NEEDY, now it's your turn to give up two cards.");
						break;
				}
			}
		}
	}
	public void enter_name() {
		Scanner scanner = new Scanner(System.in);
		while(true) {
			System.out.println("Please enter your name:");
			String tmp = scanner.nextLine();
			if(tmp.length() > Player.MAX_NAME_LENGTH) {
				System.out.println("Please enter name length smaller than 50.");
				continue;
			}
			name = tmp;
			break;
		}
	}
	private void printCards(ArrayList<Card> myCards) {
		for(int i = 0; i < myCards.size(); i ++) {
			System.out.print("(" + i + ")" + myCards.get(i).toString());
		}
		System.out.println("");
	}
}


/**
 *	TODO LIST:
 *	1. When pass, any string includes "PASS", or "-1"
 *	2. fix the bugs of two space for split.
 */
