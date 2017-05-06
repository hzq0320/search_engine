package Main;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Init.Names;
import Init.Url;
import Init.WordInUrl;
import MySql.MySql;
import Spider.jsoup;
import Spider.judgeUrl;
import Zhong.TEXT;


/** 
* @author 作者 E-mail: zhiqianghu0320@gmail.com
* @version 创建时间：2017年4月22日 下午1:33:35 
* 类说明 :数据库中url、chudu、rudu、title、text、words等全部用SplitZH.Zh.ZhToUrlEncodeUTF_8编码，转换出来需要解码
网址表中flag=0表示正常网址，flag=1表示文件网址，flag=2表示html文件的网址
flagpa=0表示没有被爬过，flagpa=1表示被爬过
词表中
*/
public class Init {

	//数据库
	public static Connection con=null;
	//sql语句
	public static String sql="";
	
	//匹配时间所需要的标志
	public static String pa="";
	
	//网址表
	//网址string(从数据库读出)
	public static ArrayList<String> urlstr=null;//SplitZH.Zh.ZhToUrlEncodeUTF_8编码
	//用来存储已有的网址
	public static LinkedHashSet<String> urlset;//不用编码，需要从urlstr解码
	//用来存储需要爬的网址
	public static ArrayList<Url> urldetail;//不用编码
	
	//词典表(从数据库读出)
	public static ArrayList<String> wordstr=null;
	//转换为LinkedHashSet<smallWord>--用来判断词是否存在于数据库中--不用编码，需要从wordstr解码
	public static LinkedHashSet<String> wordset;
	
	//初始化
	public static boolean init(){
		
		//初始化全局变量
		urlset=new LinkedHashSet<String> ();
		urldetail=new ArrayList<Url> ();
		wordset=new LinkedHashSet<String> ();
		
		//先连接数据库
		con=MySql.init(Names.sqlName);
		if(con==null){
			System.out.println("初始化init:连接数据库失败!");
			return false;
		}
		
		//判断几个表是否存在
		//真正的url表(flag表示网址类别，flagpa表示是否被爬过)
		//flagpa=0:没爬过，flagpa=1爬过
		sql="create table if not exists "+Names.urlTable+"(url VARCHAR(768),flag int,flagpa int,weight int,title VARCHAR(768),time bigint,chudu LONGTEXT,rudu LONGTEXT,text LONGTEXT,words LONGTEXT,INDEX(url),INDEX(title),INDEX(time),INDEX(flag),INDEX(weight))";
		try {
			MySql.exec(con,sql);
		} catch (SQLException e) {
			System.out.println("初始化init:url表创建失败!");
			e.printStackTrace();
			return false;
		}
		
		
		//先收集所有的url，后面用来判断是否是已经存在的url
		//搜集已有网址--一直都没有用到urldetail，只有后面搜集待爬网址才需要
		sql="select url from "+Names.urlTable+"";
		urlstr=MySql.select(con,sql);
		if(urlstr!=null){
			//当前一个网址都没有,就把首页添加到urlset中和urldetail中
			if(urlstr.size()==0){
				urlset.add(Names.rootUrl);
				//并且把首页插入进来
				sql="insert into "+Names.urlTable+"(url,flag,flagpa,weight) values('"+Zhong.Zh.ZhToUrlEncodeUTF_8(Names.rootUrl)+"',0,0,0)";
				try {
					MySql.exec(con,sql);
				} catch (SQLException e) {
					System.out.println("初始化init:插入首页网址到url表失败!");
					e.printStackTrace();
					return false;
				}
			}else{
				//如果有网址，就把他们添加到urlset中
				for(int i=0;i<urlstr.size();i++){
					urlset.add(urlstr.get(i).replace("\t",""));
				}
			}
		}else{
			System.out.println("获取网址表失败!");
			return false;
		}
		
		
		//搜集待爬网址
		sql="select url,flag,flagpa from "+Names.urlTable +" where flagpa=0 or text is null";
		urlstr=MySql.select(con,sql);
		if(urlstr!=null){
			//如果没有没爬过的网址，就找最近一个月的网址
			if(urlstr.size()==0){
				sql="select url,flag,flagpa from "+Names.urlTable +" where time>"+(Time.Time.gettime10()-3600*24*30);
				urlstr=MySql.select(con,sql);
				if(urlstr==null||urlstr.size()==0){
					//网址映射表为空，插入首页网址及其num为1
					System.out.println("一月内网址表为空!");
					return false;
				}else{
					for(int i=0;i<urlstr.size();i++){
						//先解码再分割
						String s[]=Zhong.Zh.UrlEncodeUTF_8ToZh(urlstr.get(i)).split("\t");
						if(s.length==3){
							Url u=Url.initUrl(Zhong.Zh.UrlEncodeUTF_8ToZh(urlstr.get(i)));
							urldetail.add(u);
						}
					}
				}
			}
			//否则就把没有爬过的网址记录到urldetail
			else{
				tranUrlStrToLinkedHashSet();
			}
		}
		
		
		
		
		/*//搜集待爬网址
		sql="select url,flag,flagpa from "+Names.urlTable +" where flagpa=0 or text is null";
		urlstr=MySql.select(con,sql);
		if(urlstr==null||urlstr.size()==0){
			//网址映射表为空，插入首页网址及其num为1
			System.out.println("初始化init:url表为空!");
			sql="insert into "+Names.urlTable+"(url,flag,flagpa,weight) values('"+Zhong.Zh.ZhToUrlEncodeUTF_8(Names.rootUrl)+"',0,0,0)";
			try {
				urlset.add(Names.rootUrl);
				urlstr.add(Zhong.Zh.ZhToUrlEncodeUTF_8(Names.rootUrl)+"\t0\t0");
				//只有为空的时候会添加网址，否则都不添加
				//urldetail.add(Url.initUrl(Zhong.Zh.ZhToUrlEncodeUTF_8(Names.rootUrl)+"\t0\t0"));
				MySql.exec(con,sql);
			} catch (SQLException e) {
				System.out.println("初始化init:插入首页网址到url表失败!");
				e.printStackTrace();
				return false;
			}
		}
		//链表保存到LinkedHashSet
		tranUrlStrToLinkedHashSet();
		urldetail.add(Url.initUrl(Names.rootUrl+"\t0\t0"));*/
		
		//暂时还没有建立索引，没有用到词典表，先解决前面的错误再说(已经开始用了)
		//然后是词典表
		//分别表示词、词的类型、所有包含其的网址（url:词频	的形式，以,分割）--并不需要总的词频数吧
		sql="create table if not exists "+Names.wordTable+"(word VARCHAR(768) not null,flag int not null,urls LONGTEXT,INDEX(word),INDEX(flag))";
		try {
			System.out.println(sql);
			MySql.exec(con,sql);
			System.out.println("初始化init:word表创建成功!");
		}catch (SQLException e) {
			System.out.println("初始化init:word表创建失败!");
			e.printStackTrace();
			return false;
		}
		
		//判断word表是否为空
		sql="select word from "+Names.wordTable;
		wordstr=MySql.select(con,sql);
		if(wordstr==null||wordstr.size()==0){
			//网址映射表为空，插入首页网址及其num为1
			System.out.println("初始化init:word表为空!");
		}else{
			tranWordStrToLinkedHashSet();
		}
		return true;
	}
	
	//词表转LinkedHashSet,并且清空链表
	public static void tranWordStrToLinkedHashSet(){
		for(int i=0;i<wordstr.size();i++){
			//先解码再分割
			String s[]=Zhong.Zh.UrlEncodeUTF_8ToZh(wordstr.get(i)).split("\t");
			if(s.length==1){
				wordset.add(Zhong.Zh.UrlEncodeUTF_8ToZh(wordstr.get(i)));
			}
		}
		wordstr.clear();
	}
	
	//网址表转LinkedHashMap,并且清空链表(哪些网址已经存在)
	public static void tranUrlStrToLinkedHashSet(){
		for(int i=0;i<urlstr.size();i++){
			//先解码再分割
			String s[]=Zhong.Zh.UrlEncodeUTF_8ToZh(urlstr.get(i)).split("\t");
			if(s.length==3){
				Url u=Url.initUrl(Zhong.Zh.UrlEncodeUTF_8ToZh(urlstr.get(i)));
				//将网址存入urlset（现在已有哪些网址）
				//urlset.add(s[0]);
				//并不需要下面这个
				urldetail.add(u);
			}
		}
		urlstr.clear();
	}
	
	//保存缓存的url
	public static ArrayList<Url> bufurldetail=new ArrayList<Url> ();
	
	//根据url获取各属性
	public static Url getUrl(String url){
		
		//System.out.println("子线程获取URL:"+url);
		//url=Zhong.Zh.UrlEncodeUTF_8ToZh(url);
		
		String bufsql="";
		
		Url u=new Url();
		u.url=url;
		//因为只选择flag=0的来执行，所有肯定flag=0，其实下面这句都不需要
		u.flag=0;
		u.flagpa=1;
		//设置10秒的timeout
		//获取source
		String source=jsoup.getSource(url,10000);
		if(source==null||source.length()==0){
			bufsql="update "+Names.urlTable+" set flagpa=1,time="+Time.Time.getTimeByStr10("2018-01-01")+" where url='"+Zhong.Zh.ZhToUrlEncodeUTF_8(url)+"'";
			try {
				MySql.exec(con,bufsql);
			} catch (SQLException e) {
				System.out.println(bufsql+":更新爬虫记录失败!");
				e.printStackTrace();
			}
			return null;
		}else{
		//	System.out.println(bufsql+":更新爬虫记录成功!");
		}
		
		//根据source获取text
		String text=jsoup.getTextBySource(source);
		if(text==null||text.length()==0){
			//System.out.println(url+"正在更新爬虫记录...");
			bufsql="update "+Names.urlTable+" set flagpa=1,time="+Time.Time.getTimeByStr10("2018-01-01")+" where url='"+Zhong.Zh.ZhToUrlEncodeUTF_8(url)+"'";
			try {
				MySql.exec(con,bufsql);
				//System.out.println(url+"更新爬虫记录成功!");
			} catch (SQLException e) {
				System.out.println(bufsql+":更新爬虫记录失败!");
				e.printStackTrace();
			}
			return null;
		}else{
		//	System.out.println(bufsql+":更新爬虫记录成功!");
		}
		
		//根据source获取title
		String title=jsoup.getTitleBySource(source);
		if(title==null){
			u.title="";
		}else{
			u.title=title;
		}
		
		
		long time=jsoup.getTimeByTextLong(text,pa);
		if(time==-1){
			//没有找到时间，就设置一个以后的时间，反正是根据时间与当前时间的间隔来排序
			time=Time.Time.getTimeByStr10("2018-01-01");
		}
		u.time=time;
		
		u.text="";
		//原文本按照中文分割(提取出其中的中文)
		ArrayList<String> Zh=TEXT.splitToZh(text);
		if(Zh!=null&&Zh.size()>0){
			for(int i=0;i<Zh.size();i++){
				u.text+=Zh.get(i)+",";
			}
		}
		
		//搜集所有插入词表的数据
		bufsql="insert into "+Names.wordTable+"(word,flag,urls) values";
		String wordstr="";
		//新的方法，需要先按照中文分割再分词
		ArrayList<WordInUrl> words=TEXT.splitAndSplit(text,TEXT.words_maxlen,TEXT.nouse_maxlen);
		if(words==null||words.size()==0){
			u.words.clear();
		}else{
			for(int i=0;i<words.size();i++){
				u.words.add(words.get(i));
				wordstr+=words.get(i).word+":"+words.get(i).num+",";
				if(wordset.contains(words.get(i).word)){
					//System.out.println(words.get(i).word+"正在更新词典数据表...");
					String bufsql1="update "+Names.wordTable+" set "+"urls=(select concat(urls"+",'"+Zhong.Zh.ZhToUrlEncodeUTF_8(url+":"+words.get(i).num+",")+"')) where word='"+Zhong.Zh.ZhToUrlEncodeUTF_8(words.get(i).word)+"'";
		        	try {
						MySql.exec(con,bufsql1);
						//System.out.println(words.get(i).word+"更新词典数据表成功!");
					} catch (SQLException e) {
						// TODO 自动生成的 catch 块
						System.out.println("更新词典数据表失败!");
						e.printStackTrace();
					}
				}else{
					//System.out.println(words.get(i).word+"不存在，正在插入到词典数据表...");
					wordset.add(words.get(i).word);
					if(words.get(i).word.length()==0){
						//sql="insert into "+Names.wordTable+"(word,flag,urls) values('"+Zhong.Zh.ZhToUrlEncodeUTF_8(" ")+"',"+words.get(i).flag+",'"+Zhong.Zh.ZhToUrlEncodeUTF_8(url+":"+words.get(i).num+",")+"')";
						bufsql+="('"+Zhong.Zh.ZhToUrlEncodeUTF_8(" ")+"',"+words.get(i).flag+",'"+Zhong.Zh.ZhToUrlEncodeUTF_8(url+":"+words.get(i).num+",")+"'),";
					}
					else {
						//sql="insert into "+Names.wordTable+"(word,flag,urls) values('"+Zhong.Zh.ZhToUrlEncodeUTF_8(words.get(i).word)+"',"+words.get(i).flag+",'"+Zhong.Zh.ZhToUrlEncodeUTF_8(url+":"+words.get(i).num+",")+"')";
						bufsql+="('"+Zhong.Zh.ZhToUrlEncodeUTF_8(words.get(i).word)+"',"+words.get(i).flag+",'"+Zhong.Zh.ZhToUrlEncodeUTF_8(url+":"+words.get(i).num+",")+"'),";
					}
				}
			}
		}
		if(!bufsql.equals("insert into "+Names.wordTable+"(word,flag,urls) values")){
			try {
				MySql.exec(con,bufsql.substring(0,bufsql.length()-1));
				//System.out.println(words.get(i).word+"插入词典数据表成功!");
			} catch (SQLException e) {
				// TODO 自动生成的 catch 块
				System.out.println(bufsql);
				System.out.println("插入词典数据表失败!");
				e.printStackTrace();
			}
		}else{
		//	System.out.println("插入词典数据表成功!");
		}
		
		
		u.chuUrl=jsoup.getAllLinksByUrlLinkedHashSet(url);
		//System.out.println(u.chuUrl);
		if(u.chuUrl==null){
			return null;
		}
		
		String chustr="";
		Iterator<String> it = u.chuUrl.iterator();
        while(it.hasNext()){
            String entry = it.next();
            chustr+=entry+",";
        }
		
        //判断数据库中是否已经存在该网址
        //数据库已存在，更新，不存在则插入并添加到urlset
        if(urlset.contains(url)){
        	//System.out.println(url+"正在更新网址数据表...");
        	bufsql="update "+Names.urlTable+" set flagpa=1,title='"+Zhong.Zh.ZhToUrlEncodeUTF_8(u.title)+"',text='"+Zhong.Zh.ZhToUrlEncodeUTF_8(u.text)+"',time="+u.time+",chudu='"+Zhong.Zh.ZhToUrlEncodeUTF_8(chustr)+"',words='"+Zhong.Zh.ZhToUrlEncodeUTF_8(wordstr)+"' where url='"+Zhong.Zh.ZhToUrlEncodeUTF_8(url)+"'";
        	try {
				MySql.exec(con,bufsql);
				//System.out.println(url+"更新网址数据表成功!");
			} catch (SQLException e) {
				// TODO 自动生成的 catch 块
				System.out.println("更新数据表失败!");
				e.printStackTrace();
			}
        }else{
		//	System.out.println("更新数据表成功!");
		}
        
        
		String chu[]=new String[u.chuUrl.size()];
		u.chuUrl.toArray(chu);
		String allsql="insert into "+Names.urlTable+"(url,flag,flagpa,weight) values";
		int sumweight=0;
		for(int i=0;i<chu.length;i++){
			//当前未保存此链接，才保存一下
			if(!urlset.contains(chu[i])){
				//System.out.println("不包含!");
				Url bufu=new Url();
				bufu.url=chu[i];
				switch(judgeUrl.judgeByUrlWithPat(chu[i])){
					case 1:
						//System.out.println(chu[i]+":flag=1");
						bufu.flag=1;
						allsql+="('"+Zhong.Zh.ZhToUrlEncodeUTF_8(chu[i])+"',1,0,1),";
						urlset.add(chu[i]);
						sumweight++;
						break;
					case 2:
						//System.out.println(chu[i]+":flag=2");
						bufu.flag=2;
						allsql+="('"+Zhong.Zh.ZhToUrlEncodeUTF_8(chu[i])+"',2,0,1),";
						urlset.add(chu[i]);
						sumweight++;
						break;
					case 0:
						//System.out.println(chu[i]+":flag=0");
						bufu.flag=0;
						allsql+="('"+Zhong.Zh.ZhToUrlEncodeUTF_8(chu[i])+"',0,0,1),";
						urlset.add(chu[i]);
						bufurldetail.add(bufu);
						sumweight++;
						break;
					default:
						bufu.flag=3;
						break;
				}
			}else{
				
				//包含那个网址，就把他的权重+1
				bufsql="update "+Names.urlTable+" set weight=weight+1"+" where url='"+Zhong.Zh.ZhToUrlEncodeUTF_8(chu[i])+"'";
				try {
					MySql.exec(con,bufsql);
					//System.out.println("更新权重数据表成功!");
				} catch (SQLException e) {
					// TODO 自动生成的 catch 块
					System.out.println("更新权重数据表失败!");
					e.printStackTrace();
				}
			}
		}
		
		if(!allsql.equals("insert into "+Names.urlTable+"(url,flag,flagpa,weight) values")){
			try {
				MySql.exec(con,allsql.substring(0,allsql.length()-1));
			} catch (SQLException e) {
				// TODO 自动生成的 catch 块
				System.out.println("插入数据表失败!");
				e.printStackTrace();
			}
		}else{
			//System.out.println("插入数据表成功!");
		}
		return u;
	}
	
	private static ExecutorService fixedThreadPool;
	
	//传的参数分别是：链表,线程数
	public static void start(ArrayList<Url> l,int n){
		int one=l.size()/n+1;
		for (int i = 0; i < n; i++) {  
			final int index = i;  
			fixedThreadPool.execute(new Runnable() {  
				public void run() {  
					try {  
						for(int j=(index*one);j<(index+1)*one;j++){
							if(j<l.size()){
								//在此修改
								//对链表中每个对象作何操作，此处为输出
								//System.out.println(l.get(i));
								Url u=l.get(j);
								if(u==null){
									continue;
								}
								if(u.flag==0){
									System.out.println("线程"+index+"/"+n+":"+u.url+":\t开始");
									u=getUrl(u.url);
									if(u==null){
										continue;
									}
									System.out.println("线程"+index+"/"+n+":"+u.url+":结束，\t共有"+u.words.size()+"个词");
								}
							}
						} 
					} catch (Exception e) {  
						 e.printStackTrace();
					}  
				}  
			});  
		}  
		fixedThreadPool.shutdown();
	}
	
	//选择一个月内发布的网址
	public static ArrayList<Url> getByOneMonth(){
		ArrayList<Url> re=new ArrayList<Url> ();
		String bufsql="select url,flag,flagpa from "+Names.urlTable +" where time>"+(Time.Time.gettime10()-3600*24*30);
		urlstr=MySql.select(con,bufsql);
		if(urlstr==null||urlstr.size()==0){
			//网址映射表为空，插入首页网址及其num为1
			System.out.println("一月内网址表为空!");
			return null;
		}else{
			for(int i=0;i<urlstr.size();i++){
				//先解码再分割
				String s[]=Zhong.Zh.UrlEncodeUTF_8ToZh(urlstr.get(i)).split("\t");
				if(s.length==3){
					Url u=Url.initUrl(Zhong.Zh.UrlEncodeUTF_8ToZh(urlstr.get(i)));
					re.add(u);
				}
			}
		}
		return re;
		
	}
	
	public static void main(String[] args) {
		// TODO 自动生成的方法存根

		Names.rootUrl="http://computer.hdu.edu.cn/";
		
		pa="发布时间";
		
		//初始化
		init();
		//中文分词
		TEXT.init();
		//判断网址类型初始化
		judgeUrl.init();
		//设置一波
		judgeUrl.legalPats.add("http://computer.hdu.edu.cn/(.*?)");
		
		
		int maxthreadnum=50;
		int sumlen=urldetail.size();
		System.out.println("总线程数："+sumlen);
		int flag=1;
		if(sumlen>=maxthreadnum){
			fixedThreadPool = Executors.newFixedThreadPool(maxthreadnum);
			start(urldetail,maxthreadnum);
		}else{
			fixedThreadPool = Executors.newFixedThreadPool(sumlen);
			start(urldetail,sumlen);
		}
		while(!fixedThreadPool.isTerminated()){
		}
		System.out.println("执行完第"+flag+"波");
		
		//执行完一波都存到了bufurldetaill中
		while(true){
			
			sumlen=bufurldetail.size();
			System.out.println("总线程数："+sumlen);
			ArrayList<Url> buf=(ArrayList<Url>) bufurldetail.clone();
			bufurldetail.clear();
			if(sumlen>=maxthreadnum){
				fixedThreadPool = Executors.newFixedThreadPool(maxthreadnum);
				start(buf,maxthreadnum);
			}else{
				fixedThreadPool = Executors.newFixedThreadPool(sumlen);
				start(buf,sumlen);
			}
			while(!fixedThreadPool.isTerminated()){
			}
			flag++;
			System.out.println("执行完第"+flag+"波");
			/*for(int i=0;i<bufurldetaill.size();i++){
				System.out.println(bufurldetaill.get(i));
			}*/
			if(bufurldetail.size()<50){
				ArrayList<Url> bufonemonth=getByOneMonth();
				if(bufonemonth!=null&&bufonemonth.size()>0){
					for(int i=0;i<bufonemonth.size();i++){
						bufurldetail.add(bufonemonth.get(i));
					}
				}
			}
			
			
			
			/*//System.out.println(sumlen);
			ArrayList<Url> bufonemonth=getByOneMonth();
			if(bufonemonth!=null&&bufonemonth.size()>0){
				for(int i=0;i<bufonemonth.size();i++){
					bufurldetail.add(bufonemonth.get(i));
				}
			}
			sumlen=bufurldetail.size();
			ArrayList<Url> buf=(ArrayList<Url>) bufurldetail.clone();
			bufurldetail.clear();
			if(sumlen>=maxthreadnum){
				fixedThreadPool = Executors.newFixedThreadPool(maxthreadnum);
				start(buf,maxthreadnum);
			}else{
				fixedThreadPool = Executors.newFixedThreadPool(sumlen);
				start(buf,sumlen);
			}
			while(!fixedThreadPool.isTerminated()){
			}
			flag++;
			System.out.println("执行完第"+flag+"波");*/
		}
		
	}

}
