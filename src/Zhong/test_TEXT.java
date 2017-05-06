package Zhong;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import Init.WordInUrl;

/** 
* @author 作者 E-mail: zhiqianghu0320@gmail.com
* @version 创建时间：2017年5月5日 下午10:19:57 
* 类说明 
*/
public class test_TEXT {
	
	
	public static void main(String[] args) {
		// TODO 自动生成的方法存根

		//中文分词初始化
		TEXT.init();
		ArrayList<WordInUrl> re=new ArrayList<WordInUrl> ();
		long start=Time.Time.gettime13();
		String text="大部分论坛,为了方便管理，都进行了关于敏感词的设定。 　　比如,当你发贴的时候带有某些事先设定的词时，这个贴是不能发出的。或者这个词被自动替换为星号(*)或叉号(X)等。 　　这些词一般是带有敏感政治倾向（或反执政党倾向）、暴力倾向、不健康色彩的词或不文明语。 　　敏感词设定功能在贴吧或论坛中都被广泛应用。 　　最近，网上又出现了一种论坛管理功能：当论坛自动搜索到你的贴子里含有一定敏感词时，该贴会自动被删除。或交由人工审核。 　　敏感词来讲不一定是脏话，但是脏话大部分都是敏感词，比如在日本AV里出现的众多词汇是会被屏蔽的，但是广大网民的智慧是无穷的，他们会选择一种词来代替，这反倒成就了一种\"文化\" 特定环境下的意义 　　在特定的环境下大家都明白的时候，还可以用来代指不宜言明或被过滤的词或被政府驱逐人士。比如，如果我在此编辑过分的话，百度是不会通过的。你明白吧 [敏感词过滤工具 　　敏感词过滤工具,集成了大量违法,低俗,不良等词语数据,轻松一步即可过滤掉文章中的敏感词,还你一篇健康的文章! 　　目前各大机房都严格打击违法网站信息内容,希望敏感词语过滤工具能够帮你省去一些不必要的麻烦!";
		text="江泽民主席位";
		for(int i=0;i<10000;i++){
			re=TEXT.splitAndSplit(text, TEXT.words_maxlen,TEXT.nouse_maxlen);
		}
		long end=Time.Time.gettime13();
		System.out.println(text.length()+"\t用时"+(end-start)+"毫秒");
		for(int i=0;i<re.size();i++){
			System.out.println(re.get(i));
		}
		
	}

}
