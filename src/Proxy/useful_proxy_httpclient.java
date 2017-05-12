package Proxy;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Init.Names;
import MySql.MySql;
import Spider.httpclient;
import Spider.jsoup;

/** 
* @author 作者 E-mail: zhiqianghu0320@gmail.com
* @version 创建时间：2017年5月3日 下午12:32:10 
* 类说明 ：是用来获取豆瓣可用的代理服务器列表，暂时没用到，不过可用
* 全都写入到userfulProxy中，不必写入数据库，因为一直在动态变化中
* 
* 说明(05_08更新):新建一个对象时指定测试网址url，线程数量，得到的新的代理服务器会存放在对象的usefulproxy中
*/
public class useful_proxy_httpclient extends Thread{

	//所有可用的代理服务器
	public ArrayList<class_proxy> usefulProxy;
	//所有服务器（从数据库查出来的）
	public static ArrayList<class_proxy> proxys;
	
	//测试url
	public String url;
	//爬虫线程数
	public int threadNum;
	
	//数据库
	public static Connection useful_proxy_con;
	
	//构造函数
	public useful_proxy_httpclient(String url1,int threadNum1){
		usefulProxy=new ArrayList<class_proxy>();
		url=url1;
		threadNum=threadNum1;
	}
	
	//线程池
	public ExecutorService fixedThreadPool;
	
	//传的参数分别是：链表,线程数,测试用的url（测试是否可访问）（有用）
	public void start(ArrayList<class_proxy> l,int n,String testurl){
		fixedThreadPool = Executors.newFixedThreadPool(n);
		int one=l.size()/n+1;
		for (int i = 0; i < n; i++) {  
			final int index = i;  
			fixedThreadPool.execute(new Runnable() {  
				public void run() {  
					try {  
						for(int i=(index*one);i<(index+1)*one;i++){
							if(i<l.size()){
								//String source=jsoup.getSource(testurl,10000,l.get(i).ip,l.get(i).port);
								String source=httpclient.getSourceProxy(testurl,l.get(i).ip,l.get(i).port);
								if(source!=null&&source.length()>0){
									usefulProxy.add(l.get(i));
									System.out.println(l.get(i)+"\t有效!当前可用服务器数量："+usefulProxy.size());
								}else{
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
	
	
	//初始化连接数据库
	public static boolean init(){
		//先连接数据库
		useful_proxy_con=MySql.init(ProxyName.sqlName);
		if(useful_proxy_con==null){
			System.out.println("初始化init:连接数据库失败!");
			return false;
		}
		return true;
	}
	
	//从proxy1代理服务器表获取代理服务器ip和port
	public static boolean getProxysFromProxy(){
		proxys=new ArrayList<class_proxy> ();
		String sql="select ip,port from "+ProxyName.proxyTableName;
		//先从服务器获取所有代理服务器
		ArrayList<String> re=MySql.select(useful_proxy_con,sql);
		if(re==null){
			return false;
		}
		//修改了select，所有不能从第一行开始了
		for(int i=0;i<re.size();i++){
			String s[]=re.get(i).split("\t");
			if(s.length==2){
				proxys.add(new class_proxy(s[0],Integer.parseInt(s[1])));
			}
		}
		return true;
	}

	//主方法的集合
	public void getUsefulProxy(String url,int threadnum){
		//初始化数据库
		
		if(!init()){
			System.out.println("初始化失败!");
			return ;
		}
		
		//前提是proxy表要有代理服务器
		getProxysFromProxy();
		//这句话在调用这个函数的地方写
/*		usefulProxy=new ArrayList<class_proxy> ();*/
		System.out.println("共"+proxys.size()+"个代理服务器!");
		System.out.println("线程数为"+threadNum+",测试网址为"+url);
		start(proxys,threadNum,url);
		while(!fixedThreadPool.isTerminated()){
		}
		System.out.println("所有可用服务器全部运行结束!");
	}

	//线程方法
	public void run(){
		getUsefulProxy(url,threadNum);
	}
	
	public static void main(String[] args) {
		// TODO 自动生成的方法存根

		useful_proxy_httpclient up=new useful_proxy_httpclient("https://movie.douban.com",1000);
		up.start();

	}

}
