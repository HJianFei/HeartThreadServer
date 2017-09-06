package test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {

	private ServerSocket serverSocket = null;
	private int port = 9090;

	public void startServer() {
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("服务已启动");
			Socket accept = serverSocket.accept();
			new HandlerThread(accept).start();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public static void main(String[] args) throws Exception {

		new SocketServer().startServer();
	}

}
