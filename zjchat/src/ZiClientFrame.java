import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.ItemSelectable;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Vector;


public class ZiClientFrame{
	public static void main(String args[]) {

		loginFrame lf =new loginFrame();
	}
}

class loginFrame extends Frame {
	TextField pass, name;
	Button breg, blogin;
	Label wellabel, lpass, lname;
	Panel pbut, ppass, pname;
	Socket s = null;
	DataInputStream dis = null;
	DataOutputStream dos = null;
	DatagramSocket ds = null;
	Vector uservector;
	int localport;
	boolean islogin=true;
	boolean isLogin(){
		return islogin;
	}
	Vector getuservector(){
		return uservector;
	}
	void setds(){
		try {
			ds = new DatagramSocket();
			try {
				System.out.println("设置监听成功"+InetAddress.getLocalHost().getHostAddress());
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			System.out.println(ds.getInetAddress());
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			System.out.println("无可用聊天端口");
			e.printStackTrace();
		}
	}
	loginFrame() {
		setds();
		pbut = new Panel();
		pass = new TextField(20);
		name = new TextField(20);
		lpass = new Label("密码");
		lname = new Label("账号");
		ppass = new Panel();
		
	
		ppass.setLayout(new BorderLayout());
		pname = new Panel();
		pname.setLayout(new BorderLayout());
		ppass.add(lpass, BorderLayout.WEST);
		ppass.add(pass, BorderLayout.CENTER);
		pname.add(lname, BorderLayout.WEST);
		pname.add(name, BorderLayout.CENTER);
		pass.setEchoChar('*');
		breg = new Button("reg");
		blogin = new Button("login");
		breg.addActionListener(new ButtonAction());
		blogin.addActionListener(new ButtonAction());

		wellabel = new Label("please input the pass&name!");
		pbut.setLayout(new GridLayout());
		pbut.add(breg, BorderLayout.EAST);
		pbut.add(blogin, BorderLayout.CENTER);
		this.setLayout(new GridLayout(4, 1));
		this.add(wellabel);
		this.add(pname);
		this.add(ppass);
		this.add(pbut);
		this.setLocation(400, 300);
		this.setSize(240, 180);
		this.setVisible(true);
		this.addWindowListener(new HandleClose());
		try {
			s = new Socket("127.0.0.1", 8888);
			dos = new DataOutputStream(s.getOutputStream());
			dis = new DataInputStream(s.getInputStream());
		} catch (UnknownHostException e1) {

			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			// e1.printStackTrace();
			wellabel.setText("网络无连接或服务器错误请检查网络");

		}
		uservector = new Vector(15, 10);
//		uservector.addElement(new Friend("zixing", "admin", "127.0.0.1"));
		try {
			String userNo = null, nick = null, loginip = null, str = null;
			int n = dis.readInt();
			System.out.println("用户数：" + n);
			while (n != 0) {
				String t;
				str = dis.readUTF();
				// System.out.println(str);
				userNo = str.substring(0, str.indexOf('@'));
				t = str.substring(str.indexOf('@') + 1, str.length());
				nick = t.substring(0, t.indexOf('@'));
				loginip = t.substring(t.indexOf('@') + 1, t.length());
				uservector.addElement(new Friend(userNo, nick, loginip));
				n--;
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	class ButtonAction implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			String sname = null, spass = null, statu = null;

			sname = name.getText();
			spass = pass.getText();

			if (e.getSource().equals(blogin)) {
				if (sname.equals("") || spass.equals("")) {
					wellabel.setText("请输入用户名和密码！");
				} else {
					statu = "login";
					try {
						dos.writeUTF(statu);
						dos.flush();
					} catch (IOException e2) {
						e2.printStackTrace();
					}
					try {
						dos.writeUTF(sname + "@" + spass);
						dos.flush();
						dos.writeUTF(InetAddress.getLocalHost().getHostAddress()+"@"+ds.getLocalPort());
						dos.flush();
						if (dis.readBoolean()) {
							islogin=true;
							System.out.println(ds.getInetAddress());
							
							chatFrame1 cf =new chatFrame1(uservector,ds,sname);
							new Thread(cf).start();
							System.out.println("是否阻塞");
//							new Thread(new ChatThread()).start();
							localport++;
							dispose();
						} else {
							wellabel.setText("用户名或密码错误！");
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			// 注册事件
			else {
				statu = "reg";
				try {
					dos.writeUTF(statu);
					dos.flush();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				try {
					dos.writeUTF(sname + "@" + spass);
					dos.flush();
					if (dis.readBoolean()) {
						wellabel.setText("注册成功");
					} else {
						wellabel.setText("请重试 ");
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}

		}

	}

	class HandleClose extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			// if(dis.)
			dispose();
			// disconnect();
			System.exit(0);
		}
	}
}

class chatFrame1 extends Frame implements Runnable{
	Panel pl, pr, pb, pi;
	TextArea output;
	TextField input;
	Button bsend, bclear;
	List friendlist;
	Vector friendvector;
	DatagramSocket ds = null;
	DatagramPacket dp1 = null;
	DatagramPacket dp2 = null;
	InetAddress ip;
	int serverport = 6666;
	int localPort;
	String friendip = "127.0.0.1";
	String friendname = "zinian";
	String name = null;
	boolean con=false;

	chatFrame1(Vector uservector,DatagramSocket das,String userno) {
		ds = das;
		if(ds!=null){
			localPort = ds.getLocalPort();
			con=true;
			System.out.println("当前监听的窗口是"+ds.getLocalAddress().getHostAddress()+localPort);
		}
		localPort = ds.getLocalPort();
		this.setTitle(userno);
		this.name=userno;
		pl = new Panel();
		pr = new Panel();
		pb = new Panel();
		pi = new Panel();
		pi.setLayout(new BorderLayout());
		pl.setLayout(new BorderLayout());
		pr.setLayout(new BorderLayout());
		pb.setLayout(new GridLayout(2, 1));
		this.setLayout(new BorderLayout());
		output = new TextArea(10,20);
		input = new TextField();
		bsend = new Button("send");
		bclear = new Button("clear");
		friendvector = new Vector(15, 10);
		// friendvector.addElement(new Friend("test1"));
		// friendvector.addElement(new Friend("test2"));
		System.out.println("接收到在线用户个数：" + uservector.size());
		for (int i = 0; i < uservector.size(); i++) {
			friendvector.add((Friend) uservector.elementAt(i));
		}
		friendlist = new List(15);
		for (int i = 0; i < friendvector.size(); i++) {
			friendlist.add(((Friend) friendvector.elementAt(i)).getNick());
		}
		// friendlist.add("test1");
		// friendlist.add("test1");
		// friendlist.add("test1");
		// friendlist.add("test1");
		pb.add(bclear);
		pb.add(bsend);
		pi.add(input, BorderLayout.CENTER);
		pi.add(pb, BorderLayout.EAST);
		pr.add(output, BorderLayout.CENTER);
		pr.add(pi, BorderLayout.SOUTH);
		pl.add(friendlist, BorderLayout.CENTER);
		this.add(pr, BorderLayout.CENTER);
		this.add(pl, BorderLayout.WEST);

		this.setSize(640,480);
		this.setLocation(300, 200);
		this.setVisible(true);
		friendlist.addItemListener(new selectFriend());
		bsend.addActionListener(new sendAction());
		bclear.addActionListener(new clearAction());
		this.addWindowListener(new HandleClose());
		input.addActionListener(new sendAction());

		try {
			ip = InetAddress.getByName("127.0.0.1");
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			System.out.println("not found the 127.0.0.1");
			e1.printStackTrace();
		}
	}
//	弃用线程
	class ChatThread implements Runnable{
//		private boolean bConnected = false;
//		public void start(){
//			
//		}
		public void run(){
			System.out.println("run");
			if(ds == null)
				{
					return;
				}
			System.out.println(ds.getLocalPort());
			while(true){
				
				try{
					byte[] dataBuf = new byte[512];
					DatagramPacket ServerPacket;
//					InetAddress remoteHost;
//					int remotePort;
					String datagram,s,senderip,messages;
					ServerPacket = new DatagramPacket(dataBuf,512);
					System.out.println(ServerPacket.getPort());
					ds.receive(ServerPacket);
					
//					remoteHost = ServerPacket.getAddress();
//					remotePort = ServerPacket.getPort();
					datagram = new String(ServerPacket.getData());
					
					senderip = datagram.substring(0, datagram.indexOf('@'));
					output.append(senderip +":      \n"+ datagram + "\n");
					
//					senderip = datagram.substring(0, datagram.indexOf('@'));
//					messages = datagram.substring(datagram.indexOf('@')+1, datagram.length());
//					System.out.println("收到如下主机发来邮件"+remoteHost.getHostAddress()+"\n"+datagram);
//					datagram = new String(remoteHost.getHostName()+": mailServere"+"has already get your mails.");
//					dataBuf = datagram.getBytes();
//					ServerPacket = new DatagramPacket(dataBuf, dataBuf.length,remoteHost,remotePort);
//					ds.send(ServerPacket);
				}catch(Exception e){
					System.err.println(e);
				}
				
			}
			
			
			
		}
		protected void finalize(){
			
			if(ds != null){
				ds.close();
				ds = null;
				System.out.println("服务已关闭");
			}
			
		}
	}
	class clearAction implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			input.getText();
			input.setText("");
		}
	}

	class sendAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub

			InetAddress ip = null;
			try {
				ip = InetAddress.getByName("127.0.0.1");
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block

				System.out.println("找不到主机");
				e1.printStackTrace();
			}
			try {
				byte[] dataBuf = new byte[512];
				byte[] db = new byte[512];
				String datagram;
				datagram = name+"@"+friendname+"@"+input.getText();
//				if(friendname!=null){
//					datagram = friendname+datagram;
//				}
//				datagram = name+"@";
				dataBuf = datagram.getBytes();
				dp1 = new DatagramPacket(dataBuf, dataBuf.length, ip, serverport);
				try {
//					向服务器发送 “发送者@接收者@信息” 串
					ds.send(dp1);
					output.append(name+":\n    "+input.getText()+"\n");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					System.out.println("发生失败");
					e1.printStackTrace();
				}
			} catch (Exception e1) {
				System.out.println("UDP接收错误");
				System.err.println(e1);
			}
		}
	}
	class HandleClose extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			// if(dis.)
			dispose();
			// s.close();
			// disconnect();
			System.exit(0);
		}
	}
	class selectFriend implements ItemListener {

		public void itemStateChanged(ItemEvent e) {
			// TODO Auto-generated method stub
			int index = ((List) e.getItemSelectable()).getSelectedIndex();
			friendip = ((Friend) friendvector.elementAt(index)).getloginip();
			friendname = ((Friend) friendvector.elementAt(index)).getuserNo();
		}
	}
	public void run() {
		String str;
		byte[] buf;
		DatagramPacket dp;
			if(ds!=null)
			{
				while(con){
					
					buf = new byte[512];
					dp = new DatagramPacket(buf,512);
					try {
						ds.receive(dp);
					} catch (IOException e) {
						System.out.println("用户数据包接收出错");
						e.printStackTrace();
					}
					
					str = new String(dp.getData());
					output.append("\n"+str+"换行\n");
					
				}
			}
	}
	protected void finalize(){
		
		if(ds != null){
			ds.close();
			ds = null;
			System.out.println("服务已关闭");
		}
		
	}
}
class Friend {
	private String userNo;
	private String nick;
	private String loginip;

	Friend(String userNo) {
		this.userNo = userNo;
		this.nick = userNo;

	}

	Friend(String userNo, String nick) {
		this.userNo = userNo;
		this.nick = nick;
	}

	Friend(String userNo, String nick, String loginip) {
		this.userNo = userNo;
		this.nick = nick;
		this.loginip = loginip;
	}

	String getNick() {
		return nick;
	}

	String getuserNo() {
		return userNo;
	}

	String getloginip() {
		return loginip;
	}

	void setName(String userNo) {
		this.userNo = userNo;
	}

	void setNick(String nick) {
		this.nick = nick;
	}

	void setuserNo(String userNo) {
		this.userNo = userNo;
	}
}


