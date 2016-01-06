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
					System.out.println(input.readUTF());
					break;
				}
			} catch(IOException e) {
				e.printStackTrace();
			} finally {
				if(input != null)
					input.close();
				if(output != null)
					output.close();
			}
		} catch(IOException e) {
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