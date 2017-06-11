import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Vector;


public class ZiServer{
	boolean started=false;
	ServerSocket ss=null;
	DatagramSocket ds=null;
	boolean loginstatu = false;//�Д��䛠�B
	InetAddress ip = null;
	static int serverport = 6666;//�Ѽ���
	static int clientport = 6667;
	String userip;
	DButil db = new DButil();
	public static void main(String[] args) {
		
		new ZiServer().start();
	}
	
	public void start(){
		try{
			ss = new ServerSocket(8888);
			started = true;
			
		}catch(BindException e){
			System.out.println("�˿����ڱ�ʹ�ã�");
			System.out.println("���˿�ռ�ã�����������");
			System.exit(0);
		}catch(IOException e){
			e.printStackTrace();
		}
		try {
			ds = new DatagramSocket(serverport);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			ip = InetAddress.getByName("127.0.0.1");
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			System.out.println("not found the 127.0.0.1");
			e1.printStackTrace();
		}
		try{
			while(started){
				Socket s = ss.accept();
				loginstatu = false;
//				�Д��Ƿ��ѽ���䛵�䛳ɹ�����ChatThread
				if(!loginstatu){
					Thread loginT = new Thread(new LoginThread(s));
					loginT.start();
					loginstatu = true;
					System.out.println("�Ñ��M���䛽���"+s.getInetAddress());	
					//statu=true;
					if(loginstatu){
//						��ꑳɹ��������쾀��
						System.out.println("����chat�߳�");
						new Thread(new ChatThread()).start();
					}
				}
//				Client c = new Client(s);
//				new Thread(c).start();
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try {
				ss.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
//	����䛳ɹ��Ologinstatu��true
	class LoginThread implements Runnable{
		private Socket s;
		private DataInputStream dis = null;
		private DataOutputStream dos=null;
		private boolean bConnected = false;
		private Vector uservector = null;
//		private InetAddress ip;
		LoginThread(Socket s){
			this.s=s;
			try {
				dis = new DataInputStream(s.getInputStream());
				dos = new DataOutputStream(s.getOutputStream());
				bConnected = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		public void run() {
//			DButil db = new DButil();
//			db.userip();
			this.uservector = db.getUserList();
			int n=uservector.size();
//			int count=0;
//			for(int i = 0;i<n;i++){
//				
//			}
			System.out.println("��ǰע��������"+n);
			try {
				dos.writeInt(n);
				dos.flush();
			} catch (IOException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			
			Friend f=null;
			String str;
			for(int i=0;i<n;i++){
				f=(Friend)uservector.elementAt(i);
				str=f.getuserNo()+"@"+f.getNick()+"@"+f.getloginip();
				try {
					dos.writeUTF(str);
					dos.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			String statu=null;
			try{
				while(bConnected&&dis!=null){
//					!loginstatu&&
					try {
						statu = dis.readUTF();
					} catch (IOException e2) {
						System.out.println("����Ͽ����� �޷���ð�ť״̬");
//						System.out.println("dis errrrrrrrrrr");
//						e2.printStackTrace();
					}
					if(statu.equals("login")){
							
							str = dis.readUTF();
							String name = str.substring(0, str.indexOf('@'));
							String pass = str.substring(str.indexOf('@')+1, str.length());
							userip = dis.readUTF();
							System.out.println(userip);
							db.setip(userip);
							if(db.Logindb(name, pass)){
								dos.writeBoolean(true);
								dos.flush();
							
//								loginstatu = true;
							}
							else{
								dos.writeBoolean(false);
								dos.flush();
							}							
						}
						else if(statu.equals("reg")){
//							DButil db = new DButil();
						
								str = dis.readUTF();
								String name = str.substring(0, str.indexOf('@'));
								String pass = str.substring(str.indexOf('@')+1, str.length());
								if(db.regdb(name,pass)==1){
									dos.writeBoolean(true);
									dos.flush();
								}
								else{
									dos.writeBoolean(false);
									dos.flush();
								}
						System.out.println("ע��");
						
					}
					
				}
			}catch(EOFException e){
				System.out.println("Client Closed!");			
			}
			catch(IOException e){
//				e.printStackTrace();
				System.out.println("����Ͽ�����");
			}catch(Exception e2){
				
			}
			finally{
				
				try {
					if(s != null) s.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		}
		
	}
	
	class ChatThread implements Runnable{
		private boolean bConnected = false;
//		DButil db = new DButil();
		public void run(){
			if(ds == null)
				return;
			while(true){
				
				try{
					byte[] dataBuf = new byte[512];
					DatagramPacket ServerPacket;
					InetAddress remoteHost;
					int remotePort,toport;
					String datagram,s,receiverip,messages,userno,receiverport,remoteaddr,senderno;//remoteaddr��ʽΪhost@port
					ServerPacket = new DatagramPacket(dataBuf,512);
					ds.receive(ServerPacket);
					remoteHost = ServerPacket.getAddress();
					remotePort = ServerPacket.getPort();
//					remoteaddr = remoteHost.getHostAddress()+"@"+remotePort;
					datagram = new String(ServerPacket.getData());
//					System.out.println(remoteaddr);
					senderno = datagram.substring(0, datagram.indexOf('@'));
					s=datagram.substring(datagram.indexOf('@')+1, datagram.length());
//					����userno����messages
					userno = s.substring(0, s.indexOf('@'));
					messages = s.substring(s.indexOf('@')+1, s.length());
//					��ȡuserno�ĵ�ַ
					s=db.getAddress(userno);
					receiverip = s.substring(0, s.indexOf('@'));
					receiverport = s.substring(s.indexOf('@')+1, s.length());
					toport = Integer.parseInt(receiverport);
					
					System.out.println("�յ��������������ʼ�:\n    "+remoteHost.getHostAddress()+"\n"+userno+":"+messages);
					datagram = new String("������"+remoteHost.getHostName()+": ���յ�\n");
					dataBuf = datagram.getBytes();
//					for(int i=0;i<dataBuf.length;i++){
//						System.out.print((char)dataBuf[i]);
//					}
//					System.out.println("\n���͹�ȥ�õ�����"+datagram.length());
					remoteHost = InetAddress.getByName(receiverip);
					System.out.println("��"+remoteHost.getHostAddress()+"/"+remotePort+"����");
					ServerPacket = new DatagramPacket(dataBuf, dataBuf.length,remoteHost,remotePort);
					ds.send(ServerPacket);
//					senderip = db.getAdress(messages);
					datagram = new String(senderno+":\n    "+messages);
					dataBuf = datagram.getBytes();
					ServerPacket = new DatagramPacket(dataBuf,dataBuf.length,InetAddress.getByName(receiverip),toport);
					ds.send(ServerPacket);
				}catch(Exception e){
					System.err.println(e);
				}
				
			}
			
			
			
		}
		protected void finalize(){
			
			if(ds != null){
				ds.close();
				ds = null;
				System.out.println("�����ѹر�");
			}
			
		}
	}
}
