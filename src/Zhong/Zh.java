package Zhong;

import java.util.Scanner;

import Spider.jsoup;

//说明：中文的常见编码与解码

public class Zh {

	/**
	 * 判断是否为中文字符
	 */
	public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }
	
	/**
	 * 判断是否为中文字符串(全部要是中文字符)
	 */
	public static boolean isChinese(String s) {
		char c[]=s.toCharArray();
		for(int i=0;i<c.length;i++){
			if(isChinese(c[i])==false){
				return false;
			}
		}
		return true;
    }
	
	
	
	/*
	 * 编码和解码
	 */
	
	
	/*
	 * unicode
	 */
	//中文转unicode
	public static String ZhToUnicode(String s){
		return Unicode.ZhToUnicode(s);
	}
	
	//unicode转中文
	public static String UnicodeToZh(String s){
		return Unicode.DecodeUnicode(s);
	}
	
	/*
	 * UrlEncode
	 */
	/*
	 * "你好"的中文转urlEncode编码(utf-8)：%e4%bd%a0%e5%a5%bd
	 */
	public static String ZhToUrlEncodeUTF_8(String zh){
		try{
			return java.net.URLEncoder.encode(zh, "UTF-8"); 
		}catch(Exception e){
			return "";
		}
	}
	
	//urlencode_utf8解码为中文
	public static String UrlEncodeUTF_8ToZh(String urlencodeutf_8){
		try{
			return java.net.URLDecoder.decode(urlencodeutf_8, "UTF-8"); 
		}catch(Exception e){
			return "";
		}
	}
	
	/*
	 * "你好"的中文转urlEncode编码(gbk)：%c4%e3%ba%c3
	 */
	public static String ZhToUrlEncodeGBK(String zh){
		try{
			return java.net.URLEncoder.encode(zh, "GBK"); 
		}catch(Exception e){
			return "";
		}
	}
	
	//urlencode_gbk解码为中文
	public static String UrlEncodeGBKToZh(String urlencodeGBK){
		try{
			return java.net.URLDecoder.decode(urlencodeGBK, "GBK"); 
		}catch(Exception e){
			return "";
		}
	}
	
	/*
	 * 中文转其他编码
	 */
	public static void ZhToOthers(String zh){
		String unicode=ZhToUnicode(zh);
		String urlencodeutf_8=ZhToUrlEncodeUTF_8(zh);
		String urlencodegbk=ZhToUrlEncodeGBK(zh);
		
		System.out.println("中文为:\t\t"+zh);
		System.out.println("Unicode编码为:\t\t"+unicode);
		System.out.println("URLEncode的UTF-8编码为:\t\t"+urlencodeutf_8);
		System.out.println("URLEncode的GBK编码为:\t\t"+urlencodegbk);
		
	}
	
	
	/*
	 * 其他编码转中文
	 */
	public static void OthersToZh(String others){
		String unicode_zh=UnicodeToZh(others);
		String urlencodeutf8_zh=UrlEncodeUTF_8ToZh(others);
		String urlencodegbk_zh=UrlEncodeGBKToZh(others);
		
		System.out.println("原码为:\t\t"+others);
		System.out.println("Unicode转中文为:\t\t"+unicode_zh);
		System.out.println("URLEncode的UTF-8转中文为:\t\t"+urlencodeutf8_zh);
		System.out.println("URLEncode的GBK转中文为:\t\t"+urlencodegbk_zh);
		
	}
	
	
	public static void main(String[] args) {
		// TODO 自动生成的方法存根
		
		while(true){
			Scanner in=new Scanner(System.in);
			String s=in.nextLine();
			ZhToOthers(s);
			OthersToZh(s);
		}
	}

}
