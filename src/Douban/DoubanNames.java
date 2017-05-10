package Douban;

import java.sql.Connection;

/** 
* @author 作者 E-mail: zhiqianghu0320@gmail.com
* @version 创建时间：2017年5月9日 下午6:51:54 
* 类说明 :豆瓣的一些名称
*/
public class DoubanNames {

	//数据库
	public static Connection con;
	//数据库名称
	public static String sqlName="douban";
	//表名称(豆瓣具体影评表)
	public static String articleTableName="douban";
	//表的名称(从每部电影首页获取的电影信息)
	public static String movieTableName="movie";
	//表的名称(从每部电影获取的影评网址)
	public static String shortArticleTableName="shortarticle";
	//表的名称(豆瓣从年份和页数获取的电影信息表)
	public static String shortMovieTableName="shortmovie";
	
	
	//初始化连接数据库
	public static boolean init(){
		con=MySql.MySql.init(DoubanNames.sqlName);
		if(con==null){
			System.out.println("连接数据库失败!");
			return false;
		}
		return true;
	}

}
