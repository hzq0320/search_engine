package Douban;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
	//所有num的集合(暂时没用到)
	public static LinkedHashSet<Integer> nums;
	//sql
	public static String sql;
	//标记当前使用第几个代理服务器
	public static int proxyNum;
	//代理服务器
	public static class_proxy cp;
	
	//根据首页获取年份列表
	public static ArrayList<String> getYearsByRoot(){
		ArrayList<String> re=new ArrayList<String> ();
		String source=jsoup.getSource(root_url);
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
	
	//根据年份获取按照日期排序的页数
	public static int getPageNumByYear(String year){
		String url=url_head+year+url_end;
		String source=jsoup.getSource(url);
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
		String source=jsoup.getSource(url);
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
			String source=jsoup.getSource(url);
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
				//取得可用代理服务器就行
				while(true){
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
			//	System.out.println("继续...");
				continue;
			}
		}
	}

	//初始化电影表
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
		return true;
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
	
	
	public static void main(String[] args) {
		// TODO 自动生成的方法存根

		
		//初始化表
		if(!init()){
			System.out.println("初始化失败!");
			return ;
		}
		System.out.println("初始化成功!");
		
		//从数据表中获取电影num数据
		if(!getNumsFromSql()){
			System.out.println("从数据表中获取num失败!");
		}else{
			/*Iterator it =nums.iterator();
			while(it.hasNext()){
				System.out.println(it.next());
			}*/
			System.out.println("从数据表中获取num成功!");
		}
		
		
		
		
		
		
		try {
			Thread.sleep(3600*1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		
		
		//初始化年份列表
		//ArrayList<String> years=getYearsByRoot();
		ArrayList<String> years=new ArrayList<String> ();
		//添加年份
		years.add("2016");
		//综合排序
		changeEnd(4);
		
		
		//代理服务器开始获取
		//初始取第0个标志位
		proxyNum=0;
		//可用代理服务器
		cp=new class_proxy();
		//测试网址
		String douban_url="https://movie.douban.com";
		//后台线程数
		int doubanproxy_threadnum=1000;
		//后台获取可用代理服务器
		useful_proxy douban_up=new useful_proxy(douban_url,doubanproxy_threadnum);
		douban_up.start();
		//取得第一个可用代理服务器就行
		while(true){
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
		

		//把某个年份的电影数据爬完
		if(years!=null&&years.size()>0){
			for(int i=0;i<years.size();i++){
				//需要传递对象（因为里面有可用代理服务器列表）
				getMoviesByYearProxy(douban_up,i+1,years.size(),years.get(i));
			}
		}
		
		
		
		
		
		//运行完之后还需要关闭运行的多线程
		douban_up.stop();
		
	}

}
