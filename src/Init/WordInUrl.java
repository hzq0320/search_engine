package Init;

import java.util.LinkedHashSet;


/** 
* @author 作者 E-mail: zhiqianghu0320@gmail.com
* @version 创建时间：2017年4月25日 上午10:12:38 
* 类说明 ：用来记录每个网址中的词及其属性
*/
public class WordInUrl {

	//词
	public String word;
	//属性(0表示普通词，1表示停用词，2表示未登录词（一般是字母等拉丁文字）
	public int flag;
	//词频
	public int num;
		
	public WordInUrl(){
		word="";
		flag=0;
		num=0;
	}
	
	public WordInUrl(String word1,int flag1,int num1){
		word=word1;
		flag=flag1;
		num=num1;
				
	}
	
	//使用HashSet保存自定义类需要修改equals和hashCode方法
	//不要直接强制类型转化，要先判断类型
	public boolean equals(Object obj){
		if(obj instanceof WordInUrl){
			WordInUrl u=(WordInUrl)obj;
			return (word.equals(u.word));
		}
		return super.equals(obj);
	}
			
			
	public int hashCode() {
		return word.hashCode();
	}
	
	
	public String toString(){
		return "word="+word+",flag="+flag+",num="+num;
	}
	
	
}
