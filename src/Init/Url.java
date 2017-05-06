package Init;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/** 
* @author 作者 E-mail: zhiqianghu0320@gmail.com
* @version 创建时间：2017年4月10日 下午8:36:06 
* 类说明 
*/
public class Url {

	//网址
	public String url;
	//文章发布时间(时间戳)
	public long time;
	//标记（网址类型）
	public int flag;
	//标记网站时候被爬过
	public int flagpa;
	//网页权重
	public int weight;
	//网址标题
	public String title;
	//正文
	public String text;
	//父网址(入度)--用,分割
	//public LinkedHashSet<String> ruUrl;
	//子网址(出度)--用,分割
	public LinkedHashSet<String> chuUrl;
	//分词结果
	public LinkedHashSet<WordInUrl> words;
	
	//构造函数
	public Url(){
		url="";
		text="";
		
		chuUrl=new LinkedHashSet<String>();
		words=new LinkedHashSet<WordInUrl>();
	}
	
	//根据url和flag和flagpa构成Url
	public Url(String url1,int flag1,int flagpa1){
		url=url1;
		flag=flag1;
		flagpa=flagpa1;
	}
	
	//查询数据库的时候根据url、flag、flagpa三者来查询，根据此构成Url
	public static Url initUrl(String fromsql){
		String buf[]=fromsql.split("\t");
		if(buf.length==3){
			//测试发现下面这种方法不能正常构建对象，因此用static的方法返回对象
			//new Url(buf[0],Integer.parseInt(buf[1]),Integer.parseInt(buf[2]));
			return new Url(buf[0],Integer.parseInt(buf[1]),Integer.parseInt(buf[2]));
		}
		return null;
	}
	
	//使用HashSet保存自定义类需要修改equals和hashCode方法
	//不要直接强制类型转化，要先判断类型
	public boolean equals(Object obj){
		if(obj instanceof Url){
			Url u=(Url)obj;
			return (url.equals(u.url));
		}
		return super.equals(obj);
	}
	
	
	public int hashCode() {
        return url.hashCode();
    }
	
	public String toString(){
		return "url="+url+",title="+title+",有"+words.size()+"个词";
	}
}
