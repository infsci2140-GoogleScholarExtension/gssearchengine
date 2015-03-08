package edu.pitt.sis.infsci2140.gssearchengine.model.query;


public class SearchScholarQuery extends ScholarQuery{

	private String scholarQueryUrl;
	private String author;
	private boolean includeCitations=true;
	private boolean includePatents=true;
	private String phrase;
	private int pub;
	private boolean scopeTitle=false;//If True, search in title only
	private String[][] timeFrame;
	private String words;//The default search behavior
	private String someWords;//At least one of those word
	private String noneWords;
}
