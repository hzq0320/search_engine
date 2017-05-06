package Zhong;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map.Entry;

import FileOperation.Catalog;
import FileOperation.Txt;
import Init.WordInUrl;
import Time.Time;

/** 
* @author 作者 E-mail: zhiqianghu0320@gmail.com
* @version 创建时间：2017年5月6日 下午3:22:52 
* 类说明 
*/
public class SensitiveWords {

	//所有敏感词
	public static LinkedHashSet<String> lset;
	public static ArrayList<String> larray;
	public static String[] lsz;
	//敏感词正则
	public static String mgcpat;
	
	//词表所在路径
	public static String outpath;
	
	//初始化
	public static void init(){
		lset=new LinkedHashSet<String> ();
		larray=new ArrayList<String> ();
		outpath=System.getProperty("user.dir")+"\\敏感词.txt";
	}
	
	//合并所有有用的敏感词表
	public static void addAll(){
		String pat="(.*?)=";
		//敏感词文件夹
		String path="D://自然语言处理/敏感词库/敏感词整理";
		ArrayList<String> re=FileOperation.Catalog.getFiles(path);
		for(int i=0;i<re.size();i++){
			if(re.get(i).contains(".txt")){
				//System.out.println(re.get(i));
				ArrayList<String> buf=new ArrayList<String> ();
				Txt.read(re.get(i),buf);
				for(int j=0;j<buf.size();j++){
					ArrayList<String> pa=ZhengZe.ZhengZe.getRe(buf.get(j),pat);
					for(int k=0;k<pa.size();k++){
						lset.add(pa.get(k));
					}
				}
			}
		}
		ArrayList<String> out=new ArrayList<String> ();
		Iterator<String> it = lset.iterator();
        while(it.hasNext()){
            String key = it.next();
            out.add(key);
        }
        //注意，这是追加的方式，最好改一改
        //改为如果存在这个文件就删掉它，后面会自动生成
        if(Catalog.fileExists(outpath)){
        	Catalog.delete(outpath);
        }
        Txt.write(out,outpath,"Utf-8");
	}
	
	//读取敏感词表
	public static void initread(){
		Txt.read(outpath,larray,"utf-8");
		lsz=new String[larray.size()];
		mgcpat="(";
		for(int i=0;i<larray.size();i++){
			String bufs=larray.get(i);
			lset.add(bufs);
			lsz[i]=bufs;
			mgcpat+=bufs.replace("{","\\{").replace("}","\\}")+"|";
		}
		if(mgcpat.equals("(")){
			System.out.println("敏感词匹配初始化失败!");
		}else{
			mgcpat=mgcpat.substring(0,mgcpat.length()-1)+")";
		}
		
		//用来判断正则表达式哪里有错
		//for(int j=0;j<mgcpat.length();j++){
		//	System.out.println(j+"\t"+mgcpat.charAt(j));
		//}
		
		
		
	}
	
	//判断是否包含敏感词的方法1
	public static ArrayList<String> judge1(String text){
		ArrayList<String> rel=new ArrayList<String> ();
		ArrayList<WordInUrl> re=TEXT.splitAndSplit(text, TEXT.words_maxlen,TEXT.nouse_maxlen);
		for(int i=0;i<re.size();i++){
			if(lset.contains(re.get(i).word)){
				rel.add(re.get(i).word);
			}
		}
		return rel;
	}
	
	//判断是否包含敏感词的方法2
	public static ArrayList<String> judge2(String text){
		return ZhengZe.ZhengZe.getRealRe(text,mgcpat);
	}
	
	//判断是否包含敏感词的方法3
	public static ArrayList<String> judge3(String text){
		ArrayList<String> re=new ArrayList<String> ();
		for(int i=0;i<larray.size();i++){
			if(text.contains(larray.get(i))){
				re.add(larray.get(i));
			}
		}
		return re;
	}

	//判断是否包含敏感词的方法4
	public static ArrayList<String> judge4(String text){
		ArrayList<String> re=new ArrayList<String> ();
		for(int i=0;i<lsz.length;i++){
            if(text.contains(lsz[i])){
				re.add(lsz[i]);
			}
        }
		return re;
	}
	
	
	public static void main(String[] args) {
		// TODO 自动生成的方法存根

		//初始化全局变量
		init();
		//初始化读取到链表和HashSet
		initread();
		//中文分词初始化
		TEXT.init();
		
		long start;
		long end;
		
		int times=10000;
		
		String test="江泽民主席到杭州电子科技大学视察,发现杭州电子科技大学的学生十分勤奋!";
		ArrayList<String> re=new ArrayList<String> ();
		
		
		//方法1
		start=Time.gettime13();
		for(int i=0;i<times;i++){
			re=judge1(test);
		}
		end=Time.gettime13();
		System.out.println("方法1：用时"+(end-start)+"毫秒");
		if(re.size()==0){
			System.out.println("不包含敏感词!");
		}else{
			System.out.println("包含敏感词!"+re);
		}
		
		
		//方法2
		start=Time.gettime13();
		for(int i=0;i<times;i++){
			re=judge2(test);
		}
		end=Time.gettime13();
		System.out.println("方法2：用时"+(end-start)+"毫秒");
		
		
		if(re.size()==0){
			System.out.println("不包含敏感词!");
		}else{
			System.out.println("包含敏感词!"+re);
		}
		
		
		//方法3
		start=Time.gettime13();
		for(int i=0;i<times;i++){
			re=judge3(test);
		}
		end=Time.gettime13();
		System.out.println("方法3：用时"+(end-start)+"毫秒");
		
		
		if(re.size()==0){
			System.out.println("不包含敏感词!");
		}else{
			System.out.println("包含敏感词!"+re);
		}
		
		
		//方法4
		start=Time.gettime13();
		for(int i=0;i<times;i++){
			re=judge4(test);
		}
		end=Time.gettime13();
		System.out.println("方法4：用时"+(end-start)+"毫秒");
		
		
		if(re.size()==0){
			System.out.println("不包含敏感词!");
		}else{
			System.out.println("包含敏感词!"+re);
		}
		
		
		
	}

}
