package MySql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/** 
* @author 作者 E-mail: zhiqianghu0320@gmail.com
* @version 创建时间：2017年4月14日 下午6:13:17 
* 类说明 
*/
public class MySql {
	
	//和时间相关的代码
	//按年月日显示当前时间：select DATE_FORMAT(NOW(),'%Y-%d-%m')
	//插入时间到date格式：insert into testdate values('1982-1-23 00:00:00')
	//按照时间排序：select DATE_FORMAT(da,'%Y-%d-%m') from testdate order by da asc
	
    //初始化连接数据库,返回初始化后的数据库
    public static  Connection init(String sqlName){
    	//驱动程序名
        String driver = "com.mysql.jdbc.Driver";
        //URL指向要访问的数据库名mydata
        String url = "jdbc:mysql://localhost:3306/"+sqlName+"?characterEncoding=UTF-8&useSSL=true";
        //MySQL配置时的用户名
        String user = "root";
        //MySQL配置时的密码
        String password = "wshzq03202839";
        Connection con =null ;
        //遍历查询结果集
        try {
            //加载驱动程序
            Class.forName(driver);
            //1.getConnection()方法，连接MySQL数据库！！
            con = DriverManager.getConnection(url,user,password);
            if(con.isClosed()){
            	System.out.println("连接数据库失败!");
            }
        }catch(Exception e){
        	System.out.println("连接数据库失败!");
        	e.printStackTrace();
        }
        return con;
    }
    
    //执行语句（增删改）
    //不要在里面处理异常
    public static  synchronized void exec(Connection con,String sql) throws SQLException{
    	//创建statement类对象，用来执行SQL语句!!
        Statement statement = con.createStatement();
        //要执行的SQL语句
        statement.executeUpdate(sql);
	}
	
    //不要在里面处理异常(解决插入中文时的问题，插入时运行set names utf8mb4)
    public static synchronized void execInsert(Connection con,String sql) throws SQLException{
    	//创建statement类对象，用来执行SQL语句!!
        Statement statement = con.createStatement();
        statement.executeUpdate("SET names utf8mb4");
        //要执行的SQL语句
        statement.executeUpdate(sql);
	}
    
    
	//查询表
	//以链表形式返回，每行数据按照空格分开了
	public static synchronized ArrayList<String> selectWithTitle(Connection con,String sql){
		ArrayList<String> re=new ArrayList<String> ();
		try{
			Statement stmt = con.createStatement();
			//查询
			ResultSet rs = stmt.executeQuery(sql);
			//大概是获取字段名
			String title="";
			ResultSetMetaData data = rs.getMetaData();
			int count=data.getColumnCount();
			for(int i=1;i<=count;i++){
				title+=data.getColumnName(i)+"\t";
				//System.out.println(data.getColumnName(i));
			}
			if(!title.equals("")){
				title=title.substring(0,title.length()-1);
			}
			re.add(title);
			rs = stmt.executeQuery(sql);
            while (rs.next()){
            	//实测就算不是String类型，也可以根据getString获得，问题就是不知道下标
            	//一种方法是while（当抛出异常时即达到最大）
            	int maxSize=1;
            	String buf="";
            	while(true){
            		try{
            			if(maxSize==1){
            				buf+=rs.getString(maxSize);
            				//re.add(rs.getString(maxSize));
                			maxSize++;
            			}else{
            				buf+="\t"+rs.getString(maxSize);
            				//re.add("\t"+rs.getString(maxSize));
                			maxSize++;
            			}
            		}catch(Exception e){
            			break;
            		}
            	}
            	re.add(buf);
            }
            rs.close();
            stmt.close();
        }catch(Exception e)
        {
            //e.printStackTrace();
        }
		return re;
	}
	
	//查询表
	//以链表形式返回，每行数据按照空格分开了
	public static synchronized ArrayList<String> select(Connection con,String sql){
		ArrayList<String> re=new ArrayList<String> ();
		try{
			Statement stmt = con.createStatement();
			//查询
			ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()){
            	//实测就算不是String类型，也可以根据getString获得，问题就是不知道下标
            	//一种方法是while（当抛出异常时即达到最大）
            	int maxSize=1;
            	String buf="";
            	while(true){
            		try{
            			if(maxSize==1){
            				buf+=rs.getString(maxSize);
            				//re.add(rs.getString(maxSize));
                			maxSize++;
            			}else{
            				buf+="\t"+rs.getString(maxSize);
            				//re.add("\t"+rs.getString(maxSize));
                			maxSize++;
            			}
            		}catch(Exception e){
            			break;
            		}
            	}
            	re.add(buf);
            }
            rs.close();
            stmt.close();
        }catch(Exception e)
        {
            //e.printStackTrace();
        }
		return re;
	}
	
	//远程连接数据库
	public static void connectToOtherIP(){
		//驱动程序名
        String driver = "com.mysql.jdbc.Driver";
        //URL指向要访问的数据库名mydata
        String url = "jdbc:mysql://120.76.145.124:3306/search"+"?characterEncoding=UTF-8&useSSL=true";
        //MySQL配置时的用户名
        String user = "root";
        //MySQL配置时的密码
        String password = "fKH31da8eqJHIU134ms1";
        Connection con =null ;
        //遍历查询结果集
        try {
            //加载驱动程序
            Class.forName(driver);
            //1.getConnection()方法，连接MySQL数据库！！
            con = DriverManager.getConnection(url,user,password);
            if(con.isClosed()){
            	System.out.println("连接数据库失败!");
            }else{
            	System.out.println("连接数据库成功!");
            }
        }catch(Exception e){
        	System.out.println("连接数据库失败!");
        	e.printStackTrace();
        }
        String sql="select count(*) from urls where flagpa=1 and text is not null";
       // sql="select * from urls where url like 'http\\%3a\\%2f\\%2fjwc.hdu.edu.cn\\%2fnode\\%2f%.jspx'";
       // String sql="select urls from words where word='"+Zhong.Zh.ZhToUrlEncodeUTF_8("杭州")+"'";
        long start=Time.Time.gettime13();
        ArrayList<String> re=select(con,sql);
        long end=Time.Time.gettime13();
        System.out.println("用时"+(end-start)+"毫秒:"+re);
        
	}
	
	
	public static void main(String[] args) throws SQLException {
		// TODO 自动生成的方法存根
		
		/*//初始化连接数据库
		Connection con=init("tianmao");
		//数据库获取失败，直接退出
		if(con==null){
			System.out.println("数据库获取失败!");
			return ;
		}
		//执行更新语句
		String m="UPDATE productclasses SET productclasses.flag=1 WHERE productclasses.cla='电脑用品'";
		exec(con,m);
		try {
			Thread.sleep(500000);
		} catch (InterruptedException e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
		}
		
		
		//执行创建表的语句(需要制定varchar最大长度)
		String sql="create table if not exists ZhWord(word VARCHAR(100),times int)";
		exec(con,sql);
		
		//查询行数
		// SELECT count(*) FROM 搜索引擎.zhwords;
		//显示行号
		//SELECT @r:=@r+1 as row_num ,zhwords.* FROM 搜索引擎.zhwords,(select @r:=0) b;
		//插入数据--增
		sql="insert into ZhWord(word,times) values('哈哈','1')";
		exec(con,sql);
		
		//删除数据--删(删除数据,不是删除表--drop table)
		//sql="delete from ZhWord where word='哈哈'";
		//exec(con,sql);
		
		//修改数据(updata)--改
		//sql="update ZhWord set times=times+1 where word='哈哈'";
		//exec(con,sql);
		
		//查询数据(select)--查
		sql="select * from ZhWord";
		ArrayList<String> re=select(con,sql);
		for(int i=0;i<re.size();i++){
			System.out.println(re.get(i));
		}
		
		try {
			con.close();
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}*/
		
		connectToOtherIP();
		
		
	}
	

}
