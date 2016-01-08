package daifugo;
import java.lang.*;
import java.util.*;

class Message {
	
	// ----- constants ----- //

	/**
	 * to indicate that this message is for basic information
	 */
	public static final byte BASIC = 1;
	
	/**
	 * to indicate that this message is to inform player of an error
	 */
	public static final byte ERROR = 2;

	// ----- fields ----- //

	/**
	 * the type of this message: BASIC or ERROR
	 */
	byte type;

	/**
	 * the position of the player to whom the lastHand belongs
	 */
	int lastPlayer;

	/**
	 * the last hand on table
	 */
	Hand lastHand;

	/**
	 * the position of the next player to play
	 */
	int whoseTurn;

	/**
	 * array of boolean to indicate whether the player is active or not
	 */
	boolean[] playerStatus;

	// ----- actions ----- //

	/**
	 * to construct a message containing basic information
	 */
	public Message() {
		;
	}

	/**
	 * to construct a message to inform player of his/her erroneus move
	 * @param errMsg the error message
	 */
	public Message(errMsg) {
		;
	}

}