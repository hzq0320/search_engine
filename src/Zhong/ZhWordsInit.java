package Zhong;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import FileOperation.Txt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


/** 
* @author 作者 E-mail: zhiqianghu0320@gmail.com
* @version 创建时间：2017年4月14日 下午4:47:40 
* 类说明 :初始化词典
*/
public class ZhWordsInit {

	
	//重点:停用词的最大长度可能要比词典中词最大长度要短，所以构建停用词链表时也使用词典的最大长度
	
	
	//词按词的长度分开的链表
	//l.get(i)表示词的长度为i,String类型为词，Integer类型为词频率
	public static ArrayList<LinkedHashMap<String,Integer>> all=new ArrayList<LinkedHashMap<String,Integer>> ();
	
	//停用词表
	public static ArrayList<LinkedHashMap<String,Integer>> nouse=new ArrayList<LinkedHashMap<String,Integer>> ();
	
	//保存读取出来的中文词数据
	private static ArrayList<String> alll=new ArrayList<String> ();
	
	//保存读取出来的停用词数据
	private static ArrayList<String> nousel=new ArrayList<String> ();
	
	//用于自定义用户分词(用链表来存放分词表的路径，但是要求其中的词要以词+词频+词性构成)
	public static ArrayList<String> allpaths=new ArrayList<String> ();
	
	//用于自定义停用词表(用链表来存放停用词表的路径)
	public static ArrayList<String> noUsePaths=new ArrayList<String> ();
	
	
	//初始化词典路径
	public static void initPaths(){
		allpaths.add(System.getProperty("user.dir")+"\\词表.txt");
		allpaths.add(System.getProperty("user.dir")+"\\杭电词表.txt");
	}
	
	//初始化停用词词典路径
	public static void initNoUsePaths(){
		noUsePaths.add(System.getProperty("user.dir")+"\\停用词表.txt");
	}
	
	
	//读取文本中所有中文词
	private static void getAllWords(){
		for(int i=0;i<allpaths.size();i++){
			Txt.read(allpaths.get(i),alll,"utf-8");
		}
	}
	
	//读取所有停用词表数据
	private static void getAllNoUseWords(){
		for(int i=0;i<noUsePaths.size();i++){
			Txt.read(noUsePaths.get(i),nousel,"utf-8");
		}
	}
	
	//获取最大词长度
	private static int getAllMaxLen(){
		int maxlen=0;
		for(int i=0;i<alll.size();i++){
			String buf[]=alll.get(i).split(" ");
			if(buf.length>=2){
				if(buf[0].length()>maxlen){
					maxlen=buf[0].length();
				}
			}
		}
		return maxlen;
	}
	
	//获取最大停用词长度
	private static int getNoUseMaxLen(){
		int maxlen=0;
		for(int i=0;i<nousel.size();i++){
			if(nousel.get(i).length()>maxlen){
				maxlen=nousel.get(i).length();
			}
		}
		return maxlen;
	}
	
	//初始化哈希map的链表
	//每个长度的词都有一个链表来保存
	private static void initAll(int maxlen){
		for(int i=0;i<=maxlen;i++){
			LinkedHashMap<String,Integer> buf=new LinkedHashMap<String,Integer> ();
			all.add(buf);
		}
	}
	
	//初始化停用词哈希map的链表
	private static void initNoUse(int maxlen){
		for(int i=0;i<=maxlen;i++){
			LinkedHashMap<String,Integer> buf=new LinkedHashMap<String,Integer> ();
			nouse.add(buf);
		}
	}
	
	//处理所有数据--词(保存到哈希Map的链表)
	private static void dealAll(){
		for(int i=0;i<alll.size();i++){
			String s[]=alll.get(i).split(" ");
			if(s.length>=3){
				//System.out.println(s[0].length()+":\t"+s[0]);
				all.get(s[0].length()).put(s[0],Integer.parseInt(s[1]));
			}
		}
	}
	
	//处理所有数据--停用词(保存到哈希Map的链表)
	private static void dealNoUse(){
		for(int i=0;i<nousel.size();i++){
			nouse.get(nousel.get(i).length()).put(nousel.get(i),1);
		}
	}
	
	//全部初始化(未写入数据库，仅仅是从文件读取)
	//返回最大词长度
	public static int initAll(){
		initPaths();
		//读取文本中词到链表l
		getAllWords();
		//最大词长
		int maxLen=getAllMaxLen();
		//初始化链表all
		initAll(maxLen);
		//将l添加到all中
		dealAll();
		System.out.println("中文词典全部初始化成功!");
		return maxLen;
	}
	
	//全部初始化--停用词(未写入数据库，仅仅是从文件读取)
	//返回最大词长度
	public static int initAllNoUse(){
		initNoUsePaths();
		//读取文本中停用词到链表l
		getAllNoUseWords();
		//最大词长
		int maxLen=getNoUseMaxLen();
		//初始化链表all
		initNoUse(all.size());
		//将l添加到all中
		dealNoUse();
		System.out.println("停用词全部初始化成功!");
		return maxLen;
	}
	
	
	public static void main(String[] args) {
		// TODO 自动生成的方法存根
		//词典表全部初始化
		initAll();
		
		//设置中文分词表（词表）
		ZhWordsInit.allpaths.add("");
		ZhWordsInit.noUsePaths.add("");
		
		System.out.println("一共有"+alll.size()+"个词");
		int sum=0;
		for(int i=0;i<all.size();i++){
			System.out.println(i+"字长的词有"+all.get(i).size()+"个");
			sum+=all.get(i).size();
		}
		System.out.println("一共"+sum+"个词");
		
		
		
		//全部初始化停用词
		initAllNoUse();
		System.out.println("一共有"+nousel.size()+"个停用词");
		sum=0;
		for(int i=0;i<nouse.size();i++){
			System.out.println(i+"字长的停用词有"+nouse.get(i).size()+"个");
			sum+=nouse.get(i).size();
		}
		System.out.println("一共"+sum+"个停用词");
		
		
	}

}
