package Spider;

import java.util.ArrayList;

/** 
* @author 作者 E-mail: zhiqianghu0320@gmail.com
* @version 创建时间：2017年4月22日 上午10:42:23 
* 类说明 
*/
public class judgeUrl {
	
	//文件后缀
	private static ArrayList<String> file_end;
	
	//html文件后缀(js,css,jsp等等)
	private static ArrayList<String> html_end;
	
	//判断是否是有效网址(保存的是合法的匹配模式)--只要满足其一就行
	public static ArrayList<String> legalPats;
	//判断是否是有效网址(保存的是明显不合法的匹配模式)--只要满足其一就不行
	public static ArrayList<String> illegalPats;
	
	
	//初始化操作
	public static void init(){
		file_end=new ArrayList<String> ();
		html_end=new ArrayList<String> ();
		legalPats=new ArrayList<String> ();
		illegalPats=new ArrayList<String> ();
		
		
		//添加所有文件的后缀
		file_end.add(".pdf");
		file_end.add(".doc");
		file_end.add(".docx");
		file_end.add(".png");
		file_end.add(".xls");
		file_end.add(".jpg");
		file_end.add(".gif");
		file_end.add(".rar");
		file_end.add(".zip");
		file_end.add(".xlsx");
		file_end.add(".ppt");
		file_end.add(".XLS");
		file_end.add(".DOC");
		file_end.add(".PDF");
		
		
		html_end.add(".js");
		html_end.add(".css");
	}
	
	//初始化合法和非法的匹配--暂时没用到，还是在搜索引擎主函数里面加吧
	public static void initPat(){
		//"http://([^/]*?)hdu.edu.cn/(.*?)":匹配以hdu.edu.cn为域名后缀的网址
		//legalPats.add("http://([^/]*?)hdu.edu.cn/(.*?)");
	}
	
	//判断网址是否符合正则表达式
	public static boolean fitPat(String url,String pat){
		ArrayList<String> re=ZhengZe.ZhengZe.getAll(url,pat);
		if(re==null||re.size()==0){
			return false;
		}
		else{
			return true;
		}
	}
	
	//根据url判断网址类型
	//返回0表示正常网址，返回1表示文件网址，返回2表示html网址(.js,.css)
	public static int judgeByUrl(String url){
		if(url.contains("lib.hdu.edu.cn")||url.contains("acm.hdu.edu.cn")){
			return 3;
		}
		if(!url.contains("hdu.edu.cn")){
			return 3;
		}
		int lastIndex=url.lastIndexOf(".");
		String urlEnd=url.substring(lastIndex,url.length());
		for(int i=0;i<file_end.size();i++){
			if(file_end.get(i).equals(urlEnd)){
				return 1;
			}
		}
		for(int i=0;i<html_end.size();i++){
			if(html_end.get(i).equals(urlEnd)){
				return 2;
			}
		}
		return 0;
	}
	
	//根据url判断网址类型
	//返回-1表示是不合法网址，0表示正常网址，返回1表示文件网址，返回2表示html网址(.js,.css)
	public static int judgeByUrlWithPat(String url){
		
		//判断是否到最后一个
		int i=0;
		for(i=0;i<legalPats.size();i++){
			if(fitPat(url,legalPats.get(i))){
				//只要符合一种匹配模式就是符合的
				break;
			}
		}
		//如果根本就没有一个符合，就是不符合了
		if(i==legalPats.size()&&i!=0){
			return -1;
		}
		
		for(i=0;i<illegalPats.size();i++){
			if(fitPat(url,illegalPats.get(i))){
				//只要符合一种匹配模式就是不符合的
				return -1;
			}
		}
		/*
		
		
		if(url.contains("lib.hdu.edu.cn")||url.contains("acm.hdu.edu.cn")){
			return 3;
		}
		if(!url.contains("hdu.edu.cn")){
			return 3;
		}*/
		
		int lastIndex=url.lastIndexOf(".");
		String urlEnd=url.substring(lastIndex,url.length());
		for(i=0;i<file_end.size();i++){
			if(file_end.get(i).equals(urlEnd)){
				return 1;
			}
		}
		for(i=0;i<html_end.size();i++){
			if(html_end.get(i).equals(urlEnd)){
				return 2;
			}
		}
		return 0;
	}
	
	
	public static void main(String[] args) {
		// TODO 自动生成的方法存根
		
		//初始化文件url和网址url的后缀
		//判断网址类型初始化
		judgeUrl.init();
		//init();
		//满足正确正则的也未必是合法网址，只有一个正确的正则都不匹配才返回-1
		judgeUrl.legalPats.add("http://([^/]*?)hdu.edu.cn/(.*?)");
		
		//judgeUrl.legalPats.add("http://computer.hdu.edu.cn/(.*?)");
		//初始化非法匹配模式
		//只要满足一个不正确正则就是非法网址
		illegalPats.add("http://([^/]*?)acm.hdu.edu.cn/(.*?)");
		String url="http://computer.hdu.edu.cn/index.php/article/1192";
		System.out.println(judgeByUrlWithPat(url));
		
		
		
		/*if(fitPat(url,"http://([^/]*?)hdu.edu.cn/(.*?).css")){
			System.out.println("匹配");
		}*/
		
		
	}

}
