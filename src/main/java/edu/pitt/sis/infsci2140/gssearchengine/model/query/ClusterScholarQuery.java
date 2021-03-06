package edu.pitt.sis.infsci2140.gssearchengine.model.query;

import edu.pitt.sis.infsci2140.gssearchengine.utils.conf.ScholarConf;


public class ClusterScholarQuery extends ScholarQuery{

	private String cluster;
	private int num;
	private String ScholarClusterUrl;
	
	
	public ClusterScholarQuery(){
		cluster="";
		this.addAttributeType("num_results", 0, "Results");
		setCluster(cluster);
	}
	
	public ClusterScholarQuery(String cluster){
		this.cluster=cluster;
		this.addAttributeType("num_results", 0, "Results");
		setCluster(cluster);
	}
	

	//Sets search to a Google Scholar results cluster ID.
	public void setCluster(String cluster){
		
		this.cluster=cluster;
		
	}

	public String getUrl(){
		if(cluster==""||cluster==null){
			return "cluster query needs an exist cluster ID";
		}else{
			
			this.num=getNumOfResult();
			ScholarClusterUrl=ScholarConf.SCHOLAR_SITE + "/scholar?" 
			                  + "cluster="+this.cluster + "&num="+this.num;

			return ScholarClusterUrl;
		}
			
	}
	
}
