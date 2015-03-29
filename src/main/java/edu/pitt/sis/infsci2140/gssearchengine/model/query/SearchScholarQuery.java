package edu.pitt.sis.infsci2140.gssearchengine.model.query;

import edu.pitt.sis.infsci2140.gssearchengine.utils.conf.ScholarConf;


public class SearchScholarQuery extends ScholarQuery{

	private String scholarQueryUrl="";
	private String author="";
	private boolean includeCitations=true;
	private boolean includePatents=true;
	private String phrase="";
	private String pub="";
	private boolean scopeTitle=false;//If True, search in title only
	private int[] timeFrame= new int[2];//[0] is start time, [1] is end time
	private String words="";//The default search behavior
	private String someWords="";//At least one of those word
	private String noneWords="";
	
	/*
	 * This version represents the search query parameters the user can
     * configure on the Scholar website, in the advanced search options.
	 */
	public SearchScholarQuery(){
		this.addAttributeType("num_results", 0, "Results");
		
	}
	
	//Sets words that *all* must be found in the result.
	public void setWords(String words){
		this.words=words;
	}
	
	//Sets words of which *at least one* must be found in result
	public void setSomeWords(String someWords){
		this.someWords=someWords;
	}
	
	//Sets words of which *none* must be found in the result.
	public void setNoneWords(String noneWords){
		this.noneWords=noneWords;
	}
	
	//Sets phrase that must be found in the result exactly
	public void setPhrase(String phrase){
		this.phrase=phrase;
	}
	
	//Sets Boolean indicating whether to search entire article or title only
	public void setScope(boolean titleOnly){
		this.scopeTitle=titleOnly;
	}
	
	//Sets names that must be on the result's author list
	public void setAuthor(String author){
		this.author=author;
	}
	
	//Sets the publication in which the result must be found
	public void setPub(String pub){
		this.pub=pub;
	}
	
	
	//Sets timeframe (in years as integer) in which result must have appeared.
    //It's fine to specify just start or end, or both.
	public void setTimeframe(int start, int end){
		if(start>0){
			
			this.timeFrame[0]=start;
		}else{
			this.timeFrame[0]=0;
		}
		
		if(end>0){
			
			this.timeFrame[1]=end;
		}else{
			this.timeFrame[1]=0;
		}
		
	}
	
	//Sets Boolean indicating whether to search include the citations
	public void setIncludeCitations(boolean include){
		this.includeCitations=include;
		
	}
	
	//Sets Boolean indicating whether to search include the patents
	public void setIncludePatents(boolean include){
		this.includePatents=include;
	}
	
	
	public String getUrl(){
		if(words==""&&pub==""&&author==""&&someWords==""
		   &&noneWords==""&&timeFrame[0]==0&&timeFrame[1]==0&&phrase==""){
			
			return "search query needs more parameters";
		}
		
		/*
		 * If we have some-words or none-words lists, we need to
		 * process them so GS understands them. For simple
		 * space-separeted word lists, there's nothing to do. For lists
		 * of phrases we have to ensure quotations around the phrases,
		 * separating them by whitespace.
		 */
		if(someWords!=null){
			this.someWords=this.parenthesizePhrases(someWords);
		}
		
		if(noneWords!=null){
			this.noneWords=this.parenthesizePhrases(noneWords);
		}
		
		String title="any";
		if(this.scopeTitle){
			title="title";
		}else{
			title="any";
		}
		int citations=1;
		if(this.includeCitations){
			citations=0;
		}else{
			citations=1;
		}
		
		int patent=1;
		if(this.includePatents){
			patent=0;
		}else{
			patent=1;
		}
		
		String ylo="";
		if(this.timeFrame[0]!=0){
			ylo=String.valueOf(this.timeFrame[0]);
		}
		
		String yhi="";
		if(this.timeFrame[1]!=0){
			yhi=String.valueOf(this.timeFrame[1]);
		}
		
		int num=0;
		if(this.getNumOfResult()<=ScholarConf.MAX_PAGE_RESULTS){
			num=this.getNumOfResult();
		}else{
			num=ScholarConf.MAX_PAGE_RESULTS;
		}
		
		scholarQueryUrl=ScholarConf.SCHOLAR_SITE + "/scholar?" 
				        +"as_q="+this.words+"&as_epq="+this.phrase
				        +"&as_oq="+this.someWords+"&as_eq="+this.noneWords
				        +"&as_occt="+title+"&as_sauthors="+this.author
				        +"&as_publication="+this.pub+"&as_ylo="+ylo
				        +"&as_yhi="+yhi+"&as_sdt="+patent
				        +"&as_vis="+citations+"&btnG=&hl=en'"+"&num="+num;
		
		return scholarQueryUrl;
	}
	
}
