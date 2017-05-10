package Douban;
/** 
* @author 作者 E-mail: zhiqianghu0320@gmail.com
* @version 创建时间：2017年4月29日 下午10:50:44 
* 类说明 :豆瓣的class
*/
public class class_douban {

	//影评编号
	public int articlenum;
	//影评标题
	public String articletitle;
	//影评作者编号(作者主页标识，可能是字符串)
	public String ownernum;
	//影评作者名
	public String ownername;
	//电影/电视剧编号
	public int movienum;
	//电影/电视剧名
	public String moviename;
	//影评作者的电影/电视剧评分(不知道是哪种类型，用string先保存着)--影评只能是整数类型评分，只有电影评分需要根据平均数计算
	public String ratingstr;
	public int ratingint;
	//影评发布时间
	public String time;
	//影评正文
	public String content;
	//影评有用数(暂时没有分隔开)
	public String usefulstr;
	public int usefulint;
	//影评没用数(暂时没有分隔开)
	public String uselessstr;
	public int uselessint;
	//电影/电视剧各种信息
	//电影标题
	public String movietitle;
	//电影导演
	public String moviedirector;
	//电影演员
	public String moviestars;
	//电影类型
	public String movieclass;
	//电影地区
	public String moviearea;
	//电影上映
	public String movieshow;
	
	public class_douban(){
		articlenum=-1;
		articletitle=" ";
		ownernum=" ";
		ownername=" ";
		movienum=-1;
		moviename=" ";
		ratingstr=" ";
		ratingint=-1;
		time=" ";
		content=" ";
		usefulstr=" ";
		usefulint=-1;
		uselessstr=" ";
		uselessint=-1;
		movietitle=" ";
		moviedirector=" ";
		moviestars=" ";
		movieclass=" ";
		moviearea=" ";
		movieshow=" ";
	}
	
	public String toString(){
		return "articlenum="+articlenum+"\tarticletitle="+articletitle+"\townernum="+ownernum+"\townername="+ownername+"\tmovienum="+movienum+"\tmoviename="+moviename+"\tratingstr="+
				ratingstr+"\tratingint="+ratingint+"\ttime="+time+"\tcontent="+content+"\tusefulstr="+usefulstr+"\tusefulint="+usefulint+"\tuselessstr="+uselessstr+"\tuselessint="+uselessint+
				"\tmovietitle="+movietitle+"\tmoviedirector="+moviedirector+"\tmoviestars="+moviestars+"\tmovieclass="+movieclass+"\tmoviearea="+moviearea+"\tmovieshow="+movieshow;
	}
	
}
