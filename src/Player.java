package daifugo;
import java.lang.*;
import java.util.*;

public abstract class Player {

	// ----- fields ----- //

	/**
	 * to store how many points this player has,
	 * a point system designed to determine the final winner.
	 */
	private int point;
	private byte title;
	private String name;
	public static final int MAX_NAME_LENGTH = 50;
	Player() {
		point = 0;	
	}

	// ----- actions ----- //

	/**
	 * to increase the number of points this player has
	 * @param the amount of points to add
	 */
	public final void win_points(int amount) {
		point += amount;
	}
	
	/**
	 * to decrease the number of points this player has
	 * @param the amount of points to decrease
	 */
	public final void lose_points(int amount) {
		point -= amount;
	}

	/**
	 * @return the total points this player has
	 */
	public final int get_points() {
		int ret = point;
		return ret;
	}

	/**
	 * to set the title of this player
	 * @param t the title
	 */
	public final void set_title(byte t) {
		title = t;
	}

	/**
	 * to get the title of this player
	 * @param the title of this player
	 */
	public final byte get_title() {
		byte ret = title;
		return ret;
	}


	/**
	 *	Get player's name.
	 */
	public final String get_name() {
		String n = name;
		return name;
	}

	// ----- abstract methods ----- //

	public abstract ArrayList<Card> play_card(ArrayList<Card> myCards);
	/**
	 *	Give up number cards (7 or 10)
	 */
	public abstract ArrayList<Card> give_up_card(ArrayList<Card> myCards, int number);
	public abstract void update_info(Message msg);
	public abstract void enter_name();
}
