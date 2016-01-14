
import java.lang.*;
import java.util.*;

class HumanPlayer extends Player{
	HumanPlayer() {
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
		// update_info will also be called when all people pass.
		
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
