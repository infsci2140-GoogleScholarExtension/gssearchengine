package edu.pitt.sis.infsci2140.gssearchengine.utils.conf;

import edu.pitt.sis.infsci2140.gssearchengine.utils.error.FormatError;

/**
 * 
 * @author hongzhang
 *
 * This class lets you adjust the Scholar settings for your
   session. It's intended to mirror the features tunable in the
   Scholar Settings pane, but right now it's a bit basic.
 */
public class ScholarSettings {
	public static final int CITFORM_NONE = 0;
	public static final int	CITFORM_REFWORKS = 4;
	public static final int CITFORM_REFMAN = 3;
	public static final int CITFORM_ENDNOTE = 2;
	public static final int	CITFORM_BIBTEX = 1;
	
	private boolean isConfigured = false;
	private int citform = 0;
	private int per_page_results = ScholarConf.MAX_PAGE_RESULTS;

	public Boolean isConfigured() {
		return this.isConfigured;
	}
	
	public void setCitationFormat(int citform) throws FormatError {
		if (citform < 0 || citform > ScholarSettings.CITFORM_REFWORKS)
			throw new FormatError("citation format invalid, is " + citform);
		this.citform = citform;
		this.isConfigured = true;
	}

	public int getCitform() {
		return citform;
	}

	public int getPer_page_results() {
		return per_page_results;
	}

	public void setPerPageResults(int perPageResults) {
		this.per_page_results = Math.min(perPageResults, ScholarConf.MAX_PAGE_RESULTS);
		this.isConfigured = true;
	}

}
