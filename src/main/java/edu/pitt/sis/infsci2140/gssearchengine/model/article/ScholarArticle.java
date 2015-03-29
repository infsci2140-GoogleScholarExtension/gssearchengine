package edu.pitt.sis.infsci2140.gssearchengine.model.article;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;




/**
 * 
 * @author hongzhang
 *
 * A class representing articles listed on Google Scholar.  The class
   provides basic dictionary-like behavior.
 */

public class ScholarArticle extends Article {
	
	//build a data structure to save the [value,label,ordering index]
		public class Tuple<ContentType>{
			private String tag;
			private ContentType value;
			private int num;
			
			//(1) the actual value, (2) a user-suitable label for the item, and (3) an ordering index:
			Tuple(ContentType v, String t, int n){
				value=v;
				tag=t;
				num=n;
			}
			
			public String getTag(){
				return tag;
			}
			
			public void setTag(String s){
				tag=s;
			}
			
			public ContentType getValue(){
				return value;
			}
			
			public void setValue(ContentType v){
				value=v;
			}
			
			public int getOrderNum(){
				return num;
			}
			
			public void setOrderNum(int n){
				num=n;
			}
		}
		
		
		public Tuple<Integer> createTuple(int value, String tag, int num) {
			Tuple<Integer> tuple = new Tuple<Integer>(value, tag, num);
			return tuple;
		}
		
		public Tuple<String> createTuple(String value, String tag, int num) {
			Tuple<String> tuple = new Tuple<String>(value, tag, num);
			return tuple;
		}
		
		//use treemap to achieve thr data scuture like attr(key,value(value, tag/label, ordering index))
		@SuppressWarnings("rawtypes")
		private  Map<String, Tuple> attrs= new TreeMap<String, Tuple>(); 
		private String citationData;
		
		//A class representing articles listed on Google Scholar.  The class
	    //provides basic dictionary-like behavior.
		
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
		
		public Object getItem(String key){
			if(attrs.containsKey(key)){
				return attrs.get(key).getValue();
			}
			return "none";
		}
		
		public int getSize(){
			return attrs.size();
		}
		
		@SuppressWarnings("unchecked")
		public void setItem(String key, int value){
			if(attrs.containsKey(key)){
				attrs.get(key).setValue(value);
			}else{
				attrs.put(key, this.createTuple(value, key, this.getSize()));
			}
		}
		
		@SuppressWarnings("unchecked")
		public void setItem(String key, String value){
			if(attrs.containsKey(key)){
				attrs.get(key).setValue(value);
			}else{
				attrs.put(key, this.createTuple(value, key, this.getSize()));
			}
		}
		@SuppressWarnings("unchecked")
		public void deleteItem(String key){
			if(attrs.containsKey(key)){
				if(attrs.get(key).getOrderNum()>10){
					attrs.remove(key);
				}else{
					if(attrs.get(key).getOrderNum()==3||attrs.get(key).getOrderNum()==4){
						attrs.get(key).setValue(0);
					}else{
						attrs.get(key).setValue("none");
					}
					
				}
				
			}
		}
		
		public void setCitationData(String citationData){
			this.citationData=citationData;
		}
		
		@SuppressWarnings("rawtypes")
		public String asTxt(){
			Set<String> keys=attrs.keySet();
			ArrayList<Tuple> t=new ArrayList<Tuple>();
			for(String keyk:keys){
				t.add(attrs.get(keyk));
			}
			Collections.sort(t, new tupleCompare());
			
			int max_label_len=0;
			
			for(String keyk:keys){
				if(max_label_len<attrs.get(keyk).getTag().length()){
					max_label_len=attrs.get(keyk).getTag().length();
				}
			}
			StringBuilder sb=new StringBuilder();
			
			for(int i=0;i<t.size();i++){
				if(t.get(i).getValue()!="none"){
					sb.append(t.get(i).getTag()+createSpace(max_label_len-t.get(i).getTag().length()));
					sb.append(t.get(i).getValue());
					sb.append("\n");
				}
			}
			return sb.toString();
		}
		
		public String asCsv(boolean header){
			
			ArrayList<String[]> list=new ArrayList<String[]>();
			Set<String> keys=attrs.keySet();
			for(String keyk:keys){
				String[] pair=new String[2];
				pair[0]=keyk;
				pair[1]=String.valueOf(attrs.get(keyk).getOrderNum());
				list.add(pair);
				
			}
			
			Collections.sort(list, new listCompare());
			String[] k=new String[list.size()];
			for(int i=0;i<list.size();i++){
				k[i]=list.get(i)[0];
			}
			
			StringBuilder sb=new StringBuilder();
			if(header){
				for(int i=0;i<k.length;i++){
					sb.append(k[i]+"|");
				}
				sb.deleteCharAt(sb.length()-1);
				sb.append("\n");
			}
			
			
			for(int i=0;i<k.length;i++){
				sb.append(attrs.get(k[i]).getValue()+"|");
				
			}
			sb.deleteCharAt(sb.length()-1);
			
			return sb.toString();
		}
		
		/*
		 * Reports the article in a standard citation format. This works only
         * if you have configured the querier to retrieve a particular
         * citation export format. (See ScholarSettings.)
		 */
		public String asCitation(){
			if(this.citationData!=""){
				return this.citationData;
			}
			return "";
		}
		
		
		//use to create whitespace
		private String createSpace(int n){
			String s="";
			for(int i=0;i<n;i++){
				s=s+" ";
			}
			return s;
		}
		
		
		//use to compare tuple
		@SuppressWarnings("rawtypes")
		private class tupleCompare implements Comparator<Tuple>{
			public int compare(Tuple r1, Tuple r2) {
				if(r1.getOrderNum()>r2.getOrderNum()){
					return 1;
				}else if(r1.getOrderNum()<r2.getOrderNum()){
					return -1;
				}else{
				    return 0;
				}
			}
			
		}
		
		
		private class listCompare implements Comparator<String[]>{
			public int compare(String[] r1, String[] r2) {
				if(r1[1].compareTo(r2[1])>0){
					return 1;
				}else if(r1[1].compareTo(r2[1])<0){
					return -1;
				}else{
				    return 0;
				}
			}
			
		}
		


}
