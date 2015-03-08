package edu.pitt.sis.infsci2140.gssearchengine.utils.conf;

public class ScholarSettings {
	public static final int CITFORM_NONE = 0;
	public static final int	CITFORM_REFWORKS = 1;
	public static final int CITFORM_REFMAN = 2;
	public static final int CITFORM_ENDNOTE = 3;
	public static final int	CITFORM_BIBTEX = 4;
	
	private boolean isConfigured;
	private int citform = 0;
	private int per_page_results = 20;

}
