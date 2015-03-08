package edu.pitt.sis.infsci2140.gssearchengine.engine.querier;

import java.util.ArrayList;
import edu.pitt.sis.infsci2140.gssearchengine.utils.conf.*;
import edu.pitt.sis.infsci2140.gssearchengine.model.article.*;
import edu.pitt.sis.infsci2140.gssearchengine.model.query.*;

public class ScholarQuerier extends Querier {
	
	private String GET_SETTINGS_URL = ScholarConf.SCHOLAR_SITE + "/scholar_settings?sciifh=1&hl=en&as_sdt=0,5";
	private String SET_SETTINGS_URL = ScholarConf.SCHOLAR_SITE + "/scholar_setprefs?q=&scisig=";
	private ArrayList<ScholarArticle> articles = new ArrayList<ScholarArticle>();
	private ScholarQuery query=null;
	//may have a method to load cookie
	
	
	public String getGET_SETTINGS_URL() {
		return GET_SETTINGS_URL;
	}
	public void setGET_SETTINGS_URL(String gET_SETTINGS_URL) {
		GET_SETTINGS_URL = gET_SETTINGS_URL;
	}
	public String getSET_SETTINGS_URL() {
		return SET_SETTINGS_URL;
	}
	public void setSET_SETTINGS_URL(String sET_SETTINGS_URL) {
		SET_SETTINGS_URL = sET_SETTINGS_URL;
	}
	public ArrayList<ScholarArticle> getArticles() {
		return articles;
	}
	public void setArticles(ArrayList<ScholarArticle> articles) {
		this.articles = articles;
	}

}
