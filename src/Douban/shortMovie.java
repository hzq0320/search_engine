package Douban;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import Proxy.class_proxy;
import Proxy.useful_proxy;
import Spider.jsoup;

/** 
* @author 作者 E-mail: zhiqianghu0320@gmail.com
* @version 创建时间：2017年5月1日 下午9:49:30 
* 类说明 ：根据首页获取所有电影及其影评的num
* 首页：https://movie.douban.com/tag/
* 年份日期排序：https://movie.douban.com/tag/1988?type=R
* 年份+页数：https://movie.douban.com/tag/2016?start=20&type=R表示的是2016年第(20/20+1)页（所以第i页那个值改为(i-1)*20
* 从年份和页数获取的简单的电影信息
* 发现从年份和页数获取的是不全的，还是应该根据标注次数排序，问题是这样不知道总页数，需要计算一下或者判断是否为非法页面
* 
* 05_09:发现排序方式是标注次数的时候无法获取页数（获取的是噪音），只能一页一页获取
*/
public class shortMovie {

	//首页
	public static final String root_url="https://movie.douban.com";
	//网址开头
	public static final String url_head="https://movie.douban.com/tag/";
	//网址结尾(用来表示排序方式)
	public static String url_end="&type=O";
	//所有电影num的集合(暂时没用到)
	public static LinkedHashSet<Integer> nums;
	//所有影评的num的集合
	public static LinkedHashSet<Integer> articlenums;
	
	//测试网址
	public static String douban_url="https://movie.douban.com";
	//后台线程数
	public static int doubanproxy_threadnum=2000;
	
	
	//sql
	public static String sql;
	//标记当前使用第几个代理服务器
	public static int proxyNum;
	//代理服务器
	public static class_proxy cp;
	
	//标记爬虫是否被屏蔽
	public static int threadFlag;
	
	//搜集sql
	public static ArrayList<String> sqls;
	
	//根据首页获取年份列表
	public static ArrayList<String> getYearsByRoot(){
		ArrayList<String> re=new ArrayList<String> ();
		String source=jsoup.getSource(root_url,10000,cp.ip,cp.port);
		if(source!=null&&source.length()>0){
			Document doc=Jsoup.parse(source);
			Elements tags=doc.select("[class=tagCol]");
			if(tags.size()==4){
				Elements as=tags.get(3).getElementsByTag("a");
				for(int i=0;i<as.size();i++){
					re.add(as.get(i).text());
				}
			}
		}
		return re;
	}
	
	//根据排序类型修改网址
	public static void changeEnd(int i){
		switch(i){
			case 1:
				//综合排序
				url_end="&type=";
				return ;
			case 2:
				//评分排序
				url_end="&type=S";
				return ;
			case 3:
				//日期排序
				url_end="&type=R";
				return ;
			case 4:
				//标注次数排序
				url_end="&type=O";
				return ;
		}
	}
	
	//加锁修改flag
	public synchronized static void setNum(int i){
		proxyNum=i;
	}
	
	//加锁修改flag
	public synchronized static void addNum(){
		proxyNum++;
	}
	
	//加锁修改flag
	public synchronized static void setflag(int i){
		threadFlag=i;
	}
	
	//加锁关闭线程池
	public synchronized static void articlePage_shutdown(){
		articlePage_fixedThreadPool.shutdownNow();
	}
	
	
	//根据年份获取按照日期排序的页数
	public static int getPageNumByYear(String year){
		String url=url_head+year+url_end;
		String source=jsoup.getSource(url,10000,cp.ip,cp.port);
		if(source!=null&&source.length()>0){
			String pat="data-total-page=\"(\\d+?)\"";
			ArrayList<String> re=ZhengZe.ZhengZe.getRe(source,pat);
			if(re!=null&&re.size()==1){
				try{
					return Integer.parseInt(re.get(0));
				}catch(Exception e){
					return -1;
				}
			}
		}
		return -1;
	}
	
	//根据年份和页数获取电影信息--日期排序
	public static ArrayList<class_shortmovieinfo> getMoviesByPageNum(String year,int pagenum){
		String url=url_head+year+"?start="+(pagenum-1)*20+url_end;
		ArrayList<class_shortmovieinfo> l=new ArrayList<class_shortmovieinfo> ();
		String source=jsoup.getSource(url,10000,cp.ip,cp.port);
		if(source!=null&&source.length()>0){
			//先找到所有电影的位置
			Document doc=Jsoup.parse(source);
			Elements infos=doc.select("[class=item]");
			if(infos.size()<21){
				for(int i=0;i<infos.size();i++){
					class_shortmovieinfo in=new class_shortmovieinfo();
					String buf=infos.get(i).toString();
					
					//先正则表达式匹配电影num
					String pat_movienum="class=\"nbg\" href=\"https://movie.douban.com/subject/(\\d+?)/\"";
					ArrayList<String> patre=ZhengZe.ZhengZe.getRe(buf,pat_movienum);
					if(patre.size()==1){
						try{
							in.num=Integer.parseInt(patre.get(0));
						}catch(Exception e){
						}
						//System.out.println(in.num);
					}
					
					//先匹配到class=p12再执行匹配电影名称
					Elements pl2s=infos.get(i).select("[class=pl2]");
					//System.out.println(pl2s.size());
					if(pl2s.size()==1){
						//jsoup匹配电影名称
						Elements names=pl2s.get(0).select("[href=https://movie.douban.com/subject/"+in.num+"/]");
						if(names.size()==1){
							in.name=names.get(0).text();
							//System.out.println(in.name);
						}
						
						//jsoup匹配主演信息
						Elements stars=pl2s.get(0).getElementsByTag("p");
						if(stars.size()==1){
							in.stars=stars.get(0).text();
							//System.out.println(in.stars);
						}
						
						//jsoup匹配上映信息
						Elements shows=pl2s.get(0).select("[class=star clearfix]");
						if(shows.size()==1){
							in.show=shows.get(0).text();
							//System.out.println(in.show);
						}
					}
					l.add(in);
				}
			}
		}
		return l;
	}

	//只根据年份获取电影信息（页数一直加，直到为空）--标注次数排序
	//传的参数分别是年份在年份数组中的位置、年份数组总长度、年份值
	public static void getMoviesByYear(int index,int len,String year){
		int pagenum=1;
		while(true){
			String url=url_head+year+"?start="+(pagenum-1)*20+url_end;
			String source=jsoup.getSource(url,10000,cp.ip,cp.port);
			if(source!=null&&source.length()>0){
				//如果非法的页面就退出了
				if(source.contains("没有找到符合条件的电影")){
					break;
				}
				System.out.println("第"+index+"/"+len+"个年份："+year+"\t第"+pagenum+"页");
				ArrayList<class_shortmovieinfo> l=new ArrayList<class_shortmovieinfo> ();
				//先找到所有电影的位置
				Document doc=Jsoup.parse(source);
				Elements infos=doc.select("[class=item]");
				if(infos.size()<21){
					for(int i=0;i<infos.size();i++){
						class_shortmovieinfo in=new class_shortmovieinfo();
						String buf=infos.get(i).toString();
						
						//先正则表达式匹配电影num
						String pat_movienum="class=\"nbg\" href=\"https://movie.douban.com/subject/(\\d+?)/\"";
						ArrayList<String> patre=ZhengZe.ZhengZe.getRe(buf,pat_movienum);
						if(patre.size()==1){
							try{
								in.num=Integer.parseInt(patre.get(0));
							}catch(Exception e){
							}
							//System.out.println(in.num);
						}
						
						//先匹配到class=p12再执行匹配电影名称
						Elements pl2s=infos.get(i).select("[class=pl2]");
						//System.out.println(pl2s.size());
						if(pl2s.size()==1){
							//jsoup匹配电影名称
							Elements names=pl2s.get(0).select("[href=https://movie.douban.com/subject/"+in.num+"/]");
							if(names.size()==1){
								in.name=names.get(0).text();
								//System.out.println(in.name);
							}
							
							//jsoup匹配主演信息
							Elements stars=pl2s.get(0).getElementsByTag("p");
							if(stars.size()==1){
								in.stars=stars.get(0).text();
								//System.out.println(in.stars);
							}
							
							//jsoup匹配上映信息
							Elements shows=pl2s.get(0).select("[class=star clearfix]");
							if(shows.size()==1){
								in.show=shows.get(0).text();
								//System.out.println(in.show);
							}
						}
						l.add(in);
					}
				}
				
				sql="insert into "+DoubanNames.shortMovieTableName+"(year,num,moviename,stars,shows,flag) values";
				for(int j=0;j<l.size();j++){
					sql+="("+Integer.parseInt(year)+","+l.get(j).num+",'"+l.get(j).name.replace("'","\"")+"','"+l.get(j).stars.replace("'","\"")+"','"+l.get(j).show.replace("'","\"")+"',0),";
				}
				sql=sql.substring(0,sql.length()-1);
				try{
					MySql.MySql.execInsert(DoubanNames.con,sql);
					pagenum++;
				}catch(Exception e){
					System.out.println(sql);
					System.out.println("插入到表失败!");
					e.printStackTrace();
				}	
			}else{
				System.out.println("可能ip被屏蔽了，重新连接...");
				continue;
			}
		}
	}
	
	
	//只根据年份获取电影信息（页数一直加，直到为空）--标注次数排序---代理服务器
	//传的参数分别是年份在年份数组中的位置、年份数组总长度、年份值---代理服务器
	public static void getMoviesByYearProxy(useful_proxy douban_up,int index,int len,String year){
		int pagenum=1;
		while(true){
			//System.out.println("第"+pagenum+"页");
			String url=url_head+year+"?start="+(pagenum-1)*20+url_end;
			//System.out.println(url);
			String source=jsoup.getSource(url,10000,cp.ip,cp.port);
			if(source!=null&&source.length()>0){
				//如果非法的页面就退出了
				if(source.contains("没有找到符合条件的电影")){
					break;
				}
				System.out.println("第"+index+"/"+len+"个年份："+year+"\t第"+pagenum+"页");
				ArrayList<class_shortmovieinfo> l=new ArrayList<class_shortmovieinfo> ();
				//先找到所有电影的位置
				Document doc=Jsoup.parse(source);
				Elements infos=doc.select("[class=item]");
				if(infos.size()<21){
					for(int i=0;i<infos.size();i++){
						class_shortmovieinfo in=new class_shortmovieinfo();
						String buf=infos.get(i).toString();
						
						//先正则表达式匹配电影num
						String pat_movienum="class=\"nbg\" href=\"https://movie.douban.com/subject/(\\d+?)/\"";
						ArrayList<String> patre=ZhengZe.ZhengZe.getRe(buf,pat_movienum);
						if(patre.size()==1){
							try{
								in.num=Integer.parseInt(patre.get(0));
							}catch(Exception e){
							}
							//System.out.println(in.num);
						}
						
						//先匹配到class=p12再执行匹配电影名称
						Elements pl2s=infos.get(i).select("[class=pl2]");
						//System.out.println(pl2s.size());
						if(pl2s.size()==1){
							//jsoup匹配电影名称
							Elements names=pl2s.get(0).select("[href=https://movie.douban.com/subject/"+in.num+"/]");
							if(names.size()==1){
								in.name=names.get(0).text();
								//System.out.println(in.name);
							}
							
							//jsoup匹配主演信息
							Elements stars=pl2s.get(0).getElementsByTag("p");
							if(stars.size()==1){
								in.stars=stars.get(0).text();
								//System.out.println(in.stars);
							}
							
							//jsoup匹配上映信息
							Elements shows=pl2s.get(0).select("[class=star clearfix]");
							if(shows.size()==1){
								in.show=shows.get(0).text();
								//System.out.println(in.show);
							}
						}
						l.add(in);
					}
				}
				
				sql="insert into "+DoubanNames.shortMovieTableName+"(year,num,moviename,stars,shows,flag) values";
				for(int j=0;j<l.size();j++){
					sql+="("+Integer.parseInt(year)+","+l.get(j).num+",'"+l.get(j).name.replace("'","\"")+"','"+l.get(j).stars.replace("'","\"")+"','"+l.get(j).show.replace("'","\"")+"',0),";
				}
				sql=sql.substring(0,sql.length()-1);
				try{
					MySql.MySql.execInsert(DoubanNames.con,sql);
					pagenum++;
				//	System.out.println("页数+1变为:"+pagenum);
				}catch(Exception e){
					System.out.println(sql);
					System.out.println("插入到表失败!");
					e.printStackTrace();
				}	
			}else{
				System.out.println("可能ip被屏蔽了，重新连接...");
				getValiable(douban_up);
			//	System.out.println("继续...");
				continue;
			}
		}
	}

	//初始化几个表
	public static boolean init(){
		if(!DoubanNames.init()){
			return false;
		}
		//flag用来判断一部电影是否爬完
		sql="create table if not exists "+ DoubanNames.shortMovieTableName+"(year int,num int,flag int,moviename VARCHAR(750),stars VARCHAR(750),shows VARCHAR(750),INDEX(year),INDEX(num),INDEX(moviename),INDEX(stars),INDEX(shows))";
		try{
			MySql.MySql.exec(DoubanNames.con,sql);
		}catch(Exception e){
			System.out.println(sql);
			System.out.println("初始化表失败!!");
			e.printStackTrace();
			return false;
		}
		
		sql="create table if not exists "+ DoubanNames.shortArticleTableName+"(movienum int,articlenum int,flag int,INDEX(movienum),INDEX(articlenum),INDEX(flag))";
		try{
			MySql.MySql.exec(DoubanNames.con,sql);
		}catch(Exception e){
			System.out.println(sql);
			System.out.println("初始化影评表失败!!");
			e.printStackTrace();
			return false;
		}
		
		//判断表是否存在(两个表，一个错误表一个正确表，错误表一般是网络被屏蔽，爬完一趟后慢慢爬错误表)
		//其实只用一个表就行了,用max函数(flag用来标记是否为空)
		sql="create table if not exists "+DoubanNames.articleTableName+"(articlenum int,flag int,articletitle VARCHAR(750),ownernum VARCHAR(750),ownername VARCHAR(750),movienum int,moviename VARCHAR(750),ratingint int,time VARCHAR(750),content LONGTEXT,usefulint int,uselessint int,movietitle VARCHAR(750),moviedirector VARCHAR(750),moviestars VARCHAR(750),movieclass VARCHAR(750),moviearea VARCHAR(750),movieshow VARCHAR(750),"
															+"INDEX(articlenum),INDEX(flag),INDEX(articletitle),INDEX(ownernum),INDEX(ownername),INDEX(movienum),INDEX(moviename),INDEX(ratingint),INDEX(time),INDEX(usefulint),INDEX(uselessint),INDEX(movietitle),INDEX(moviedirector),INDEX(moviestars),INDEX(movieclass),INDEX(moviearea),INDEX(movieshow))";
		try{
			MySql.MySql.exec(DoubanNames.con,sql);
		}catch(Exception e){
			System.out.println(sql);
			System.out.println("初始化表失败!!");
			e.printStackTrace();
			return false;
		}
		
		return true ;
	}
	
	//从shortmovie获取num
	public static boolean getNumsFromSql(){
		sql="select num from "+DoubanNames.shortMovieTableName;
		ArrayList<String> re=MySql.MySql.select(DoubanNames.con,sql);
		if(re!=null&&re.size()>0){
			nums=new LinkedHashSet<Integer> ();
			for(int i=0;i<re.size();i++){
				try{
					nums.add(Integer.parseInt(re.get(i)));
				}catch(Exception e){
					e.printStackTrace();
					return false;
				}
			}
		}
		return true;
	}
	
	//从shortarticle表获取articlenum
	public static boolean getArticleNumsFromSql(){
		sql="select articlenum from "+DoubanNames.shortArticleTableName+" where flag=0";
		ArrayList<String> re=MySql.MySql.select(DoubanNames.con,sql);
		//System.out.println(re.size()+"\t"+re);
		if(re!=null&&re.size()>0){
			articlenums=new LinkedHashSet<Integer> ();
			for(int i=0;i<re.size();i++){
				try{
					articlenums.add(Integer.parseInt(re.get(i)));
				}catch(Exception e){
					e.printStackTrace();
					return false;
				}
			}
		}
		return true;
	}
	
	//根据movie的num获取影评页数
	public static int getPageNumByMovieNumProxy(useful_proxy douban_up,int num){
		String url="https://movie.douban.com/subject/"+num+"/reviews";
		int flag=0;
		while(flag<3){
			String source=jsoup.getSource(url,10000,cp.ip,cp.port);
			if(source!=null&&source.length()>0){
				String pat="data-total-page=\"(\\d+?)\"";
				ArrayList<String> l=ZhengZe.ZhengZe.getRe(source,pat);
				if(l!=null&&l.size()==1){
					return Integer.parseInt(l.get(0));
				}else{
					//没有总页数，说明可能只有一页
					return 1;
				}
			}else{
				getValiable(douban_up);
				flag++;
			}
		}
		return -1;
	}
	
	//根据电影年份和排序方式获取电影num等(year不仅仅是年份，还可以是其他（比如香港电影等）
	public static void getByYearAndSort(useful_proxy douban_up,String year,int sort){
		//初始化年份列表
		//ArrayList<String> years=getYearsByRoot();
		ArrayList<String> years=new ArrayList<String> ();
		//添加年份
		years.add(year);
		//综合排序
		changeEnd(sort);
		
		//把某个年份的电影数据爬完
		if(years!=null&&years.size()>0){
			for(int i=0;i<years.size();i++){
				//需要传递对象（因为里面有可用代理服务器列表）
				getMoviesByYearProxy(douban_up,i+1,years.size(),years.get(i));
			}
		}	
		
	}
	
	//根据电影num和页数以及代理服务器获取影评网址
	//分别表示电影页数url、电影位置、电影的num、电影页数、电影总页数
	public static int getUrlByMovieNumAndPageNumProxy(String url,int index,int movienum,int i,int pagenum){
		String source=jsoup.getSource(url,10000,cp.ip,cp.port);
		int sum=0;
		//System.out.println("正在获取:"+url);
		System.out.println("正在获取数据--第"+index+"/"+nums.size()+"个电影："+movienum+"\t第"+i+"/"+pagenum+"页");
		//System.out.println("source="+source);
		if(source!=null&&source.length()>0){
			String pat="https://movie.douban.com/review/(\\d+?)/\"";
			ArrayList<String> l=ZhengZe.ZhengZe.getRe(source,pat);
			//System.out.println("url="+url+"l.size="+l.size());
			if(l!=null&&l.size()<21){
				String bufsql="insert into "+DoubanNames.shortArticleTableName+"(movienum,articlenum,flag) values";
				for(int j=0;j<l.size();j++){
					try{
						//System.out.println("nowind="+ind);
						bufsql+="("+movienum+","+Integer.parseInt(l.get(j))+",0),";
					//	System.out.println("bufsql="+bufsql);
					}catch(Exception e){
						System.out.println(l.get(j));
						e.printStackTrace();
					}
				}
				if(bufsql.equals("insert into "+DoubanNames.shortArticleTableName+"(movienum,articlenum,flag) values")){
					return 1;
				}
				bufsql=bufsql.substring(0,bufsql.length()-1);
				//System.out.println(bufsql);
				sqls.add(bufsql);
			}
			return 1;
		}else{
			return -1;
		}
	}
	
	//影评页线程池
	private static ExecutorService articlePage_fixedThreadPool;
		
	//传的参数分别是：链表,线程数
	public static void start(ArrayList<String> l,int index1,int movienum,int pageNum,int n,int one1,int now){
		articlePage_fixedThreadPool = Executors.newFixedThreadPool(n);
		int one=l.size()/n+1;
		for (int i = 0; i < n; i++) {  
			final int index = i;  
			articlePage_fixedThreadPool.execute(new Runnable() {  
				public void run() {  
					try {  
						for(int i=(index*one);i<(index+1)*one;i++){
							if(threadFlag==1){
								return;
							}
							if(i<l.size()){
								System.out.println("线程池的第"+index+"个线程:"+l.get(i));
								int re=getUrlByMovieNumAndPageNumProxy(l.get(i),index1,movienum,one1*now+i+1,pageNum);
								if(re==-1){
									System.out.println("ip被屏蔽，换下一个");
									setflag(1);
									articlePage_shutdown();
								}
							}
						} 
					} catch (Exception e) {  
						 e.printStackTrace();
					}  
				}  
			});  
		}  
		articlePage_shutdown();
	}
	
	//得到可用代理服务器
	public static void getValiable(useful_proxy douban_up){
		//取得第一个可用代理服务器就行
		while(true){
			//判断有没有爬完一遍可用代理服务器
			if(douban_up.fixedThreadPool.isTerminated()){
				//后台获取可用代理服务器
				douban_up=new useful_proxy(douban_url,doubanproxy_threadnum);
				douban_up.start();
				while(douban_up.fixedThreadPool==null){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			}
			if(douban_up.usefulProxy.size()>proxyNum){
				cp=douban_up.usefulProxy.get(proxyNum);
				System.out.println("取得第"+(proxyNum+1)+"个可用代理服务器:"+cp);
				addNum();
				break;
			}else{
				//休眠，避免一直循环浪费资源
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	//根据电影的num集合来获取电影及其对应的影评编号
	//传的参数分别是useful_proxy对象和线程数量
	@SuppressWarnings("deprecation")
	public static void getArticleNumByMovieNumProxy(useful_proxy douban_up,int getThreadNum){
		sqls=new ArrayList<String> ();
		
		//nums的HashSet转数组
		Integer [] nums_sz=new Integer[nums.size()];
		nums.toArray(nums_sz);
		
		//遍历所有已有的nums
		for(int index=0;index<nums_sz.length;){
			setflag(0);
			sqls.clear();
			
			int num=nums_sz[index];
			int pagenum=getPageNumByMovieNumProxy(douban_up,num);
			if(pagenum==-1){
				System.out.println("获取电影影评页数失败!");
				continue;
			}else{
				System.out.println("获取电影影评页数成功!共"+pagenum+"页");
				//分批爬虫
				int one=getThreadNum;
				int len=pagenum/one+2;
				ArrayList<String> pageUrls=new ArrayList<String> ();
				for(int i=0;i<len;){
					pageUrls.clear();
					sqls.clear();
					//把电影影评网址加入到链表
					for(int j=i*one+1;j<=(i+1)*one;j++){
						if(j<=pagenum){
							pageUrls.add("https://movie.douban.com/subject/"+num+"/reviews?start="+(j-1)*20);
						}
					}
					if(pageUrls.size()==0){
						i++;
						continue;
					}
					System.out.println("第"+index+"部电影:"+num+":开启多线程");
					
					//分别表示电影页数网址集合、电影的位置、电影的num、电影总页数、线程数、用来判断当前是多少页
					start(pageUrls,index,num,pagenum,getThreadNum,i,one);
					
					while(!articlePage_fixedThreadPool.isTerminated()){
					}
					if(threadFlag==1){
						System.out.println("ip被屏蔽，重新获取");
						getValiable(douban_up);
					}else{
						System.out.println("批量插入表格..."+sqls.size());
						for(int mk=0;mk<sqls.size();mk++){
							try{
								MySql.MySql.execInsert(DoubanNames.con,sqls.get(mk));
							}catch(Exception e){
								System.out.println(sqls.get(mk));
								System.out.println("插入到表失败!");
								e.printStackTrace();
							}
						}
						System.out.println("修改flag...");
						sql="update "+DoubanNames.shortMovieTableName+" set flag=1 where num="+nums_sz[index];
						try{
							MySql.MySql.execInsert(DoubanNames.con,sql);
							i++;
						}catch(Exception e){
							System.out.println(sql);
							System.out.println("修改flag失败!");
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
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
	
	//多线程爬虫
	private static ExecutorService article_fixedThreadPool;
	//传的参数分别是：链表,线程数
	public static void start(ArrayList<Integer> l,int n){
		article_fixedThreadPool = Executors.newFixedThreadPool(n);
		int one=l.size()/n+1;
		for (int i = 0; i < n; i++) {  
			final int index = i;  
			article_fixedThreadPool.execute(new Runnable() {  
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
									String bufsql="insert into "+DoubanNames.articleTableName+"(articlenum,flag,articletitle,ownernum,ownername,movienum,moviename,ratingint,time,content,usefulint,uselessint,movietitle,moviedirector,moviestars,movieclass,moviearea,movieshow) values"
											+"("+db.articlenum+",1,'"+db.articletitle.replace("'","\"")+"',+'"+db.ownernum.replace("'","\"")+"','"+db.ownername.replace("'","\"")+"',"+db.movienum+",'"+db.moviename.replace("'","\"")+"',"+db.ratingint+",'"+db.time.replace("'","\"")+"','"+db.content.replace("'","\"")+"',"
											+db.usefulint+","+db.uselessint+",'"+db.movietitle.replace("'","\"")+"','"+db.moviedirector.replace("'","\"")+"','"+db.moviestars.replace("'","\"")+"','"+db.movieclass.replace("'","\"")+"','"+db.moviearea.replace("'","\"")+"','"+db.movieshow.replace("'","\"")+"')";
									/*try {
										MySql.MySql.execInsert(DoubanNames.con,bufsql);
									} catch (SQLException e) {
										// TODO 自动生成的 catch 块
										System.out.println(bufsql);
										System.out.println("线程"+index+"：num="+l.get(i)+"\t插入新表失败!");
										e.printStackTrace();
									}*/
									sqls.add(bufsql);
								}else{
									System.out.println("线程"+index+"：num="+l.get(i)+"为空");
									System.out.println("ip被屏蔽，换下一个");
									setflag(1);
									articlePage_shutdown();
								}
							}
						} 
					} catch (Exception e) {  
						 e.printStackTrace();
					}  
				}  
			});  
		}  
		article_fixedThreadPool.shutdown();
	}
	
	//根据影评num获取影评数据
	public static void getArticleByArticleNumProxy(useful_proxy douban_up,int getThreadNum){
		//开始爬影评
		sqls=new ArrayList<String> ();
		
		//nums的HashSet转数组
		Integer [] nums_ypsz=new Integer[articlenums.size()];
		articlenums.toArray(nums_ypsz);
		
		int one=getThreadNum;
		int len=nums_ypsz.length/one+2;
		int alllen=nums_ypsz.length;
		ArrayList<Integer> articleNums=new ArrayList<Integer> ();
		for(int i=0;i<len;){
			sqls.clear();
			setflag(0);
			articleNums.clear();
			for(int j=i*one;j<(i+1)*one;j++){
				if(j<alllen){
					articleNums.add(nums_ypsz[j]);
				}
			}
			if(articleNums.size()==0){
				i++;
				continue;
			}
			//分别表示电影页数网址集合、电影的位置、电影的num、电影总页数、线程数、用来判断当前是多少页
			start(articleNums,getThreadNum);
			
			while(!article_fixedThreadPool.isTerminated()){
			}
			if(threadFlag==1){
				System.out.println("ip被屏蔽，重新获取");
				getValiable(douban_up);
			}else{
				System.out.println("批量插入表格..."+sqls.size());
				for(int mk=0;mk<sqls.size();mk++){
					try{
						MySql.MySql.execInsert(DoubanNames.con,sqls.get(mk));
					}catch(Exception e){
						System.out.println(sqls.get(mk));
						System.out.println("插入到表失败!");
						e.printStackTrace();
					}
				}
				System.out.println("修改flag...");
				int flagnow=0;
				for(int k=0;k<articleNums.size();k++){
					sql="update "+DoubanNames.shortArticleTableName+" set flag=1 where articlenum="+articleNums.get(k);
					try{
						MySql.MySql.execInsert(DoubanNames.con,sql);
					}catch(Exception e){
						System.out.println(sql);
						System.out.println("修改flag失败!");
						e.printStackTrace();
						flagnow=1;
						break;
					}
				}
				if(flagnow==0){
					i++;
				}
			}
		}	
	}
	
	
	public static void main(String[] args) {
		// TODO 自动生成的方法存根

		//初始化一些参数
		String year="2016";
		int sort=4;
		
		//修改表的名称
		DoubanNames.articleTableName="douban"+"_"+year+"_"+sort;
		DoubanNames.movieTableName="movie"+"_"+year+"_"+sort;
		DoubanNames.shortArticleTableName="shortarticle"+"_"+year+"_"+sort;
		DoubanNames.shortMovieTableName="shortmovie"+"_"+year+"_"+sort;
		
		
		
		//初始化表
		if(!init()){
			System.out.println("初始化失败!");
			return ;
		}
		System.out.println("初始化成功!");
		
		
		
		//代理服务器开始获取
		//初始取第0个标志位
		proxyNum=0;
		//可用代理服务器
		cp=new class_proxy();
		
		//测试网址
		douban_url="https://movie.douban.com";
		//后台线程数
		doubanproxy_threadnum=2000;
		//后台获取可用代理服务器
		useful_proxy douban_up=new useful_proxy(douban_url,doubanproxy_threadnum);
		douban_up.start();
		while(douban_up.fixedThreadPool==null){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		getValiable(douban_up);
		
		
		//开始爬电影数据
		getByYearAndSort(douban_up,year,sort);
		
		//从数据表中获取电影num数据（爬完电影数据后爬影评num）
		if(!getNumsFromSql()){
			System.out.println("从数据表中获取num失败!");
		}else{
			System.out.println("从数据表中获取num成功!");
		}
		
		//表示影评爬虫线程数
		int getThreadNum=5;
		
		getArticleNumByMovieNumProxy(douban_up,getThreadNum);
		
		//从数据表中获取影评num数据（爬影评num）
		if(!getArticleNumsFromSql()){
			System.out.println("从数据表中获取影评num失败!");
		}else{
			System.out.println("从数据表中获取影评num成功!");
		}
		
		getArticleByArticleNumProxy(douban_up,getThreadNum);
		
		
		//运行完之后还需要关闭运行的多线程
		douban_up.stop();
	}

}
