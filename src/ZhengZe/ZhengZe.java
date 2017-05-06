package ZhengZe;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 
* @author 作者 E-mail: zhiqianghu0320@gmail.com
* @version 创建时间：2017年4月16日 上午11:07:21 
* 类说明 
*/
public class ZhengZe {

	//根据正则表达式语法匹配(输出整个匹配结果)--两者区别见主函数
	public static ArrayList<String> getAll(String text,String patterString){
		ArrayList<String> l=new ArrayList<String> ();
		Pattern pattern = Pattern.compile(patterString);
		Matcher m = pattern.matcher(text);
	    while (m.find()) {
	    	l.add(m.group());
	    }
	    return l;
	}
	
	//根据正则表达式语法匹配(仅仅输出匹配结果)--两者区别见主函数
	public static ArrayList<String> getRe(String text,String patterString){
		ArrayList<String> l=new ArrayList<String> ();
		Pattern pattern = Pattern.compile(patterString);
		Matcher m = pattern.matcher(text);
	    while (m.find()) {
	    	for(int i=1;i<m.groupCount()+1;i++)  
	        {
	    		l.add(m.group(i));  
	        }
	    }
	    return l;
	}

	//根据正则表达式语法匹配(仅仅输出匹配结果)--两者区别见主函数
	public static ArrayList<String> getRealRe(String text,String patterString){
		if(text.length()==0){
			return null;
		}
		ArrayList<String> l=new ArrayList<String> ();
		Pattern pattern = Pattern.compile(patterString);
		Matcher m = pattern.matcher(text);
		while (m.find()) {
			//匹配到了第一个，然后从这个地方开始匹配下一个
			//只要匹配到了就返回
			String s=m.group();
			l.add(s);
			//System.out.println("开始匹配："+text.substring(text.indexOf(s)+1,text.length()));
			ArrayList<String> buf=getRealRe(text.substring(text.indexOf(s)+1,text.length()),patterString);
			if(buf!=null){
				for(int i=0;i<buf.size();i++){
					l.add(buf.get(i));
				}
			}
			//System.out.println("返回的是："+l);
			return l;
		}
		return null;
	}
	
	public static void main(String[] args) {
		// TODO 自动生成的方法存根
		String s="hahadhadasuausduahsd";
		System.out.println(getAll(s,"ha(.*?)a"));
		System.out.println(getRe(s,"ha(.*?)a"));
	}

}
