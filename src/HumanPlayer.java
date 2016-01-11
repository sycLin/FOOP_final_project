package daifugo;
import java.lang.*;
import java.util.*;

class HumanPlayer extends Player{
	HumanPlayer() {
		super();
	}
	public ArrayList<Card> play_card(ArrayList<Card> myCards) {

	}
	public ArrayList<Card> give_up_card(ArrayList<Card> myCards, int number) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please give up " + number + " cards.");

	}
	public void update_info(Message msg);
	public ArrayList<Card> sort(ArrayList<Card> myCards) {
		
	}
}
