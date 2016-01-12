import java.lang.*;
import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.net.*;

public class Server {

	public static final int PORT_NUM = 5566;

	public void listenRequest() {
		ServerSocket serverSocket = null;
		ExecutorService threadExecutor = Executors.newCachedThreadPool();
		try {
			serverSocket = new ServerSocket(PORT_NUM);
			System.out.println("listening on " + PORT_NUM + "...");
			while(true) {
				Socket socket = serverSocket.accept();
				threadExecutor.execute( new RequestThread(socket) );
			}
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if(threadExecutor != null)
				threadExecutor.shutdown();
			if(serverSocket != null) {
				try {
					serverSocket.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] argv) {
		Server server = new Server();
		server.listenRequest();
	}

	class RequestThread implements Runnable {
		private Socket clientSocket;

		public RequestThread(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}

		@Override
		public void run() {
			System.out.println("connection from " + clientSocket.getRemoteSocketAddress());
			DataInputStream input = null;
			DataOutputStream output = null;

			try {
				input = new DataInputStream(this.clientSocket.getInputStream());
				output = new DataOutputStream(this.clientSocket.getOutputStream());
				output.writeUTF(String.format("Hi, %s!\n", clientSocket.getRemoteSocketAddress()));
				int counter = 0;
				while(true) {
					output.writeUTF(String.format("This is the %d-th message for you from server XD. (waiting for your response...)", counter));
					counter += 1;
					try {
						String res = input.readUTF();
						System.out.println("Response from client: " + res);
					} catch(Exception e) {
						// cannot get response from client
						break;
					}
				}
			} catch(IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if(input != null)
						input.close();
					if(output != null)
						output.close();
					if(this.clientSocket != null && !this.clientSocket.isClosed())
						this.clientSocket.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}