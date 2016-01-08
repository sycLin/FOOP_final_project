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
	public static final byte GRAND_MILLIONAIRE = 1;
	public static final byte MILLIONAIRE = 2;
	public static final byte COMMONER = 3;
	public static final byte NEEDY = 4;
	public static final byte EXTREME_NEEDY = 5;
	private byte title;
	

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

	// ----- abstract methods ----- //

	public abstract ArrayList<Card> play_card(ArrayList<Card> myCards);
	public abstract void print_message(Message msg);
}
