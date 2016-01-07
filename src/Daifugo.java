package daifugo;
import java.lang.*;
import java.util.*;

public class Daifugo {

	// fields
	private ArrayList<Player> players;
	private boolean isUnderJackBack;
	private boolean isUnderRevolution;
	private static int nPlayer;
	private static int nHumanPlayer;
	private static int nAIPlayer;
	private static int nRounds;
	public static final byte GRAND_MILLIONAIRE = 1;
	public static final byte MILLIONAIRE = 2;
	public static final byte COMMONER = 3;
	public static final byte NEEDY = 4;
	public static final byte  EXTREME_NEEDY = 5;
	/**
	 * main function for game execution flow
	 * @param argv command-line arguments
	 */
	public static void main(String argv[]) {
		ArrayList<Player> players = new ArrayList<Player>();
		Deck deck = new Deck();
		InfoCenter infoCenter;
		// System.err.println("warning: main() not implemented.");
		// 
		// get init settings from user's input
		// 
		getInitSetting();
		// 
		// set init settings
		//
		setInitSetting(players);
		infoCenter = new InfoCenter(players);

		for(int r=0; r<nRounds; r++) {
			startNewgame(infoCenter, deck, players);
		}

		/*test deck cards*/
		for(int i=0; i<52; i++) {
			System.out.print("" + deck.getNextCard() + " ");
		}

		
	}

	public static void getInitSetting() {
		String inputInfo;
		Scanner input = new Scanner(System.in);
		while(true) {
			System.out.print("How many players? ");
			try {
				nPlayer = Integer.valueOf(input.nextLine());
			} catch(Exception e) {
				System.out.println("Input players' number should be integer");
				continue;
			}
			if (nPlayer >=4 && nPlayer<=6) {
				break;
			} else if(nPlayer > 6) {
				System.out.println("Players' number should not more than 6.");
			} else {
				System.out.println("Players' number should more than 3.");
			} 
		}

		while(true) {
			System.out.print("How many human players? ");
			try {
				nHumanPlayer = Integer.valueOf(input.nextLine());
			} catch(Exception e) {
				System.out.println("Input human players' number should be integer");
				continue;
			}
			if (nHumanPlayer <= nPlayer && nHumanPlayer >= 0) {
				break;
			} else if(nHumanPlayer > nPlayer) {
				System.out.println("Human players' number should not more than total number of players.");
			} else {
				System.out.println("Human players' number should not less than zero.");
			} 
		}
		
		while(true) {
			System.out.print("How many rounds? ");
			try {
				nRounds = Integer.valueOf(input.nextLine());
			} catch(Exception e) {
				System.out.println("Input rounds' number should be integer");
				continue;
			}
			if (nRounds > 0) {
				break;
			} else {
				System.out.println("Rounds' number should not less than zero.");
			} 
		}
		nAIPlayer = nPlayer - nHumanPlayer;
	}

	public static void setInitSetting(ArrayList<Player> players) {
		// 
		// adding players to the game
		// 
		for(int i=0; i<nHumanPlayer; i++) {
			// players.add(new HumanPlayer());
			players.add(new Player());
		}
		for(int i=0; i<nAIPlayer; i++) {
			// players.add(new AIPlayer());
			players.add(new Player());
		}
 		Collections.shuffle(players);
	}

	public static void startNewgame(InfoCenter _infoCenter, Deck _deck, ArrayList<Player> _players) {
		// 
 		// init deck setting
 		// 
 		_deck.open();
 		_deck.shuffle();
 		// 
 		// arrange players' position by their status or card
 		// 
		Player firstPlayer = null;  
		if(_infoCenter.firstGame) {
			_infoCenter.firstGame = false;
			for(int i=0; i<54; i++) {
				for(int j=0; j<nPlayer; j++) {
					Card card =  _deck.getNextCard();
					_infoCenter.addPlayerHand(_players.get(j), card);
					if (card.getSuit() == Card.SPADE && card.getRank() == 3) {
						firstPlayer = _players.get(j);
					}
				}
			}
			_players.remove(firstPlayer);
			_players.add(0, firstPlayer);
		} else {
			for(int i=0; i<54; i++) {
				for(int j=0; j<nPlayer; j++) {
					Card card =  _deck.getNextCard();
					_infoCenter.addPlayerHand(_players.get(j), card);
				}
			}
			int insertedNumb = 0;
			byte max = -1;
			byte min = 10;
			for(int i=0; i<_players.size(); i++) {
				byte s = _infoCenter.getPlayerStatus(_players.get(i));
				if (s >= max) {
					max = s;
					firstPlayer = _players.get(i);
				}	
			}
			_players.remove(firstPlayer);
			_players.add(insertedNumb, firstPlayer);
			insertedNumb += 1;

			while (insertedNumb != nPlayer) {
				for(int i=insertedNumb; i<nPlayer; i++) {
					byte s = _infoCenter.getPlayerStatus(_players.get(i));
					if (s <= min) {
						min = s;
						firstPlayer = _players.get(i);
					}
				}
				_players.remove(firstPlayer);
				_players.add(insertedNumb, firstPlayer);
				insertedNumb += 1;
			}
		}
	}
}

class InfoCenter {
	ArrayList<Player> players;
	ArrayList<Integer> scores;
	ArrayList<Boolean> isLeader;
	ArrayList<Boolean> isLastPlayer;
	ArrayList<ArrayList<Card>> playerHand;
	ArrayList<Byte> playerStatus;
	boolean firstGame;
	public static final byte GRAND_MILLIONAIRE = 1;
	public static final byte MILLIONAIRE = 2;
	public static final byte COMMONER = 3;
	public static final byte NEEDY = 4;
	public static final byte  EXTREME_NEEDY = 5;

	public InfoCenter(ArrayList<Player> _players) {
		this.players = new ArrayList<Player>();
		this.scores = new ArrayList<Integer>();
		this.isLeader = new ArrayList<Boolean>();
		this.isLastPlayer = new ArrayList<Boolean>();
		this.playerHand = new ArrayList<ArrayList<Card>>();
		this.playerStatus = new ArrayList<Byte>();
		this.firstGame = true;
		// 
		// !!! should not change the player order in InfoCenter's ArrayList !!!
		// 
		for(int i=0; i<_players.size(); i++) {
			this.players.add(_players.get(i));
			this.scores.add(0);
			this.isLeader.add(false);
			this.isLastPlayer.add(false);
			this.playerHand.add(new ArrayList<Card>());
			this.playerStatus.add(this.COMMONER);
		}		
	}

	public void setPlayerScore(Player _player, int point) {
		int index = this.getPlayerIndex(_player);
		this.scores.set(index, this.getPlayerScore(_player) + point);
	}

	public int getPlayerScore(Player _player) {
		int index = this.getPlayerIndex(_player);
		return this.scores.get(index);
	}

	public void setPlayerIsLeader(Player _player, boolean status) {
		int index = this.getPlayerIndex(_player);
		this.isLeader.set(index, status);
	}

	public boolean getPlayerIsLeader(Player _player) {
		int index = this.getPlayerIndex(_player);
		return this.isLeader.get(index);
	}

	public void setPlayerIsLastPlayer(Player _player, boolean status) {
		int index = this.getPlayerIndex(_player);
		this.isLastPlayer.set(index, status);
	}

	public boolean getPlayerIsLastPlayer(Player _player) {
		int index = this.getPlayerIndex(_player);
		return this.isLastPlayer.get(index);
	}

	public void removePlayerHand(Player _player, ArrayList<Card> hand) {
		int index = this.getPlayerIndex(_player);
		for(int i=0; i<hand.size(); i++) {
			this.playerHand.get(index).remove(hand.get(i));
		}
	}

	public void setPlayerHand(Player _player, ArrayList<Card> hand) {
		int index = this.getPlayerIndex(_player);
		this.playerHand.set(index, hand);
	}
	public ArrayList<Card> getPlayerHand(Player _player) {
		int index = this.getPlayerIndex(_player);
		return this.playerHand.get(index);
	}

	public void addPlayerHand(Player _player, Card _card) {
		int index = this.getPlayerIndex(_player);
		this.playerHand.get(index).add(_card);
	}

	public void playersGetStatusScore() {
		for(int i=0; i<this.players.size(); i++) {
			Player _player = this.players.get(i);
			switch (this.getPlayerStatus(_player)) {
				case GRAND_MILLIONAIRE:
					this.setPlayerScore(_player, 2);
					break;
				case MILLIONAIRE:
					this.setPlayerScore(_player, 1);
					break;
				case COMMONER:
					this.setPlayerScore(_player, 0);
					break;
				case NEEDY:
					this.setPlayerScore(_player, -1);
					break;
				case EXTREME_NEEDY:
					this.setPlayerScore(_player, -2);
					break;
				default:
					break;
			}
		}
	}

	public void setPlayerStatus(Player _player, byte status) {
		int index = this.getPlayerIndex(_player);
		this.playerStatus.set(index, status);
	}

	public byte getPlayerStatus(Player _player) {
		int index = this.getPlayerIndex(_player);
		return this.playerStatus.get(index);
	}

	public int getPlayerIndex(Player _player) {
		return this.players.indexOf(_player);
	}

}


