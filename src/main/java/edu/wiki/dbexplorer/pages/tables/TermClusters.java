package edu.wiki.dbexplorer.pages.tables;

import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;

import edu.wiki.api.concept.IConceptVector;
import edu.wiki.search.ESASearcher;
import edu.wiki.util.WikiprepESAdb;

public class TermClusters {
	@Property
	@PageActivationContext
	private Integer clusterId;

	@Property
	private boolean displayTermView;

	@Property
	private IConceptVector centroid;
	
	@Property
	private Map<String,Double> termDistances;
	
	public void setupRender() {
		if (clusterId != null) {
			populateTermView();
			populateTermList();
		}
	}
	
	public Object onSuccessFromSearch()
    {
        return this;
    }

	public void populateTermView() {
		try{
			PreparedStatement pstmt = WikiprepESAdb.getInstance().getConnection()
					.prepareStatement("SELECT id, vector FROM terms_clusters WHERE id=?");
			pstmt.setInt(1, clusterId);
			pstmt.execute();
			ResultSet rs = pstmt.getResultSet();
			if (!rs.next()) {
				displayTermView = false;
				return;
			}
			displayTermView = true;
			ESASearcher searcher = new ESASearcher();
			centroid = searcher.getConceptESAVector(rs.getBytes(2));
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
	}

	public void populateTermList() {
		try{
			PreparedStatement pstmt = WikiprepESAdb.getInstance().getConnection()
					.prepareStatement("SELECT concept, distance FROM terms_document_cluster WHERE cluster=?");
			pstmt.setInt(1, clusterId);
			pstmt.execute();
			termDistances = new HashMap<String, Double>();
			
			ResultSet rs = pstmt.getResultSet();
			while(rs.next()){
				termDistances.put(new String(rs.getBytes(1), "UTF-8"), (double)rs.getFloat(2));
			}
			displayTermView = true;
		}catch(SQLException | UnsupportedEncodingException e){
			throw new RuntimeException(e);
		}
	}

	
}
