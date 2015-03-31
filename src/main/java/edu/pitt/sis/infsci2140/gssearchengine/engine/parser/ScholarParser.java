package edu.pitt.sis.infsci2140.gssearchengine.engine.parser;
import java.util.ArrayList;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


import edu.pitt.sis.infsci2140.gssearchengine.model.article.*;
import edu.pitt.sis.infsci2140.gssearchengine.utils.conf.*;
//import java.util.regex.*;

public class ScholarParser extends Parser {
	
	public ScholarParser(String HTML) 
	{
		super(HTML);
		doc = Jsoup.parse(HTML);  //set the document by HTML 
	}
	
	private Document doc;
	private ArrayList<ScholarArticle> articles = new ArrayList<ScholarArticle>();
	private String site=ScholarConf.SCHOLAR_SITE;
	//private Pattern year_re = Pattern.compile("\r\'\\\b(?:20|19)\\d{2}\\\b\'");
	

	public ArrayList<ScholarArticle> getArticle() {
		return articles;
	}

	
	public ArrayList<ScholarArticle> parse()
	{		
		if(getResultNum()>0)
		{
			for(Element gs_r : doc.select("div.gs_r")) // gs_r contains one result
			{
				if(gs_r.select("table[cellspacing]").size()==0)
				{
					ScholarArticle article = new ScholarArticle();
					getArticleInfo(gs_r,article);  //do parse the infomantion
					articles.add(article);
				}
			}
			if(articles.size()==0)
				articles=null;
			return articles;
		}
		else
			return null;
		
	}
	
	private void getArticleInfo(Element gs_r,ScholarArticle article)
	{
		String title="None";
		String url="None";
		String url_pdf="None";
		String excerpt = "None";   //ini the attribute of article
		String url_citations="None";
		String url_citation="None";
		String cluster_id="None";
		String url_versions="None";
		int versionNum=0;
		int citationNum=0;
		int year=0;
		
		if(!gs_r.select("h3.gs_rt").select("a").isEmpty())  // if there is an <a> 
		{
			url = gs_r.select("h3.gs_rt").select("a").attr("href");  //get the url from href
			title = gs_r.select("h3.gs_rt").select("a").text();    // get the title form the innerHTML
		}
		else
		{
			gs_r.select("h3.gs_rt").select("span").remove();  // if there is no <a>, get rid of <span>
			title = gs_r.select("h3.gs_rt").text();           //get the title
		}
		
		if(gs_r.select("div.gs_a").text().split("-").length>2)
		{
			String years=gs_r.select("div.gs_a").text().split("-")[1];  // split by -, gs_a contains year
			if(years.trim().contains(" "))    // if there is something else
			{
				String[] y = years.split(" ");  //get rid of them
				year = Integer.valueOf(y[y.length-1]);
			}
			else
				year = Integer.valueOf(years.trim());  //get the year
		}

		
		if(gs_r.select("div").first().className().contains("gs_ri"))  
			url_pdf="";
		else
			url_pdf=gs_r.select("div").first().select("a[href]").attr("href"); // if there is gs_ri which has the pdf link
			
		excerpt = gs_r.select("div.gs_rs").text();
		
		//System.out.println(gs_r.select("div.gs_fl").select("a").first().text());
		
		if(gs_r.select("div.gs_ri").select("div.gs_fl").select("a").first().attr("href").contains("scholar?cites=")) // if there is any citation
		{
			
			String citedUrl = gs_r.select("div.gs_ri").select("div.gs_fl").select("a").first().attr("href");
			if(citedUrl.startsWith("/"))
				url_citations = site + citedUrl;  //get the link
			else
				url_citations = citedUrl;
			
			cluster_id=url_citations.split("\\?")[1].split("\\&")[0].substring(6);  // get the id  from scholar?cites=xxxxxx
			
			String[] citedNum = gs_r.select("div.gs_ri").select("div.gs_fl").select("a").first().text().trim().split(" ");
			citationNum = Integer.valueOf( citedNum[citedNum.length-1] );	  // find the citation number from gs_fl
		}
		
		if(gs_r.select("div.gs_ri").select("div.gs_fl").select("a.gs_nph").first().attr("href").contains("scholar?cluster="))
		{
			String versionUrl =  gs_r.select("div.gs_ri").select("div.gs_fl").select("a.gs_nph").first().attr("href"); // get the version url
			if(versionUrl.startsWith("/"))
				url_versions = site + versionUrl;
			else
				url_versions = versionUrl;
			
			String[] verNum = gs_r.select("div.gs_ri").select("div.gs_fl").select("a.gs_nph").first().text().trim().split(" ");
			versionNum = Integer.valueOf(verNum[1]);  //get the version number from gs_fl which the first gs_nph class contains 
		}
		article.setItem("title", title);
		article.setItem("year", year);
		article.setItem("url", url);
		article.setItem("num_citations", citationNum);  //put information into article
		article.setItem("num_versions", versionNum);
		article.setItem("cluster_id", cluster_id);
		article.setItem("url_pdf",url_pdf);
		article.setItem("url_versions", url_versions);
		article.setItem("url_citation", url_citation);
		article.setItem("url_citations", url_citations);
		article.setItem("excerpt", excerpt);
		
	}
	
	private int getResultNum()
	{
		int resultNum=0;
//		String gs_ab_md = doc.select("#gs_ab_md").text();  //the gs_ab_md contains the result information
//		
//		if (!gs_ab_md.contains("result"))
//			resultNum=0;
//		else
//		{
//			
//			String Num = gs_ab_md.split(" ")[0];  //get the result number
//			System.out.println(Num);
//			Num=Num.replace(",", "");
//			resultNum = Integer.valueOf(Num);
//		}
		resultNum=doc.select("div.gs_r").size();

		
		return resultNum;
	}
	

	
	
	

}
