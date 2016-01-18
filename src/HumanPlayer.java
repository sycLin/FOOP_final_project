package daifugo;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;

class HumanPlayer extends Player{

	// ----- constants ----- //
	public static final int NEED_RESPONSE = 1;
	public static final int DONT_NEED_RESPONSE = 2;
	private static final String MAGIC_TOKEN = "[m0therfucker]";

	// ----- fields ----- //
	private Socket mySocket;
	private String[] names;
	private int myPosition;

	public HumanPlayer() {
		super();
		mySocket = null;
		names = new String[4];
	}
	public Hand play_card(ArrayList<Card> myCards) {
		ArrayList<Card> retCards = new ArrayList<Card>();
		ArrayList<Card> jokerAs = new ArrayList<Card>();
		ArrayList<Integer> records = new ArrayList<Integer>();
		Scanner scanner = new Scanner(System.in);
		Collections.sort(myCards);
		Hand retHand;
		
		while(true) {
			output_wrapper(DONT_NEED_RESPONSE, names[myPosition] + ", your cards:");

			print_cards(myCards);
			output_wrapper(DONT_NEED_RESPONSE, "Please enter the card indices you want to play.");
			String input = output_wrapper(NEED_RESPONSE, "Or enter -1 to pass.");

			//String input = scanner.nextLine();
			String[] cardIndices = input.split(" ");
			int jokerNum = 0;
			// PASS
			if(cardIndices.length == 0 || cardIndices[0].equals("-1")) {
				retHand = new Hand(retCards);
				break;
			}
			try {	
				for(int i = 0; i < cardIndices.length; i ++) {
					if(cardIndices[i].equals("")) {
						continue;
					}
					int idx = Integer.parseInt(cardIndices[i]);
					records.add(idx);
					Card tmp = myCards.get(idx);
					retCards.add(tmp);
					jokerNum += (tmp.getRank() == 0) ? 1 : 0;
				}
				if(repeat_record(records)) {
					records.clear();
					retCards.clear();
					output_wrapper(DONT_NEED_RESPONSE, "DON'T CHEAT! Please enter distinct cards!");
					continue;
				}
				for(int i = 0; i < retCards.size(); i ++) {
					output_wrapper(DONT_NEED_RESPONSE, retCards.get(i).toString());
				}
			}
			catch(Exception ex) {
				output_wrapper(DONT_NEED_RESPONSE, "Please enter integer within 0 ~ " + Integer.toString(myCards.size() - 1));

				continue;
			}
			if(jokerNum > 0) {
				while(true) {
					output_wrapper(DONT_NEED_RESPONSE, "You have " + jokerNum + " jokers.");
					input = output_wrapper(NEED_RESPONSE, "What do you want your jokers be?");
					//input = scanner.nextLine();
					String[] cards = input.split(" ");
					if(cards.length != jokerNum) {
						output_wrapper(DONT_NEED_RESPONSE, "Number not matched. Please enter again.");
						continue;
					}
					try {
						for(int i = 0; i < jokerNum; i ++) {
							jokerAs.add(new Card(cards[i]));
						}
					}
					catch(Exception ex) {
						output_wrapper(DONT_NEED_RESPONSE, "Wrong card format, please enter again.");
						continue;
					}
					break;
				}
				retHand = new Hand(retCards, jokerAs);
			}
			// There is no Joker.
			else {
				retHand = new Hand(retCards);	
			}
			if(retHand.getType() == Hand.UNKNOWN) {
				output_wrapper(DONT_NEED_RESPONSE, "Type unknown");
				retCards.clear();
				records.clear();
				continue;
			}
			break;
		}
		return retHand;
	}
	public ArrayList<Card> give_up_card(ArrayList<Card> myCards, int number) {
		if(myCards.size() <= number) {
			return myCards;
		}
		ArrayList<Card> retCards = new ArrayList<Card>();
		Scanner scanner = new Scanner(System.in);
		ArrayList<Integer> records = new ArrayList<Integer>();
		output_wrapper(DONT_NEED_RESPONSE, "Please give up " + number + " cards.");
		Collections.sort(myCards);


		while(true) {
			output_wrapper(DONT_NEED_RESPONSE, names[myPosition] + ", your cards:");
			print_cards(myCards);
			String input = output_wrapper(NEED_RESPONSE, "Please enter the cards you want to give up.");
			//String input = scanner.nextLine();
			String[] cardIndices = input.split(" ");
			if(cardIndices.length != number) {
				output_wrapper(DONT_NEED_RESPONSE, "Please enter " + number + " cards.");
				continue;
			}
			try {	
				for(int i = 0; i < cardIndices.length; i ++) {
					if(cardIndices[i].equals("")) {
						continue;
					}
					int idx = Integer.parseInt(cardIndices[i]);
					retCards.add(myCards.get(idx));
					records.add(idx);
				}
				if(repeat_record(records)) {
					records.clear();
					retCards.clear();
					output_wrapper(DONT_NEED_RESPONSE, "DON'T CHEAT! Please give up distinct cards.");
					continue;
				}
			}
			catch(Exception ex) {
				output_wrapper(DONT_NEED_RESPONSE, "Please enter integers within 0 ~ " + Integer.toString(myCards.size() - 1));
				continue;
			}
			break;
		}
		return retCards;
	}

	public void update_info(Message msg) {
		int thisP = msg.getPlayer();
		if(msg.getType() == Message.ERROR) {
			
			print_status(msg);
			if((msg.getAction() & Message.ACTION_CANT_BEAT) != 0) {
				output_wrapper(DONT_NEED_RESPONSE, names[thisP] + ", your hand couldn't beat the last hand, please play your cards again.");
			}
			else if((msg.getAction() & Message.ACTION_WRONG_TYPE) != 0) {
				output_wrapper(DONT_NEED_RESPONSE, names[thisP] + ", you played wrong type of hand, please play your cards again.");
			}
			Hand lastHand;
			if(msg.getContent() != null) {
				lastHand = (Hand)msg.getContent();
				output_wrapper(DONT_NEED_RESPONSE, "The last hand was " + lastHand.toString());
			}
		}
		else if(msg.getType() == Message.BASIC) {
			// The GRAND MILLIONAIRE lost
			if((msg.getAction() & Message.ACTION_LOSING) != 0) {
				if(thisP == myPosition) {
					if(thisP == 1) {
						output_wrapper(DONT_NEED_RESPONSE, "You were the GRAND MILLIONAIRE, but someone won, so you lost.");
					}
					else {
						output_wrapper(DONT_NEED_RESPONSE, "For some reason, you lost.");
					}
				}
				else {
					ArrayList<Card> kingCards = (ArrayList<Card>)msg.getContent();
					if(thisP == 1) {
						output_wrapper(DONT_NEED_RESPONSE, "The GRAND MILLIONAIRE lost, here are his remains.");	
					}
					else {
						output_wrapper(DONT_NEED_RESPONSE, names[thisP] + "(" + thisP + ") lost, here are his remains.");
					}
					print_cards(kingCards);
				}
				
			}
			// A person played his cards.
			else if((msg.getAction() & Message.ACTION_PLAYING) != 0) {
				if(myPosition != thisP) {
					Hand lastHand = (Hand)msg.getContent();
					print_status(msg);
					output_wrapper(DONT_NEED_RESPONSE, names[thisP] + "(" + thisP + ")'s hand was " + lastHand.toString());
					
					// Someone won.
					if((msg.getAction() & Message.ACTION_WINNING) != 0) {
						output_wrapper(DONT_NEED_RESPONSE, names[thisP] + "(" + thisP + ") won.");	
					}	
				}
				else {
					if((msg.getAction() & Message.ACTION_WINNING) != 0) {
						output_wrapper(DONT_NEED_RESPONSE, "You won!");	
					}	
				}
			}

			else if((msg.getAction() & Message.ACTION_NEW_ROUND) != 0) {
				String tmpLine = "-----------" + "\n" + "| Round " + 
							Integer.toString((int)msg.getContent()) + " |\n"
							+ "-----------";
				output_wrapper(DONT_NEED_RESPONSE, tmpLine);
			}
			
			else if((msg.getAction() & Message.ACTION_PASSING) != 0) {
				if(myPosition != thisP) {
					Hand lastHand = (Hand)msg.getContent();
					print_status(msg);
					output_wrapper(DONT_NEED_RESPONSE, names[thisP] + "(" + thisP + ") passed");
					output_wrapper(DONT_NEED_RESPONSE, "The last hand was " + lastHand.toString());
				}
			}
			else if((msg.getAction() & Message.ACTION_LEADING) != 0) {

				output_wrapper(DONT_NEED_RESPONSE, "----- Trick " + Integer.toString((int)msg.getContent()) + " -----");
				output_wrapper(DONT_NEED_RESPONSE, "The leader of this trick is " + names[thisP] + "(" + thisP +")");
			}
			else if((msg.getAction() & Message.ACTION_EXCH_CARD) != 0) {
				switch(this.get_title()) {
					case InfoCenter.GRAND_MILLIONAIRE:
						output_wrapper(DONT_NEED_RESPONSE, "You are the GRAND MILLIONAIRE, now it's your turn to give up two cards.");
						break;
					case InfoCenter.MILLIONAIRE:
						output_wrapper(DONT_NEED_RESPONSE, "You are the MILLIONAIRE, now it's your turn to give up one card.");	
						break;
					case InfoCenter.NEEDY:
						output_wrapper(DONT_NEED_RESPONSE, "You are the NEEDY, now it's your turn to give up THE BIGGEST card.");		
						break;
					case InfoCenter.EXTREME_NEEDY:
						output_wrapper(DONT_NEED_RESPONSE, "You are the EXTREME NEEDY, now it's your turn to give up TWO BIGGEST cards.");
						break;
				}
			}
			
			else if((msg.getAction() & Message.ACTION_ABAN_CARD) != 0) {
				if(thisP != myPosition) {
					ArrayList<Card> abanCards = (ArrayList<Card>)msg.getContent();
					output_wrapper(DONT_NEED_RESPONSE, names[thisP] + "(" + thisP +") abandoned " + abanCards.size() + " cards.");
					output_wrapper(DONT_NEED_RESPONSE, "The abandoned cards are:");
					print_cards(abanCards);	
				}
				
			}
			else if((msg.getAction() & Message.ACTION_UPDT_SCORE) != 0) {
				int[] scores = (int[])msg.getContent();
				output_wrapper(DONT_NEED_RESPONSE, "------ SCORES ------");
				for(int i = 0; i < 4; i ++) {
					output_wrapper(DONT_NEED_RESPONSE, names[i] + ": " + scores[i]);
				}
			}
			else if((msg.getAction() & Message.ACTION_UPDT_POS) != 0) {
				myPosition = msg.getPlayer();
				String[] tmpNames = (String[])msg.getContent();
				
				for(int i = 0; i < 4; i ++) {
					
					names[i] = tmpNames[i];	
					output_wrapper(DONT_NEED_RESPONSE, "Player at position " + i + ": " + names[i]);
					output_wrapper(DONT_NEED_RESPONSE, "Your position: " + myPosition);
				}
				
			}
			else if((msg.getAction() & Message.ACTION_THE_END) != 0) {
				output_wrapper(DONT_NEED_RESPONSE, "GAME OVER");	
			}
				
		}
	}
	public void enter_name() {
		Scanner scanner = new Scanner(System.in);
		while(true) {
			String tmp = output_wrapper(NEED_RESPONSE, "Please enter your name:");
			//String tmp = scanner.nextLine();
			if(tmp.length() > Player.MAX_NAME_LENGTH) {
				output_wrapper(DONT_NEED_RESPONSE, "Please enter name length smaller than 50.");
				continue;
			}
			name = tmp;
			break;
		}
	}
	private void print_cards(ArrayList<Card> myCards) {
		String tmpLine = "";
		for(int i = 0; i < myCards.size(); i ++) {
			tmpLine += "(" + i + ")" + myCards.get(i).toString();
		}
		output_wrapper(DONT_NEED_RESPONSE, tmpLine);
	}
	private boolean repeat_record(ArrayList<Integer> records) {
		Collections.sort(records);
		for(int i = 0; i < records.size() - 1; i ++) {
			if(records.get(i) == records.get(i + 1)) {
				return true;
			}
		}
		return false;
	}
	private void print_status(Message msg) {
		String tmpLine = "";
		output_wrapper(DONT_NEED_RESPONSE, "---------- STATUS ----------");
		tmpLine += "| isUnderRevolution: " + msg.isUnderRevolution();
		tmpLine += msg.isUnderRevolution() ? "  |\n" : " |\n";
		
		tmpLine += "| isUnderJackBack:   " + msg.isUnderJackBack();
		tmpLine += msg.isUnderJackBack() ? "  |\n" : " |\n";
		
		tmpLine += 	"| isTight:           " + msg.isTight();
		tmpLine += msg.isTight() ? "  |\n" : " |\n";
		
		tmpLine += 	"----------------------------";
		output_wrapper(DONT_NEED_RESPONSE, tmpLine);
	}

	/**
	 * to receive a socket reference from Daifugo
	 * @param _socket the socket got from Daifugo
	 */
	public void setSocket(Socket _socket) {
		mySocket = _socket;
	}

	/**
	 * to communicate with human
	 * @param type either NEED_RESPONSE or DONT_NEED_RESPONSE (both pre-defined constants)
	 * @param s the string to output
	 * @return a string of response, will return an empty string if DONT_NEED_RESPONSE
	 */
	private String output_wrapper(int type, String s) {
		// System.out.println("inside output_wrapper() function");
		String ret = "";
		DataInputStream input = null;
		DataOutputStream output = null;
		// write to socket
		try {
			// System.out.println("writing to socket...");
			output = new DataOutputStream(mySocket.getOutputStream());
			if(type == NEED_RESPONSE)
				output.writeUTF(MAGIC_TOKEN + s); // need magic token if want response
			else
				output.writeUTF(s);
			output.flush();
		} catch(IOException e) {
			e.printStackTrace();
		}
		// get response
		if(type == NEED_RESPONSE) {
			// System.out.println("(needing response)");
			try {
				input = new DataInputStream(mySocket.getInputStream());
				do {
					try {
						ret = input.readUTF();
						// System.out.println("read this: " + ret + ", with length = " + ret.length());
					} catch(Exception e) {
						// System.out.println("caught an error when reading DataInputStream...");
						continue;
					}
				} while(ret == null || ret.length() <= 0);
			} catch(IOException e) {
				// System.out.println("caught an error when getInputStream()...");
				e.printStackTrace();
			}
		}
		// System.out.println("output_wrapper() about to return this: " + ret);
		return ret;
	}

}


/**
 *	TODO LIST:
 *	1. When pass, any string includes "PASS", or "-1"	----- DONE
 *	2. fix the bugs of two space for split.      		----- DONE
 *	3. repetitive cards 								----- DONE
 *  4. print_cards()
 */
