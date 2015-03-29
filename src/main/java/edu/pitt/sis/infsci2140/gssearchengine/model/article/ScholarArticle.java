package edu.pitt.sis.infsci2140.gssearchengine.model.article;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


public class ScholarArticle extends Article {

	//build a data structure to save the [value,label,ordering index]
		public class Tuple{
			private String tag;
			private Object value;
			private int num;
			
			//(1) the actual value, (2) a user-suitable label for the item, and (3) an ordering index:
			Tuple(Object v, String t, int n){
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
			
			public Object getValue(){
				return value;
			}
			
			public void setValue(Object v){
				value=v;
			}
			
			public int getOrderNum(){
				return num;
			}
			
			public void setOrderNum(int n){
				num=n;
			}
		}
		
		
		public Tuple createTuple(Object value, String tag, int num) {
			Tuple tuple = new Tuple(value, tag, num);
			return tuple;
		}
		
		//use treemap to achieve thr data scuture like attr(key,value(value, tag/label, ordering index))
		private static Map<String, Tuple> attr= new TreeMap<String, Tuple>(); 
		private String citationData;
		
		//A class representing articles listed on Google Scholar.  The class
	    //provides basic dictionary-like behavior.
		
		public ScholarArticle(){
			attr.put("title",createTuple("none","Title",0));
			attr.put("url",createTuple("none","URL",1));
			attr.put("year",createTuple("none","Year",2));
			attr.put("num_citations",createTuple(0,"Citations",3));
			attr.put("num_versions",createTuple(0,"Versions",4));
			attr.put("cluster_id",createTuple("none","Cluster ID",5));
			attr.put("url_pdf",createTuple("none","PDF link",6));
			attr.put("url_citations",createTuple("none","Citations list",7));
			attr.put("url_versions",createTuple("none","Versions list",8));
			attr.put("url_citation",createTuple("none","Citation link",9));
			attr.put("excerpt",createTuple("none","Excerpt",10));
			
			//The citation data in one of the standard export formats
			citationData="";
		}
		
		public Object getItem(String key){
			if(attr.containsKey(key)){
				return attr.get(key).getValue();
			}
			return "none";
		}
		
		public int getSize(){
			return attr.size();
		}
		
		public void setItem(String key, Object value){
			if(attr.containsKey(key)){
				attr.get(key).setValue(value);
			}else{
				attr.put(key, this.createTuple(value, key, this.getSize()));
			}
		}
		
		public void deleteItem(String key){
			if(attr.containsKey(key)){
				if(attr.get(key).getOrderNum()>10){
					attr.remove(key);
				}else{
					if(attr.get(key).getOrderNum()==3||attr.get(key).getOrderNum()==4){
						attr.get(key).setValue(0);
					}else{
						attr.get(key).setValue("none");
					}
					
				}
				
			}
		}
		
		public void setCitationData(String citationData){
			this.citationData=citationData;
		}
		
		public String asTxt(){
			Set<String> keys=attr.keySet();
			ArrayList<Tuple> t=new ArrayList<Tuple>();
			for(String keyk:keys){
				t.add(attr.get(keyk));
			}
			Collections.sort(t, new tupleCompare());
			
			int max_label_len=0;
			
			for(String keyk:keys){
				if(max_label_len<attr.get(keyk).getTag().length()){
					max_label_len=attr.get(keyk).getTag().length();
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
			Set<String> keys=attr.keySet();
			for(String keyk:keys){
				String[] pair=new String[2];
				pair[0]=keyk;
				pair[1]=String.valueOf(attr.get(keyk).getOrderNum());
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
				sb.append(attr.get(k[i]).getValue()+"|");
				sb.append("\n");
			}
			sb.deleteCharAt(sb.length()-2);
			
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
		
		public static void main(String[] args){
			ScholarArticle sa=new ScholarArticle();
			
			sa.setItem("title", "QUANT");
			sa.deleteItem("title");
			sa.setItem("title", "QUANT");
			sa.setItem("year", 1923);
			sa.deleteItem("year");
			sa.setItem("year", 1923);
			System.out.println(sa.asCsv(true));
		}
}
