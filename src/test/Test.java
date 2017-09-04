package test;

import java.util.Arrays;
import java.util.List;

public class Test {

	public static void main(String[] args) throws Exception {

		String str = "fdafdsaf afd saf哈的";
		List<byte[]> header = ByteUtils.getSendData(1249537, true, str);
		for (byte[] bs : header) {
			System.out.println(bs.length);
			System.out.println(Arrays.toString(bs));
		}
	}

}
