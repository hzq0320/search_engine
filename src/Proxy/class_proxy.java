package Proxy;


/** 
* @author 作者 E-mail: zhiqianghu0320@gmail.com
* @version 创建时间：2017年5月3日 上午10:24:30 
* 类说明 
*/
public class class_proxy {

	//ip
	public String ip;
	//port
	public int port;
	
	public class_proxy(String ip1,int port1){
		ip=ip1;
		port=port1;
	}
	
	public class_proxy() {
	}

	//使用HashSet保存自定义类需要修改equals和hashCode方法
	//不要直接强制类型转化，要先判断类型
	public boolean equals(Object obj){
		if(obj instanceof class_proxy){
			class_proxy u=(class_proxy)obj;
			return (ip.equals(u.ip));
		}
		return super.equals(obj);
	}
	
	
	public int hashCode() {
        return ip.hashCode();
    }
	
	public String toString(){
		return "ip="+ip+",port="+port;
	}
	
	
}
