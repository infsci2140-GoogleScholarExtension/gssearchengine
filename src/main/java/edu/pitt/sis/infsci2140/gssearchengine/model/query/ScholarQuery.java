package edu.pitt.sis.infsci2140.gssearchengine.model.query;

import java.util.HashMap;
import java.util.Map;

public class ScholarQuery extends Query{

	
	public class Tuple<ContentType>{
		private String txt;
		private ContentType content;
		private int num;
		
		Tuple(ContentType c,String t,  int n){
			txt=t;
			content=c;
			num=n;
		}
		
		public String getTxt(){
			return txt;
		}
		
		public void setTxt(String s){
			txt=s;
		}
		
		public ContentType getContent(){
			return content;
		}
		
		public void setContent(ContentType c){
			content=c;
		}
		
		public int getNum(){
			return num;
		}
		
		public void setNum(int n){
			num=n;
		}
	}
	
	public Tuple<Integer> createTuple(int txt, String tag, int num) {
		Tuple<Integer> tuple = new Tuple<Integer>(txt, tag, num);
		return tuple;
	}
	
	
	public Tuple<String> createTuple(String txt, String tag, int num) {
		Tuple<String> tuple = new Tuple<String>(txt, tag, num);
		return tuple;
	}
	
	@SuppressWarnings("rawtypes")
	public static Map<String, Tuple> attr= new HashMap<String, Tuple>();
	
	
	private int NumOfResult;
	private String url;
	
	
	public static void main(String[] args){
		ScholarQuery sq=new ScholarQuery();
		Tuple<String> t=sq.createTuple("qqq", "title", 0);
		Tuple<Integer> n=sq.createTuple(0, "num_paper", 1);
		
		attr.put("title",t);
		attr.put("name",n);
		
		System.out.println(attr.get("name").getContent());
		
	}
}
