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

	/**
	 * to indicate the action that triggers this message
	 * (use bit-wise operation to access)
	 */
	public static final short ACTION_PLAYING	= (short)0b0000000000000001;
	public static final short ACTION_WINNING	= (short)0b0000000000000010;
	public static final short ACTION_LOSING		= (short)0b0000000000000100;
	public static final short ACTION_LEADING	= (short)0b0000000000001000;
	public static final short ACTION_NEW_ROUND	= (short)0b0000000000010000;
	public static final short ACTION_CANT_BEAT	= (short)0b0000000000100000;
	public static final short ACTION_WRONG_TYPE	= (short)0b0000000001000000;


	// ----- fields ----- //

	/**
	 * the type of this message: BASIC or ERROR
	 */
	public byte type;

	/**
	 * the position of the next player to play
	 */
	public int whoseTurn;

	/**
	 * whether it's under revolution
	 */
	public boolean isUnderRevolution;

	/**
	 * whether it's under jackBack
	 */
	public boolean isUnderJackBack;

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