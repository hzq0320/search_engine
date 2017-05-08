package Zhong;

import java.util.ArrayList;

/** 
* @author 作者 E-mail: zhiqianghu0320@gmail.com
* @version 创建时间：2017年5月6日 下午8:18:03 
* 类说明 
*/
public class test {

	public static void main(String[] args) {
		// TODO 自动生成的方法存根

		String test="江泽民主席位";
		String pat="(江泽民|平主席|十分)";
		ArrayList<String> re=ZhengZe.ZhengZe.getRealRe(test, pat);
		System.out.println(re);
		
	}

}
