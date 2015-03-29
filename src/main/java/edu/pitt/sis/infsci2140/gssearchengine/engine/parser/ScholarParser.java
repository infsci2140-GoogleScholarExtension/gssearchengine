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
		doc = Jsoup.parse(HTML);
	}
	
	private Document doc;
	private ArrayList<ScholarArticle> articles;
	private String site=ScholarConf.SCHOLAR_SITE;
	//private Pattern year_re = Pattern.compile("\r\'\\\b(?:20|19)\\d{2}\\\b\'");
	

	public ArrayList<ScholarArticle> getArticle() {
		return articles;
	}

	
	public ArrayList<ScholarArticle> Parse()
	{		
		if(getResultNum()>0)
		{
			for(Element gs_r : doc.select("div.gs_r"))
			{
				ScholarArticle article = new ScholarArticle();
				getArticleInfo(gs_r,article);
				articles.add(article);
			}
			return articles;
		}
		else
			return null;
		
	}
	
	private void getArticleInfo(Element gs_r,ScholarArticle article)
	{
		String[] articleInfo = new String[11];
		String title="";
		String url="";
		String url_pdf="";
		String excerpt = "";
		String url_citation="";
		String cluster_id="";
		String url_versions="";
		int versionNum=0;
		int citationNum=0;
		int year=0;
		
		if(!gs_r.select("h3.gs_rt").select("a").isEmpty())
		{
			url = gs_r.select("h3.gs_rt").select("a").attr("href");
			title = gs_r.select("h3.gs_rt").select("a").text();
		}
		else
		{
			gs_r.select("h3.gs_rt").select("span").remove();
			title = gs_r.select("h3.gs_rt").text();
		}
		
		String years=gs_r.select("div.gs_a").text().split("-")[1];
		if(years.trim().contains(" "))
		{
			String[] y = years.split(" ");
			year = Integer.valueOf(y[y.length-1]);
		}
		else
			year = Integer.valueOf(years);
		
		if(gs_r.select("div").first().className().contains("gs_ri"))
			url_pdf="";
		else
			url_pdf=gs_r.select("div").first().select("a[href]").attr("href");
			
		excerpt = gs_r.select("div.gs_rs").text();
		
		if(gs_r.select("div.gs_fl").select("a").first().attr("href").contains("scholar?cites="))
		{
			String citedUrl = gs_r.select("div.gs_fl").select("a").first().attr("href");
			if(citedUrl.startsWith("/"))
				url_citation = site + '/' + citedUrl;
			else
				url_citation = citedUrl;
			
			cluster_id=url_citation.split("?")[1].substring(6);
			
			String[] citedNum = gs_r.select("div.gs_fl").select("a").first().text().trim().split(" ");
			citationNum = Integer.valueOf( citedNum[citedNum.length-1] );	
		}
		
		if(gs_r.select("div.gs_fl").select("a.gs_nph").first().attr("href").contains("scholar?cluster="))
		{
			String versionUrl =  gs_r.select("div.gs_fl").select("a.gs_nph").first().attr("href");
			if(versionUrl.startsWith("/"))
				url_versions = site + '/' + versionUrl;
			else
				url_versions = versionUrl;
			
			String[] verNum = gs_r.select("div.gs_fl").select("a.gs_nph").first().text().trim().split(" ");
			versionNum = Integer.valueOf(verNum[1]);
		}

	}
	
	private int getResultNum()
	{
		int resultNum=0;
		String gs_ab_md = doc.select("#gs_ab_md").text();
		
		if (!gs_ab_md.contains("result"))
			resultNum=0;
		else
		{
			String Num = gs_ab_md.split(" ")[1];
			Num=Num.replace(",", "");
			resultNum = Integer.valueOf(Num);
		}
		return resultNum;
	}
	

	
	
	

}
