package Spider;

import java.io.IOException;

import org.apache.http.HttpEntity;
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
	
	public static void main(String[] args) {
		// TODO 自动生成的方法存根

		String url="https://list.tmall.com/search_product.htm?s=0&q=小米";
		System.out.println(httpclient.getSource(url));
		
	}

}
