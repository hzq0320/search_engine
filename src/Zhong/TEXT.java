package Zhong;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;

import FileOperation.Txt;
import Init.WordInUrl;
import Spider.jsoup;

/** 
* @author 作者 E-mail: zhiqianghu0320@gmail.com
* @version 创建时间：2017年4月11日 下午10:28:57 
* 类说明 :中文分词等
* 有一个地方一定要注意，split方法是将全中文文本分割开（只是按词，没有判断是不是停用词）
* splitAndSplit方法还标记出停用词来了，所有推荐全都用splitAndSplit
*/

public class TEXT {

	
	/*Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS ： 4E00-9FBF：CJK 统一表意符号 
	Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS ：F900-FAFF：CJK 兼容象形文字 Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A ：3400-4DBF：CJK 统一表意符号扩展 A 
	CJK的意思是“Chinese，Japanese，Korea”的简写 ，实际上就是指中日韩三国的象形文字的Unicode编码 
	Character.UnicodeBlock.GENERAL_PUNCTUATION ：2000-206F：常用标点 Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION ：3000-303F：CJK 符号和标点 Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS ：FF00-FFEF：半角及全角形式 
	*/
	
	//词的最大长度（有意义的词）
	public static int words_maxlen=-1;
	//停用词最大长度
	public static int nouse_maxlen=-1;
	
	//初始化
	public static void init(){
		words_maxlen=ZhWordsInit.initAll();
		nouse_maxlen=ZhWordsInit.initAllNoUse();
	}
	
	//判断是否为中文字符
	public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
        	||ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
        	||ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
        	||(c>='A'&&c<='Z')||(c>='a'&&c<='z')||(c>='0'&&c<='9')
        	//此处本来可以用ub==Character.UnicodeBlock.BASIC_LATIN来替代，但是由于其除了包含大小写字母及数字之外还包含很多其他符号
        	//所以退而求其次，根据字符ASCII码来判断
        	) {
            return true;
        }
        return false;
    }
	
	//按照中文分割开
	//将文本分割为文本链表
	public static ArrayList<String> splitToZh(String text){
		ArrayList<String> l=new ArrayList<String> ();
		String buf="";
		int flag=0;
		for(int i=0;i<text.length();i++){
			if(isChinese(text.charAt(i))){
				flag=0;
				buf+=text.charAt(i);
			}else{
				//碰到不是中文的字符就把前面的中文添加进链表
				if(buf.length()>0){
					l.add(buf);
					buf="";
					flag=1;
				}
			}
		}
		if(flag==0){
			l.add(buf);
		}
		return l;
	}
	
	//判断字符类型
	public static String getCharType(char c){
		return (Character.UnicodeBlock.of(c).toString());
	}

	//正向最大匹配
	//需要传入的是文本和词典最大长度和停用词词典最大长度
	public static LinkedHashMap<String,WordInUrl> fromStart(String text,int words_maxlen,int nouse_maxlen){
		LinkedHashMap<String,WordInUrl> buf=new LinkedHashMap<String,WordInUrl> ();
		int len=text.length();
		int index_start=0;
		while(index_start<len){
			int i=words_maxlen;
			for(;i>=1;i--){
				if((index_start+i)<=len&&ZhWordsInit.all.get(i).containsKey(text.substring(index_start,index_start+i))){
					if(buf.containsKey(text.substring(index_start,index_start+i))){
						buf.get(text.substring(index_start,index_start+i)).num++;
					}else{
						buf.put(text.substring(index_start,index_start+i),new WordInUrl(text.substring(index_start,index_start+i),0,1));
					}
					
					index_start+=i;
					break;
				}
				else if((index_start+i)<=len&&ZhWordsInit.nouse.get(i).containsKey(text.substring(index_start,index_start+i))){
					if(buf.containsKey(text.substring(index_start,index_start+i))){
						buf.get(text.substring(index_start,index_start+i)).num++;
					}else{
						buf.put(text.substring(index_start,index_start+i),new WordInUrl(text.substring(index_start,index_start+i),1,1));
					}
					index_start+=i;
					break;
				}
			}
			if(i==0){
				if(buf.containsKey(text.charAt(index_start))){
					buf.get(text.charAt(index_start)).num++;
				}else{
					buf.put(text.charAt(index_start)+"",new WordInUrl(text.charAt(index_start)+"",2,1));
				}
				index_start+=1;
			}
		}
		return buf;
	}
	
	//反向最大匹配
	//需要传入的是文本和词典最大长度和停用词词典最大长度
	public static LinkedHashMap<String,WordInUrl> fromEnd(String text,int words_maxlen,int nouse_maxlen){
		LinkedHashMap<String,WordInUrl> buf=new LinkedHashMap<String,WordInUrl> ();
		int len=text.length();
		int index_start=len;
		while(index_start>0){
			int i=words_maxlen;
			for(;i>=1;i--){
				if((index_start-i)>=0&&ZhWordsInit.all.get(i).containsKey(text.substring(index_start-i,index_start))){
					if(buf.containsKey(text.substring(index_start-i,index_start))){
						buf.get(text.substring(index_start-i,index_start)).num++;
					}else{
						buf.put(text.substring(index_start-i,index_start),new WordInUrl(text.substring(index_start-i,index_start),0,1));
					}
					index_start-=i;
					break;
				}
				else if((index_start-i)>=0&&ZhWordsInit.nouse.get(i).containsKey(text.substring(index_start-i,index_start))){
					if(buf.containsKey(text.substring(index_start-i,index_start))){
						buf.get(text.substring(index_start-i,index_start)).num++;
					}else{
						buf.put(text.substring(index_start-i,index_start),new WordInUrl(text.substring(index_start-i,index_start),1,1));
					}
					index_start-=i;
					break;
				}
			}
			if(i==0){
				if(buf.containsKey(text.charAt(index_start-1))){
					buf.get(text.charAt(index_start-1)).num++;
				}else{
					buf.put(text.charAt(index_start-1)+"",new WordInUrl(text.charAt(index_start-1)+"",2,1));
				}
				index_start-=1;
			}
		}
		return buf;
	}
	
	//真分词--适用于全中文
	//将正向最大匹配和反向最大匹配结合
	public static ArrayList<WordInUrl> split(String text,int words_maxlen,int nouse_maxlen ){
		LinkedHashMap<String,WordInUrl> fromstartmap=fromStart(text,words_maxlen,nouse_maxlen);
		LinkedHashMap<String,WordInUrl> fromendmap=fromEnd(text,words_maxlen,nouse_maxlen);
		
		ArrayList<WordInUrl> fromstart=new ArrayList<WordInUrl> ();
		ArrayList<WordInUrl> fromend=new ArrayList<WordInUrl> ();
		
		Iterator<Entry<String,WordInUrl>> itstart = fromstartmap.entrySet().iterator();
        while(itstart.hasNext()){
            Entry<String,WordInUrl> entry = itstart.next();
            fromstart.add(entry.getValue());
        }
        
        Iterator<Entry<String,WordInUrl>> itend = fromendmap.entrySet().iterator();
        while(itend.hasNext()){
            Entry<String,WordInUrl> entry = itend.next();
            fromend.add(entry.getValue());
        }
		//正向词数少，返回正向匹配结果
		if(fromstart.size()<fromend.size()){
			return fromstart;
		}
		//反向词数少，返回反向匹配结果的倒序
		if(fromstart.size()>fromend.size()){
			ArrayList<WordInUrl> buf=new ArrayList<WordInUrl> ();
			for(int i=fromend.size()-1;i>=0;i--){
				buf.add(fromend.get(i));
			}
			return buf;
		}
		
		//计算正向最大匹配单字词数
		int startn=0;
		for(int i=0;i<fromstart.size();i++){
			if(fromstart.get(i).word.length()==1){
				startn++;
			}
		}
		//计算反向最大匹配单字词数
		int endn=0;
		for(int i=0;i<fromend.size();i++){
			if(fromend.get(i).word.length()==1){
				endn++;
			}
		}
		
		if(startn<endn){
			return fromstart;
		}else{
			//因为一般反向匹配正确率更高，所以如果词数相同、单字词数相同，则返回反向最大匹配结果
			ArrayList<WordInUrl> buf=new ArrayList<WordInUrl> ();
			for(int i=fromend.size()-1;i>=0;i--){
				buf.add(fromend.get(i));
			}
			return buf;
		}
	
	}
	
	
	//真分词--先按照中文分割，再分词
	//将正向最大匹配和反向最大匹配结合
	public static ArrayList<WordInUrl> splitAndSplit(String text1,int words_maxlen,int nouse_maxlen ){
		
		LinkedHashMap<String,WordInUrl> re=new LinkedHashMap<String,WordInUrl> ();
		//中文分割
		ArrayList<String> zh=splitToZh(text1);
		//System.out.println(zh);
		for(int k=0;k<zh.size();k++){
			String text=zh.get(k);
			
			LinkedHashMap<String,WordInUrl> fromstartmap=fromStart(text,words_maxlen,nouse_maxlen);
			LinkedHashMap<String,WordInUrl> fromendmap=fromEnd(text,words_maxlen,nouse_maxlen);
			
			//正向匹配词数少
			if(fromstartmap.size()<fromendmap.size()){
				Iterator<Entry<String,WordInUrl>> itstart = fromstartmap.entrySet().iterator();
		        while(itstart.hasNext()){
		            Entry<String,WordInUrl> entry = itstart.next();
		            String key=entry.getKey();
		            WordInUrl wiu=entry.getValue();
		            if(re.containsKey(key)){
		            	re.get(key).num+=wiu.num;
		            }else{
		            	re.put(key,wiu);
		            }
		        }
		        continue;
			}
			
			
			if(fromstartmap.size()>fromendmap.size()){
				Iterator<Entry<String,WordInUrl>> itend = fromendmap.entrySet().iterator();
				ArrayList<String> bufstr=new ArrayList<String> ();
				ArrayList<WordInUrl> bufword=new ArrayList<WordInUrl> ();
				while(itend.hasNext()){
		            Entry<String,WordInUrl> entry = itend.next();
		            bufstr.add(entry.getKey());
		            bufword.add(entry.getValue());
		        }
				
				for(int j=bufstr.size()-1;j>=0;j--){
					if(re.containsKey(bufstr.get(j))){
		            	re.get(bufstr.get(j)).num+=bufword.get(j).num;
		            }else{
		            	re.put(bufstr.get(j),bufword.get(j));
		            }
				}
		        continue;
			}
			
			//System.out.println(text+"在这");
			//计算正向最大匹配单字词数
			int startn=0;
			Iterator<Entry<String,WordInUrl>> itstart = fromstartmap.entrySet().iterator();
	        while(itstart.hasNext()){
	            Entry<String,WordInUrl> entry = itstart.next();
	            if(entry.getValue().word.length()==1){
	            	startn++;
	            }
	        }
	        
			//计算反向最大匹配单字词数
			int endn=0;
			Iterator<Entry<String,WordInUrl>> itend = fromendmap.entrySet().iterator();
	        while(itend.hasNext()){
	            Entry<String,WordInUrl> entry = itend.next();
	            if(entry.getValue().word.length()==1){
	            	endn++;
	            }
	        }
			
			if(startn<endn){
				itstart = fromstartmap.entrySet().iterator();
		        while(itstart.hasNext()){
		            Entry<String,WordInUrl> entry = itstart.next();
		            String key=entry.getKey();
		            WordInUrl wiu=entry.getValue();
		            if(re.containsKey(key)){
		            	re.get(key).num+=wiu.num;
		            }else{
		            	re.put(key,wiu);
		            }
		        }
		        continue;
			}else{
				//因为一般反向匹配正确率更高，所以如果词数相同、单字词数相同，则返回反向最大匹配结果
				itend = fromendmap.entrySet().iterator();
				ArrayList<String> bufstr=new ArrayList<String> ();
				ArrayList<WordInUrl> bufword=new ArrayList<WordInUrl> ();
				while(itend.hasNext()){
		            Entry<String,WordInUrl> entry = itend.next();
		            bufstr.add(entry.getKey());
		            bufword.add(entry.getValue());
		        }
				
				for(int j=bufstr.size()-1;j>=0;j--){
					if(re.containsKey(bufstr.get(j))){
		            	re.get(bufstr.get(j)).num+=bufword.get(j).num;
		            }else{
		            	re.put(bufstr.get(j),bufword.get(j));
		            }
				}
		        continue;
			}
		}
		ArrayList<WordInUrl> res=new ArrayList<WordInUrl> ();
		Iterator<Entry<String,WordInUrl>> it = re.entrySet().iterator();
        while(it.hasNext()){
            Entry<String,WordInUrl> entry = it.next();
            String key=entry.getKey();
            WordInUrl wiu=entry.getValue();
            if(ZhWordsInit.nouse.get(key.length()).containsKey(key)){
            	//System.out.println(key+"是停用词");
            	wiu.flag=1;
            	res.add(wiu);
            }
            else{
            	//System.out.println(key+"不是停用词");
            	res.add(wiu);
            }
        }
        return res;
	
	}

	/*//根据文本的text返回每个词的wordinurl形式(词、属性、词频)
	public static ArrayList<WordInUrl> splitAll(String text,int words_maxlen,int nouse_maxlen ){
		ArrayList<WordInUrl> l=new ArrayList<WordInUrl> ();
		ArrayList<String> zh=splitToZh(text);
		for(int i=0;i<zh.size();i++){
			ArrayList<String> buf=split(zh.get(i),words_maxlen,nouse_maxlen);
			for(int j=0;j<buf.size();j++){
				if(l.containsKey(buf.get(j))){
					l.put(buf.get(j),l.get(buf.get(j))+1);
				}else{
					l.put(buf.get(j),1);
				}
			}
		}
		return l;
	}
	*/
	
	
	//测试分割text
	public static void testSplit(String text){
		ArrayList<WordInUrl> l=split(text,words_maxlen,nouse_maxlen);
		for(int i=0;i<l.size();i++){
			System.out.println(l.get(i).toString());
		}
	}
	
	
	
	public static void main(String[] args) {
		// TODO 自动生成的方法存根
		
		//全部初始化
		init();
		
		/*String text="随着《人民的名义》的热播，不出意外的话，《万历十五年》这本书又火了。每隔几年，这本书就要火一下，这或许能说明一个道理：黄仁宇在书中提出来的中国政治制度的死结，一直都没有解开。这个死结在《人民的名义》里面体现得很明显。把《万历十五年》和《人民的名义》联系在一起看，能看出很多有意思的东西，但最核心的则是黄仁宇在序言里说的那句话：“书中所述，不妨称为一个大失败的总记录。” 《万历十五年》里的角色是这样，《人民的名义》里的角色也同样如此。我对《人民的名义》的观感是，这部剧虽然已经经过美化处理，但仍然反映出了当代中国的四大失败，它们是：婚姻的失败，政治权力的失败，人民的失败，人性的失败。   婚姻的失败 首先是婚姻的失败。达康书记、高书记和祁厅长的婚姻可谓是败得一塌糊涂，达康书记婚姻失败的原因是事业上的分歧，高书记是出轨，祁厅长是把婚姻视为政治工具。这三种失败的方式很具有代表性。侯亮平和钟小艾的婚姻不能称之为失败，他们的问题是不真实，二人之间冷淡如白开水，就连躺在床上也在讨论反腐，不像是两个异性住在一起，可能他们的新婚之夜就是在抄党章中度过的，又或者是已经达到了此身已许国再难许卿的境界。易学习两口子倒是很真实，也能打动人，问题是等他做到李达康或是高书记的位置上呢？假如有人给他送双胞胎姐妹花呢？易书记和那个自己炒茶叶的农家女子能白头偕老吗？我觉得挺难。 《人民的名义》里面让人信服的爱情要么是未经考验的，要么是经过考验后变成了苟且。高书记爱高小凤，祁厅长爱高小琴，郑乾爱宝宝，周正爱林华华……爱情经不起检验，婚姻几乎注定失败——这很像我所感受到的现实。 这种中国式失婚姻败还有个特点，就是两口子在婚姻已经失败后还倔强地住在一起，甚至伪装出恩爱的假象，从陈羽凡白百何，到高书记两口子，莫不如此。丈夫死了妻子守寡我勉强都还能理解，但丈夫还没死这就守上了是个什么节奏？这种守活寡的婚姻已经不能用失败来形容的，它就是犯罪，是对人性的犯罪。   权力的失败 再谈权力的失败。和《万历十五年》一样，贯穿全剧的另一个关键问题是：一把手的绝对权力是腐败的根源。《人民的名义》貌似一直在很努力地探讨这个问题，并在最后给出了一个在目前条件下已经是最佳的解决方案：让强势的易学习去制衡李达康。看到这里我产生了一个疑问：如果没有易学习怎么办？如果易学习不够强势怎么办？如果易学习变成祁同伟怎么办？另外，谁又来制衡省委书记沙瑞金呢？纪委书记如果是高育良怎么办？检察院长如果是肖钢玉怎么办？我们怎么能保证沙瑞金不变成下一个赵立春呢？这种情况并非没有可能发生，因为爱打篮球的沙书记的到来，网球场已经悄悄变成了篮球场。 让我有点后怕的一个角色是赵东来。他本来和祁同伟一个系统，莫名其妙就成了侯亮平这边的人，我估计其中陆亦可的美色起了很大的作用。敌有高小琴我有陆亦可，可是如果我方没有陆亦可怎么办……如果赵东来和祁同伟穿一条裤子，那就意味着公安系统的全面腐化。那样一来，警察蜀黍们可能就真的要发飙了。 从这个角度看，《人民的名义》走的是以德反腐的路线，靠的是侯亮平这样刀枪不入的完人，甚至是圣人，而不是制度。侯亮平反贪的动力，很大程度上来自于给死党陈海报仇，而沙瑞金的动力则有些不可描述：清除前任的旧势力。从对赵立春的清算到提拔易学习，到拆除赵家的美食城，都与这个逻辑有关。在剧中，人物的行为动力来自于上层阶级的情感和利益，与制度无关，更与媒体监督或是人民的利益差之十万八千里。现任以高尚的名义否定前任的任命和建筑，这也是中国一朝天子一朝臣的问题根源，甚至昆明这么多年一直在不断的拆除和各种修路，市委书记落马大四喜，甚至连个公交站台都是孟母三迁，就是这个原因。黄仁宇在《万历十五年》的前言里说，中国二千年来，以道德代替法制，至明代而极，这就是一切问题的症结。然而真的是至明代而极么？呵呵。   人民的失败 再说人民的失败。郑乾这条线，很多人民不喜欢，要求快进，我觉得这简直是忘了本，因为郑乾就是民二代啊，他的存在就是为了反衬赵瑞龙这个赵二代有多嚣张，就是为了突出民二代有多凄惨。他老爹郑西坡的那本诗集，从电视剧的开始一直拖到最后一集，才用8000块买了个书号给出版了，还得到郑乾一个“真便宜”的好评。 说到这里，有必要探讨一个概念：何为人民，何为公仆？我觉得哈，郑家和大风厂的职工一样，都是人民，哪怕挂了董事长的头衔也一样，都要面临企业的破产，为医药费发愁，被人欺负。而公仆则是赵家，是侯亮平，甚至是林华华。公仆的衣着虽然千篇一律，但不用为住房、交通和医疗发愁，从物质上来看，人民在自力更生的社会主义初级阶段，公仆们则处于按需分配的共产主义。王文革和陈海都为了工作重伤住院，但二者享受的医疗政策完全不同。但公仆们的共产主义也就是能在国内享受，走出国门，达康书记和高书记的工资连女儿留学的学费怕是都负担不起吧？这是个无法解决的BUG。 而从更深的层面来考量，公仆和人民之间的权力和利益界限很清晰。人民不可霸王硬上弓强行越界，否则祁同伟和高小琴就是前车之鉴。 陈岩石是两者之间的桥梁。当工人和拆迁队对抗时，是陈岩石充当协调者，当郑西坡被警察抓捕后，是陈岩石安抚双方的情绪，甚至当王文革对公仆们彻底绝望时，还是陈岩石充当肉票让其绑架自己，并间接为此付出了生命。难怪我的一个朋友在看到陈岩石死去那一幕在电视机前嚎啕大哭。 但是最实际的问题是，陈岩石死了之后，还有谁来充当人民和公仆之间的传声筒呢？没有了，网络的删帖和那个迟迟没有得到解决的信访办窗口，象征着人民就此失去了抵达天听的渠道。   人性的失败 最后说人性的失败。和郑乾一样，孙连城是另一个小角色，虽然信访办的窗口是他修的，但他在精神上的追求却是所有公仆加在一起都无法企及的：仰望星空。 仰望星空是一种很重要的能力，在刘慈欣笔下，监视地球的外星人判断猿人有了智力的标志，就是某只猿猴开始凝视夜空。这是一种无用的审美，但审美本身就应该是无用的，这和某伟人认为艺术应该服务于政治的属性背道而驰。侯亮平把京剧变成了政治斗争的工具，高书记爱好万历十五年、书法和张大千的画，而其他公仆，我甚至不知道他们还有什么文化和精神上的追求。《人民的名义》里的公仆群像，就像《万历十五年》里的海瑞一样，实在是无趣无味至极。 说到海瑞，黄仁宇对他的评价是“按照规定的最高限度执行。”在李达康、侯亮平甚至是易学习的身上，我们都能见到海瑞的影子。然而，黄仁宇对海瑞是持否定态度的，海瑞也是《万历十五年》里的失败群像之一。 再说回孙区长，在被李达康批评不作为的时候，孙区长甚至不像其他公仆那样满脸堆笑唯唯诺诺，而是拍案而起：爷不伺候了！最后，孙连城这个还残留着一点人味的公仆去了青少年宫，做了孩子们的天文辅导员——孙连城的这个结局，让公仆的队伍更纯粹了，让《人民的名义》变成了一出精彩的童话。 最后我想为编剧辩解一下，因为所有这些犯了编剧大忌的BUG，我觉得都不是无意的疏漏，而是有意为之，有着鲜明的或是隐喻的指向，把所有明的暗的指向联系在一起，我们必须要承认，《人民的名义》没有像《白鹿原》一样被腰斩就是个奇迹，这部剧能顺利播完，是它失败全纪录之中的唯一胜利。";
		testSplit(text);*/
		
		//之前的测试是错的，之前把网页文本直接作为text进行分词是不对的，应该把按照中文分割后的结果放进去分词
		
		String url="http://zwhzbxpg.hdu.edu.cn/Art/Art_9/Art_9_20.aspx";
		String source=jsoup.getSource(url);
		String text=jsoup.getTextBySource(source);
		System.out.println("原文本:"+text);
		ArrayList<String> zh=splitToZh(text);
		System.out.println("按中文切割后:"+zh);
		for(int i=0;i<zh.size();i++){
			System.out.println(zh.get(i)+":"+split(zh.get(i),words_maxlen,nouse_maxlen));
		}
		long start=Time.Time.gettime13();
		//for(int k=0;k<10;k++){
		for(int j=0;j<zh.size();j++){
			ArrayList<WordInUrl> l=split(zh.get(j),words_maxlen,nouse_maxlen);
			for(int i=0;i<l.size();i++){
				System.out.println(l.get(i).toString());
			}
		}
		
		long end=Time.Time.gettime13();
		System.out.println((end-start)+"毫秒");
		
		ArrayList<WordInUrl> l=splitAndSplit(text,words_maxlen,nouse_maxlen);
		for(int i=0;i<l.size();i++){
			System.out.println(l.get(i).toString());
		}
		start=Time.Time.gettime13();
		System.out.println((start-end)+"毫秒");
		
	}

}
