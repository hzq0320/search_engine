package FileOperation;
/** 
* @author 作者 E-mail: zhiqianghu0320@gmail.com
* @version 创建时间：2017年4月22日 上午10:32:28 
* 类说明 
*/

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Txt {
	
	//判断文件是否存在,不存在则创建文件
	public static void exist(String path){
		int flag=0;
		for(int i=0;i<10;i++){
			try{
				File file=new File(path);    
				if(!file.exists())    
				{       
				    file.createNewFile();     	//文件不存在，创建新的文件
				}	
				flag=1;
			} catch (IOException e) {    
				continue;    
			} 
			if(flag==1){
				break;
			}
		}
		if(flag==0){
			System.out.println("读取文件"+path+"失败!");
		}
	}
	
	//读取文件到字符串中(可设置编码格式)
		public static String read(String path,String ma){		//ma存放的是编码格式，一般是"gbk",有时可能是"utf-8"
			int flag=0;
			String s1="";
			exist(path);
			for(int i=0;i<10;i++){
				try{
					BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path),ma));
					//gbk避免乱码
					for (String line = br.readLine(); line != null; line = br.readLine()) {
			                s1+=line+"\n";            
			        }
			        br.close();
			        flag=1;
				}
				catch(Exception e){
					continue;
				}
				if(flag==1){
					break;
				}
			}
			if(flag==0){
				System.out.println("读取文件"+path+"失败!");
			}
			return s1;
		}
		
	
	//读取路径中的文件(默认编码为"gbk")
	public static String read(String path){		//此处仅有路径一个参数，默认编码格式为"gbk".
		return read(path,"gbk");
	}
	
	
	
	
	//读取文件到链表(每一行是一个字符串--也可每读一行解析一次，存入特定类链表中)
		public static void read(String path,List<String> l,String ma){
			int flag=0;
			//String s1="";
			exist(path);
			for(int i=0;i<10;i++){
				try{
					BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path),ma));
					//gbk避免乱码
					for (String line = br.readLine(); line != null; line = br.readLine()) {
			               // s1+=line+"\n";  
			                l.add(line);
			        }
			        br.close();
			        flag=1;
				}
				catch(Exception e){
					continue;
				}
				if(flag==1){
					break;
				}
			}
			if(flag==0){
				System.out.println("读取文件"+path+"失败!");
			}
			//return s1;
			
		}
		
		//读取文件到链表(每一行是一个字符串--也可每读一行解析一次，存入特定类链表中)
		public static void read(String path,List<String> l){
			read(path,l,"gbk");
		}
		
		//将字符串写入到文件(注意用\r\n换行)，可设置编码
		public static void write(String s,String path,String ma){
			int flag=0;
			//String s1="";
			exist(path);
			for(int i=0;i<10;i++){
				try{
					File f = new File(path);    
					if (!f.exists()) 
					{     
						f.createNewFile();    
					}    
					OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(f),ma);    
					//gbk避免乱码
					BufferedWriter writer=new BufferedWriter(write);        
					writer.write(s);  
					writer.close();   
			        flag=1;
				}
				catch(Exception e){
					continue;
				}
				if(flag==1){
					break;
				}
			}
			if(flag==0){
				System.out.println("写入文件"+path+"失败!");
			}
		}
		
	
	
	//将字符串写入到文件(注意用\r\n换行)，默认为"gbk"编码
	public static void write(String s,String path){
		write(s,path,"gbk");
	}
	
	
	//根据链表在文件末尾追加内容
	public static void write(List<String> l, String path,String ma)  {
		String s="";
		exist(path);
		BufferedWriter out = null;
		for(int i=0;i<10;i++){
			try {
				out = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(path, true),ma));
				break;
			} catch (Exception e1) {
				continue;
			}
		}
		
		for(int i=0;i<l.size();i++){
			for(int i1=0;i1<100;i1++){
				try{
					out.write(l.get(i)+"\n");
					break;
				}catch(Exception e){
					continue;
				}
			}
		}
		for(int i=0;i<10;i++){
			try {
				out.close();
				break;
			} catch (IOException e) {
				continue;
			}
		}
		
	}
	//根据链表在文件末尾追加内容(默认编码为"gbk")
		public static void write(List<String> l, String path) {
			write(l,path,"gbk");
		}
	
	//根据链表在文件末尾追加内容(分隔）
	public static void writeto(List<String> l, String path,String ma,String ge) {
	String s="";
	exist(path);
	for(int i=0;i<l.size();i++){
		s=l.get(i)+ge;
		//用来间隔每条内容
		int flag=0;
		//String s1="";
		for(int i1=0;i1<100;i1++){
			try{
				BufferedWriter out = null;
					out = new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(path, true),ma));
					out.write(s+"\r\n");
					out.close();
					flag=1;
			}catch(Exception e){
				continue;
			}
			if(flag==1){
				break;
			}
		}
	}
	}
		

	//根据链表在文件末尾追加内容(分隔）
	public static void writeto(List<String> l,String path,String ge) {
		writeto(l,path,"gbk",ge);
	}
		
}


