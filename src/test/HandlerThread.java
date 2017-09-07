package test;

import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class HandlerThread extends Thread {

	private Socket socket = null;
	// 缓冲区
	private byte[] buffer = new byte[780];
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
			// 是否加密
			boolean encrypt = DecryptUtils.isEncrypt(buffer);
			// 包内容长度
			int contentLength = DecryptUtils.contentLength(buffer);

			if (subpackage) {// 分包处理
				int curPacket = DecryptUtils.getCurPacket(buffer);
				int allPacket = DecryptUtils.getAllPacket(buffer);
				List<Byte> byteList = new ArrayList<>();
				while (true) {// 循环读取分包数据
					if (curPacket != allPacket) {// 当前包不等于总包数
						boolean crc = DecryptUtils.getCrc(buffer);// 校验码
						if (crc) {// 数据校验成功
							byte[] contentByte = DecryptUtils.getContentByte(buffer, true);
							for (byte b : contentByte) {
								byteList.add(b);
							}
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
							byte[] contentByte = DecryptUtils.getContentByte(tmp, true);
							for (byte b : contentByte) {
								byteList.add(b);
							}
							break;
						} else {
							System.out.println("数据校验出错");
							break;
						}

					}

				}
				byte[] tmp = new byte[byteList.size()];
				for (int i = 0; i < byteList.size(); i++) {
					tmp[i] = byteList.get(i);
				}
				String str = null;
				if (encrypt) {
					byte[] decrypt = DESUtils.decrypt(tmp, "12345678");
					byte[] hexStringToBytes = DecryptUtils.hexStringToBytes(decrypt);
					str = new String(hexStringToBytes);

				} else {
					byte[] hexStringToBytes = DecryptUtils.hexStringToBytes(tmp);
					str = new String(hexStringToBytes);
				}

				System.out.println("接收的数据：" + str);

			} else {// 不分包处理
				byte[] packetContent = new byte[contentLength + 56];
				System.arraycopy(buffer, 0, packetContent, 0, packetContent.length);
				boolean crc = DecryptUtils.getCrc(packetContent);
				if (crc) {
					String content = DecryptUtils.getContent(packetContent, false);
					System.out.println("接收的数据：" + content);
				} else {
					System.out.println("数据校验出错");
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
