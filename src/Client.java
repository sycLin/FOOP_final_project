package daifugo;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;

class Client {

	// ----- constants ----- //

	public static final int SERVER_PORT = 5566;
	private static final String MAGIC_TOKEN = "[m0therfucker]";

	public static void main(String[] argv) {
		String host = "";
		int port = SERVER_PORT;
		Socket socket = null;
		Scanner consoleInput = new Scanner(System.in);
		System.out.print("Enter server address > ");
		host = consoleInput.nextLine();
		try {
			socket = new Socket(host, port);
			DataInputStream input = null;
			DataOutputStream output = null;
			try {
				input = new DataInputStream(socket.getInputStream());
				output = new DataOutputStream(socket.getOutputStream());
				while(true) {
					try {
						String tmp_string = input.readUTF();
						if(tmp_string.startsWith(MAGIC_TOKEN)) { // need response
							System.out.println(tmp_string.substring(MAGIC_TOKEN.length()));
							String tmp = "";
							while(true) {
								tmp = consoleInput.nextLine();
								if(tmp.length() > 1) break;
							}
							output.writeUTF(tmp);
							output.flush();
						} else { // dont need response
							System.out.println(tmp_string);
						}
					} catch(Exception e1) {
						// nothing coming in from server-end
						// sleep half a second to prevent from polling
						try {
							Thread.sleep(500);
						} catch(Exception e3) {
							// cannot sleep
							continue;
						}
					}
				}
			} catch(IOException e) {
				// something wrong with DataInputStream or DataOutputStream
				e.printStackTrace();
			} finally {
				// close those things that should be closed
				if(input != null)
					input.close();
				if(output != null)
					output.close();
			}
		} catch(IOException e) {
			// error occurrs when creating socket
			e.printStackTrace();
		} finally {
			if(socket != null) {
				try {
					socket.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			if(consoleInput != null)
				consoleInput.close();
		}
	}

}
