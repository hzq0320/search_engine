package Spider;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import Proxy.class_proxy;
import ZhengZe.ZhengZe;



/** 
* @author 作者 E-mail: zhiqianghu0320@gmail.com
* @version 创建时间：2017年4月10日 下午7:34:09 
* 类说明 
*/
public class jsoup {

	
	//根据url获取网页源代码
	public static String getSource(String url){
		Document doc=new Document("");
		for(int i=0;i<5;i++){
			try{
				doc=Jsoup.connect(url).data("query", "Java").userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36") .timeout(5000).ignoreContentType(true).get();
				return doc.toString();
			}
			catch(Exception e){
				//e.printStackTrace();
				continue;
			}
		}
		return null;
	}
	
	//根据url获取网页源代码
	public static String getSource(String url,int timeout){
		Document doc=new Document("");
		for(int i=0;i<5;i++){
			try{
				doc=Jsoup.connect(url).data("query", "Java").userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36") .timeout(timeout).ignoreContentType(true).get();
				return doc.toString();
			}
			catch(Exception e){
				//e.printStackTrace();
				continue;
			}
		}
		return null;
	}
	
	//使用代理服务器获取网页源代码
	public static String getSource(String url,int timeout,String ip,int port){
		Document doc=new Document("");
		for(int i=0;i<5;i++){
			try{
				doc=Jsoup.connect(url).data("query", "Java").userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36").proxy(ip,port).timeout(timeout).ignoreContentType(true).get();
				return doc.toString();
			}
			catch(Exception e){
				//e.printStackTrace();
				continue;
			}
		}
		return null;
	}
	
	//根据url获取网页文本
	public static String getTextByUrl(String url){
		Document doc=new Document("");
		for(int i=0;i<5;i++){
			try{
				doc=Jsoup.connect(url).data("query", "Java").userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36") .timeout(5000).ignoreContentType(true).get();
				return doc.text();
			}
			catch(Exception e){
				//e.printStackTrace();
				continue;
			}
		}
		return null;
	}
	
	//根据源码获取网页文本
	public static String getTextBySource(String source){
		Document doc=Jsoup.parse(source);
		for(int i=0;i<5;i++){
			try{
				return doc.text();
			}
			catch(Exception e){
				//e.printStackTrace();
				continue;
			}
		}
		return null;
	}
	
	
	//获取网址内所有链接
	public static ArrayList<String> getAllLinksByUrl(String url) {
		ArrayList<String> l=new ArrayList<String> ();
		for(int i=0;i<10;i++){
			try{
				Document doc=Jsoup.connect(url).data("query", "Java").userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36") .timeout(5000).ignoreContentType(true).get();
				Elements eles=doc.getAllElements();
				for(Element ele:eles){
					String link=ele.attr("abs:href");
					if(link!=null&&link.length()>0){
						l.add(link);
					}
				}
				return l;
			}
			catch(Exception e){
				//e.printStackTrace();
				l.clear();
				continue;
			}
		}
		return null;
	}
	
	//获取网址内所有链接
	public static LinkedHashSet<String> getAllLinksByUrlLinkedHashSet(String url) {
		LinkedHashSet<String> l=new LinkedHashSet<String> ();
		for(int i=0;i<10;i++){
			try{
				Document doc=Jsoup.connect(url).data("query", "Java").userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36") .timeout(5000).ignoreContentType(true).get();
				Elements eles=doc.getAllElements();
				for(Element ele:eles){
					String link=ele.attr("abs:href");
					if(link!=null&&link.length()>0){
						l.add(link);
					}
				}
				return l;
			}
			catch(Exception e){
				//e.printStackTrace();
				l.clear();
				continue;
			}
		}
		return null;
	}
	
	//获取网址内所有链接
	public static LinkedHashSet<String> getAllLinksBySource(String source) {
		LinkedHashSet<String> l=new LinkedHashSet<String> ();
		for(int i=0;i<10;i++){
			try{
				Document doc=Jsoup.parse(source);
				Elements eles=doc.getAllElements();
				for(Element ele:eles){
					String link=ele.attr("abs:href");
					if(link!=null&&link.length()>0){
						l.add(link);
					}
				}
				return l;
			}
			catch(Exception e){
				//e.printStackTrace();
				l.clear();
				continue;
			}
		}
		return null;
	}
	
	//根据url获取title
	public static String getTitleBySource(String source){
		Document doc=Jsoup.parse(source);
		for(int i=0;i<5;i++){
			try{
				return doc.title();
			}
			catch(Exception e){
				//e.printStackTrace();
				continue;
			}
		}
		return null;
	}
	
	//根据text获取时间匹配结果
	public static ArrayList<String> getTimeByTextArray(String text){
		String pat="((?:19|20)\\d\\d)-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])";
		return ZhengZe.getAll(text,pat);
	}
	
	//根据text获取时间(只匹配出一个时认为匹配成功)
	public static String getTimeByTextString(String text){
		String pat="((?:19|20)\\d\\d)-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])";
		ArrayList<String> buf= ZhengZe.getAll(text,pat);
		if(buf.size()==1){
			return buf.get(0);
		}
		return "";
	}
	
	//根据text获取时间(只匹配出一个时认为匹配成功)
	public static long getTimeByTextLong(String text){
		String pat="((?:19|20)\\d\\d)-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])";
		ArrayList<String> buf= ZhengZe.getAll(text,pat);
		if(buf.size()==1){
			return Time.Time.getTimeByStr10(buf.get(0));
		}
		return -1;
	}
	
	//根据text获取时间(只匹配出一个时认为匹配成功)--除了需要匹配出来之外，还需要满足包含pa（如发布时间等字眼）
	//见http://computer.hdu.edu.cn/index.php/list/31，不是文章却也能匹配出时间
	public static long getTimeByTextLong(String text,String pa){
		//如果不包含发布时间字眼，即使匹配出来了也不是真的时间
		if(!text.contains(pa)){
			return -1;
		}
		String pat="((?:19|20)\\d\\d)-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])";
		ArrayList<String> buf= ZhengZe.getAll(text,pat);
		//认为不需要匹配出来的结果长度为1
		if(buf.size()>0){
			return Time.Time.getTimeByStr10(buf.get(0));
		}
		return -1;
	}
	
	
	
	public static void main(String[] args) throws IOException {
		// TODO 自动生成的方法存根

		String url="http://zwhzbxpg.hdu.edu.cn/Art/Art_9/Art_9_20.aspx";
		String source=getSource(url);
		String text=getTextBySource(source);
		System.out.println(getTimeByTextLong(text));
		
	}

	

}
