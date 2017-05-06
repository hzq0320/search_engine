package Test;

import java.util.LinkedHashSet;

import Init.WordInUrl;

/** 
* @author 作者 E-mail: zhiqianghu0320@gmail.com
* @version 创建时间：2017年4月24日 下午8:19:12 
* 类说明 
*/
public class test_word_detail {

	public static void main(String[] args) {
		// TODO 自动生成的方法存根
		
		LinkedHashSet<WordInUrl> l=new LinkedHashSet<WordInUrl> ();
		l.add(new WordInUrl("haha",1,1));
		l.add(new WordInUrl("haha",2,1));
		System.out.println(l.size());
		if(l.contains(new WordInUrl("haha",5,2))){
			System.out.println("存在!");
		}
	}

}
