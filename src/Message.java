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
	public byte type;

	/**
	 * the position of the player to whom the lastHand belongs
	 */
	public int lastPlayer;

	/**
	 * the last hand on table
	 */
	public Hand lastHand;

	/**
	 * the position of the next player to play
	 */
	public int whoseTurn;

	/**
	 * array of boolean to indicate whether the player is active or not
	 */
	public boolean[] playerStatus;

	/**
	 * whether it's under revolution
	 */
	public boolean isUnderRevolution;

	/**
	 * whether it's under jackBack
	 */
	public boolean isUnderJackBack;

	/**
	 * whether it's a new trick
	 */
	public boolean isNewTrick;

	/**
	 * whether it's tight
	 */
	public boolean isTight;

	// ----- actions ----- //

	/**
	 * to construct a message containing basic information
	 */
	public Message() {
		this.type = BASIC; // default basic message
		lastPlayer = -1;
		lastHand = null;
		whoseTurn = -1;
		playerStatus = null;
		isUnderRevolution = false;
		isUnderJackBack = false;
		isNewTrick = false;
		isTight = false;
	}

	/**
	 * to construct a message to inform player of his/her erroneus move
	 * @param errMsg the error message
	 */
	public Message(byte errMsg) {
		this.type = errMsg;
		lastPlayer = -1;
		lastHand = null;
		whoseTurn = -1;
		playerStatus = null;
		isUnderRevolution = false;
		isUnderJackBack = false;
		isNewTrick = false;
		isTight = false;
	}

}