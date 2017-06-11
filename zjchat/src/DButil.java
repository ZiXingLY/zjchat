
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
	static // MySQL的JDBC URL编写方式：jdbc:mysql://主机名称：连接端口/数据库的名称?参数=值
			// 避免中文乱码要指定useUnicode和characterEncoding
			// 执行数据库操作之前要在数据库管理系统上创建一个数据库，名字自己定，
			// 下面语句之前就要先创建javademo数据库
	String url = "jdbc:mysql://bdm260213172.my3w.com:3306/bdm260213172_db?"
			+ "user=bdm260213172&password=hang183367&useUnicode=true&characterEncoding=UTF8";
	static Statement stmt;
	String userip;
	// public static void main(String args[]) throws Exception {
	DButil() {
		// throws Exception{
		try {
			// 之所以要使用下面这条语句，是因为要使用MySQL的驱动，所以我们要把它驱动起来，
			// 可以通过Class.forName把它加载进去，也可以通过初始化来驱动起来，下面三种形式都可以
			Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
			// or:
			// com.mysql.jdbc.Driver driver = new com.mysql.jdbc.Driver();
			// or：
			// new com.mysql.jdbc.Driver();

			 System.out.println("成功加载MySQL驱动程序");
//			 一个Connection代表一个数据库连接
			conn = DriverManager.getConnection(url);
			// Statement里面带有很多方法，比如executeUpdate可以实现插入，更新和删除等
			stmt = conn.createStatement();
			// sql = "create table student(NO char(20),name varchar(20),primary
			// key(NO))";
			// int result = stmt.executeUpdate(sql);//
			// executeUpdate语句会返回一个受影响的行数，如果返回-1就没有成功
			// if (result != -1) {
			// System.out.println("创建数据表成功");
			// sql = "insert into jchatuser(userNO,userPass)
			// values('zinian','183367')";
			// // result = stmt.executeUpdate(sql);
			// stmt.executeUpdate(sql);
			// sql = "insert into jchatuser(userNO,userPass)
			// values('zinian1','183367')";
			// // result = stmt.executeUpdate(sql);
			// stmt.executeUpdate(sql);
//			sql = "select * from jchatuser";
//			ResultSet rs = stmt.executeQuery(sql);// executeQuery会返回结果的集合，否则返回空值
//			System.out.println("usr\tpass");
//			while (rs.next()) {
//				System.out.println(rs.getString(1) + "\t" + rs.getString(2));// 入如果返回的是int类型可以用getInt()
//			}
			// String ch="\'";
			// sql = "select * from jchatuser where userNo="+ch+"zinian"+ch;
			// rs = stmt.executeQuery(sql);
			// while (rs.next()) {
			// System.out
			// .println(rs.getString(1) + "\t" + rs.getString(2));//
			// 入如果返回的是int类型可以用getInt()
			// }
			// }
		} catch (SQLException e) {
			System.out.println("MySQL操作错误");
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
////					 System.out.println("本机的IP = " + InetAddress.getLocalHost());
//					localip = InetAddress.getLocalHost().getHostAddress();
////					System.out.println("本机的IP = " + InetAddress.getLocalHost().getHostAddress());
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
				System.out.println("用户已存在");
				// 用户已存在

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
			ResultSet rs = stmt.executeQuery(sql);// executeQuery会返回结果的集合，否则返回空值
	//		System.out.println("usr\tpass");
			while (rs.next()) {
				String userNo = rs.getString("userNo"),nick =rs.getString("nick"),loginip = rs.getString("loginip");
				if(rs.getString("nick")==null)
					nick=userNo;
				ul.addElement(new Friend(userNo,nick,loginip));
	//			System.out.println(rs.getString(1) + "\t" + rs.getString(2));// 入如果返回的是int类型可以用getInt()
			}
		} catch (SQLException e) {
			System.out.println("MySQL操作错误");
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
//用户列表类 
//ALTER TABLE `jchatuser` 
//ADD COLUMN `onlinstatu` int(32) NULL COMMENT '在线状态 0 不在线 1 在线'
//ALTER TABLE `jchatuser` 
//MODIFY COLUMN `onlinstatu` int(32) NOT NULL DEFAULT 0 COMMENT '在线状态 0 不在线 1 在线'
//ALTER TABLE `jchatuser` 
//MODIFY COLUMN `loginip` char(32) CHARACTER SET utf8 NULL COMMENT '最近登录ip'
//ALTER TABLE `jchatuser` 
//MODIFY COLUMN `userNo` char(20) CHARACTER SET utf8 NOT NULL
//ALTER TABLE `jchatuser` 
//MODIFY COLUMN `userPass` char(20) CHARACTER SET utf8 NULL
//ALTER TABLE `jchatuser` 
//MODIFY COLUMN `onlinstatu` int(32) NULL DEFAULT 0 COMMENT '在线状态 0 不在线 1 在线'
//ALTER TABLE `jchatuser` 
//ADD COLUMN `nick` varchar(32) NULL COMMENT '昵称'
//ALTER TABLE `jchatuser` 
//ADD COLUMN `logintime` char(32) NULL
//ALTER TABLE `jchatuser` 
//MODIFY COLUMN `nick` char(32) CHARACTER SET utf8 NULL COMMENT '昵称'
//update `jchatuser` set `loginip`='127.0.0.1',`onlinstatu`=1 where `userNo`='zinizn';