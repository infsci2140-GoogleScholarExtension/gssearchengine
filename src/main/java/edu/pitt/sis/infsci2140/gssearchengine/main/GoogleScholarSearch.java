package edu.pitt.sis.infsci2140.gssearchengine.main;

import java.util.ArrayList;

import edu.pitt.sis.infsci2140.*;
import edu.pitt.sis.infsci2140.gssearchengine.engine.querier.ScholarQuerier;
import edu.pitt.sis.infsci2140.gssearchengine.model.article.ScholarArticle;
import edu.pitt.sis.infsci2140.gssearchengine.model.query.ClusterScholarQuery;
import edu.pitt.sis.infsci2140.gssearchengine.model.query.SearchScholarQuery;
import edu.pitt.sis.infsci2140.gssearchengine.utils.conf.ScholarSettings;
import edu.pitt.sis.infsci2140.gssearchengine.utils.error.FormatError;

public class GoogleScholarSearch {
	public static void main(String[] args) 
	{
		String author = "albert einstein";
//		String words = args[0];
//		String someWords = args[2];
//		String phrase = args[3];
//		String words="Tunable emission based on lanthanide";
//		String cluster="17749203648027613321";
		
		SearchScholarQuery query = new SearchScholarQuery();
//		ClusterScholarQuery query = new ClusterScholarQuery();
		query.setAuthor(author);
//		query.setPhrase(phrase);
//		query.setSomeWords(someWords);
//		query.setWords(words);
//		query.setCluster(cluster);
		query.setNumPageResult(2);
		//System.out.println(query.getUrl());
		ScholarQuerier querier = new ScholarQuerier();
		ScholarSettings setting = new ScholarSettings();
		try {
			setting.setCitationFormat(3);
		} catch (FormatError e) {
			// 
			e.printStackTrace();
		}
		
		querier.applySettings(setting);
		
		ArrayList<ScholarArticle> articles = querier.sendQuery(query);
		querier.getCitationData(articles.get(0));
		
//		System.out.println(querier);
		System.out.println(articles.get(0).getCitationData());
		System.out.println("-------------------");
		if(articles != null)
			System.out.println(articles.get(0).asTxt());
		else
			System.out.println("No result!");
		
		
	}
	
	
}
