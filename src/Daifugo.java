package daifugo;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class Daifugo {

	// fields
	private static boolean isUnderJackBack;
	private static boolean isUnderRevolution;
	private static boolean isTight;
	private static boolean isNewTrick;
	private static int nPlayer;
	private static int nHumanPlayer;
	private static int nAIPlayer;
	private static int nRounds;
	public static Message msg;
	public static Hand currentHand;
	/**
	 * main function for game execution flow
	 * @param argv command-line arguments
	 */
	public static void main(String argv[]) {
		ArrayList<Player> players = new ArrayList<Player>();
		Deck deck = new Deck();
		InfoCenter infoCenter;
		currentHand = null;
		int skipNumber = 0;
		int effectNumber = 0;
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
			isNewTrick = true;

			System.out.println("=========="+"Round "+(r+1)+"==========");
			startNewGame(infoCenter, deck, players);

			msg = new Message(-1, Message.ACTION_NEW_ROUND, (Object)(r+1), isUnderRevolution, isUnderJackBack, isTight);
			updateInfo(players);

			String[] name = new String[nPlayer];
			for(int i=0; i<nPlayer; i++) {
				name[i] = players.get(i).get_name();
			}
			for(int i=0; i<nPlayer; i++) {
				msg = new Message(i, Message.ACTION_UPDT_SCORE, (Object)name, isUnderRevolution, isUnderJackBack, isTight);
				players.get(i).update_info(msg);
			}

			for(int i=0; i<nPlayer; i++) {
				System.err.print(players.get(i).get_name()+" "+infoCenter.getPlayerStatus(players.get(i)));
			}
			System.err.println();

			while(infoCenter.getPlayingNumber() > 0) {
				for(int i=0; i<nPlayer; i++) {
					Player p = players.get(i);
					if(!infoCenter.getPlayerNoHand(p)) {
						
						if(effectNumber > 0) {
							effectNumber -= 1;
							System.out.println("Skip "+p.get_name()+".");
							continue;
						}
						if(effectNumber < 0) {
							effectNumber = 0;
						}

						System.err.println("Player name: "+p.get_name());
						System.err.println("PlayerIsLeader: "+infoCenter.getPlayerIsLeader(p));
						System.err.println("PlayerIsLastPlayer: "+infoCenter.getPlayerIsLastPlayer(p));
						System.err.println("Current table: "+currentHand);
						System.err.println("isUnderRevolution: "+isUnderRevolution);
						System.err.println("isUnderJackBack: "+isUnderJackBack);
						System.err.println("isTight:"+isTight);

						if(infoCenter.getPlayerIsLeader(p) && infoCenter.getPlayerIsLastPlayer(p)) {
							// 
							// can't skip, do anything you want.
							//
							System.err.println("-------"+"Tricks "+(++trick)+"-------");
							skipNumber = 0;
							isTight = false;
							isUnderJackBack = false;
							isNewTrick = true;
							currentHand = null;
							msg = new Message(i, Message.ACTION_LEADING, (Object)trick, isUnderRevolution, isUnderJackBack, isTight);
							updateInfo(players);
							effectNumber = getAndCheckHand(infoCenter, players, p);

						} else if(infoCenter.getPlayerIsLeader(p) && !infoCenter.getPlayerIsLastPlayer(p)) {
							// 
							// can skip or play hand
							// 
							isNewTrick = false;
							effectNumber = getAndCheckHand(infoCenter, players, p);
							if(effectNumber == -1) {
								skipNumber++;
							} else {
								infoCenter.setPlayerIsLastPlayer(p);
								skipNumber = 0;
							}

						} else if(!infoCenter.getPlayerIsLeader(p) && infoCenter.getPlayerIsLastPlayer(p)) {
							// 
							// become leader, can't skip, do anythings you want
							// 
							System.err.println("-------"+"Tricks "+(++trick)+"-------");
							skipNumber = 0;
							isTight = false;
							isUnderJackBack = false;
							isNewTrick = true;
							currentHand = null;
							infoCenter.setPlayerIsLeader(p);
							msg = new Message(i, Message.ACTION_LEADING, (Object)trick, isUnderRevolution, isUnderJackBack, isTight);
							updateInfo(players);
							effectNumber = getAndCheckHand(infoCenter, players, p);

						} else if(!infoCenter.getPlayerIsLeader(p) && !infoCenter.getPlayerIsLastPlayer(p) &&
									infoCenter.getPlayingNumber() == skipNumber) {
							// 
							// become leader and the last player, can't skip, do anythings you want
							// 
							System.err.println("-------"+"Tricks "+(++trick)+"-------");
							skipNumber = 0;
							isTight = false;
							isUnderJackBack = false;
							isNewTrick = true;
							currentHand = null;
							infoCenter.setPlayerIsLeader(p);
							msg = new Message(i, Message.ACTION_LEADING, (Object)trick, isUnderRevolution, isUnderJackBack, isTight);
							updateInfo(players);
							effectNumber = getAndCheckHand(infoCenter, players, p);
							infoCenter.setPlayerIsLastPlayer(p);

						} else {
							// 
							// can skip or play hand
							// 	
							isNewTrick = false;
							effectNumber = getAndCheckHand(infoCenter, players, p);
							if(effectNumber == -1) {
								skipNumber++;
							} else {
								infoCenter.setPlayerIsLastPlayer(p);
								skipNumber = 0;
							}
							
						}

						if(effectNumber == -7 || effectNumber == -10) {

							msg = new Message(i, Message.ACTION_LOSING, (Object)infoCenter.getPlayerHand(p), isUnderRevolution, isUnderJackBack, isTight);

							int playing = infoCenter.getPlayingNumber() - 1;
							infoCenter.setPlayerNoHand(p);
							infoCenter.setPlayerStatus(p, infoCenter.getStatus(playing));

							updateInfo(players);

						} else if(infoCenter.getPlayerNoHand(p)) {
							skipNumber = 0;
							infoCenter.setPlayerStatus(p, infoCenter.getStatus());
							System.err.println(p.get_name()+" get "+infoCenter.getPlayerStatus(p)+" status.");
							msg = new Message(i, (short)(Message.ACTION_PLAYING | Message.ACTION_WINNING), (Object)currentHand, isUnderRevolution, isUnderJackBack, isTight);
							updateInfo(players);
							if(infoCenter.getPlayingNumber() == nPlayer-1) {

								Player gm = infoCenter.getGrandMillionaire();
								if(gm != null && gm != p) {
									// 
									// gm become EXTREME_NEEDY and no hand
									// 
									msg = new Message(players.indexOf(gm), Message.ACTION_LOSING, (Object)infoCenter.getPlayerHand(gm), isUnderRevolution, isUnderJackBack, isTight);
									infoCenter.setPlayerStatus(gm, InfoCenter.EXTREME_NEEDY);
									infoCenter.setPlayerNoHand(gm);
									updateInfo(players);
								} 
							}
						} else {
							if(effectNumber == -1) {
								msg = new Message(i, Message.ACTION_PASSING, (Object)currentHand, isUnderRevolution, isUnderJackBack, isTight);
							} else {
								msg = new Message(i, Message.ACTION_PLAYING, (Object)currentHand, isUnderRevolution, isUnderJackBack, isTight);
							}
							updateInfo(players);
						}

						if(effectNumber == -8) {
							i -= 1;
							if(infoCenter.getPlayerNoHand(p)) {
								int playerIndex = players.indexOf(p);
								int cur = playerIndex;
								Player nextPlayer = null;
								while(true) {
									cur = (cur+1)%nPlayer;
									if(cur != playerIndex) {
										nextPlayer = players.get(cur);
										if(!infoCenter.getPlayerNoHand(nextPlayer)) {
											break;
										}
									}
								}
								infoCenter.setPlayerIsLeader(nextPlayer);
								infoCenter.setPlayerIsLastPlayer(nextPlayer);
							}
						}

						System.err.println("effectNumber: "+effectNumber);

						if(infoCenter.getPlayingNumber() == 1) {
							Player lastPlayer = infoCenter.getLastPlayer();
							msg = new Message(players.indexOf(lastPlayer), Message.ACTION_LOSING, (Object)infoCenter.getPlayerHand(lastPlayer), isUnderRevolution, isUnderJackBack, isTight);
							infoCenter.setPlayerNoHand(lastPlayer);
							infoCenter.setPlayerStatus(lastPlayer, infoCenter.getStatus());
							updateInfo(players);
							System.err.println(lastPlayer.get_name()+" get "+infoCenter.getPlayerStatus(lastPlayer)+" status.");
						}
					}
				}
			}
			infoCenter.playersGetStatusScore();

			int[] score = new int[nPlayer];
			for(int i=0; i<nPlayer; i++) {
				score[i] = infoCenter.getPlayerScore(players.get(i));
			}
			msg = new Message(-1, Message.ACTION_UPDT_SCORE, (Object)score, isUnderRevolution, isUnderJackBack, isTight);
			updateInfo(players);

		}

		msg = new Message(-1, Message.ACTION_THE_END, (Object)null, isUnderRevolution, isUnderJackBack, isTight);
		updateInfo(players);

		System.out.println("===========================");
		infoCenter.printResult();
	}

	/**
	 * function to create socket connection with client
	 * @param _players ArrayList of Player reference
	 */
	public static void createConnection(ArrayList<Player> _players) {
		Server myServer = new Server(nHumanPlayer);
		ArrayList<Socket> mySocket = myServer.startListen();
		for(int i=0; i<nHumanPlayer; i++) {
			players.get(i).setSocket(mySocket.get(i));
		}
	}

	/**
	 * function to jude the effects of hand
	 * @param _infoCenter InfoCenter reference
	 * @param _players ArrayList of Player reference
	 * @param _player Crrent player reference
	 * @param lastHand last hand on table
	 * @param newHand new hand on table
	 * @return effect type in integer
	 */
	public static int judge(InfoCenter _infoCenter, ArrayList<Player> _players, Player _player, Hand lastHand, Hand newHand) {
		int effectNumber = 0;
		Map<String, Integer> effects = newHand.getEffects();
		int playerIndex = _players.indexOf(_player);
		int cur = playerIndex;
		Player nextPlayer = null;
		while(true) {
			cur = (cur+1)%nPlayer;
			if(cur != playerIndex) {
				nextPlayer = _players.get(cur);
				if(!_infoCenter.getPlayerNoHand(nextPlayer)) {
					break;
				}
			}
		}
		if(!isNewTrick) {
			if(lastHand.isTight(newHand)) {
				isTight = true;
			}
		} 

		if(newHand.getType() == Hand.STRAIGHT_FLUSH) {
			isUnderRevolution = !isUnderRevolution;
		}
		if(newHand.getType() == Hand.FOUR_OF_A_KIND) {
			isUnderRevolution = !isUnderRevolution;
		}

		if(effects.get("SkipFive") > 0) {
			effectNumber = effects.get("SkipFive");
		}
		if(effects.get("GiveSeven") > 0) {
			ArrayList<Card> giveCards = _player.give_up_card(_infoCenter.getPlayerHand(_player), effects.get("GiveSeven"));
			int give = giveCards.size();
			if(give != effects.get("GiveSeven")) {
				effectNumber = -7;
				return effectNumber;
			} else {
				_infoCenter.addPlayerHand(nextPlayer, giveCards);
				_infoCenter.removePlayerHand(_player, giveCards);
			}
		}
		if(effects.get("EndEight") > 0) {
			_infoCenter.setPlayerIsLeader(_player);
			_infoCenter.setPlayerIsLastPlayer(_player);
			effectNumber = -8;
		}
		if(effects.get("AbandonTen") > 0) {
			ArrayList<Card> giveCards = _player.give_up_card(_infoCenter.getPlayerHand(_player), effects.get("AbandonTen"));
			int give = giveCards.size();
			if(give != effects.get("AbandonTen")) {
				effectNumber = -10;
				return effectNumber;
			} else {
				_infoCenter.removePlayerHand(_player, giveCards);
				msg = new Message(_players.indexOf(_player), Message.ACTION_ABAN_CARD, (Object)giveCards, isUnderRevolution, isUnderJackBack, isTight);
				updateInfo(_players);
			}
		}
		if(effects.get("JackBack") > 0) {
			isUnderJackBack = true;
		}
		
		return effectNumber;
	}

	/**
	 * to get player's action
	 * @param beats Whether new hand can beats last hand without bias
	 * @param lastHand Last hand on table
	 * @param newHand New hand on table
	 * @return if new hand can beat last hand in boolean value
	 */
	public static boolean canBeat(boolean beats, Hand lastHand, Hand newHand) {
		boolean truth = beats;

		if(lastHand.getType() == newHand.getType() && lastHand.getType() == Hand.SINGLE && 
			(newHand.getContent().get(0).getRank() == 3 && newHand.getContent().get(0).getSuit()==Card.SPADE && lastHand.getContent().get(0).getSuit()==Card.JOKER)) {
			;
		} else if(beats && newHand.hasJoker() && lastHand.getPower() == newHand.getPower()) {
			;
		} else if(lastHand.getType() == newHand.getType()) {
			if(isUnderRevolution && lastHand.getType() == newHand.getType() && lastHand.getPower() != newHand.getPower()) {
				truth = !truth;
			}
			if(isUnderJackBack && lastHand.getType() == newHand.getType() && lastHand.getPower() != newHand.getPower()) {
				truth = !truth;
			}
		} else {
			;
		}

		if(truth && isTight) {
			if(!lastHand.isTight(newHand)) {
				truth = !truth;
			}
		}

		System.err.println("Han side beat: "+truth);
		return truth;
	}

	/**
	 * to get player's action
	 * @param _infoCenter InfoCenter reference
	 * @param _players ArrayList of Player reference
	 * @param _player Crrent player reference
	 * @param _currentHand current hand on table
	 * @return effect type in integer
	 */
	public static int getAndCheckHand(InfoCenter _infoCenter, ArrayList<Player> _players, Player _player) {
		boolean success = false;
		int chance = 5;
		int effectNumber = -1;
		Hand playHand;
		if(currentHand == null) {
			while(!success) {
				try {
					playHand = _player.play_card(_infoCenter.getPlayerHand(_player));

					if(playHand.getType() == Hand.UNKNOWN || playHand.getType() == Hand.PASS || !_infoCenter.getPlayerHasThisHand(_player, playHand.getContent())) {
						success = false;
						msg = new Message(Message.ERROR, _players.indexOf(_player), Message.ACTION_WRONG_TYPE, (Object)currentHand, isUnderRevolution, isUnderJackBack, isTight);
						updateInfo(_players);
						System.out.println("Wrong Hand! Try again.");
					} else {
						_infoCenter.removePlayerHand(_player, playHand.getContent());
						effectNumber = judge(_infoCenter, _players, _player, currentHand, playHand);
						success = true;
						currentHand = playHand;
					}
				} catch(Exception e) {
					System.out.println("Something Wrong.");
				}
			}
		} else {
			while(chance-- > 0) {
				try {
					playHand = _player.play_card(_infoCenter.getPlayerHand(_player));
					System.err.println("Brian side beat: "+playHand.beats(currentHand));
					if (canBeat(playHand.beats(currentHand), currentHand, playHand) && _infoCenter.getPlayerHasThisHand(_player, playHand.getContent())) {
						_infoCenter.removePlayerHand(_player, playHand.getContent());
						effectNumber = judge(_infoCenter, _players, _player, currentHand, playHand);
						success = true;
						currentHand = playHand;
						break;
					} else if(currentHand.getType() == Hand.UNKNOWN || !_infoCenter.getPlayerHasThisHand(_player, playHand.getContent())) {
						success = false;
						effectNumber = -1;
						msg = new Message(Message.ERROR, _players.indexOf(_player), Message.ACTION_WRONG_TYPE, (Object)currentHand, isUnderRevolution, isUnderJackBack, isTight);
						updateInfo(_players);
						System.out.println("Wrong Hand! Try again. You have "+chance+" times to try.");
					} else if(playHand.getType() == Hand.PASS) {
						break;
					} else {
						success = false;
						effectNumber = -1;
						msg = new Message(Message.ERROR, _players.indexOf(_player), Message.ACTION_CANT_BEAT, (Object)currentHand, isUnderRevolution, isUnderJackBack, isTight);
						updateInfo(_players);
						System.out.println("Can't beat current hand! Try again. You have "+chance+" times to try.");
					}
				} catch(Exception e) {
					System.out.println("Something Wrong.");
				}
			}
		}
		return effectNumber;
	}

	public static void updateInfo(ArrayList<Player> _players) {
		for(int i=0; i<nPlayer; i++) {
			_players.get(i).update_info(msg);
		}
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
			players.add(new HumanPlayer());
		}
		for(int i=0; i<nAIPlayer; i++) {
			// players.add(new AIPlayer());
			players.add(new HumanPlayer());
		}

		// createConnection(players);

		for(int i=0; i<nPlayer; i++) {
			players.get(i).enter_name();
			players.get(i).set_title(InfoCenter.COMMONER);
		}
	}

	/**
	 * to init new game setting for each round
	 * @param _infoCenter InfoCenter's reference
	 * @param _deck 	  Deck's reference
	 * @param _players 	  ArrayList of Player's reference
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
				min = 10;
			}
			// 
			// Exchange Cards
			// 
			msg = new Message(-1, Message.ACTION_EXCH_CARD, (Object)null, isUnderRevolution, isUnderJackBack, isTight);
			Player en = _players.get(0);
			Player n = _players.get(nPlayer-1);
			Player gm = _players.get(1);
			Player m = _players.get(2);
			ArrayList<Card> giveCards;
			en.update_info(msg);
			n.update_info(msg);
			while(true) {
				giveCards = en.give_up_card(_infoCenter.getPlayerHand(en), 2);
				if(giveCards.size() != 2) {
					continue;
				}
				if(_infoCenter.biggestCardsInHand(en, giveCards)) {
					_infoCenter.addPlayerHand(gm, giveCards);
					_infoCenter.removePlayerHand(en, giveCards);
					break;
				}
			}
			while(true) {
				giveCards = n.give_up_card(_infoCenter.getPlayerHand(n), 1);
				if(giveCards.size() != 1) {
					continue;
				}
				if(_infoCenter.biggestCardsInHand(n, giveCards)) {
					_infoCenter.addPlayerHand(m, giveCards);
					_infoCenter.removePlayerHand(n, giveCards);
					break;
				}
			}
			gm.update_info(msg);
			m.update_info(msg);
			while(true) {
				giveCards = gm.give_up_card(_infoCenter.getPlayerHand(gm), 2);
				if(giveCards.size() != 2) {
					continue;
				} else {
					_infoCenter.addPlayerHand(en, giveCards);
					_infoCenter.removePlayerHand(gm, giveCards);
					break;
				}
			}

			while(true) {
				giveCards = m.give_up_card(_infoCenter.getPlayerHand(m), 1);
				if(giveCards.size() != 1) {
					continue;
				} else {
					_infoCenter.addPlayerHand(n, giveCards);
					_infoCenter.removePlayerHand(m, giveCards);
					break;
				}
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
	 * to add card to player's hand
	 * @param _player player object
	 * @param _cards cards to be added
	 */
	public void addPlayerHand(Player _player, ArrayList<Card> _cards) {
		int index = this.getPlayerIndex(_player);
		for(int i=0; i<_cards.size(); i++) {
			this.playerHand.get(index).add(_cards.get(i));
		}
	}

	/**
	 * to check whether player has this hand
	 * @param _player player object
	 * @param _cards cards to be checked
	 * @retrun if player has this hand
	 */
	public boolean getPlayerHasThisHand(Player _player, ArrayList<Card> _cards) {
		int index = this.getPlayerIndex(_player);
		boolean result = true;
		for(int i=0; i<_cards.size(); i++) {
			try {
				this.playerHand.get(index).indexOf(_cards.get(i));
			} catch(Exception e) {
				result = false;
				break;
			}
		}
		return result;
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
	 * to check whether input cards are the biggest hand
	 * @param _player player object
	 * @param _cards ArrayList of card to be checked
	 * @return if input cards are the biggest in player's hand
	 */
	public boolean biggestCardsInHand(Player _player, ArrayList<Card> _cards) {
		int index = this.getPlayerIndex(_player);
		boolean status = true;
		ArrayList<Card> hand = this.getPlayerHand(_player);
		for(int i=0; i<_cards.size(); i++) {
			hand.remove(_cards.get(i));
		}
		for(int i=0; i<_cards.size(); i++) {
			for(int j=0; j<hand.size(); j++) {
				if(hand.get(j).isBiggerThan(_cards.get(i))) {
					status = false;
					break;
				}
			}
			if(!status) {
				break;
			}
		}
		return status;
	}

	/**
	 * to set player current status
	 * @param _player player object
	 * @param status player's status to be set
	 */
	public void setPlayerStatus(Player _player, byte status) {
		int index = this.getPlayerIndex(_player);
		this.playerStatus.set(index, status);
		_player.set_title(status);
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
	 * to get player index in InfoCenter by input player object
	 * @param _player player object
	 * @return the index of input player in InfoCenter
	 */
	public int getPlayerIndex(Player _player) {
		return this.players.indexOf(_player);
	}

	/**
	 * to get whether player is still playing
	 * @param _player player object
	 * @return if player is still playing
	 */
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

	public void printResult() {
		for(int i=0; i<this.players.size(); i++) {
			Player p = this.players.get(i);
			System.out.println(p.get_name()+" has "+this.scores.get(i)+" points.");
		}
		System.out.println("===========================");
	}
}


