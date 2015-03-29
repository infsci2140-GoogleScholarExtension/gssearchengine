package edu.pitt.sis.infsci2140.gssearchengine.engine.querier;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.CookieManager;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import edu.pitt.sis.infsci2140.gssearchengine.utils.conf.*;
import edu.pitt.sis.infsci2140.gssearchengine.engine.parser.ScholarParser;
import edu.pitt.sis.infsci2140.gssearchengine.model.article.*;
import edu.pitt.sis.infsci2140.gssearchengine.model.query.*;

/**
 * 
 * @author hongzhang
 *
 * ScholarQuerier instances can conduct a search on Google Scholar
   with subsequent parsing of the resulting HTML content.  The
   articles found are collected in the articles member, a list of
   ScholarArticle instances.
 */
public class ScholarQuerier extends Querier {
	
	private String GET_SETTINGS_URL = ScholarConf.SCHOLAR_SITE + "/scholar_settings?sciifh=1&hl=en&as_sdt=0,5";
	private String SET_SETTINGS_URL = ScholarConf.SCHOLAR_SITE  + "/scholar_setprefs?q="
																+ "&scisig=%(scisig)s"
																+ "&inststart=0"
																+ "&as_sdt=1,5"
																+ "&as_sdtp="
																+ "&num=%(num)s"
																+ "&scis=%(scis)s"
																+ "%(scisf)s"
																+ "&hl=en&lang=all&instq=&inst=569367360547434339&save=";
	
	private ArrayList<ScholarArticle> articles = new ArrayList<ScholarArticle>();
	private ScholarQuery query = null;
	private ScholarSettings settings = null;
	private HttpURLConnection connection = null;
	
	private CookieManager cookieManager = new CookieManager();
	
	
	public ScholarQuerier() {
		// If we have a cookie file, load it
	}
	
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
	
	
	/**
	 * apply settings
	 *
	 */
	public Boolean applySettings(ScholarSettings settings) {
		if (settings == null || !settings.isConfigured()) return true;
		
		this.settings = settings;
		
		//This is a bit of work. We need to actually retrieve the
        //contents of the Settings pane HTML in order to extract
        //hidden fields before we can compose the query for updating
        //the settings.
		
		String html = null;
		URL url = null;
		try {
			url = new URL(this.GET_SETTINGS_URL);
		} catch (MalformedURLException e) {
			ScholarUtils.log("erro", e.getMessage());
			return false;
		}
		html = this.getHttpResponse(url,
                "dump of settings form HTML",
                "requesting settings failed");
		
		if (html == null) return false;
		
		//Now parse the required stuff out of the form. We require the
        //"scisig" token to make the upload of our settings acceptable
        //to Google.
		Document doc = Jsoup.parse(html);
		
		Elements form = doc.select("form#gs_settings_form"); // find <form> with id 'gs_settings_form'
		if (form == null) {
			ScholarUtils.log("info", "parsing settings failed: no form");
			return false;
		}
		
		ScholarUtils.log("info", "settings applied");
		return true;
	}
	
	
	/**
	 * send query
	 * 
	 * This method initiates a search query (a ScholarQuery instance) 
	   with subsequent parsing of the response.
	 */
	public void sendQuery(ScholarQuery query) {
		
		this.clearArticles();
		this.query = query;
		
		String html = null;
		URL url = null;
		try {
			url = new URL(""); // query.get_url()
		} catch (MalformedURLException e) {
			ScholarUtils.log("erro", e.getMessage());
			return;
		}
		html = this.getHttpResponse(url,
                "dump of query response HTML",
                "results retrieval failed");
		if (html == null) return;
		
		this.parse(html);
	}
	
	
	/**
	 * add article
	 * 
	 */
	public void addArticle(ScholarArticle art) {
		this.getCitationData(art);
		this.articles.add(art);
	}
	
	/**
	 * clear articles
	 * 
	 * Clears any existing articles stored from previous queries
	 */
	public void clearArticles() {
		this.articles = new ArrayList<ScholarArticle>();
	}
	
	
	/**
	 * get citation data
	 * 
	 * Given an article, retrieves citation link. Note, this requires that
       you adjusted the settings to tell Google Scholar to actually
       provide this information, *prior* to retrieving the article.
	 */
	public Boolean getCitationData(ScholarArticle art) {
		
//		if article['url_citation'] is None:
//            return False
//        if article.citation_data is not None:
//            return True
		
		String data = null;
		ScholarUtils.log("info", "retrieving citation export data");
		
		URL url = null;
		try {
			url = new URL(""); // article['url_citation']
		} catch (MalformedURLException e) {
			ScholarUtils.log("erro", e.getMessage());
			return false;
		}
		data = this.getHttpResponse(url,
                "citation data response",
                "requesting citation data failed");
		if (data == null) return false;

		art.setCitationData(data);
		return true;
	}
	
	/**
	 * parse
	 * 
	 * This method allows parsing of provided HTML content
	 */
	public void parse(String html) {
		
		Parser parser = new Parser(this);
		parser.parse(html);
	}
	
	
	
	class Parser extends ScholarParser {
		
		private ScholarQuerier querier = null;
		
		public Parser(ScholarQuerier querier) {
			super();
			this.querier = querier;
		}
		
		public void parse(String html) {
			
		}
		
		public void handleNumResults(int num_results) {
			if (querier != null && querier.query != null)
				querier.query.setNumOfResult(num_results);
		}
		
		public void handleArticles(ScholarArticle art) {
			querier.addArticle(art);
		}
	}
	
	
	/**
	 * save cookies
	 * 
	 * This stores the latest cookies we're using to disk, for reuse in a
       later session.
	 */
	public Boolean saveCookies() {
		
		if (ScholarConf.COOKIE_JAR_FILE == null) return false;
		
		try {
			// this.cjar xxxxx
			
			ScholarUtils.log("info", "saved cookies file");
			return true;
		} catch (Exception e) {
			ScholarUtils.log("warn", "could not save cookies file: " + e.getMessage());
			return false;
		}
	}
	
	
	/**
	 * get http response
	 * 
	 * Helper method, sends HTTP request and returns response payload.
	 */
	public String getHttpResponse(URL url) {
		return getHttpResponse(url, null, null);
	}
	
	public String getHttpResponse(URL url, String log_msg, String err_msg) {

		log_msg = log_msg == null ? "HTTP response data follow" : log_msg;
		err_msg = err_msg == null ? "request failed"            : err_msg;
		
		String html = null;
		
		try {
			ScholarUtils.log("info", "requesting " + url.toString());
			
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("User-Agent", ScholarConf.USER_AGENT);
			
			BufferedReader in = new BufferedReader
                    (new InputStreamReader(connection.getInputStream(), "UTF-8"));
            
            StringBuilder strBuilder = new StringBuilder();
            String str = in.readLine();
            while (str != null) {
                strBuilder.append(str);
                str = in.readLine();
            }
            
            html = strBuilder.toString();
            
            ScholarUtils.log("debug", log_msg);
            ScholarUtils.log("debug", ">>>>" + '-'*68);
            ScholarUtils.log("debug", "url: " + connection.getURL());
            ScholarUtils.log("debug", "result: " + connection.getResponseCode());
//            ScholarUtils.log("debug", "headers:\n" + connection.getHeaderFields());
            ScholarUtils.log("debug", "data:\n" + html);
            ScholarUtils.log("debug", "<<<<" + '-'*68);
            
            in.close();
		} catch (Exception e) {
			ScholarUtils.log("info", err_msg + ": " + e);
		}
		
		return html;
	}
	
	
	
	
	
	
	
	
	
	
	
}
