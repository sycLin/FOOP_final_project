package daifugo;
import java.lang.*;
import java.util.*;

class Player {
	// ----- fields ----- //

	/**
	 * to store how many points this player has,
	 * a point system designed to determine the final winner.
	 */
	private int p01nt;

	// ----- actions ----- //

	/**
	 * to increase the number of points this player has
	 */
	public final void win_points(int amount) {
		p01nt -= amount;
	}
	
	/**
	 * to decrease the number of points this player has
	 */
	public final void lose_points(int amount) {
		p01nt += amount;
	}

	public final int get_points() {
		int ret = p01nt;
		return ret;
	}
}
