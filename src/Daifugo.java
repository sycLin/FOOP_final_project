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
	public static final byte EXTREME_NEEDY = 5;
	/**
	 * main function for game execution flow
	 * @param argv command-line arguments
	 */
	public static void main(String argv[]) {
		ArrayList<Player> players = new ArrayList<Player>();
		Deck deck = new Deck();
		InfoCenter infoCenter;
		Hand currentHand = null;
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
			System.out.println("=========="+"Round "+(r+1)+"==========");
			startNewGame(infoCenter, deck, players);

			/*test player get card*/
			for(int i=0; i<nPlayer; i++) {
				System.out.println("Player"+i+" : ");
				ArrayList<Card> hand = infoCenter.getPlayerHand(players.get(i));
				for(int j=0; j<hand.size(); j++) {
					System.out.print(hand.get(j)+" ");
				}
				System.out.println();
			}

			while(infoCenter.getPlayingNumber() > 0) {
				int skipNumber = 0;
				for(int i=0; i<nPlayer; i++) {
					Player p = players.get(i);
					if(!infoCenter.getPlayerNoHand(p)) {
						if(infoCenter.getPlayerIsLeader(p) && infoCenter.getPlayerIsLastPlayer(p)) {
							// 
							// can't skip, do anything you want.
							// 
						} else if(infoCenter.getPlayerIsLeader(p) && !infoCenter.getPlayerIsLastPlayer(p)) {
							// 
							// can skip or play hand
							// 
							if(true) {
								skipNumber++;
							} else {
								infoCenter.setPlayerIsLastPlayer(p);
							}

						} else if(!infoCenter.getPlayerIsLeader(p) && infoCenter.getPlayerIsLastPlayer(p)) {
							// 
							// become leader, can't skip, do anythings you want
							// 
							infoCenter.setPlayerIsLeader(p);

						} else if(!infoCenter.getPlayerIsLeader(p) && !infoCenter.getPlayerIsLastPlayer(p) &&
									infoCenter.getPlayingNumber() == skipNumber) {
							// 
							// become leader and the last player, can't skip, do anythings you want
							// 
							infoCenter.setPlayerIsLeader(p);
							infoCenter.setPlayerIsLastPlayer(p);

						} else {
							// 
							// can skip or play hand
							// 
							if(true) {
								skipNumber++;
							} else {
								infoCenter.setPlayerIsLastPlayer(p);
							}
						}

						if(infoCenter.getPlayerNoHand(p)) {
							if(infoCenter.getPlayingNumber() == nPlayer) {
								Player gm = infoCenter.getGrandMillionaire();
								if(gm != null && gm != p) {
									// 
									// gm become EXTREME_NEEDY and no hand
									// 
									infoCenter.setPlayerStatus(gm, EXTREME_NEEDY);
									infoCenter.setPlayerNoHand(gm);
								} 
							}
							infoCenter.setPlayerStatus(p, infoCenter.getStatus());
						}

					}
				}
			}
			infoCenter.playersGetStatusScore();
		}
		System.out.println("===========================");
	}

	// 
	// if return false force player to skip this trick
	//
	/* 
	public static boolean getAndCheckHand(Player _player, Hand _currentHand) {
		boolean success = false;
		int chance = 5;
		ArrayList<Card> playedHand;
		Hand challenger;
		if(_currentHand == null) {
			while(!success) {
				try {
					// 
					// player getHand function have not implemented yet
					// 
					
					playedHand = _player.getHand(_currentHand);
					_currentHand = new Hand(playedHand);
					if(_currentHand.type == Hand.UNKNOWN) {
						success = false;
						System.out.println("Wrong Hand! Try again.");
					} else {
						success = true;
					}
				} catch(Exception e) {
					System.out.println("Something Wrong.");
				}
			}
		} else {
			while(chance--) {
				try {
					playedHand = _player.getHand(_currentHand);
					challenger = new Hand(playedHand);
					if (_currentHand.beats(challenger)) {
						success = true;
						break;
					} else if(_currentHand.type == Hand.UNKNOWN) {
						success = false;
						System.out.println("Wrong Hand! Try again. You have "+chance+" time to try.");
					} else {
						success = false;
						System.out.println("Can't beat current hand! Try again. You have "+chance+" time to try.");
					}
				} catch(Exception e) {
					System.out.println("Something Wrong.");
				}
			}
		}
	}
	*/
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

	public static void startNewGame(InfoCenter _infoCenter, Deck _deck, ArrayList<Player> _players) {
		_infoCenter.startNewGame();
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
			int cardNumber = 0;
			while(cardNumber<54) {
				for(int j=0; j<nPlayer; j++) {
					Card card =  _deck.getNextCard();
					_infoCenter.addPlayerHand(_players.get(j), card);
					if (card.getSuit() == Card.SPADE && card.getRank() == 3) {
						firstPlayer = _players.get(j);
					}
					cardNumber++;
					if(cardNumber==54) {
						break;
					}
				}
			}
			_players.remove(firstPlayer);
			_players.add(0, firstPlayer);
			_infoCenter.setPlayerIsLeader(firstPlayer);
			_infoCenter.setPlayerIsLastPlayer(firstPlayer);
		} else {
			int cardNumber = 0;
			while(cardNumber<54) {
				for(int j=0; j<nPlayer; j++) {
					Card card =  _deck.getNextCard();
					_infoCenter.addPlayerHand(_players.get(j), card);
					cardNumber++;
					if(cardNumber==54) {
						break;
					}
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
			_infoCenter.setPlayerIsLeader(firstPlayer);
			_infoCenter.setPlayerIsLastPlayer(firstPlayer);
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
	ArrayList<Boolean> playerNoHand;
	ArrayList<ArrayList<Card>> playerHand;
	ArrayList<Byte> playerStatus;
	ArrayList<Integer> rewards;
	ArrayList<Byte> status;
	boolean firstGame;
	int currentIndex_r;
	int currentIndex_s;
	public static final byte GRAND_MILLIONAIRE = 1;
	public static final byte MILLIONAIRE = 2;
	public static final byte COMMONER = 3;
	public static final byte NEEDY = 4;
	public static final byte EXTREME_NEEDY = 5;

	public InfoCenter(ArrayList<Player> _players) {
		this.players = new ArrayList<Player>();
		this.scores = new ArrayList<Integer>();
		this.isLeader = new ArrayList<Boolean>();
		this.isLastPlayer = new ArrayList<Boolean>();
		this.playerHand = new ArrayList<ArrayList<Card>>();
		this.playerStatus = new ArrayList<Byte>();
		this.playerNoHand = new ArrayList<Boolean>();
		this.rewards = new ArrayList<Integer>();
		this.status = new ArrayList<Byte>();
		this.firstGame = true;
		this.currentIndex_r = 0;
		this.currentIndex_s = 0;
		// 
		// !!! should not change the player order in InfoCenter's ArrayList !!!
		// 
		for(int i=0; i<_players.size(); i++) {
			this.players.add(_players.get(i));
			this.scores.add(0);
			this.isLeader.add(false);
			this.isLastPlayer.add(false);
			this.playerNoHand.add(false);
			this.playerHand.add(new ArrayList<Card>());
			this.playerStatus.add(this.COMMONER);
		}
		this.rewardsSetting(_players.size());
		this.statusSetting(_players.size());		
	}

	public int getScore() {
		return this.rewards.get(currentIndex_r++);
	}

	public int getScore(int index) {
		return this.rewards.get(index);
	}

	public byte getStatus() {
		return this.status.get(currentIndex_s++);
	}

	public byte getStatus(int index) {
		return this.status.get(index);
	}

	public void statusSetting(int nPlayer) {
		if(nPlayer == 4) {
			this.status.add(GRAND_MILLIONAIRE);
			this.status.add(MILLIONAIRE);
			this.status.add(NEEDY);
			this.status.add(EXTREME_NEEDY);
		} else if(nPlayer == 5) {
			this.status.add(GRAND_MILLIONAIRE);
			this.status.add(MILLIONAIRE);
			this.status.add(COMMONER);
			this.status.add(NEEDY);
			this.status.add(EXTREME_NEEDY);
		} else {
			this.status.add(GRAND_MILLIONAIRE);
			this.status.add(MILLIONAIRE);
			this.status.add(COMMONER);
			this.status.add(COMMONER);
			this.status.add(NEEDY);
			this.status.add(EXTREME_NEEDY);
		}
	}

	public void rewardsSetting(int nPlayer) {
		if(nPlayer == 4) {
			this.rewards.add(2);
			this.rewards.add(1);
			this.rewards.add(-1);
			this.rewards.add(-2);
		} else if(nPlayer == 5) {
			this.rewards.add(2);
			this.rewards.add(1);
			this.rewards.add(0);
			this.rewards.add(-1);
			this.rewards.add(-2);
		} else {
			this.rewards.add(2);
			this.rewards.add(1);
			this.rewards.add(0);
			this.rewards.add(0);
			this.rewards.add(-1);
			this.rewards.add(-2);
		}
	}

	public void startNewGame() {
		this.currentIndex_r = 0;
		this.currentIndex_s = 0;
		for(int i=0; i<this.players.size(); i++) {
			this.isLeader.set(i, false);
			this.isLastPlayer.set(i, false);
			this.playerNoHand.set(i, false);
			this.playerHand.set(i, new ArrayList<Card>());
		}	
	}

	public Player getGrandMillionaire() {
		int i;
		for(i=0; i<this.players.size(); i++) {
			if(this.playerStatus.get(i) == GRAND_MILLIONAIRE) {
				break;
			}
		}
		if(i == this.players.size()) {
			return null;
		} else {
			return this.players.get(i);
		}
	}

	public void setPlayerNoHand(Player _player) {
		int index = this.getPlayerIndex(_player);
		this.playerHand.set(index, new ArrayList<Card>());
	}

	public boolean getPlayerNoHand(Player _player) {
		int index = this.getPlayerIndex(_player);
		if(this.playerHand.get(index).size()==0) {
			this.playerNoHand.set(index, true); 
		} else {
			this.playerNoHand.set(index, false);
		}
		return this.playerNoHand.get(index);
	}

	public void changePlayerScore(Player _player, int point) {
		int index = this.getPlayerIndex(_player);
		this.scores.set(index, this.getPlayerScore(_player) + point);
		if(point >= 0) {
			_player.win_points(point);
		} else {
			_player.lose_points(point);
		}
	}

	public int getPlayerScore(Player _player) {
		int index = this.getPlayerIndex(_player);
		return this.scores.get(index);
	}

	public void setPlayerIsLeader(Player _player) {
		int index = this.getPlayerIndex(_player);
		for(int i=0; i<this.players.size(); i++) {
			this.isLeader.set(i, false);
		}
		this.isLeader.set(index, true);
	}

	public boolean getPlayerIsLeader(Player _player) {
		int index = this.getPlayerIndex(_player);
		return this.isLeader.get(index);
	}

	public void setPlayerIsLastPlayer(Player _player) {
		int index = this.getPlayerIndex(_player);
		for(int i=0; i<this.players.size(); i++) {
			this.isLastPlayer.set(i, false);
		}
		this.isLastPlayer.set(index, true);
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
					this.changePlayerScore(_player, 2);
					break;
				case MILLIONAIRE:
					this.changePlayerScore(_player, 1);
					break;
				case COMMONER:
					this.changePlayerScore(_player, 0);
					break;
				case NEEDY:
					this.changePlayerScore(_player, -1);
					break;
				case EXTREME_NEEDY:
					this.changePlayerScore(_player, -2);
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

	public int getPlayingNumber() {
		int p = 0;
		for(int i=0; i<this.players.size(); i++) {
			if(this.playerNoHand.get(i)) {
				p += 1;
			}
		}
		return p;
	}
}


