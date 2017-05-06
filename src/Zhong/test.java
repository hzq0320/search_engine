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

		String test="习近平主席到杭州电子科技大学视察,发现杭州电子科技大学的学生十分勤奋!";
		String pat="(习近平|江泽民|平主席|十分)";
		ArrayList<String> re=ZhengZe.ZhengZe.getRealRe(test, pat);
		System.out.println(re);
		
	}

}
