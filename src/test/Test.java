package test;

import java.util.Arrays;
import java.util.List;

public class Test {

	public static void main(String[] args) throws Exception {

		String str="123";
		byte[] bytes = str.getBytes("UTF-8");
		System.out.println(bytes.length);
		System.out.println(Arrays.toString(bytes));
		List<byte[]> header = ByteUtils.getSendData(1249537, true, str);
		for (byte[] bs : header) {
			System.out.println(bs.length);
			System.out.println(Arrays.toString(bs));
		}
	}

}
