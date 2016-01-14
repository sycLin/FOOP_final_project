package daifugo;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;

class Server {

	// ----- constants ----- //
	public static final int PORT_NUM = 5566;

	

	// ----- fields ----- //
	int expConnCount;


	/**
	 * to construct a dummy server
	 */
	public Server() {
		expConnCount = 0;
	}

	/**
	 * to construct a server who waits for a given number of connection
	 * @param expect_conn the number of connections needed
	 */
	public Server(int expect_conn) {
		expConnCount = expect_conn;
	}

	/**
	 * to start listening on a certain port (defined as constant)
	 * @return an arraylist of Socket, size = number of human players
	 */
	public ArrayList<Socket> startListen() {
		ArrayList<Socket> ret = new ArrayList<Socket>();
		ServerSocket serverSocket = null;
		int current_count = 0;
		try {
			serverSocket = new ServerSocket(PORT_NUM);
			System.err.println("listening on " + PORT_NUM + "...");
			while(current_count < expConnCount) {
				Socket socket = serverSocket.accept();
				ret.add(socket);
				current_count += 1;
			}
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if(serverSocket != null) {
				try {
					serverSocket.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ret;
	}


}
