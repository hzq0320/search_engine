package Spider;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import FileOperation.Txt;

/** 
* @author 作者 E-mail: zhiqianghu0320@gmail.com
* @version 创建时间：2017年5月7日 下午12:59:08 
* 类说明 :没用
*/
public class 处理 {

	
	
	public static void main(String[] args) {
		// TODO 自动生成的方法存根

		LinkedHashSet<String> ls=new LinkedHashSet<String> ();
		
		String path="D://test.txt";
		ArrayList<String> l=new ArrayList<String> ();
		Txt.read(path,l);
		int flag=0;
		for(int i=0;i<l.size();i++){
			if(ls.contains(l.get(i))){
				System.out.println("delete * from urls where url="+l.get(i)+" and text is null;");
				flag++;
			}else{
				ls.add(l.get(i));
			}
		}
		System.out.println(flag);
	}

}
