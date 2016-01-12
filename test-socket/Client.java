import java.lang.*;
import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.net.*;

public class Client {

	public static void main(String[] argv) {
		String host = "";
		int port = 5566;
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
						// show the message from server on screen
						System.out.println(input.readUTF());
						// get user input and send it to server
						output.writeUTF(consoleInput.nextLine());
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