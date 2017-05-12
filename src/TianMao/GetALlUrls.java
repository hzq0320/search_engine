package TianMao;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import Spider.httpclient;
import Spider.jsoup;
import ZhengZe.ZhengZe;
import Zhong.Zh;

/** 
* @author 作者 E-mail: zhiqianghu0320@gmail.com
* @version 创建时间：2017年4月15日 下午10:42:40 
* 类说明 
*/
public class GetALlUrls {
	
	//网址如下：天猫搜索关键字"小米",编码方式为URLEncode的GBK编码
	//https://list.tmall.com/search_product.htm?s=0&q=%D0%A1%C3%D7

	//全局网址
	public static LinkedHashSet<String> result=new LinkedHashSet<String> ();
	
	//根据关键字获取商品搜索后源代码
	public static String getSourceByProduct(String product){
		String url="https://list.tmall.com/search_product.htm?s=0&q="+Zh.ZhToUrlEncodeGBK(product);
		return httpclient.getSource(url);
	}
	
	//根据源码获取商品页数
	public static int getNumOfPagesBySource(Document doc){
		if(doc!=null){
			Elements els=doc.select("[name=totalPage]");
			if(els.size()==1){
				try{
					return Integer.parseInt(els.get(0).attr("value"));
				}catch(Exception e){
					return -1;
				}
			}
		}
		return -1;
	}
	
	//根据产品名称和页数获取网址
	public static ArrayList<String> getUrlsByProductAndPagenum(String product,int pagenum){
		ArrayList<String> l=null;
		if(pagenum<=0){
			return l;
		}
		l=new ArrayList<String> ();
		String gbk=Zh.ZhToUrlEncodeGBK(product);
		for(int i=0;i<pagenum;i++){
			l.add("https://list.tmall.com/search_product.htm?s="+i*60+"&q="+gbk);
		}
		return l;
	}
	
	//加锁判断是否存在
	public static synchronized boolean containUrl(String url){
		return result.contains(url);
	}
	
	//加锁添加到result
	public static synchronized void addUrl(String url){
		if(!containUrl(url)){
			result.add(url);
		}
	}
	
	//根据url获取商品网址
	public static ArrayList<String> getProductUrls(String url){
		ArrayList<String> l=new ArrayList<String> ();
		String source=httpclient.getSource(url);
		
		LinkedHashSet<String> bufl=new LinkedHashSet<String> ();
		
		ArrayList<String> buf=ZhengZe.getAll(source,"\"//detail.tmall.com/item.htm?(.+?)\"");
		for(int i=0;i<buf.size();i++){
			String bufs=buf.get(i);
			//判断前后两个链接是否相同（实际有点区别的）
			if(i==0){
				bufl.add(bufs);
				l.add(bufs);
				continue;
			}
			if(bufs.contains(buf.get(i-1).replace("\"",""))){
				continue;
			}
			
			
			if(!bufl.contains(bufs)){
				bufl.add(bufs);
				l.add(bufs);
			}
		}
		return l;
	}
	
	//总方法
	//根据商品类别获取所有商品网址
	public static String[] getAllUrlsByProduct(String product){
		//获取第一页源代码
		Document doc=Jsoup.parse(getSourceByProduct(product));
		LinkedHashSet<String> re=new LinkedHashSet<String> ();
		//获取页数
		int pagenum=getNumOfPagesBySource(doc);
		//获取商品类别的每一页的网址
		ArrayList<String> urls=getUrlsByProductAndPagenum(product,pagenum);
		if(urls!=null){
			for(int i=0;i<urls.size();i++){
				System.out.println("第"+(i+1)+"页/共"+urls.size()+"页");
				//解析每一页中商品的网址,保存到了LinkedHashSet中
				ArrayList<String> l=getProductUrls(urls.get(i));
				for(int j=0;j<l.size();j++){
					if(!re.contains(l.get(j))){
						re.add(l.get(j));
					}
				}
			}
		}
		
		String s[]=new String[re.size()];
		re.toArray(s);
		re.clear();
		for(int i=0;i<s.length;i++){
			System.out.println(s[i]);
		}
		return s;
	}
	
	
	public static void main(String[] args) {
		// TODO 自动生成的方法存根
		
		String urls[]=getAllUrlsByProduct("拍照");
		
	}

}
