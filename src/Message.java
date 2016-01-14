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
	public static final short ACTION_NOTHING	= (short)0b0000000000000000;
	public static final short ACTION_PLAYING	= (short)0b0000000000000001;
	public static final short ACTION_WINNING	= (short)0b0000000000000010;
	public static final short ACTION_LOSING		= (short)0b0000000000000100;
	public static final short ACTION_PASSING	= (short)0b0000000000001000;
	public static final short ACTION_LEADING	= (short)0b0000000000010000;
	public static final short ACTION_NEW_ROUND	= (short)0b0000000000100000;
	public static final short ACTION_CANT_BEAT	= (short)0b0000000010000000;
	public static final short ACTION_WRONG_TYPE	= (short)0b0000000100000000;

	// ----- fields ----- //

	/**
	 * the type of this message: BASIC or ERROR (initially BASIC)
	 */
	private byte type = BASIC;

	/**
	 * the position of the player who triggered this message,
	 * (-1 if system triggered)
	 */
	private int playerPosition;

	/**
	 * the action of the player at player_position did,
	 * (use bit-wise operations with ACTION_XXX constants)
	 */
	private short action;

	/**
	 * the content of the cards that triggered by specific effects,
	 * could be an instance of Hand, or ArrayList of Card.
	 */
	private Object content;

	/**
	 * whether it's under revolution
	 */
	private boolean isUnderRevolution;

	/**
	 * whether it's under jackBack
	 */
	private boolean isUnderJackBack;

	/**
	 * whether it's tight
	 */
	private boolean isTight;

	// ----- actions ----- //

	/**
	 * to construct a dummy message
	 */
	public Message() {
		type = BASIC;
		playerPosition = -1;
		action = ACTION_NOTHING;
		content = null;
		isUnderRevolution = false;
		isUnderJackBack = false;
		isTight = false;
	}

	/**
	 * to construct a message containing basic information
	 * @param pPosition the player position
	 * @param theAction the action took by the player
	 * @param theContent the content of the action
	 * @param isR whether under revolution or not
	 * @param isJ whether under Jack back or not
	 * @param isT whether being tight or not
	 */
	public Message(int pPosition, short theAction, Object theContent, boolean isR, boolean isJ, boolean isT) {
		type = BASIC; // default message type: BASIC
		playerPosition = pPosition;
		action = theAction;
		content = theContent;
		isUnderRevolution = isR;
		isUnderJackBack = isJ;
		isTight = isT;
	}

	/**
	 * to construct a message to inform player of his/her erroneus move
	 * @param msgType the type of this message
	 * @param pPosition the player position
	 * @param theAction the action took by the player
	 * @param theContent the content of the action
	 * @param isR whether under revolution or not
	 * @param isJ whether under Jack back or not
	 * @param isT whether being tight or not
	 */
	public Message(byte msgType, int pPosition, short theAction, Object theContent, boolean isR, boolean isJ, boolean isT) {
		type = msgType;
		playerPosition = pPosition;
		action = theAction;
		content = theContent;
		isUnderRevolution = isR;
		isUnderJackBack = isJ;
		isTight = isT;
	}

	public byte getType() {
		byte ret = type;
		return ret;
	}

	public int getPlayer() {
		int ret = playerPosition;
		return ret;
	}

	public Object getContent() {
		Object ret = content;
		return ret;
	}

	public boolean isUnderRevolution() {
		boolean ret = isUnderRevolution;
		return ret;
	}

	public boolean isUnderJackBack() {
		boolean ret = isUnderJackBack;
		return ret;
	}

	public boolean isTight() {
		boolean ret = isTight;
		return ret;
	}

	public String toString() {
		String ret = "";
		String[] typeList = {"", "BASIC", "ERROR"};
		ret += ("TYPE: " + typeList[(int)type] + '\n');
		ret += ("playerPosition: " + playerPosition + '\n');
		ret += ("action: " + action + '\n');
		ret += ("content: " + content + '\n');
		ret += ("revolution: " + isUnderRevolution + '\n');
		ret += ("jackback: " + isUnderJackBack + '\n');
		ret += ("tight: " + isTight + '\n');
		return ret;
	}
}
