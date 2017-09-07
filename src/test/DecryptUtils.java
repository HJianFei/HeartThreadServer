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
			String binary = tmpStr.length() < 8 ? ("00000000" + tmpStr).substring(tmpStr.length())
					: tmpStr.substring(tmpStr.length() - 8);
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
	 * @param flag
	 *            是否分包
	 * @return
	 */
	public static String getContent(byte[] data, boolean flag) {

		byte[] decrypt = null;
		byte[] tmp = null;
		if (flag) {// 分包
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {// 数据不加密
			decrypt = tmp;
		}
		
		byte[] hexStringToBytes = DecryptUtils.hexStringToBytes(decrypt);
		String hexString = new String(hexStringToBytes);
		return hexString;
	}

	/**
	 * 内容字节数组
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
	 * 总包数
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
	 * 当前包
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
	public static String bytesToHexString(byte[] bytes) {
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

	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/**
	 * 转义字符转回原来的字符串
	 * 
	 * @param str
	 * @return
	 */
	public static String parsingEscape(String str) {
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
		if (str.contains("3D12")) {
			str = str.replace("3D12", "2F");
		}
		return str;
	}

}
