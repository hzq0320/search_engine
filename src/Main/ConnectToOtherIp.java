package Main;

import java.sql.Connection;
import java.sql.DriverManager;
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
		
		sql="desc urls";
        re=MySql.MySql.selectWithTitle(con,sql);
		System.out.println("url表信息:");
		for(int i=0;i<re.size();i++){
			System.out.println(re.get(i));
		}
		
		sql="desc words";
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
		String bufsql="select urls from words where word='"+Zhong.Zh.ZhToUrlEncodeUTF_8(zhWord)+"'";
		ArrayList<String> re=MySql.MySql.select(con,bufsql);
		return re;
	}
	
	public static void main(String[] args) {
		// TODO 自动生成的方法存根

		//初始化
		if(init()==false){
			System.out.println("初始化失败!");
			return ;
		}
		Scanner in=new Scanner(System.in);
		while(true){
			String s=in.next();
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
