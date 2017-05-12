package Proxy;

import Spider.httpclient;

/** 
* @author 作者 E-mail: zhiqianghu0320@gmail.com
* @version 创建时间：2017年5月8日 下午9:53:57 
* 类说明 
*/
public class test_proxy_httpclient {

	//标志
	public static int flag;
	
	//加锁修改flag
	public synchronized static void setflag(int i){
		flag=i;
	}
	//加锁修改flag
	public synchronized static void addflag(){
		flag++;
	}
	
	
	public static void main(String[] args) {
		// TODO 自动生成的方法存根
		
		//初始取第0个标志位
		flag=0;
		//可用代理服务器
		class_proxy cp=new class_proxy();
		
		//测试网址
		String douban_url="https://rate.tmall.com/list_detail_rate.htm?itemId=545663194713&sellerId=2043230365&currentPage=1";
		//后台线程数
		int doubanproxy_threadnum=1000;
		//后台获取可用代理服务器
		useful_proxy_httpclient douban_up=new useful_proxy_httpclient(douban_url,doubanproxy_threadnum);
		douban_up.start();
		while(true){
			if(douban_up.usefulProxy.size()>flag){
				cp=douban_up.usefulProxy.get(flag);
				addflag();
				System.out.println("取得可用代理服务器:"+cp);
				String url="https://rate.tmall.com/list_detail_rate.htm?itemId=545663194713&sellerId=2043230365&currentPage=1";
				System.out.println(cp+":\n"+httpclient.getSourceProxy(url,cp.ip,cp.port));
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

}
