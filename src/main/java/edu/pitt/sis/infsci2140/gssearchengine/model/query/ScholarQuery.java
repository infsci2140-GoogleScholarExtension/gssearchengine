package edu.pitt.sis.infsci2140.gssearchengine.model.query;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public abstract class ScholarQuery extends Query{

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
	public static Map<String, Tuple> attr= new TreeMap<String, Tuple>(); 
	
	//The number of results requested from Scholar -- not the total number of results it reports
	private int NumOfResult;
	
	//setter for the number of page result
	public void setNumPageResult(int NumOfResult){
		this.NumOfResult=NumOfResult;
	}
	
	//getter for the number of page result
	public int getNumOfResult(){
		return NumOfResult;
	}
	
	//getter for thr url
	public abstract String getUrl();
	
	/*
	 * Adds a new type of attribute to the list of attributes understood by this query. 
     * Meant to be used by the constructors in derived classes.
	 */
	public void addAttributeType(String key, Object value, String tag){
		if(attr.size()==0){
			Tuple t=createTuple(value, tag, 0);
			attr.put(key, t);
		}else{
			Set<String> keys=attr.keySet();
			int max=0;
			for(String keyk:keys){
				if(max<attr.get(keyk).getOrderNum()){
					max=attr.get(keyk).getOrderNum();
				}
			}
			Tuple t=createTuple(value, tag, max+1);
			attr.put(key, t);
		}
		
	}
	
	
	//Getter for attribute ordering. Returns None if no such key
	public Object getOrderNum(String key){
		if(attr.containsKey(key)){
			return attr.get(key).getOrderNum();
		}
		return "none";
	}
	
	
	
	//Getter for attribute value. Returns None if no such key
	public Object getValue(String key){
		if(attr.containsKey(key)){
			return attr.get(key).getValue();
		}
		return "none";
	}
	
	
	//Setter for attribute value. Does nothing if no such key
	public void setValue(String key, Object v){
		if(attr.containsKey(key)){
			attr.get(key).setValue(v);
		}
	}
	
	
	/*
	 * Turns a query string containing comma-separated phrases into a
     * space-separated list of tokens, quoted if containing whitespace. 
     * For example, input 'some words, foo, bar' becomes '"some words" foo bar'
     * This comes in handy during the composition of certain queries.
	 */
	public String parenthesizePhrases(String query){
		if(!query.contains(",")){
			return query;
		}else{
			String[] phrase=query.split(",");
			StringBuilder sb =new StringBuilder();
			for(int i=0;i<phrase.length;i++){
				phrase[i]=phrase[i].trim();
				if(phrase[i].contains(" ")){
					phrase[i] = '"' + phrase[i] + '"';
				}
				sb.append(phrase[i]+" ");
			}
			return sb.toString().trim();
		}
		
	}
	

//	public static void main(String[] args){
//		ScholarQuery sq=new ScholarQuery();
//		System.out.println(sq.getValue("fuck"));
//		sq.addAttributeType("title", 0, "title");
//		sq.addAttributeType("cit", 0, "cit");
//		sq.setValue("title", "mubook");
//		System.out.println(sq.getValue("title"));
//		
//	}
}
