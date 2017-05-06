package Test;

import java.util.ArrayList;

import Init.WordInUrl;
import Zhong.TEXT;

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
		String text="你好啊，今天天气怎么样，你好吗？";
		ArrayList<WordInUrl> re=TEXT.split(text, TEXT.words_maxlen,TEXT.nouse_maxlen);
		for(int i=0;i<re.size();i++){
			System.out.println(re.get(i));
		}
		
	}

}
