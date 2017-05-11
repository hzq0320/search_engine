package Douban;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import Proxy.ProxyName;
import Proxy.proxy;
import Spider.jsoup;


/** 
* @author 作者 E-mail: zhiqianghu0320@gmail.com
* @version 创建时间：2017年4月29日 下午8:37:14 
* 类说明 ：https://movie.douban.com/review/8503473/
*/
public class Douban {

	//根据最后面那个编号获取影评
	public static String getSourceByNum(int num){
		String url="https://movie.douban.com/review/"+num;
		return jsoup.getSource(url);
	}
	
	//根据source获取Document（后面都要用）
	public static Document getDocBySource(String source){
		return Jsoup.parse(source);
	}
	
	
	//根据Document获取影评标题
	public static String getTitleByDocument(Document doc){
		Elements titles=doc.select("[property=v:summary]");
		if(titles.size()==1){
			return titles.get(0).text();
		}
		return null;
	}
	
	//根据Document获取header
	public static Element getHeaderByDocument(Document doc){
		Elements headers=doc.select("[class=main-hd]");
		if(headers.size()==1){
			return headers.get(0);
		}
		return null;
	}
	
	//根据header的Element获取各信息(用空格分割，返回字符串)
	public static String getInfoByHeader(Element header,class_douban db){
		String re="";
		
		//正则表达式匹配影评作者编号
		String pat_owner="https://www.douban.com/people/(.+?)/";
		ArrayList<String> owner_num=ZhengZe.ZhengZe.getRe(header.toString(),pat_owner);
		////System.out.println(owner_num);
		if(owner_num.size()==1){
			//发现未必是编号，可能是字符串
			//System.out.println("影评作者编号="+owner_num.get(0));
			db.ownernum=owner_num.get(0);
		}
		
		//jsoup匹配作者名字
		Elements owner_name=header.select("[property=v:reviewer]");
		if(owner_name.size()==1){
			//System.out.println("影评作者="+owner_name.get(0).text());
			db.ownername=owner_name.get(0).text();
		}
		
		//正则表达式匹配电影/电视剧编号
		String pat_movie="https://movie.douban.com/subject/(\\d+?)/";
		ArrayList<String> movie_num=ZhengZe.ZhengZe.getRe(header.toString(),pat_movie);
		if(movie_num.size()==1){
			//System.out.println("电影/电视剧编号="+movie_num.get(0));
			db.movienum=Integer.parseInt(movie_num.get(0));
			
			//jsoup匹配电影/电视剧名
			Elements movie_names=header.select("[href=https://movie.douban.com/subject/"+movie_num.get(0)+"/");
			if(movie_names.size()==1){
				//System.out.println("电影/电视剧名="+movie_names.get(0).text());
				db.moviename=movie_names.get(0).text();
			}
		}
		
		//jsoup匹配电影/电视剧评分
		Elements rating=header.select("[property=v:rating]");
		if(rating.size()==1){
			//System.out.println("影评作者的电影/电视剧评分="+rating.get(0).text());
			db.ratingstr=rating.get(0).text();
			try{
				db.ratingint=Integer.parseInt(db.ratingstr);
			}catch(Exception e){
				
			}
		}
		
		//jsoup匹配影评发布时间
		Elements time=header.select("[property=v:dtreviewed]");
		if(time.size()==1){
			//System.out.println("影评发布时间="+time.get(0).text());
			db.time=time.get(0).text();
		}
		return re;
	}
	
	
	//根据doc获取影评正文
	public static String getContentByDoc(Document doc){
		//jsoup匹配影评正文
		Elements contents=doc.select("[property=v:description]");
		if(contents.size()==1){
			//System.out.println("影评正文="+contents.get(0).text());
			return contents.get(0).text();
		}
		return "";				
	}
	
	
	//根据doc和影评编号获取有用或者没用的数量以及推荐的数量
	public static String getInfoByDoc(Document doc,int num,class_douban db){
		//jsoup匹配有用
		Elements useful=doc.select("[class=btn useful_count "+num+" j a_show_login]");
		if(useful.size()==1){
			//System.out.println("有用="+useful.get(0).text());
			db.usefulstr=useful.get(0).text().replace("有用 ","");
			try{
				db.usefulint=Integer.parseInt(db.usefulstr);
			}catch(Exception e){
				
			}
		}
		
		//jsoup匹配没用
		Elements useless=doc.select("[class=btn useless_count "+num+" j a_show_login]");
		if(useless.size()==1){
			//System.out.println("没用="+useless.get(0).text());
			db.uselessstr=useless.get(0).text().replace("没用 ","");
			try{
				db.uselessint=Integer.parseInt(db.uselessstr);
			}catch(Exception e){
				
			}
			
		}
		
		
		//jsoup获取电影/电视剧信息
		Elements movie_info=doc.select("[class=sidebar-info-wrapper]");
		if(movie_info.size()==1){
			
			//jsoup匹配title
			Elements titles=movie_info.get(0).select("[class=subject-title]");
			if(titles.size()==1){
				//System.out.println("title="+titles.get(0).text().replace("> ",""));
				db.movietitle=titles.get(0).text().replace("> ","");
			}
			
			//jsoup匹配其它主要信息
			Elements infos=movie_info.get(0).select("[class=info-item]");
			if(infos!=null&&infos.size()>0){
				for(int i=0;i<infos.size();i++){
					////System.out.println(infos.get(i).text());
					String key="";
					String value="";
					Elements keys=infos.get(i).select("[class=info-item-key]");
					if(keys.size()==1){
						key=keys.get(0).text();
					}
					Elements values=infos.get(i).select("[class=info-item-val]");
					if(values.size()==1){
						value=values.get(0).text();
					}
					if(key.length()>0&&value.length()>0){
						switch(key){
							case "导演:":
								//System.out.println("导演="+value);
								db.moviedirector=value;
								break;
							case "主演:":
								//System.out.println("主演="+value);
								db.moviestars=value;
								break;
							case "类型:":
								//System.out.println("类型="+value);
								db.movieclass=value;
								break;
							case "地区:":
								//System.out.println("地区="+value);
								db.moviearea=value;
								break;
							case "上映:":
								//System.out.println("上映="+value);
								db.movieshow=value;
								break;
							default:
								break;
						}
					}
				}
			}
			
		}
		
		return "";
		
	}
	
	//所有方法合起来
	public static class_douban getAllDoubanByNum(int num){
		class_douban db=new class_douban();
		
		//System.out.println("影评编号="+num);
		db.articlenum=num;
		String source=getSourceByNum(num);
		if(source==null||source.length()==0){
			return null;
		}
		////System.out.println(source);
		Document doc=getDocBySource(source);
		if(doc==null){
			return null;
		}
		
		String title=getTitleByDocument(doc);
		if(title!=null&&title.length()>0){
			//System.out.println("title="+title);
			db.articletitle=title;
		}
		
		Element header=getHeaderByDocument(doc);
		if(header!=null){
			getInfoByHeader(header,db);
		}
		
		String content=getContentByDoc(doc);
		if(content!=null&&content.length()>0){
			db.content=content;
		}
		
		getInfoByDoc(doc,num,db);
		return db;
	}
	
	
	//最后一个有效的编号num
	public static int maxnum;
	//sql
	public static String sql="";
	//判断当前共有多少影评(即从哪里开始)
	//private static int num;
	
	//初始化数据库,返回是否成功的结果
	public static boolean init(){
		if(!Names.init()){
			return false;
		}
		
		//判断表是否存在(两个表，一个错误表一个正确表，错误表一般是网络被屏蔽，爬完一趟后慢慢爬错误表)
		//其实只用一个表就行了,用max函数(flag用来标记是否为空)
		sql="create table if not exists "+Names.articleTableName+"(articlenum int,flag int,articletitle VARCHAR(750),ownernum VARCHAR(750),ownername VARCHAR(750),movienum int,moviename VARCHAR(750),ratingint int,time VARCHAR(750),content LONGTEXT,usefulint int,uselessint int,movietitle VARCHAR(750),moviedirector VARCHAR(750),moviestars VARCHAR(750),movieclass VARCHAR(750),moviearea VARCHAR(750),movieshow VARCHAR(750),"
													+"INDEX(articlenum),INDEX(flag),INDEX(articletitle),INDEX(ownernum),INDEX(ownername),INDEX(movienum),INDEX(moviename),INDEX(ratingint),INDEX(time),INDEX(usefulint),INDEX(uselessint),INDEX(movietitle),INDEX(moviedirector),INDEX(moviestars),INDEX(movieclass),INDEX(moviearea),INDEX(movieshow))";
		try{
			MySql.MySql.exec(Names.con,sql);
		}catch(Exception e){
			System.out.println(sql);
			System.out.println("初始化表失败!!");
			e.printStackTrace();
			return false;
		}
		
		
		maxnum=1;
		//找到最大的有效的
		sql="select articlenum from "+Names.articleTableName+" where flag=-2";
		ArrayList<String> re=MySql.MySql.select(Names.con,sql);
		//如果表不存在，插入一条
		if(re!=null&&re.size()==0){
			sql="insert into "+Names.articleTableName+"(articlenum,flag) values(1,-2)";
			try {
				MySql.MySql.execInsert(Names.con,sql);
			} catch (SQLException e) {
				// TODO 自动生成的 catch 块
				System.out.println("初始化插入新表失败!");
				e.printStackTrace();
				return false;
			}
		}else{
			//表存在，找到第一条(表示当前最大有效的num是哪个)
			sql="select articlenum from "+Names.articleTableName+" limit 1,1";
			ArrayList<String> bufre=MySql.MySql.select(Names.con,sql);
			if(bufre!=null&&bufre.size()==1){
				try{
					maxnum=Integer.parseInt(bufre.get(0));
				}catch(Exception e){
					System.out.println("获取最大影评编号失败!");
					e.printStackTrace();
					return false;
				}
			}
		}
		return true;
	}
	
	//多线程爬虫
	private static ExecutorService fixedThreadPool;
	//判断一次有多少个无效的网页
	public static int count;
	
	public synchronized static void add(){
		count++;
	}
	
	public synchronized static void addSql(String s){
		sql+=s;
	}
	
	//传的参数分别是：链表,线程数
	public static void start(ArrayList<Integer> l,int n){
		fixedThreadPool = Executors.newFixedThreadPool(n);
		int one=l.size()/n+1;
		for (int i = 0; i < n; i++) {  
			final int index = i;  
			fixedThreadPool.execute(new Runnable() {  
				public void run() {  
					try {  
						for(int i=(index*one);i<(index+1)*one;i++){
							if(i<l.size()){
								//在此修改
								//对链表中每个对象作何操作，此处为输出
								//System.out.println(l.get(i));
								class_douban db=getAllDoubanByNum(l.get(i));
								if(db!=null){
									System.out.println("线程"+index+"：num="+l.get(i)+"非空");
									String bufsql="insert into "+Names.articleTableName+"(articlenum,flag,articletitle,ownernum,ownername,movienum,moviename,ratingint,time,content,usefulint,uselessint,movietitle,moviedirector,moviestars,movieclass,moviearea,movieshow) values"
											+"("+db.articlenum+",1,'"+db.articletitle.replace("'","\"")+"',+'"+db.ownernum.replace("'","\"")+"','"+db.ownername.replace("'","\"")+"',"+db.movienum+",'"+db.moviename.replace("'","\"")+"',"+db.ratingint+",'"+db.time.replace("'","\"")+"','"+db.content.replace("'","\"")+"',"
											+db.usefulint+","+db.uselessint+",'"+db.movietitle.replace("'","\"")+"','"+db.moviedirector.replace("'","\"")+"','"+db.moviestars.replace("'","\"")+"','"+db.movieclass.replace("'","\"")+"','"+db.moviearea.replace("'","\"")+"','"+db.movieshow.replace("'","\"")+"')";
									try {
										MySql.MySql.execInsert(Names.con,bufsql);
									} catch (SQLException e) {
										// TODO 自动生成的 catch 块
										System.out.println(bufsql);
										System.out.println("线程"+index+"：num="+l.get(i)+"\t插入新表失败!");
										e.printStackTrace();
									}
								}else{
									System.out.println("线程"+index+"：num="+l.get(i)+"为空");
									add();
								}
							}
						} 
					} catch (Exception e) {  
						 e.printStackTrace();
					}  
				}  
			});  
		}  
		fixedThreadPool.shutdown();
	}
	
	
	
	public static void main(String[] args) {
		// TODO 自动生成的方法存根
		
		proxy.init();
		proxy.getProxysFromProxy();
		
		
		System.out.println(proxy.proxys.size());
		for(int i=0;i<proxy.proxys.size();i++){
			System.out.println(proxy.proxys.get(i));
			String source=jsoup.getSource(ProxyName.douban_rooturl,5000,proxy.proxys.get(i).ip,proxy.proxys.get(i).port);
			if(source!=null){
				System.out.println(source.length());
			}else{
				System.out.println("null");
			}
		}
		System.out.println("休眠中。。。");
		Time.Time.sleep(600000);
		
		
		if(init()){
			System.out.println("当前最大有效的num="+maxnum);
		}else{
			System.out.println("初始化失败!");
		}
		
		int one=10000;
		for(int i=0;i<750;i++){
			//一次推进750个
			count=0;
			ArrayList<Integer> nums=new ArrayList<Integer> ();
			for(int num=maxnum;num<maxnum+one;num++){
				
				nums.add(num);
			}
			start(nums,150);
			while(!fixedThreadPool.isTerminated()){
			}
			if(count==one){
				System.out.println("网页全都无效，可能ip被禁");
				sql="update "+Names.articleTableName+" set articlenum="+(maxnum-2*one)+" where flag=-2";
				try{
					MySql.MySql.execInsert(Names.con,sql);
				}catch(Exception e){
					System.out.println("被封禁了，且写入数据库也失败了。");
					System.out.println("当前应该写入的maxnum="+(maxnum-2*one));
					e.printStackTrace();
				}
				return ;
			}
			//否则的话加一倍推进距离
			maxnum+=one;
		}
		
	}
	

}
