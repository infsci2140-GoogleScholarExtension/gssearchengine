package edu.pitt.sis.infsci2140.gssearchengine.model.article;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author hongzhang
 *
 * A class representing articles listed on Google Scholar.  The class
   provides basic dictionary-like behavior.
 */
public class ScholarArticle extends Article {
	
	@SuppressWarnings("rawtypes")
	public Map<String, Tuple> attrs = new HashMap<String, Tuple>();
	public String citationData = null;
	
	public ScholarArticle() {
		attrs.put("title",         createTuple(null, "Title",          0));
		attrs.put("url",           createTuple(null, "URL",            1));
		attrs.put("year",          createTuple(null, "Year",           2));
		attrs.put("num_citations", createTuple(0,    "Citations",      3));
		attrs.put("num_versions",  createTuple(0,    "Versions",       4));
		attrs.put("cluster_id",    createTuple(null, "Cluster ID",     5));
		attrs.put("url_pdf",       createTuple(null, "PDF link",       6));
		attrs.put("url_citations", createTuple(null, "Citations list", 7));
		attrs.put("url_versions",  createTuple(null, "Versions list",  8));
		attrs.put("url_citation",  createTuple(null, "Citation link",  9));
		attrs.put("excerpt",       createTuple(null, "Excerpt",       10));
	}
	
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

	
	public void setCitationData(String citationData) {
		this.citationData = citationData;
	}
	
	
}
