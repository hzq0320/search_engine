package Time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
* @author 作者 E-mail: zhiqianghu0320@gmail.com
* @version 创建时间：2017年4月10日 下午8:21:02 
* 类说明 
*/
public class Time {
	
	
	//获取当前时间(精确到毫秒)
	public static String gettime(){
		SimpleDateFormat df = new SimpleDateFormat("YYYY_MM_dd_hh_mm_ss_SSSSSSS");//设置日期格式
		//Date d=df.
		String s=df.format(new Date());
		return s;
	}
			
			
	//获取时间戳(13位)---精确到毫秒
	//三种方式
	public static long gettime13(){
		return System.currentTimeMillis() ;
		//return Calendar.getInstance().getTimeInMillis() ;
		//return new Date().getTime() ;
	}

	//获取时间戳(10位)---精确到秒
	//三种方式
	public static long gettime10(){
		return System.currentTimeMillis()/1000 ;
		//return Calendar.getInstance().getTimeInMillis()/1000 ;
		//return new Date().getTime() /1000;
	}
		
	//获取当前时间
	public static String getDetailTime(){
		SimpleDateFormat df = new SimpleDateFormat("YYYY_MM_dd_hh_mm_ss_SSSSSSS");//设置日期格式
		//Date d=df.
		String s=df.format(new Date());
		return s;
	}
	
	//获取当前时间
	public static String getDay(){
		SimpleDateFormat df = new SimpleDateFormat("MM_dd");//设置日期格式
		//Date d=df.
		String s=df.format(new Date());
		return s;
	}

	//获取当前时间
	public static int getHour(){
		SimpleDateFormat df = new SimpleDateFormat("HH");//设置日期格式
		//Date d=df.
		String s=df.format(new Date());
		return Integer.parseInt(s);
	}
	
	
	//根据日期字符串转时间戳(传的分别是日期和日期的格式)
	public static long getTimeByStr(String str,String format){
		SimpleDateFormat simpleDateFormat =new SimpleDateFormat(format);
	    try {
			Date date=(Date) simpleDateFormat .parse(str);
			return date.getTime();
		} catch (ParseException e) {
			return -1;
		}
	}
	
	//根据日期字符串转时间戳(传的是日期字符串)
	public static long getTimeByStr10(String str){
		SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd");
	    try {
			Date date=(Date) simpleDateFormat .parse(str);
			return date.getTime()/1000;
		} catch (ParseException e) {
			return -1;
		}
	}
	
	//休眠（除去莫名其妙的Exception）
	public static void sleep(int time){
		for(int i=0;i<10;i++){
			try{
				Thread.sleep(time);
			}catch(Exception e){
				continue;
			}
		}
	}

	public static void main(String[] args) {
		
		/*long a=getTimeByStr10("1970-1-2");
		System.out.println(a);*/
		for(int i=0;i<10;i++){
			System.out.println(Time.gettime10());
			System.out.println(Time.gettime13());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
		/*long start;
		long end;
		for(int i=0;i<10;i++){
			start=Time.gettime13();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			end=Time.gettime13();
			System.out.println(end-start);
		}*/
	}

}
