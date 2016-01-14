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
	public static final short ACTION_ROUND_END	= (short)0b0000000000100000;
	public static final short ACTION_CANT_BEAT	= (short)0b0000000001000000;
	public static final short ACTION_WRONG_TYPE	= (short)0b0000000010000000;

	/**
	 * to indicate the occasions of the game
	 * (use bit-wise operation to access)
	 */
	public static final short WHEN_WINNING	= (short)0b0000000000000011;
	public static final short WHEN_LOSING	= (short)0b0000000000000100;
	public static final short WHEN_PLAY		= (short)0b0000000000000001;
	public static final short NEW_TRICK		= (short)0b0000000000001000;
	public static final short ROUND_START	= (short)0b0000000000010000;
	public static final short ROUND_END		= (short)0b0000000000100000;
	public static final short SOMETHING_BAD	= (short)0b0000000011000000;

	// ----- fields ----- //

	/**
	 * the type of this message: BASIC or ERROR (initially BASIC)
	 */
	private byte type = BASIC;

	/**
	 * the type of this message: BASIC or ERROR (initially BASIC)
	 */
	private short action;

	/**
	 * the position of the current playing player
	 */
	private int currentPlayer;

	/**
	 * the hand of this player 
	 */
	private Hand currentHand;

	/**
	 * the content of the cards that triggered by specific effects
	 */
	private ArrayList<Card> content;

	/**
	 * the position of the last player to play
	 */
	private int lastPlayer;

	/**
	 * the hand of last player 
	 */
	private Hand lastHand;

	/**
	 * the position of the next player to play
	 */
	private int nextPlayer;

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
	 * to construct a message containing basic information
	 */
	public Message(short act, int cPlayer, Hand cHand, int lPlayer, Hand lHand, ArrayList<Card> cont, int nPlayer, boolean isR, boolean isJ, boolean isT) {
		action = act;
		currentPlayer = cPlayer;
		if (cHand != null)
			currentHand = new Hand(cHand.getContent());
		else 
			currentHand = new Hand(new ArrayList<Card>());
		lastPlayer = lPlayer;
		if (lHand != null)
			lastHand = new Hand(lHand.getContent());
		else
			lastHand = new Hand(new ArrayList<Card>());
		content = cont;
		nextPlayer = nPlayer;
		isUnderRevolution = isR;
		isUnderJackBack = isJ;
		isTight = isT;
	}

	/**
	 * to construct a message to inform player of his/her erroneus move
	 * @param errMsg the error message
	 */
	public Message(byte errMsg, short act, int cPlayer, Hand cHand, int lPlayer, Hand lHand, ArrayList<Card> cont, int nPlayer, boolean isR, boolean isJ, boolean isT) {
		this(act, cPlayer, cHand, lPlayer, lHand, cont, nPlayer, isR, isJ, isT);
		this.type = errMsg;
	}

	public short getAction() {
		short ret = action;
		return ret;
	}

	public byte getType() {
		byte ret = type;
		return ret;
	}

	public int getCurrentPlayer() {
		int ret = currentPlayer;
		return ret;
	}

	public Hand getCurrentHand() {
		ArrayList<Card> cont = currentHand.getContent();
		ArrayList<Card> joker = currentHand.getJokerContent();
		return new Hand(cont, joker);
	}

	public ArrayList<Card> getContent() {
		ArrayList<Card> ret = content;
		return ret;
	}

	public int getLastPlayer() {
		int ret = lastPlayer;
		return ret;
	}

	public Hand getLastHand() {
		ArrayList<Card> cont = lastHand.getContent();
		ArrayList<Card> joker = lastHand.getJokerContent();
		return new Hand(cont, joker);
	}

	public int getNextPlayer() {
		int ret = nextPlayer;
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
		ret += ("action: " + action + '\n');
		ret += ("current Player: " + currentPlayer + '\n');
		ret += ("current Hand: " + currentHand + '\n');
		ret += ("content: " + content + '\n');
		ret += ("last Player: " + lastPlayer + '\n');
		ret += ("last Hand: " + lastHand + '\n');
		ret += ("next Player: " + nextPlayer + '\n');
		ret += ("revolution: " + isUnderRevolution + '\n');
		ret += ("jackback: " + isUnderJackBack + '\n');
		ret += ("tight: " + isTight + '\n');
		return ret;
	}
}