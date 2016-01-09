package daifugo;
import java.lang.*;
import java.util.*;

public class Daifugo {

	// fields
	private static boolean isUnderJackBack;
	private static boolean isUnderRevolution;
	private static boolean isTight;
	private static int nPlayer;
	private static int nHumanPlayer;
	private static int nAIPlayer;
	private static int nRounds;
	public static Message msg;
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
		int skipNumber = 0;
		msg = new Message(Message.BASIC);
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
			int trick = 0;
			isUnderJackBack = false;
			isUnderRevolution = false;
			isTight = false;

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
			System.out.println(infoCenter.getPlayingNumber());

			while(infoCenter.getPlayingNumber() > 0) {
				for(int i=0; i<nPlayer; i++) {
					Player p = players.get(i);
					if(!infoCenter.getPlayerNoHand(p)) {
						if(infoCenter.getPlayerIsLeader(p) && infoCenter.getPlayerIsLastPlayer(p)) {
							// 
							// can't skip, do anything you want.
							//
							System.out.println("-------"+"Tricks "+(++trick)+"-------");
							skipNumber = 0;
							msg.isNewTrick = true;
							msg.isTight = false;
							msg.isUnderJackBack = false; 
							isTight = false;
							isUnderJackBack = false;
						} else if(infoCenter.getPlayerIsLeader(p) && !infoCenter.getPlayerIsLastPlayer(p)) {
							// 
							// can skip or play hand
							// 
							msg.isNewTrick = false;
							if(true) {
								skipNumber++;
							} else {
								infoCenter.setPlayerIsLastPlayer(p);
							}

						} else if(!infoCenter.getPlayerIsLeader(p) && infoCenter.getPlayerIsLastPlayer(p)) {
							// 
							// become leader, can't skip, do anythings you want
							// 
							System.out.println("-------"+"Tricks "+(++trick)+"-------");
							skipNumber = 0;
							msg.isNewTrick = true;
							msg.isTight = false;
							msg.isUnderJackBack = false;
							isTight = false;
							isUnderJackBack = false;
							infoCenter.setPlayerIsLeader(p);

						} else if(!infoCenter.getPlayerIsLeader(p) && !infoCenter.getPlayerIsLastPlayer(p) &&
									infoCenter.getPlayingNumber() == skipNumber) {
							// 
							// become leader and the last player, can't skip, do anythings you want
							// 
							System.out.println("-------"+"Tricks "+(++trick)+"-------");
							skipNumber = 0;
							msg.isNewTrick = true;
							msg.isTight = false;
							msg.isUnderJackBack = false;
							isTight = false;
							isUnderJackBack = false;
							infoCenter.setPlayerIsLeader(p);
							infoCenter.setPlayerIsLastPlayer(p);

						} else {
							// 
							// can skip or play hand
							// 
							msg.isNewTrick = false;
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
						if(infoCenter.getPlayingNumber() == 1) {
							Player lastPlayer = infoCenter.getLastPlayer();
							infoCenter.setPlayerNoHand(lastPlayer);
							infoCenter.setPlayerStatus(lastPlayer, infoCenter.getStatus());
						}
					}
				}
			}
			infoCenter.playersGetStatusScore();
		}
		System.out.println("===========================");
	}

	public static int judge(InfoCenter _infoCenter, ArrayList<Player> _players, Player _player, Hand lastHand, Hand newHand) {
		if(!msg.isNewTrick) {
			if(lastHand.isTight(newHand)) {
				isTight = true;
			}
		} else {
			if(newHand.getType() == Hand.STRAIGHT_FLUSH) {
				isUnderRevolution = !isUnderRevolution;
			}
		}
	}

	public static boolean canBeat(boolean beats, Hand lastHand, Hand newHand) {
		boolean truth = beats;
		if(lastHand.getType() == newHand.getType()) {
			if(isUnderRevolution) {
				truth = !truth;
			}
			if(isUnderJackBack) {
				truth = !truth;
			}
		}
		return truth;
	}

	// 
	// return number is the number of player to skip
	//
	public static int getAndCheckHand(InfoCenter _infoCenter, ArrayList<Player> _players, Player _player, Hand _currentHand) {
		boolean success = false;
		int chance = 5;
		int skipNumb = 0;
		ArrayList<Card> playCard;
		Hand playHand;
		setMessage(_infoCenter, _players, _player, _currentHand, Message.BASIC);
		if(_currentHand == null) {
			while(!success) {
				try {
					_player.update_info(msg);
					playCard = _player.play_card(_infoCenter.getPlayerHand(_player));
					playHand = new Hand(playCard);
					if(playHand.getType() == Hand.UNKNOWN) {
						success = false;
						setMessage(_infoCenter, _players, _player, _currentHand, Message.ERROR);
						System.out.println("Wrong Hand! Try again.");
					} else {
						_infoCenter.removePlayerHand(_player, playHand.getContent());
						setMessage(_infoCenter, _players, _player, _currentHand, Message.BASIC);
						success = true;
					}
				} catch(Exception e) {
					System.out.println("Something Wrong.");
				}
			}
		} else {
			while(chance-- > 0) {
				try {
					_player.update_info(msg);
					playCard = _player.play_card(_infoCenter.getPlayerHand(_player));
					playHand = new Hand(playCard);
					if (canBeat(_currentHand.beats(playHand), _currentHand, playHand)) {
						success = true;
						_infoCenter.removePlayerHand(_player, playHand.getContent());
						setMessage(_infoCenter, _players, _player, _currentHand, Message.BASIC);
						break;
					} else if(_currentHand.getType() == Hand.UNKNOWN) {
						success = false;
						setMessage(_infoCenter, _players, _player, _currentHand, Message.ERROR);
						System.out.println("Wrong Hand! Try again. You have "+chance+" time to try.");
					} else {
						success = false;
						setMessage(_infoCenter, _players, _player, _currentHand, Message.ERROR);
						System.out.println("Can't beat current hand! Try again. You have "+chance+" time to try.");
					}
				} catch(Exception e) {
					System.out.println("Something Wrong.");
				}
			}
			setMessage(_infoCenter, _players, _player, _currentHand, Message.BASIC);
		}
	}

	public static void setMessage(InfoCenter _infoCenter, ArrayList<Player> _players, Player _player, Hand _currentHand, byte _type) {
		msg.type = _type;
		msg.lastPlayer = _players.indexOf(_infoCenter.getLastPlayedPlayer());
		msg.lastHand = _currentHand;
		msg.whoseTurn = _players.indexOf(_player);
		boolean[] playing = new boolean[nPlayer];
		for(int i=0; i<nPlayer; i++) {
			if(_infoCenter.getPlayerIsPlaying(_players.get(i))) {
				playing[i] = true;
			} else {
				playing[i] = false;
			}
		}
		msg.playerStatus = playing;
		msg.isUnderRevolution = isUnderRevolution;
		msg.isUnderJackBack = isUnderJackBack;
		msg.isTight = isTight;
	}

	/**
	 * to get inital settings' infomation from input
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

	/**
	 * to set inital settings' infomation
	 * @param players players in ArrayList of Player
	 */
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
	}

	/**
	 * to init new game setting for each round
	 * @param _infoCenter InforCenter's reference
	 * @param _deck 	  Deck's reference
	 * @param _players 	  Arraylist of Player's reference
	 */
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
 		Collections.shuffle(_players); 
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

	/**
	 * Constructor of InfoCenter, init player's settings
	 * @param _players players in ArrayList of Player 
	 */
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
			this.playerNoHand.add(true);
			this.playerHand.add(new ArrayList<Card>());
			this.playerStatus.add(this.COMMONER);
		}
		this.rewardsSetting(_players.size());
		this.statusSetting(_players.size());		
	}	

	/**
	 * to get score 
	 * @return score in int
	 */
	public int getScore() {
		return this.rewards.get(currentIndex_r++);
	}

	/**
	 * to get score by given index
	 * @param index score index
	 * @return score in int
	 */
	public int getScore(int index) {
		return this.rewards.get(index);
	}

	/**
	 * to get status
	 * @return status in byte
	 */
	public byte getStatus() {
		return this.status.get(currentIndex_s++);
	}

	/**
	 * to get status by given index
	 * @param index status index
	 * @return status in byte
	 */
	public byte getStatus(int index) {
		return this.status.get(index);
	}

	/**
	 * to init status setting
	 * @param nPlayer total number of players in this game
	 */
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

	/**
	 * to init reward setting
	 * @param nPlayer total number of players in this game
	 */
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

	/**
	 * to init new game setting
	 */
	public void startNewGame() {
		this.currentIndex_r = 0;
		this.currentIndex_s = 0;
		for(int i=0; i<this.players.size(); i++) {
			this.isLeader.set(i, false);
			this.isLastPlayer.set(i, false);
			this.playerNoHand.set(i, true);
			this.playerHand.set(i, new ArrayList<Card>());
		}	
	}

	/**
	 * to get the last player in the game
	 * @return the last player
	 */
	public Player getLastPlayer() {
		if(this.getPlayingNumber() == 1) {
			int i;
			for(i=0; i<this.players.size(); i++) {
				if(!this.getPlayerNoHand(this.players.get(i))) {
					break;
				} 
			}
			return this.players.get(i);
		} else {
			return null;
		}
	}

	/**
	 * to get GRAND_MILLIONAIRE in players
	 * @return player with GRAND_MILLIONAIRE status
	 */
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

	/**
	 * to set player has no hand
	 * @param _player player object
	 */
	public void setPlayerNoHand(Player _player) {
		int index = this.getPlayerIndex(_player);
		this.playerHand.set(index, new ArrayList<Card>());
	}

	/**
	 * to get whether player has no hand
	 * @param _player player object
	 * @return if player has hand in boolean value
	 */
	public boolean getPlayerNoHand(Player _player) {
		int index = this.getPlayerIndex(_player);
		if(this.playerHand.get(index).size()==0) {
			this.playerNoHand.set(index, true); 
		} else {
			this.playerNoHand.set(index, false);
		}
		return this.playerNoHand.get(index);
	}

	/**
	 * to change player's socre
	 * @param _player player object
	 * @param point value to be set
	 */
	public void changePlayerScore(Player _player, int point) {
		int index = this.getPlayerIndex(_player);
		this.scores.set(index, this.getPlayerScore(_player) + point);
		if(point >= 0) {
			_player.win_points(point);
		} else {
			_player.lose_points(point);
		}
	}

	/**
	 * to get player's socre
	 * @param _player player object
	 * @return player's score
	 */
	public int getPlayerScore(Player _player) {
		int index = this.getPlayerIndex(_player);
		return this.scores.get(index);
	}

	/**
	 * set player to be leader
	 * @param _player player object
	 */
	public void setPlayerIsLeader(Player _player) {
		int index = this.getPlayerIndex(_player);
		for(int i=0; i<this.players.size(); i++) {
			this.isLeader.set(i, false);
		}
		this.isLeader.set(index, true);
	}

	/**
	 * to get whether player is leader
	 * @param _player player object
	 * @return if player is leader in boolean value
	 */
	public boolean getPlayerIsLeader(Player _player) {
		int index = this.getPlayerIndex(_player);
		return this.isLeader.get(index);
	}

	/**
	 * to get last played player
	 * @return the last played player
	 */
	public Player getLastPlayedPlayer() {
		int i;
		for(i=0; i<this.players.size(); i++) {
			if(this.getPlayerIsLastPlayer(this.players.get(i))) {
				break;
			}
		}
		if(i == this.players.size()) {
			return null;
		} else {
			return this.players.get(i);
		}
	}

	/**
	 * set player to be the last player
	 * @param _player player object
	 */
	public void setPlayerIsLastPlayer(Player _player) {
		int index = this.getPlayerIndex(_player);
		for(int i=0; i<this.players.size(); i++) {
			this.isLastPlayer.set(i, false);
		}
		this.isLastPlayer.set(index, true);
	}

	/**
	 * to get whether player is the last player
	 * @param _player player object
	 * @return if player is the last player in boolean value
	 */
	public boolean getPlayerIsLastPlayer(Player _player) {
		int index = this.getPlayerIndex(_player);
		return this.isLastPlayer.get(index);
	}

	/**
	 * to remove player's hand
	 * @param _player player object
	 * @param hand hand to be remove in ArrayList of Card
	 */
	public void removePlayerHand(Player _player, ArrayList<Card> hand) {
		int index = this.getPlayerIndex(_player);
		for(int i=0; i<hand.size(); i++) {
			this.playerHand.get(index).remove(hand.get(i));
		}
	}

	/**
	 * to set player hand
	 * @param _player player object
	 * @param hand hand to be set in ArrayList of Card
	 */
	public void setPlayerHand(Player _player, ArrayList<Card> hand) {
		int index = this.getPlayerIndex(_player);
		this.playerHand.set(index, hand);
	}

	/**
	 * to get player's hand
	 * @param _player player object
	 * @return Player's hand in ArrayList of Card
	 */
	public ArrayList<Card> getPlayerHand(Player _player) {
		int index = this.getPlayerIndex(_player);
		return this.playerHand.get(index);
	}

	/**
	 * to add card to player's hand
	 * @param _player player object
	 * @param _card card to be added
	 */
	public void addPlayerHand(Player _player, Card _card) {
		int index = this.getPlayerIndex(_player);
		this.playerHand.get(index).add(_card);
	}

	/**
	 * Players get their socres by current status
	 */
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

	/**
	 * to set player current status
	 * @param _player player object
	 * @param status player's status to be set
	 */
	public void setPlayerStatus(Player _player, byte status) {
		int index = this.getPlayerIndex(_player);
		this.playerStatus.set(index, status);
	}

	/**
	 * to get player current status
	 * @param _player player object
	 * @return the player status in byte
	 */
	public byte getPlayerStatus(Player _player) {
		int index = this.getPlayerIndex(_player);
		return this.playerStatus.get(index);
	}

	/**
	 * to get player index in InforCenter by input player object
	 * @param _player player object
	 * @return the index of input player in InfoCenter
	 */
	public int getPlayerIndex(Player _player) {
		return this.players.indexOf(_player);
	}

	public boolean getPlayerIsPlaying(Player _player) {
		if(!this.getPlayerNoHand(_player)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * to get the number of playing players
	 * @return number of playing players 
	 */
	public int getPlayingNumber() {
		int p = 0;
		for(int i=0; i<this.players.size(); i++) {
			if(!this.getPlayerNoHand(this.players.get(i))) {
				p += 1;
			}
		}
		return p;
	}
}


