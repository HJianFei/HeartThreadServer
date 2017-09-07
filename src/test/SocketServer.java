package test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer extends Thread {

	private ServerSocket serverSocket = null;
	private int port = 9090;
	private Socket accept = null;

	public SocketServer() {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			try {
				accept = serverSocket.accept();
				new HandlerThread(accept).start();
			} catch (IOException e) {

				e.printStackTrace();
			}

		}
	}

	public static void main(String[] args) throws Exception {
		new SocketServer().start();
	}

}
