package daifugo;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*

class HumanPlayer extends Player{
	public HumanPlayer() {
		super();
	}
	public Hand play_card(ArrayList<Card> myCards) {
		ArrayList<Card> retCards = new ArrayList<Card>();
		ArrayList<Card> jokerAs = new ArrayList<Card>();
		ArrayList<Integer> records = new ArrayList<Integer>();
		Scanner scanner = new Scanner(System.in);
		Collections.sort(myCards);
		Hand retHand;
		
		while(true) {
			System.out.println("Your cards:");
			print_cards(myCards);
			System.out.println("Please enter the card indices you want to play.");
			System.out.println("Or enter -1 to pass.");

			String input = scanner.nextLine();
			String[] cardIndices = input.split(" ");
			int jokerNum = 0;
			// PASS
			if(cardIndices.length == 0 || cardIndices[0].equals("-1")) {
				retHand = new Hand(retCards);
				break;
			}
			try {	
				for(int i = 0; i < cardIndices.length; i ++) {
					if(cardIndices[i].equals("")) {
						continue;
					}
					int idx = Integer.parseInt(cardIndices[i]);
					records.add(idx);
					Card tmp = myCards.get(idx);
					retCards.add(tmp);
					jokerNum += (tmp.getRank() == 0) ? 1 : 0;
				}
				if(repeat_record(records)) {
					records.clear();
					retCards.clear();
					System.out.println("DON'T CHEAT! Please enter distinct cards!");
					continue;
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
				records.clear();
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
		ArrayList<Integer> records = new ArrayList<Integer>();
		System.out.println("Please give up " + number + " cards.");
		Collections.sort(myCards);


		while(true) {
			System.out.println("Your cards:");
			print_cards(myCards);
			System.out.println("Please enter the cards you want to give up.");
			String input = scanner.nextLine();
			String[] cardIndices = input.split(" ");
			if(cardIndices.length != number) {
				System.out.println("Please enter " + number + " cards.");
				continue;
			}
			try {	
				for(int i = 0; i < cardIndices.length; i ++) {
					if(cardIndices[i].equals("")) {
						continue;
					}
					int idx = Integer.parseInt(cardIndices[i]);
					retCards.add(myCards.get(idx));
					records.add(idx);
				}
				if(repeat_record(records)) {
					records.clear();
					retCards.clear();
					System.out.println("DON'T CHEAT! Please give up distinct cards.");
					continue;
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
			
			print_status(msg);
			if((msg.getAction() & Message.ACTION_CANT_BEAT) != 0) {
				System.out.println("Your hand couldn't beat the last hand, please play your cards again.");
			}
			else if((msg.getAction() & Message.ACTION_WRONG_TYPE) != 0) {
				System.out.println("You played wrong type of hand, please play your cards again.");
			}
			Hand lastHand;
			if(msg.getContent() != null) {
				lastHand = (Hand)msg.getContent();
				System.out.println("The last hand was " + lastHand.toString());
			}
		}
		else if(msg.getType() == Message.BASIC) {
			// The KING lost
			if((msg.getAction() & Message.ACTION_LOSING) != 0) {
				ArrayList<Card> kingCards = (ArrayList<Card>)msg.getContent();
				if(this.get_title() == InfoCenter.GRAND_MILLIONAIRE) {
					System.out.println("The GRAND MILLIONAIRE lost, here are his remains.");	
				}
				else {
					System.out.println("Player at position " + msg.getPlayer() + " lost, here are his remains.");
				}
				print_cards(kingCards);
			}
			// A person played his cards.
			else if((msg.getAction() & Message.ACTION_PLAYING) != 0) {
				Hand lastHand = (Hand)msg.getContent();
				print_status(msg);
				System.out.println("Player" + Integer.toString(msg.getPlayer()) + "'s hand was " + lastHand.toString());
				
				// Someone won.
				if((msg.getAction() & Message.ACTION_WINNING) != 0) {
					System.out.println("Player at position " + msg.getPlayer() + " won.");	
				}
			}

			else if((msg.getAction() & Message.ACTION_NEW_ROUND) != 0) {
				System.out.println("-----------");
				System.out.println("| Round " + Integer.toString((int)msg.getContent()) + " |");
				System.out.println("-----------");
			}
			
			else if((msg.getAction() & Message.ACTION_PASSING) != 0) {
				Hand lastHand = (Hand)msg.getContent();
				print_status(msg);
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
			else if((msg.getAction() & Message.ACTION_ABAN_CARD) != 0) {
				ArrayList<Card> abanCards = (ArrayList<Card>)msg.getContent();
				System.out.println("Player at position " + msg.getPlayer() + " abandoned " + abanCards.size() + " cards.");
				System.out.println("The abandoned cards are:");
				print_cards(abanCards);
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
	private void print_cards(ArrayList<Card> myCards) {
		for(int i = 0; i < myCards.size(); i ++) {
			System.out.print("(" + i + ")" + myCards.get(i).toString());
		}
		System.out.println("");
	}
	private boolean repeat_record(ArrayList<Integer> records) {
		Collections.sort(records);
		for(int i = 0; i < records.size() - 1; i ++) {
			if(records.get(i) == records.get(i + 1)) {
				return true;
			}
		}
		return false;
	}
	private void print_status(Message msg) {
		System.out.println("---------- STATUS ----------");
		
		System.out.print("| isUnderRevolution: " + msg.isUnderRevolution());
		if(msg.isUnderRevolution()) {
			System.out.println("  |");
		}
		else {
			System.out.println(" |");	
		}
		System.out.print("| isUnderJackBack:   " + msg.isUnderJackBack());
		if(msg.isUnderJackBack()) {
			System.out.println("  |");
		}
		else {
			System.out.println(" |");	
		}	
		System.out.print("| isTight:           " + msg.isTight());
		if(msg.isTight()) {
			System.out.println("  |");
		}
		else {
			System.out.println(" |");	
		}	
		System.out.println("----------------------------");
	}
}


/**
 *	TODO LIST:
 *	1. When pass, any string includes "PASS", or "-1"	-----
 *	2. fix the bugs of two space for split.      		----- DONE
 *	3. repetitive cards 								----- DONE
 *  4. print_cards()
 */
