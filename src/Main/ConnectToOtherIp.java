package Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import Time.Time;

/** 
* @author 作者 E-mail: zhiqianghu0320@gmail.com
* @version 创建时间：2017年5月8日 下午2:26:33 
* 类说明 
*/
public class ConnectToOtherIp {
	
	//数据库
	public static Connection con;
	//sql
	public static String sql;

	//url表
	public static String urlTableName="urls_computer";
	//word表
	public static String wordTableName="words_computer";
	
	
	//初始化连接
	public static boolean init(){
		//驱动程序名
        String driver = "com.mysql.jdbc.Driver";
        //URL指向要访问的数据库名mydata
        String url = "jdbc:mysql://120.76.145.124:3306/search"+"?characterEncoding=UTF-8&useSSL=true";
        //MySQL配置时的用户名
        String user = "root";
        //MySQL配置时的密码
        String password = "fKH31da8eqJHIU134ms1";
        //遍历查询结果集
        try {
            //加载驱动程序
            Class.forName(driver);
            //1.getConnection()方法，连接MySQL数据库！！
            con = DriverManager.getConnection(url,user,password);
            if(con.isClosed()){
            	System.out.println("连接数据库失败!");
            	return false;
            }else{
            	System.out.println("连接数据库成功!");
            }
        }catch(Exception e){
        	System.out.println("连接数据库失败!");
        	e.printStackTrace();
        	return false;
        }
        sql="show table status";
        ArrayList<String> re=MySql.MySql.selectWithTitle(con,sql);
		System.out.println("数据库信息:");
		for(int i=0;i<re.size();i++){
			System.out.println(re.get(i));
		}
		
		sql="desc "+urlTableName;
        re=MySql.MySql.selectWithTitle(con,sql);
		System.out.println("url表信息:");
		for(int i=0;i<re.size();i++){
			System.out.println(re.get(i));
		}
		
		sql="desc "+wordTableName;
        re=MySql.MySql.selectWithTitle(con,sql);
		System.out.println("words表信息:");
		for(int i=0;i<re.size();i++){
			System.out.println(re.get(i));
		}
		
		return true;
	}
	
	//查询（从word表从查网址）
	public static ArrayList<String> selectUrlFromWord(String zhWord){
		long start=Time.gettime13();
		String bufsql="select urls from "+wordTableName+" where word='"+Zhong.Zh.ZhToUrlEncodeUTF_8(zhWord)+"'";
		ArrayList<String> re=MySql.MySql.select(con,bufsql);
		return re;
	}
	
	//查询
	public static void se(){
		sql="SELECT count(url) FROM "+urlTableName;
		ArrayList<String> re=MySql.MySql.select(con,sql);
		System.out.println("共有网址数："+re);
		
		sql="SELECT count(url) FROM "+urlTableName+" where text is not null;";
		re=MySql.MySql.select(con,sql);
		System.out.println("有效网址数："+re);
		
		sql="select count(distinct url) from "+urlTableName;
		re=MySql.MySql.select(con,sql);
		System.out.println("不重复网址数："+re);
		
		sql="SELECT count(word) FROM "+wordTableName;
		re=MySql.MySql.select(con,sql);
		System.out.println("共有词数："+re);
		
		sql="SELECT count(distinct word) FROM "+wordTableName;
		re=MySql.MySql.select(con,sql);
		System.out.println("不重复词数："+re);
		
	}
	
	public static void main(String[] args) {
		// TODO 自动生成的方法存根

		//初始化
		if(init()==false){
			System.out.println("初始化失败!");
			return ;
		}
		
		se();
		
		
		Scanner in=new Scanner(System.in);
		
		/*//一直查询表信息
		while(true){
			System.out.println(Time.getDetailTime());
			se();
			try {
				Thread.sleep(60*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}*/

		
		while(true){
			String s=in.next();
			//根据命令查询
			if(s.equals("1")){
				//判断服务器是否被关闭
				try {
					if(con.isClosed()){
						System.out.println("关闭了");
						break;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				se();
				continue;
			}
			long start=Time.gettime13();
			ArrayList<String> re=selectUrlFromWord(s);
			System.out.println(re.size());
			//搜索出来如果长度是0表示是没有
			//搜索出来如果长度是1表第一个是详细信息
			if(re!=null&&re.size()>0){
				for(int i=0;i<re.size();i++){
					System.out.println(re.get(i));
				}
			}
			long end=Time.gettime13();
			System.out.println("用时:"+(end-start)+"秒");
		}
		
	}

}
