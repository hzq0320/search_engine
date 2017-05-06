package Zhong;

import java.io.UnsupportedEncodingException;


/*
 * 中文：		你好
 * unicode编码：\u4f60\u597d
 * utf-8编码：&#x4F60;&#x597D;
 * urlEncode编码(utf-8)：%e4%bd%a0%e5%a5%bd
 * urlEncode编码(gb2312)：%c4%e3%ba%c3
 * 参考网址：
 * 1. http://canofy.iteye.com/blog/718659
 * 
 */

//说明：unicode和中文的相互转换

public class Unicode {

	/**
	 * 把中文转成Unicode码
	 * @param str
	 * @return
	 */
	public static String ZhToUnicode(String str){
		String result="";
		for (int i = 0; i < str.length(); i++){
            int chr1 = (char) str.charAt(i);
            if(chr1>=19968&&chr1<=171941){//汉字范围 \u4e00-\u9fa5 (中文)
                result+="\\u" + Integer.toHexString(chr1);
            }else{
            	result+=str.charAt(i);
            }
        }
		return result;
	}

	/**
	 * 判断是否为中文字符
	 * @param c
	 * @return
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
	 * @param c
	 * @return
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
	 * unicode解码：解码为中文
	 */
	public static String DecodeUnicode(String theString) {    
	    char aChar;    
	    int len = theString.length();    
	    StringBuffer outBuffer = new StringBuffer(len);    
	    for (int x = 0; x < len;) {    
	    	aChar = theString.charAt(x++);    
	    	if (aChar == '\\') {    
	    		aChar = theString.charAt(x++);    
	    			if (aChar == 'u') {    
	    				int value = 0;    
	    				for (int i = 0; i < 4; i++) {    
	    					aChar = theString.charAt(x++);    
	    					switch (aChar) {    
	    					case '0':    	  
	    					case '1':    	  
	    					case '2':    	  
	    					case '3':    	  
	    					case '4':    	  
	    					case '5':    
	    					case '6':    
	    					case '7':    
	    					case '8':    
	    					case '9':    
	    						value = (value << 4) + aChar - '0';    
	    						break;    
	    					case 'a':    
	    					case 'b':    
	    					case 'c':    
	    					case 'd':    
	    					case 'e':    
	    					case 'f':    
	    						value = (value << 4) + 10 + aChar - 'a';    
	    						break;    
	    					case 'A':    
	    					case 'B':    
	    					case 'C':    
	    					case 'D':    
	    					case 'E':    
	    					case 'F':    
	    						value = (value << 4) + 10 + aChar - 'A';    
	    						break;    
	    					default:    
	    						throw new IllegalArgumentException(    
	    								"Malformed   \\uxxxx   encoding.");    
	    					}    
	    				}    
	    				outBuffer.append((char) value);    
	    			} else {    
	    				if (aChar == 't')    
	    					aChar = '\t';    
	    				else if (aChar == 'r')    
	    					aChar = '\r';    
	    				else if (aChar == 'n')    
	    					aChar = '\n';    
	    				else if (aChar == 'f')    
	    					aChar = '\f';    
	    				outBuffer.append(aChar);    
	    			}    
	    		} else   
	    			outBuffer.append(aChar);    
	    }     
	    return outBuffer.toString();    
	}   

	
	
	//测试代码
	private static void test() {
		// TODO 自动生成的方法存根
		String zh="你好haha";
		String zh_unicode=ZhToUnicode(zh);
		String unicode_zh=DecodeUnicode(zh_unicode);
		System.out.println("中文为："+zh);
		System.out.println("中文转unicode后为："+zh_unicode);
		System.out.println("中文转unicode后转中文为："+unicode_zh);
	}
	
	
	public static void main(String[] args) {
		// TODO 自动生成的方法存根
		test();
	}

	

}
