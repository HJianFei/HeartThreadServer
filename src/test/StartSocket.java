package test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class StartSocket {

	public static void main(String[] args) throws Exception {

		


		 new Thread(new Runnable() {
		
		 @Override
		 public void run() {
		 // TODO Auto-generated method stub
		 Socket socket = null;
		 try {
		 socket = new Socket("192.168.1.60", 9090);
		 } catch (UnknownHostException e) {
		 // TODO Auto-generated catch block
		 e.printStackTrace();
		 } catch (IOException e) {
		 // TODO Auto-generated catch block
		 e.printStackTrace();
		 }
		 OutputStream os = null;
		 try {
		 os = socket.getOutputStream();
		 } catch (IOException e) {
		 // TODO Auto-generated catch block
		 e.printStackTrace();
		 }
		 List<byte[]> sendData = ByteUtils.getSendData(131101, true, "(123/dfe)/(45)/(wer)/");
		 for (byte[] bs : sendData) {
		 try {
		 os.write(bs);
		 } catch (Exception e) {
		 // TODO Auto-generated catch block
		 e.printStackTrace();
		 }
		 }
		 if (os != null) {
		 try {
		 os.close();
		 } catch (IOException e) {
		 // TODO Auto-generated catch block
		 e.printStackTrace();
		 }
		 }
		
		 }
		 }).start();

	}
}
