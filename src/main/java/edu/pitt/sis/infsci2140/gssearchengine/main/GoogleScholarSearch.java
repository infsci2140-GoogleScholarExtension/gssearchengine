package edu.pitt.sis.infsci2140.gssearchengine.main;

import java.util.ArrayList;

import edu.pitt.sis.infsci2140.*;
import edu.pitt.sis.infsci2140.gssearchengine.engine.querier.ScholarQuerier;
import edu.pitt.sis.infsci2140.gssearchengine.model.article.ScholarArticle;
import edu.pitt.sis.infsci2140.gssearchengine.model.query.ClusterScholarQuery;
import edu.pitt.sis.infsci2140.gssearchengine.model.query.SearchScholarQuery;

public class GoogleScholarSearch {
	public static void main(String[] args) 
	{
//		String author = args[0];
//		String words = args[0];
//		String someWords = args[2];
//		String phrase = args[3];
//		String words="Tunable emission based on lanthanide (III) metal¨Corganic frameworks: an alternative approach to white light";
		String cluster="17749203648027613321";
		
//		SearchScholarQuery query = new SearchScholarQuery();
		ClusterScholarQuery query = new ClusterScholarQuery();
//		query.setAuthor(author);
//		query.setPhrase(phrase);
//		query.setSomeWords(someWords);
//		query.setWords(words);
		query.setCluster(cluster);
		query.setNumPageResult(2);
		System.out.println(query.getUrl());
		ScholarQuerier querier = new ScholarQuerier();
		querier.applySettings(null);
		ArrayList<ScholarArticle> articles = querier.sendQuery(query);
		if(articles != null)
			System.out.println(articles.get(0).asTxt());
		else
			System.out.println("No result!");
		
	}
	
	
}
