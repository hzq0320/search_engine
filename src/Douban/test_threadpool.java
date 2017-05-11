package Douban;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** 
* @author 作者 E-mail: zhiqianghu0320@gmail.com
* @version 创建时间：2017年5月10日 下午10:15:00 
* 类说明 
*/
public class test_threadpool {
	//影评页线程池
	private static ExecutorService articlePage_fixedThreadPool;
			
	//传的参数分别是：链表,线程数
	public static void start(ArrayList<String> l,int n){
		articlePage_fixedThreadPool = Executors.newFixedThreadPool(n);
		int one=l.size()/n+1;
		for (int i = 0; i < n; i++) {  
			final int index = i;  
			articlePage_fixedThreadPool.execute(new Runnable() {  
				public void run() {  
					try {  
						for(int i=(index*one);i<(index+1)*one;i++){
							if(i<l.size()){
								System.out.println("线程池的第"+index+"个线程:"+l.get(i));
								int re=test(l.get(i),i,i);
								if(re==-1){
									System.out.println("ip被屏蔽，换下一个");
								}
							}
						} 
					} catch (Exception e) {  
						 e.printStackTrace();
					}  
				}

				
			});  
		}  
		articlePage_fixedThreadPool.shutdown();
	}
		
	public static int test(String string,int i1,int i2) {
		System.out.println("第"+i1+"个线程起:"+i2);
		try {
			Thread.sleep(1000*5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("第"+i1+"个线程止:"+i2);
		return 0;
	}  
	
	public static void main(String[] args) {
		// TODO 自动生成的方法存根

		ArrayList<String> l=new ArrayList<String> ();
		for(int i=0;i<1000;i++){
			l.add(""+i);
		}
		
		start(l,100);
	}

}
