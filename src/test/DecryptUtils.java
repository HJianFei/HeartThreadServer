package test;

import java.math.BigInteger;
import java.util.Arrays;

public class DecryptUtils {

	/**
	 * 命令编号
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
	 * 发送时间
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
			String binary = tmpStr.length() < 8 ? ("00000000" + tmpStr).substring(tmpStr.length()) : tmpStr;
			sb.append(binary);
		}
		return sb.toString();
	}

	/**
	 * 是否分包
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
	 * 是否加密
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
	 * 数据内容长度
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
	 * 流水号
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
	 * 后台用户标识
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
	 * 用户验证标识
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
	 * 用户Id
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
	 * 包内容
	 * 
	 * @param data
	 * @return
	 */
	public static String getContent(byte[] data) {

		byte[] decrypt = null;
		byte[] tmp = new byte[data.length - 56];
		System.arraycopy(data, 53, tmp, 0, tmp.length);
		if (isEncrypt(data)) {
			try {
				decrypt = DESUtils.decrypt(tmp, "12345678");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			decrypt = tmp;
		}
		String hexString = new String(decrypt);
		return hexString;
	}

	/**
	 * 校验码
	 * 
	 * @param data
	 * @return
	 */
	public static boolean getCrc(byte[] data) {

		byte[] tmp = new byte[2];
		System.arraycopy(data, data.length - 3, tmp, 0, tmp.length);
		byte[] crcByte = new byte[data.length - 3];
		System.arraycopy(data, 0, crcByte, 0, data.length - 3);
		byte[] crc = ByteUtils.getCrc(crcByte);
		boolean b = Arrays.equals(tmp, crc);
		return b;
	}

	/**
	 * byte[]数组转换为16进制的字符串
	 *
	 * @param bytes
	 *            要转换的字节数组
	 * @return 转换后的结果
	 */
	private static String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = (bytes.length - 1); i >= 0; i--) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}

}
