package test;

import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class StartSocket {

	public static void main(String[] args) throws Exception {

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Socket socket = new Socket("192.168.1.62", 9090);
					OutputStream os = socket.getOutputStream();
					List<byte[]> sendData = ProtocolUtils.getSendData(131101, false, "12fdasafds3");
					for (byte[] bs : sendData) {
						os.write(bs);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}
}
