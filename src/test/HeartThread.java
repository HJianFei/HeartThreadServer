package test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Arrays;

/**
 * UDP心跳监听类，主要监听客户端和服务器端连接是否断开
 * 
 * @author Administrator
 *
 */
public class HeartThread {

	private DatagramSocket socket;
	private boolean running = true;// 线程是否运行
	private int receiveDelay = 30000;// 接收等待时间（时间间隔,单位：毫秒）
	private int port;// 端口号

	public HeartThread(int port) {
		this.port = port;
	}

	public void start() {
		try {
			// 实例化UDP
			socket = new DatagramSocket(port);
			// 设置UDP接收等待时间
			socket.setSoTimeout(receiveDelay);
			// 启动接收线程
			new Thread(new ReceiveWatchDog()).start();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 心跳监听线程
	 * 
	 * @author Administrator
	 *
	 */
	class ReceiveWatchDog implements Runnable {

		// 定义接收的数据字节数组
		byte[] buff = new byte[5];
		DatagramPacket pack = new DatagramPacket(buff, buff.length);
		long lastTime = System.currentTimeMillis();// 最后一次接收时间

		@Override
		public void run() {
			if (socket == null) {// 线程已启动
				return;
			}
			while (running) {

				if (System.currentTimeMillis() - lastTime > receiveDelay) {// 超过最长的心跳等待时间
					// 心跳包超时，主动断开TCP连接，回收资源
					disconnSocket();
				} else {
					try {
						socket.receive(pack);// 线程阻塞，等待接收心跳数据包
						byte[] res = Arrays.copyOf(buff, pack.getLength());
						System.out.println("收到客户端心跳包：" + Arrays.toString(res));
						String address = pack.getAddress().getHostAddress();
						int port = pack.getPort();
						// 心跳响应，向客户端发送心跳响应包
						DatagramPacket packet = new DatagramPacket(res, res.length,
								new InetSocketAddress(address, port));
						socket.send(packet);
						// 更改最后一次接收心跳包的时间
						lastTime = System.currentTimeMillis();
					} catch (Exception e) {
						e.printStackTrace();
						disconnSocket();
					}
				}
			}
		}

	}

	/**
	 * 心跳超时，主动断开TCP连接
	 */
	private void disconnSocket() {

		// TODO 超时没有收到心跳包的数据包，心跳超时，客户端主动端开TCP连接
		running = false;
		System.out.println("连接超时，主动断开TCP连接");

	}

}
