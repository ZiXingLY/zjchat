
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Vector;

public class DButil {

	static Connection conn = null;
	static String sql;
	static // MySQL��JDBC URL��д��ʽ��jdbc:mysql://�������ƣ����Ӷ˿�/���ݿ������?����=ֵ
			// ������������Ҫָ��useUnicode��characterEncoding
			// ִ�����ݿ����֮ǰҪ�����ݿ����ϵͳ�ϴ���һ�����ݿ⣬�����Լ�����
			// �������֮ǰ��Ҫ�ȴ���javademo���ݿ�
	String url = "jdbc:mysql://bdm260213172.my3w.com:3306/bdm260213172_db?"
			+ "user=bdm260213172&password=hang183367&useUnicode=true&characterEncoding=UTF8";
	static Statement stmt;
	String userip;
	// public static void main(String args[]) throws Exception {
	DButil() {
		// throws Exception{
		try {
			// ֮����Ҫʹ������������䣬����ΪҪʹ��MySQL����������������Ҫ��������������
			// ����ͨ��Class.forName�������ؽ�ȥ��Ҳ����ͨ����ʼ������������������������ʽ������
			Class.forName("com.mysql.jdbc.Driver");// ��̬����mysql����
			// or:
			// com.mysql.jdbc.Driver driver = new com.mysql.jdbc.Driver();
			// or��
			// new com.mysql.jdbc.Driver();

			 System.out.println("�ɹ�����MySQL��������");
//			 һ��Connection����һ�����ݿ�����
			conn = DriverManager.getConnection(url);
			// Statement������кܶ෽��������executeUpdate����ʵ�ֲ��룬���º�ɾ����
			stmt = conn.createStatement();
			// sql = "create table student(NO char(20),name varchar(20),primary
			// key(NO))";
			// int result = stmt.executeUpdate(sql);//
			// executeUpdate���᷵��һ����Ӱ����������������-1��û�гɹ�
			// if (result != -1) {
			// System.out.println("�������ݱ�ɹ�");
			// sql = "insert into jchatuser(userNO,userPass)
			// values('zinian','183367')";
			// // result = stmt.executeUpdate(sql);
			// stmt.executeUpdate(sql);
			// sql = "insert into jchatuser(userNO,userPass)
			// values('zinian1','183367')";
			// // result = stmt.executeUpdate(sql);
			// stmt.executeUpdate(sql);
//			sql = "select * from jchatuser";
//			ResultSet rs = stmt.executeQuery(sql);// executeQuery�᷵�ؽ���ļ��ϣ����򷵻ؿ�ֵ
//			System.out.println("usr\tpass");
//			while (rs.next()) {
//				System.out.println(rs.getString(1) + "\t" + rs.getString(2));// ��������ص���int���Ϳ�����getInt()
//			}
			// String ch="\'";
			// sql = "select * from jchatuser where userNo="+ch+"zinian"+ch;
			// rs = stmt.executeQuery(sql);
			// while (rs.next()) {
			// System.out
			// .println(rs.getString(1) + "\t" + rs.getString(2));//
			// ��������ص���int���Ϳ�����getInt()
			// }
			// }
		} catch (SQLException e) {
			System.out.println("MySQL��������");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} // finally {
			// conn.close();
			// }

	}
	void setip(String s){
		userip = s;
	}
	boolean Logindb(String name, String pass) throws SQLException {
		String ch = "\'";
		sql = "select userPass from jchatuser where userNo=" + ch + name + ch;
		boolean flag = false;
		try {
			ResultSet rs = this.stmt.executeQuery(sql);
			if (!rs.wasNull() && rs.next() && rs.getString(1).equals(pass))
			// if(rs.next())
			{
//				String localip=null;
//				 try {
////					 System.out.println("������IP = " + InetAddress.getLocalHost());
//					localip = InetAddress.getLocalHost().getHostAddress();
////					System.out.println("������IP = " + InetAddress.getLocalHost().getHostAddress());
//				} catch (UnknownHostException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				
				flag = true;
				sql = "update jchatuser set loginip = '"+userip+"' ,onlinestatu = 1 where userNo = '"+name+"';";
//		update `jchatuser` set `loginip`='127.0.0.1',`onlinstatu`=1 where `userNo`='zinizn';
				// System.out.println(rs.getString(1));
//				System.out.println(sql);
				stmt.executeUpdate(sql);
			} else {
				flag = false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
//		finally {
//			conn.close();
//		}
		return flag;
	}

	int regdb(String name, String pass) {
		sql = "select userNo from jchatuser where userNo=" + "'" + name + "'";
		int flag = 0;
		try {
			ResultSet rs = this.stmt.executeQuery(sql);
			if (!rs.wasNull() && rs.next()) {
				flag = -1;
				System.out.println("�û��Ѵ���");
				// �û��Ѵ���

			} else {
				sql = "insert into jchatuser(userNO,userPass) values('" + name + "','" + pass + "')";
				int result = stmt.executeUpdate(sql);
				flag = 1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return flag;

	}
	Vector getUserList(){
		Vector ul=new Vector(15,10);
		sql = "select * from jchatuser";
		try{
			ResultSet rs = stmt.executeQuery(sql);// executeQuery�᷵�ؽ���ļ��ϣ����򷵻ؿ�ֵ
	//		System.out.println("usr\tpass");
			while (rs.next()) {
				String userNo = rs.getString("userNo"),nick =rs.getString("nick"),loginip = rs.getString("loginip");
				if(rs.getString("nick")==null)
					nick=userNo;
				ul.addElement(new Friend(userNo,nick,loginip));
	//			System.out.println(rs.getString(1) + "\t" + rs.getString(2));// ��������ص���int���Ϳ�����getInt()
			}
		} catch (SQLException e) {
			System.out.println("MySQL��������");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ul;
	}
	String getAddress(String s)
	{
		sql = "select loginip from jchatuser where userNo ="+"'"+s+"'";
		try {
			ResultSet 
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				s=rs.getString(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}
	String getuserNobyip(String ip){
		sql = "select userNo from jchatuser where loginip="+"'"+ip+"'";
		String s=null;
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				s=rs.getString(1);
				System.out.println(s);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}
	
}
//�û��б��� 
//ALTER TABLE `jchatuser` 
//ADD COLUMN `onlinstatu` int(32) NULL COMMENT '����״̬ 0 ������ 1 ����'
//ALTER TABLE `jchatuser` 
//MODIFY COLUMN `onlinstatu` int(32) NOT NULL DEFAULT 0 COMMENT '����״̬ 0 ������ 1 ����'
//ALTER TABLE `jchatuser` 
//MODIFY COLUMN `loginip` char(32) CHARACTER SET utf8 NULL COMMENT '�����¼ip'
//ALTER TABLE `jchatuser` 
//MODIFY COLUMN `userNo` char(20) CHARACTER SET utf8 NOT NULL
//ALTER TABLE `jchatuser` 
//MODIFY COLUMN `userPass` char(20) CHARACTER SET utf8 NULL
//ALTER TABLE `jchatuser` 
//MODIFY COLUMN `onlinstatu` int(32) NULL DEFAULT 0 COMMENT '����״̬ 0 ������ 1 ����'
//ALTER TABLE `jchatuser` 
//ADD COLUMN `nick` varchar(32) NULL COMMENT '�ǳ�'
//ALTER TABLE `jchatuser` 
//ADD COLUMN `logintime` char(32) NULL
//ALTER TABLE `jchatuser` 
//MODIFY COLUMN `nick` char(32) CHARACTER SET utf8 NULL COMMENT '�ǳ�'
//update `jchatuser` set `loginip`='127.0.0.1',`onlinstatu`=1 where `userNo`='zinizn';