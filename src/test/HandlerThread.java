package test;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class HandlerThread extends Thread {

	private Socket socket = null;
	private InputStream is = null;
	private OutputStream os=null;
	// 缓冲区（通用协议最大字节数）
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
			// 获取输入流
			is = socket.getInputStream();
			os=socket.getOutputStream();
			// 第一次读取流
			is.read(buffer, 0, len);
			// 是否分包
			boolean subpackage = ProtocolUtils.isSubpackage(buffer);
			// 是否加密
			boolean encrypt = ProtocolUtils.isEncrypt(buffer);
			// 当前包内容长度
			int contentLength = ProtocolUtils.contentLength(buffer);

			if (subpackage) {// 分包处理
				// 当前包
				int curPacket = ProtocolUtils.getCurPacket(buffer);
				// 总包数
				int allPacket = ProtocolUtils.getAllPacket(buffer);
				// 临时保存字节数组
				List<Byte> byteList = new ArrayList<>();
				while (true) {// 循环读取分包数据
					if (curPacket != allPacket) {// 当前包不等于总包数
						// 获取数据校验结果
						boolean crc = ProtocolUtils.checkCrc(buffer);
						if (crc) {// 数据校验成功
							// 获取当前包的内容字节数组
							byte[] contentByte = ProtocolUtils.getContentByte(buffer, true);
							for (byte b : contentByte) {
								byteList.add(b);
							}
							// 读取下一数据包
							is.read(buffer, 0, len);
							// 读取当前包
							curPacket = ProtocolUtils.getCurPacket(buffer);
							// 读取包总数
							allPacket = ProtocolUtils.getAllPacket(buffer);
							// 读取当前包的内容长度
							contentLength = ProtocolUtils.contentLength(buffer);
						} else {// 校验失败，数据出错
							// TODO 数据校验出错，丢弃数据包，要求客户端或者服务器端重发数据包
							break;
						}
					} else {// 当前包等于总包数
						// 最后一个数据包的实际长度（为什么+56，因为通用协议除了数据内容，还有56个字节是协议头的内容）
						byte[] tmp = new byte[contentLength + 56];
						System.arraycopy(buffer, 0, tmp, 0, tmp.length);
						// 获取校验结果
						boolean crc = ProtocolUtils.checkCrc(tmp);
						if (crc) {// 校验成功
							// 获取最后一个数据包的内容字节数组
							byte[] contentByte = ProtocolUtils.getContentByte(tmp, true);
							for (byte b : contentByte) {
								byteList.add(b);
							}
							break;
						} else {// 校验失败
							// TODO 内容校验失败，可以要求客户端或者服务器端重发当前数据包
							System.out.println("数据校验出错");
							break;
						}
					}
				}
				// 内容字节重组
				byte[] tmp = new byte[byteList.size()];
				for (int i = 0; i < byteList.size(); i++) {
					tmp[i] = byteList.get(i);
				}
				String str = null;
				if (encrypt) {// 数据加密，需要先进行解密，再转换传输内容
					byte[] decrypt = DESUtils.decrypt(tmp, "12345678");
					byte[] hexStringToBytes = ProtocolUtils.hexString2Bytes(decrypt);
					str = new String(hexStringToBytes);
				} else {// 数据没有加密，直接转换传输内容
					byte[] hexStringToBytes = ProtocolUtils.hexString2Bytes(tmp);
					str = new String(hexStringToBytes);
				}
				System.out.println("接收的数据：" + str);

			} else {// 不分包处理
				// 实际的传输内容字节数组
				byte[] packetContent = new byte[contentLength + 56];
				System.arraycopy(buffer, 0, packetContent, 0, packetContent.length);
				// 获取校验结果
				boolean crc = ProtocolUtils.checkCrc(packetContent);
				if (crc) {// 校验成功
					// 获取传输内容
					String content = ProtocolUtils.getContent(packetContent, false);
					os.write(buffer);

					System.out.println("接收的数据：" + content);
				} else {// 校验失败
					// TODO 数据内容校验失败，可以要求客户端或者服务器端重发当前数据包
					System.out.println("数据校验出错");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
	}
}
