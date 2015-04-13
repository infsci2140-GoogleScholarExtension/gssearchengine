package edu.pitt.sis.infsci2140.gssearchengine.engine.querier;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.CookieManager;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;

import javax.sound.midi.MidiDevice.Info;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import edu.pitt.sis.infsci2140.gssearchengine.utils.conf.*;
import edu.pitt.sis.infsci2140.gssearchengine.utils.error.FormatError;
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
																+ "&scisig=%s"
																+ "&inststart=0"
																+ "&as_sdt=1,5"
																+ "&as_sdtp="
																+ "&num=%d"
																+ "&scis=%s"
																+ "%s"
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
		
		Elements input = doc.select("input[type=hidden]").select("[name=\"scisig\"]");
		if (input == null) {
			ScholarUtils.log("info", "parsing settings failed: scisig");
			return false;
		}
		
		String scisig = input.first().val();
		int    num    = settings.getPer_page_results();
		String scis   = "no";
		String scisf  = "";

		if (settings.getCitform() != 0) {
			scis  = "yes";
			scisf = "&scisf=" + settings.getCitform();
		}
		
		
		try {
			String settingURL = String.format(SET_SETTINGS_URL, scisig, num, scis, scisf);
			url = new URL(settingURL);
		} catch (MalformedURLException e) {
			ScholarUtils.log("erro", e.getMessage());
			return false;
		}
		html = this.getHttpResponse(url,
                "dump of settings result HTML",
                "applying setttings failed");
        if (html == null) return false;
		
		ScholarUtils.log("info", "settings applied");
		return true;
	}
	
	
	/**
	 * send query
	 * 
	 * This method initiates a search query (a ScholarQuery instance) 
	   with subsequent parsing of the response.
	 */
	public ArrayList<ScholarArticle> sendQuery(ScholarQuery query) {
		
		this.clearArticles();
		this.query = query;
		
		String html = null;
		URL url = null;
		try {
			url = new URL(query.getUrl()); // query.get_url()
		} catch (MalformedURLException e) {
			ScholarUtils.log("erro", e.getMessage());
			return null;
		}
		html = this.getHttpResponse(url,
                "dump of query response HTML",
                "results retrieval failed");
		if (html == null) return null;
		
		return this.parse(html);
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
		
		if (art.getItem("url_citation") == null) {
			return false;
		}	
		if (this.settings == null || this.settings.getCitform() == 0) {
			return false;
		}
		if (art.getCitationData() != null) {
			return true;
		}
		
			
		
		String data = null;
		ScholarUtils.log("info", "retrieving citation export data");
		
		
		// get citation data div
		String citiHtml;
		URL url = null;
		try {
			String info = (String) art.getItem("url_citation");
			String CITIDIV = "https://scholar.google.com/scholar?q=info:" + info + ":scholar.google.com/&output=cite&scirp=0&hl=zh-CN"; 
			art.setItem("url_citation", CITIDIV);
			url = new URL(CITIDIV); // article['url_citation']
		} catch (MalformedURLException e) {
			ScholarUtils.log("erro", e.getMessage());
			return false;
		}
		citiHtml = this.getHttpResponse(url,
                "citation data response",
                "requesting citation data failed");
		if (citiHtml == null) return false;
		
		
		// get citation data
		url = null;
		try {
			int format = this.settings.getCitform();
			
			Document citiDoc = Jsoup.parse (citiHtml);
			
			String citiURL = citiDoc.select("#gs_citi").select("a.gs_citi").get(format-1).attr("href"); 
			
			url = new URL("https://scholar.google.com" + citiURL); // citation data url
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
	public ArrayList<ScholarArticle> parse(String html) {
		
		Parser parser = new Parser(this, html);
		return parser.parse();
	}
	
	
	
	class Parser extends ScholarParser {
		
		private ScholarQuerier querier = null;
		
		public Parser(ScholarQuerier querier,String html) {
			super(html);
			this.querier = querier;
		}
			
		public void handleNumResults(int num_results) {
			if (querier != null && querier.query != null)
				querier.query.setNumPageResult(num_results);
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
		return getHttpResponse(url, "", "");
	}
	
	public String getHttpResponse(URL url, String log_msg, String err_msg) {

		log_msg = log_msg == "" ? "HTTP response data follow" : log_msg;
		err_msg = err_msg == "" ? "request failed"            : err_msg;
		
		String html = null;
		
		try {
			ScholarUtils.log("info", "requesting " + url.toString());
			
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("User-Agent", ScholarConf.USER_AGENT);
			connection.setRequestProperty("Cookie", "PREF=ID=2c3b7553758d0a8a:U=982a5bd4ec9e589f:TM=1411619673:LM=1428025690:S=4CMZ0jJ2Asvz3-uT; NID=67=r6HK6aP8yM5bpe2tfQ9JXEwvEt39dR7mHw5vMRz0M9N6QD9fZj2zV0eYIy-qoKmALQirv4fDQPDffTpDxGYLUvQaxTdWbPEQMR9Q6ZivcKFoxEa1E-rhRVTHxMjFnvAlQ-JcD4Ri_ftA6ZN_mgcyxycSIWgpxHhs; GSP=ID=2c3b7553758d0a8a:LM=1428876023:S=rr2W5rGrPxqVyCBD; OGPC=5061492-1:");
			
			String cookie = connection.getRequestProperty("Cookie");
			System.out.println(cookie);
			
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
