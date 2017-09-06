package test;

import java.io.InputStream;
import java.net.Socket;

public class HandlerThread extends Thread {

	private Socket socket = null;
	// 缓冲区
	private byte[] buffer = new byte[68];
	private int len = buffer.length;
	// 实际长度
	int dataLen = 0;

	public HandlerThread(Socket socket) {
		super();
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			InputStream is = socket.getInputStream();

			is.read(buffer, 0, len);
			// 是否分包
			boolean subpackage = DecryptUtils.isSubpackage(buffer);
			// 包内容长度
			int contentLength = DecryptUtils.contentLength(buffer);

			if (subpackage) {// 分包处理
				int curPacket = DecryptUtils.getCurPacket(buffer);
				int allPacket = DecryptUtils.getAllPacket(buffer);
				StringBuilder sb = new StringBuilder();
				while (true) {// 循环读取分包数据

					if (curPacket != allPacket) {// 当前包不等于总包数
						boolean crc = DecryptUtils.getCrc(buffer);// 校验码
						if (crc) {// 数据校验成功
							String content = DecryptUtils.getContent(buffer, true);
							sb.append(content);
							is.read(buffer, 0, len);
							curPacket = DecryptUtils.getCurPacket(buffer);
							allPacket = DecryptUtils.getAllPacket(buffer);
							contentLength = DecryptUtils.contentLength(buffer);
						} else {// 校验失败，数据出错
							break;
						}
					} else {// 当前包等于总包数
						byte[] tmp = new byte[contentLength + 56];
						System.arraycopy(buffer, 0, tmp, 0, tmp.length);
						boolean crc = DecryptUtils.getCrc(tmp);
						if (crc) {
							String content = DecryptUtils.getContent(tmp, true);
							sb.append(content);
							break;
						} else {
							System.out.println("数据校验出错");
							break;
						}

					}

				}
				System.out.println("接收的数据："+sb.toString());

			} else {// 不分包处理
				byte[] packetContent = new byte[contentLength + 56];
				System.arraycopy(buffer, 0, packetContent, 0, packetContent.length);
				boolean crc = DecryptUtils.getCrc(packetContent);
				if (crc) {
					String content = DecryptUtils.getContent(packetContent, false);
					System.out.println(content);
				} else {
					System.out.println("数据校验出错");
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
