package test;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 通讯协议的工具类
 * 
 * @author Administrator
 *
 */
public class ProtocolUtils {

	/**
	 * 命令编号
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] getTerminalBytes(int data) {
		byte[] bytes = new byte[3];
		bytes[0] = (byte) (data & 0xff);
		bytes[1] = (byte) ((data & 0xff00) >> 8);
		bytes[2] = (byte) ((data & 0xff0000) >> 16);
		return bytes;
	}

	/**
	 * 发送时间
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] getTimeBytes(long data) {
		byte[] bytes = new byte[8];
		bytes[0] = (byte) (data & 0xff);
		bytes[1] = (byte) ((data >> 8) & 0xff);
		bytes[2] = (byte) ((data >> 16) & 0xff);
		bytes[3] = (byte) ((data >> 24) & 0xff);
		bytes[4] = (byte) ((data >> 32) & 0xff);
		bytes[5] = (byte) ((data >> 40) & 0xff);
		bytes[6] = (byte) ((data >> 48) & 0xff);
		bytes[7] = (byte) ((data >> 56) & 0xff);
		return bytes;
	}

	/**
	 * 流水号
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] getSerialBytes(int data) {
		byte[] bytes = new byte[2];
		bytes[0] = (byte) (data & 0xff);
		bytes[1] = (byte) ((data & 0xff00) >> 8);
		return bytes;
	}

	/**
	 * 后台服务器用户标识
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] getServerBytes(int data) {
		byte[] bytes = new byte[1];
		bytes[0] = (byte) (data & 0xff);
		return bytes;
	}

	/**
	 * 发送用户Id
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] getUserIdBytes(int data) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) (data & 0xff);
		bytes[1] = (byte) ((data & 0xff00) >> 8);
		bytes[2] = (byte) ((data & 0xff0000) >> 16);
		bytes[3] = (byte) ((data & 0xff000000) >> 24);
		return bytes;
	}

	/**
	 * 总包数
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] getAllPacketBytes(int data) {
		byte[] bytes = new byte[2];
		bytes[0] = (byte) (data & 0xff);
		bytes[1] = (byte) ((data & 0xff00) >> 8);
		return bytes;
	}

	/**
	 * 当前包
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] getCurrPacketBytes(int data) {
		byte[] bytes = new byte[2];
		bytes[0] = (byte) (data & 0xff);
		bytes[1] = (byte) ((data & 0xff00) >> 8);
		return bytes;
	}

	/**
	 * 包内容属性
	 * 
	 * @param subpackage
	 *            是否分包
	 * @param encrypt
	 *            是否加密
	 * @param length
	 *            数据长度
	 * @return
	 */
	public static byte[] getPacketBytes(boolean subpackage, boolean encrypt, int length) {
		byte[] data = new byte[2];
		String str = "";
		String binaryLength = toBinaryString(length);
		if (subpackage && encrypt) {// 分包且加密
			str = "001001" + binaryLength;
		} else if (subpackage && !encrypt) {// 分包但不加密
			str = "001000" + binaryLength;

		} else if (!subpackage && encrypt) {// 不分包但加密
			str = "000001" + binaryLength;

		} else if (!subpackage && !encrypt) {// 不分包不加密
			str = "000000" + binaryLength;
		}
		data = BinaryToByteArray(str);

		return data;
	}

	/**
	 * 用户验证标志
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] getVerifyBytes(String verity) {

		byte[] data = null;
		try {
			data = verity.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

		return addLen(data, 32);
	}

	/**
	 * 字节数组自定义长度
	 * 
	 * @param b
	 *            原数组
	 * @param len
	 *            数组长度
	 * @return
	 */
	public static byte[] addLen(byte[] b, int len) {

		if (b != null && b.length < len) {
			byte[] retAry = new byte[len];
			for (int i = 0; i < len - b.length; i++) {
				retAry[i] = 0;
			}
			System.arraycopy(b, 0, retAry, len - b.length, b.length);
			return retAry;
		}
		return b;
	}

	/**
	 * 将 int 类型数据转成二进制的字符串，不足 int 类型位数时在前面添“0”以凑足位数
	 * 
	 * @param num
	 * @return
	 */
	public static String toBinaryString(int num) {
		char[] chs = new char[10];
		for (int i = 0; i < 10; i++) {
			chs[9 - i] = (char) (((num >> i) & 1) + '0');
		}
		return new String(chs);
	}

	/**
	 * 二进制字符串转字节数组
	 * 
	 * @param binaryString
	 * @return
	 */
	public static byte[] BinaryToByteArray(String binaryString) {

		binaryString = binaryString.trim();
		if ((binaryString.length() % 8) != 0) {
			System.out.println("二进制字符串长度不对");
			return null;
		}
		byte[] buffer = new byte[binaryString.length() / 8];
		for (int i = 0; i < buffer.length; i++) {
			String tmp = binaryString.substring(i * 8, (i + 1) * 8).trim();
			buffer[i] = (byte) Integer.parseInt(tmp, 2);
		}
		return buffer;
	}

	/**
	 * 字节数组转字符串
	 * 
	 * @param data
	 *            字节数组
	 * @return
	 */
	public static String ByteToString(byte[] data) {

		String tmpStr = new BigInteger(1, data).toString(2);
		if (tmpStr.length() < 16) {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < 16 - tmpStr.length(); i++) {
				builder.append("0");
			}
			tmpStr = builder.append(tmpStr).toString();
		}
		return tmpStr;
	}

	/**
	 * 字节数组转16进制字符串
	 * 
	 * @param src
	 * @return
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		String parsingEscape = parsingEscape(stringBuilder.toString());
		return parsingEscape;
	}

	/**
	 * 十六进制字符串转字节数组
	 * 
	 * @param hexString
	 * @return
	 */
	public static byte[] hexStringToBytes(byte[] src) {

		String hexString = bytesToHexString(src);
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/**
	 * 字符转义
	 * 
	 * @param str
	 * @return
	 */
	public static String parsingEscape(String str) {
		str = str.toUpperCase();
		if (str.contains("3D")) {
			str = str.replace("3D", "3D00");
		}
		if (str.contains("28")) {
			str = str.replace("28", "3D15");
		}
		if (str.contains("29")) {
			str = str.replace("29", "3D14");
		}
		return str;
	}

	/**
	 * 获取发送的数据内容
	 * 
	 * @param cmd
	 *            命令编号
	 * @param encrypt
	 *            是否需要加密
	 * @param content
	 *            发送内容
	 * @return
	 */
	public static List<byte[]> getSendData(int cmd, boolean encrypt, String content) {

		try {
			byte[] start = new byte[] { 40 };// 开始符
			byte[] terminalBytes = getTerminalBytes(cmd);// 命令编号
			byte[] timeBytes = getTimeBytes(System.currentTimeMillis());// 发送时间
			byte[] serialBytes = getSerialBytes(Constans.SERIAL_NUM);// 流水号
			byte[] serverBytes = getServerBytes(Constans.SERVER_NUM);// 后台服务器用户标识
			byte[] verifyBytes = getVerifyBytes(Constans.USER_VERIFY_ID);// 用户验证标识
			byte[] userIdBytes = getUserIdBytes(Constans.USER_ID);// 发送用户Id
			byte[] end = new byte[] { 41 };// 结束符

			List<byte[]> byteList = new ArrayList<>();

			byte[] hexStringToBytes = content.getBytes("UTF-8");// 包内容字节数组

			byte[] tmp = hexStringToBytes(hexStringToBytes);

			byte[] contentBytes = null;
			if (encrypt) {
				contentBytes = DESUtils.encrypt(tmp, Constans.PWD);// 数据加密
			} else {
				contentBytes = tmp;// 不需要加密
			}
			int packetCount = contentBytes.length / (Constans.PACKET_LENGTH - 4);// 数据内容分包数，为什么-4，是因为包内容里面包含2个字节的总包数，2个字节的当前包
			int endPacketLength = (contentBytes.length) % (Constans.PACKET_LENGTH - 4);// 最后一个数据包的长度

			byte[] allPacketBytes = null;// 总包数的字节数组
			if (endPacketLength > 0) {
				// 获取总包数
				allPacketBytes = getAllPacketBytes(packetCount + 1);
				endPacketLength = endPacketLength + 4;
			} else {
				allPacketBytes = getAllPacketBytes(packetCount);
			}

			int i = 0;
			if (packetCount > 1 || (packetCount == 1 && endPacketLength > 0)) {// 需要分包处理
				for (i = 0; i < packetCount; i++) {
					// 临时协议头
					byte[] tmp_head = new byte[53];
					// 每个数据包长度
					byte[] retAryBytes = new byte[Constans.PACKET_LENGTH];
					// 需要校验的数据内容
					byte[] data = new byte[tmp_head.length + Constans.PACKET_LENGTH];
					// 最后发送的数据包
					byte[] allData = new byte[tmp_head.length + Constans.PACKET_LENGTH + 3];
					// 当前包（从1开始）
					byte[] currPacketBytes = getCurrPacketBytes(i + 1);
					// 包内容
					System.arraycopy(allPacketBytes, 0, retAryBytes, 0, allPacketBytes.length);
					System.arraycopy(currPacketBytes, 0, retAryBytes, allPacketBytes.length, currPacketBytes.length);
					System.arraycopy(contentBytes, i * (Constans.PACKET_LENGTH - 4), retAryBytes,
							allPacketBytes.length + currPacketBytes.length, (Constans.PACKET_LENGTH - 4));
					// 包内容属性
					byte[] packetBytes = getPacketBytes(true, encrypt, Constans.PACKET_LENGTH);
					// 数组合并：开始符
					System.arraycopy(start, 0, tmp_head, 0, start.length);
					// 数组合并：开始符+命令编号
					System.arraycopy(terminalBytes, 0, tmp_head, start.length, terminalBytes.length);
					// 数组合并：开始符+命令编号+发送时间
					System.arraycopy(timeBytes, 0, tmp_head, start.length + terminalBytes.length, timeBytes.length);
					// 数组合并：开始符+命令编号+发送时间+包内容属性
					System.arraycopy(packetBytes, 0, tmp_head, start.length + terminalBytes.length + timeBytes.length,
							packetBytes.length);
					// 数组合并：开始符+命令编号+发送时间+包内容属性+流水号
					System.arraycopy(serialBytes, 0, tmp_head,
							start.length + terminalBytes.length + timeBytes.length + packetBytes.length,
							serialBytes.length);
					// 数组合并：开始符+命令编号+发送时间+包内容属性+流水号+后台服务器用户标识
					System.arraycopy(serverBytes, 0, tmp_head, start.length + terminalBytes.length + timeBytes.length
							+ packetBytes.length + serialBytes.length, serverBytes.length);
					// 数组合并：开始符+命令编号+发送时间+包内容属性+流水号+后台服务器用户标识+用户验证标识
					System.arraycopy(verifyBytes, 0, tmp_head, start.length + terminalBytes.length + timeBytes.length
							+ packetBytes.length + serialBytes.length + serverBytes.length, verifyBytes.length);
					// 数组合并：开始符+命令编号+发送时间+包内容属性+流水号+后台服务器用户标识+用户验证标识+发送用户Id
					System.arraycopy(
							userIdBytes, 0, tmp_head, start.length + terminalBytes.length + timeBytes.length
									+ packetBytes.length + serialBytes.length + serverBytes.length + verifyBytes.length,
							userIdBytes.length);
					// 数组合并：开始符+命令编号+发送时间+包内容属性+流水号+后台服务器用户标识+用户验证标识+发送用户Id+包内容
					System.arraycopy(tmp_head, 0, data, 0, tmp_head.length);
					System.arraycopy(retAryBytes, 0, data, tmp_head.length, retAryBytes.length);

					// 获取校验码
					byte[] crcBytes = CRCCheck.getCRCByteValue(data);
					// 数组合并：开始符+命令编号+发送时间+包内容属性+流水号+后台服务器用户标识+用户验证标识+发送用户Id+包内容+校验码
					System.arraycopy(data, 0, allData, 0, data.length);
					System.arraycopy(crcBytes, 0, allData, data.length, crcBytes.length);
					// 数组合并：开始符+命令编号+发送时间+包内容属性+流水号+后台服务器用户标识+用户验证标识+发送用户Id+包内容+校验码+结束符
					System.arraycopy(end, 0, allData, data.length + crcBytes.length, end.length);
					// 添加到发送数据集合
					byteList.add(allData);

				}
				if (endPacketLength > 4) {// 最后一个数据包
					// 临时协议头
					byte[] tmp_head = new byte[53];
					// 需要校验的数据内容
					byte[] data = new byte[tmp_head.length + endPacketLength];
					// 最后发送的数据包
					byte[] allData = new byte[tmp_head.length + endPacketLength + 3];
					// 最后一个数据包的长度
					byte[] endPacket = new byte[endPacketLength];
					// 当前包
					byte[] currPacketBytes = getCurrPacketBytes(i + 1);
					// 包内容
					System.arraycopy(allPacketBytes, 0, endPacket, 0, allPacketBytes.length);
					System.arraycopy(currPacketBytes, 0, endPacket, allPacketBytes.length, currPacketBytes.length);
					System.arraycopy(contentBytes, i * (Constans.PACKET_LENGTH - 4), endPacket,
							allPacketBytes.length + currPacketBytes.length, (endPacketLength - 4));
					// 包内容属性
					byte[] packetBytes = getPacketBytes(true, encrypt, endPacketLength);
					// 数组合并：开始符
					System.arraycopy(start, 0, tmp_head, 0, start.length);
					// 数组合并：开始符+命令编号
					System.arraycopy(terminalBytes, 0, tmp_head, start.length, terminalBytes.length);
					// 数组合并：开始符+命令编号+发送时间
					System.arraycopy(timeBytes, 0, tmp_head, start.length + terminalBytes.length, timeBytes.length);
					// 数组合并：开始符+命令编号+发送时间+包内容属性
					System.arraycopy(packetBytes, 0, tmp_head, start.length + terminalBytes.length + timeBytes.length,
							packetBytes.length);
					// 数组合并：开始符+命令编号+发送时间+包内容属性+流水号
					System.arraycopy(serialBytes, 0, tmp_head,
							start.length + terminalBytes.length + timeBytes.length + packetBytes.length,
							serialBytes.length);
					// 数组合并：开始符+命令编号+发送时间+包内容属性+流水号+后台服务器用户标识
					System.arraycopy(serverBytes, 0, tmp_head, start.length + terminalBytes.length + timeBytes.length
							+ packetBytes.length + serialBytes.length, serverBytes.length);
					// 数组合并：开始符+命令编号+发送时间+包内容属性+流水号+后台服务器用户标识+用户验证标识
					System.arraycopy(verifyBytes, 0, tmp_head, start.length + terminalBytes.length + timeBytes.length
							+ packetBytes.length + serialBytes.length + serverBytes.length, verifyBytes.length);
					// 数组合并：开始符+命令编号+发送时间+包内容属性+流水号+后台服务器用户标识+用户验证标识+发送用户Id
					System.arraycopy(
							userIdBytes, 0, tmp_head, start.length + terminalBytes.length + timeBytes.length
									+ packetBytes.length + serialBytes.length + serverBytes.length + verifyBytes.length,
							userIdBytes.length);
					// 数组合并：开始符+命令编号+发送时间+包内容属性+流水号+后台服务器用户标识+用户验证标识+发送用户Id+包内容
					System.arraycopy(tmp_head, 0, data, 0, tmp_head.length);
					System.arraycopy(endPacket, 0, data, tmp_head.length, endPacket.length);
					// 校验码
					byte[] crcBytes = CRCCheck.getCRCByteValue(data);
					// 数组合并：开始符+命令编号+发送时间+包内容属性+流水号+后台服务器用户标识+用户验证标识+发送用户Id+包内容+校验码
					System.arraycopy(data, 0, allData, 0, data.length);
					System.arraycopy(crcBytes, 0, allData, data.length, crcBytes.length);
					// 数组合并：开始符+命令编号+发送时间+包内容属性+流水号+后台服务器用户标识+用户验证标识+发送用户Id+包内容+校验码+结束符
					System.arraycopy(end, 0, allData, data.length + crcBytes.length, end.length);
					byteList.add(allData);
				}

			} else {// 不需要分包处理
				// 临时协议头
				byte[] tmp_head = new byte[53];
				// 需要验证的数据内容
				byte[] data = new byte[tmp_head.length + contentBytes.length];
				// 最后发送的数据包
				byte[] allData = new byte[tmp_head.length + contentBytes.length + 3];
				// 包内容属性
				byte[] packetBytes = getPacketBytes(false, encrypt, contentBytes.length);
				// 数组合并：开始符
				System.arraycopy(start, 0, tmp_head, 0, start.length);
				// 数组合并：开始符+命令编号
				System.arraycopy(terminalBytes, 0, tmp_head, start.length, terminalBytes.length);
				// 数组合并：开始符+命令编号+发送时间
				System.arraycopy(timeBytes, 0, tmp_head, start.length + terminalBytes.length, timeBytes.length);
				// 数组合并：开始符+命令编号+发送时间+包内容属性
				System.arraycopy(packetBytes, 0, tmp_head, start.length + terminalBytes.length + timeBytes.length,
						packetBytes.length);
				// 数组合并：开始符+命令编号+发送时间+包内容属性+流水号
				System.arraycopy(serialBytes, 0, tmp_head,
						start.length + terminalBytes.length + timeBytes.length + packetBytes.length,
						serialBytes.length);
				// 数组合并：开始符+命令编号+发送时间+包内容属性+流水号+后台服务器用户标识
				System.arraycopy(serverBytes, 0, tmp_head, start.length + terminalBytes.length + timeBytes.length
						+ packetBytes.length + serialBytes.length, serverBytes.length);
				// 数组合并：开始符+命令编号+发送时间+包内容属性+流水号+后台服务器用户标识+用户验证标识
				System.arraycopy(verifyBytes, 0, tmp_head, start.length + terminalBytes.length + timeBytes.length
						+ packetBytes.length + serialBytes.length + serverBytes.length, verifyBytes.length);
				// 数组合并：开始符+命令编号+发送时间+包内容属性+流水号+后台服务器用户标识+用户验证标识+发送用户Id
				System.arraycopy(
						userIdBytes, 0, tmp_head, start.length + terminalBytes.length + timeBytes.length
								+ packetBytes.length + serialBytes.length + serverBytes.length + verifyBytes.length,
						userIdBytes.length);
				// 数组合并：开始符+命令编号+发送时间+包内容属性+流水号+后台服务器用户标识+用户验证标识+发送用户Id+包内容
				System.arraycopy(tmp_head, 0, data, 0, tmp_head.length);
				System.arraycopy(contentBytes, 0, data, tmp_head.length, contentBytes.length);
				byte[] crcBytes = CRCCheck.getCRCByteValue(data);
				// 数组合并：开始符+命令编号+发送时间+包内容属性+流水号+后台服务器用户标识+用户验证标识+发送用户Id+包内容+校验码
				System.arraycopy(data, 0, allData, 0, data.length);
				System.arraycopy(crcBytes, 0, allData, data.length, crcBytes.length);
				// 数组合并：开始符+命令编号+发送时间+包内容属性+流水号+后台服务器用户标识+用户验证标识+发送用户Id+包内容+校验码+结束符
				System.arraycopy(end, 0, allData, data.length + crcBytes.length, end.length);
				byteList.add(allData);
			}
			return byteList;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	// 从十六进制字符串到字节数组转换
	public static byte[] HexString2Bytes(String hexstr) {
		byte[] b = new byte[hexstr.length() / 2];
		int j = 0;
		for (int i = 0; i < b.length; i++) {
			char c0 = hexstr.charAt(j++);
			char c1 = hexstr.charAt(j++);
			b[i] = (byte) ((parse(c0) << 4) | parse(c1));
		}
		return b;
	}

	private static int parse(char c) {
		if (c >= 'a')
			return (c - 'a' + 10) & 0x0f;
		if (c >= 'A')
			return (c - 'A' + 10) & 0x0f;
		return (c - '0') & 0x0f;
	}

	/**
	 * 解析命令编号
	 * 
	 * @param data
	 * @return
	 */
	public static String getTerminal(byte[] data) {

		byte[] tmp = new byte[3];
		System.arraycopy(data, 1, tmp, 0, tmp.length);
		String hexString = bytesToHexString(tmp);
		return hexString;
	}

	/**
	 * 解析发送时间
	 * 
	 * @param data
	 * @return
	 */
	public static long getTime(byte[] data) {

		byte[] tmp = new byte[8];
		System.arraycopy(data, 4, tmp, 0, tmp.length);
		String toHexString = bytesToHexString(tmp);
		return Long.parseLong(toHexString, 16);
	}

	/**
	 * 包内容属性二进制字符串
	 * 
	 * @param data
	 * @return
	 */
	public static String getPacket(byte[] data) {
		byte[] tmp = new byte[2];
		System.arraycopy(data, 12, tmp, 0, tmp.length);
		StringBuilder sb = new StringBuilder();
		for (byte b : tmp) {
			String tmpStr = Integer.toBinaryString(b);
			String binary = tmpStr.length() < 8 ? ("00000000" + tmpStr).substring(tmpStr.length())
					: tmpStr.substring(tmpStr.length() - 8);
			sb.append(binary);
		}
		return sb.toString();
	}

	/**
	 * 解析是否分包
	 * 
	 * @param data
	 * @return
	 */
	public static boolean isSubpackage(byte[] data) {
		String packet = getPacket(data);
		String string = packet.substring(2, 3);
		if (string.equals("1")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 解析是否加密
	 * 
	 * @param data
	 * @return
	 */
	public static boolean isEncrypt(byte[] data) {
		String packet = getPacket(data);
		String string = packet.substring(5, 6);
		if (string.equals("1")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 解析数据内容长度
	 * 
	 * @param data
	 * @return
	 */
	public static int contentLength(byte[] data) {
		String packet = getPacket(data);
		String substring = packet.substring(6, packet.length());
		BigInteger src = new BigInteger(substring, 2);// 转换为BigInteger类型
		return Integer.parseInt(src.toString());
	}

	/**
	 * 解析流水号
	 * 
	 * @param data
	 * @return
	 */
	public static int getSerial(byte[] data) {

		byte[] tmp = new byte[2];
		System.arraycopy(data, 14, tmp, 0, tmp.length);
		String hexString = bytesToHexString(tmp);

		return Integer.parseInt(hexString, 16);
	}

	/**
	 * 解析后台用户标识
	 * 
	 * @param data
	 * @return
	 */
	public static int getServer(byte[] data) {
		byte[] tmp = new byte[1];
		System.arraycopy(data, 16, tmp, 0, tmp.length);
		String hexString = bytesToHexString(tmp);
		return Integer.parseInt(hexString, 16);
	}

	/**
	 * 解析用户验证标识
	 * 
	 * @param data
	 * @return
	 */
	public static String getVerify(byte[] data) {
		byte[] tmp = new byte[32];
		System.arraycopy(data, 17, tmp, 0, tmp.length);
		String hexString = new String(tmp);
		return hexString.trim();
	}

	/**
	 * 解析用户Id
	 * 
	 * @param data
	 * @return
	 */
	public static int getUserId(byte[] data) {
		byte[] tmp = new byte[4];
		System.arraycopy(data, 49, tmp, 0, tmp.length);
		String hexString = bytesToHexString(tmp);
		return Integer.parseInt(hexString, 16);
	}

	/**
	 * 解析包内容
	 * 
	 * @param data
	 * @param flag
	 *            是否分包
	 * @return
	 */
	public static String getContent(byte[] data, boolean flag) {

		byte[] decrypt = null;
		byte[] tmp = null;
		if (flag) {// 分包，分包情况下，包内容前四个字节代表总包数和当前包
			tmp = new byte[data.length - 60];
			System.arraycopy(data, 57, tmp, 0, tmp.length);
		} else {// 不分包
			tmp = new byte[data.length - 56];
			System.arraycopy(data, 53, tmp, 0, tmp.length);
		}

		if (isEncrypt(data)) {// 数据加密
			try {
				decrypt = DESUtils.decrypt(tmp, "12345678");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {// 数据不加密
			decrypt = tmp;
		}
		byte[] hexStringToBytes = hexString2Bytes(decrypt);
		String hexString = new String(hexStringToBytes);
		return hexString;
	}

	/**
	 * 解析内容字节数组
	 * 
	 * @param data
	 * @param flag
	 * @return
	 */
	public static byte[] getContentByte(byte[] data, boolean flag) {

		byte[] tmp = null;
		if (flag) {
			tmp = new byte[data.length - 60];
			System.arraycopy(data, 57, tmp, 0, tmp.length);
		} else {
			tmp = new byte[data.length - 56];
			System.arraycopy(data, 53, tmp, 0, tmp.length);
		}

		return tmp;
	}

	/**
	 * 解析总包数
	 * 
	 * @param data
	 * @return
	 */
	public static int getAllPacket(byte[] data) {
		byte[] tmp = new byte[2];
		System.arraycopy(data, 53, tmp, 0, tmp.length);
		String hexString = bytesToHexString(tmp);
		return Integer.parseInt(hexString, 16);
	}

	/**
	 * 解析当前包
	 * 
	 * @param data
	 * @return
	 */
	public static int getCurPacket(byte[] data) {
		byte[] tmp = new byte[2];
		System.arraycopy(data, 55, tmp, 0, tmp.length);
		String hexString = bytesToHexString(tmp);
		return Integer.parseInt(hexString, 16);
	}

	/**
	 * 验证校验码
	 * 
	 * @param data
	 * @return
	 */
	public static boolean checkCrc(byte[] data) {

		byte[] tmp = new byte[2];
		System.out.println("接收的数据：" + Arrays.toString(data));
		System.arraycopy(data, data.length - 3, tmp, 0, tmp.length);
		byte[] crcByte = new byte[data.length - 3];
		System.arraycopy(data, 0, crcByte, 0, data.length - 3);
		System.out.println("需要校验的数据:" + Arrays.toString(crcByte));
		byte[] crc = CRCCheck.getCRCByteValue(crcByte);
		boolean b = Arrays.equals(tmp, crc);
		System.out.println("原校验码:" + Arrays.toString(tmp));
		System.out.println("校验码:" + Arrays.toString(crc));
		System.out.println("数据校验成功与否：" + b);
		return b;
	}

	/**
	 * 字节数组转16进制字符串并还原数据转义
	 * 
	 * @param src
	 * @return
	 */
	public static String bytes2HexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		String parsingEscape = unEscape(stringBuilder.toString());
		return parsingEscape;
	}

	/**
	 * 
	 * 
	 * @param hexString
	 * @return
	 */
	public static byte[] hexString2Bytes(byte[] src) {

		String hexString = bytes2HexString(src);
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * 转义字符转回原来的字符串
	 * 
	 * @param str
	 * @return
	 */
	public static String unEscape(String str) {
		str = str.toUpperCase();
		if (str.contains("3D00")) {
			str = str.replace("3D00", "3D");
		}
		if (str.contains("3D15")) {
			str = str.replace("3D15", "28");
		}
		if (str.contains("3D14")) {
			str = str.replace("3D14", "29");
		}
		return str;
	}
}
