package Init;

import java.util.LinkedHashSet;

/** 
* @author 作者 E-mail: zhiqianghu0320@gmail.com
* @version 创建时间：2017年4月23日 下午1:26:42 
* 类说明 :用来保存到数据库里的word
*/
public class Word {

	//词
	public String word;
	//属性(0表示普通词，1表示停用词，2表示未登录词（一般是字母等拉丁文字）
	public int flag;
	//所有网址及其属性
	public LinkedHashSet<String> url;
	
	public Word(){
		word="";
		flag=0;
		url=new LinkedHashSet<String> ();
	}
	
	//使用HashSet保存自定义类需要修改equals和hashCode方法
	//不要直接强制类型转化，要先判断类型
	public boolean equals(Object obj){
		if(obj instanceof Word){
			Word u=(Word)obj;
			return (word.equals(u.word));
		}
		return super.equals(obj);
	}
		
		
	public int hashCode() {
		return word.hashCode();
	}

	public String toString(){
		return "word="+word+",flag="+flag+",url.size="+url.size();
	}
	
}
