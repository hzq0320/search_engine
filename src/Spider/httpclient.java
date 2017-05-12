package Spider;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;


/** 
* @author 作者 E-mail: zhiqianghu0320@gmail.com
* @version 创建时间：2017年4月15日 下午11:15:29 
* 类说明 
*/
public class httpclient {

	//根据url获取网页源代码
	public static String getSource(String s){
		for(int i=0;i<10;i++){
			HttpClientBuilder httpClientBuilder=null;
			CloseableHttpClient closeableHttpClient=null;
			httpClientBuilder=HttpClientBuilder.create();
			closeableHttpClient=httpClientBuilder.build();
			HttpGet httpget=new HttpGet(s);
			HttpResponse response;
			try {
				response=closeableHttpClient.execute(httpget);
				HttpEntity entity=response.getEntity();
				if (entity != null) {
					return EntityUtils.toString(entity,"UTF-8");
				}
			} catch (ClientProtocolException e) {
				continue;
			} catch (IOException e) {
				continue;
			}
		}
		return null;
	}
	
	
	//根据url获取网页源代码
	public static String getSourceProxy(String s,String ip,int port){
		for(int i=0;i<10;i++){
			//设置代理服务器
			HttpHost proxy = new HttpHost(ip,port); 
			
			HttpClientBuilder httpClientBuilder=null;
			CloseableHttpClient closeableHttpClient=null;
			httpClientBuilder=HttpClientBuilder.create().setProxy(proxy);
			closeableHttpClient=httpClientBuilder.build();
			HttpGet httpget=new HttpGet(s);
			HttpResponse response;
			try {
				response=closeableHttpClient.execute(httpget);
				HttpEntity entity=response.getEntity();
				if (entity != null) {
					return EntityUtils.toString(entity,"UTF-8");
				}
			} catch (ClientProtocolException e) {
				continue;
			} catch (IOException e) {
				continue;
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		// TODO 自动生成的方法存根

		//String url="https://list.tmall.com/search_product.htm?s=0&q=小米";
		String url="https://rate.tmall.com/list_detail_rate.htm?itemId=545663194713&sellerId=2043230365&currentPage=1";
		System.out.println(httpclient.getSourceProxy(url,"180.76.137.46",8080));
		System.out.println(httpclient.getSourceProxy(url,"218.61.39.50",55336));
		System.out.println(httpclient.getSourceProxy(url,"115.159.186",80));
		System.out.println(httpclient.getSourceProxy(url,"202.100.167.170",80));
		
	}

}
