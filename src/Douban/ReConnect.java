package Douban;

import java.io.IOException;

/** 
* @author 作者 E-mail: zhiqianghu0320@gmail.com
* @version 创建时间：2017年5月2日 上午10:37:41 
* 类说明 ：断开宽带连接以及重新连接
*/
public class ReConnect {
	
	//使用cmd命令重新连接
	public static boolean Reconnect(){
		String cmd;
		
		cmd = "rasphone -h 宽带连接"; 
        try {
			Runtime.getRuntime().exec(cmd);
			System.out.println("已断开宽带连接!");
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			return false;
		}
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
			return false;
		}
        cmd="rasdial 宽带连接 hzxib57159247 654321";
        try {
			Runtime.getRuntime().exec(cmd);
			System.out.println("已启用宽带连接!");
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			return false;
		} 
        try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
			return false;
		}
        return true;
        
	}
	
	

	public static void main(String[] args) {
		// TODO 自动生成的方法存根

		
        Reconnect();
		
	}

}
