package Zhong;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import FileOperation.Txt;
import Init.WordInUrl;

/** 
* @author 作者 E-mail: zhiqianghu0320@gmail.com
* @version 创建时间：2017年5月5日 下午10:19:57 
* 类说明 
*/
public class test_TEXT {
	
	
	public static void main(String[] args) {
		// TODO 自动生成的方法存根

		//中文分词初始化
		TEXT.init();
		
		String text=Txt.read(System.getProperty("user.dir")+"\\Split.txt","utf-8");
		text="杭州电子科技大学敖尔东敖川岚裘哲勇";
		ArrayList<WordInUrl> re=new ArrayList<WordInUrl> ();
		long start=Time.Time.gettime13();
		
		for(int i=0;i<100;i++){
			re=TEXT.splitAndSplit(text, TEXT.words_maxlen,TEXT.nouse_maxlen);
		}
		long end=Time.Time.gettime13();
		System.out.println(text.length()+"\t用时"+(end-start)+"毫秒");
		for(int i=0;i<re.size();i++){
			System.out.println(re.get(i));
		}
		
	}

}
