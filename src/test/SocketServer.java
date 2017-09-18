package test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer extends Thread {

	private ServerSocket serverSocket = null;
	private int port = 9090;
	private Socket accept = null;
	private static ExecutorService executorService;

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
				System.out.println("连接成功");
				executorService.execute(new ThreadHandler(accept));
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public static void main(String[] args) throws Exception {
		executorService = Executors.newCachedThreadPool();
		new SocketServer().start();
		new HeartThread(8989).start();
	}

}
