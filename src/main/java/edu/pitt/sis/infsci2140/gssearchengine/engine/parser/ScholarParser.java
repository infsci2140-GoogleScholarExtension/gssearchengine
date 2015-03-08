package edu.pitt.sis.infsci2140.gssearchengine.engine.parser;
import org.jsoup.*;

import edu.pitt.sis.infsci2140.gssearchengine.model.article.*;
import edu.pitt.sis.infsci2140.gssearchengine.utils.conf.*;
//import java.util.regex.*;

public class ScholarParser extends Parser {
	
	private Jsoup soup;
	private ScholarArticle article;
	private String site=ScholarConf.SCHOLAR_SITE;
	//private Pattern year_re = Pattern.compile("\r\'\\\b(?:20|19)\\d{2}\\\b\'");
	
	public Jsoup getSoup() {
		return soup;
	}
	public void setSoup(Jsoup soup) {
		this.soup = soup;
	}
	public ScholarArticle getArticle() {
		return article;
	}
	public void setArticle(ScholarArticle article) {
		this.article = article;
	}
	

}
