package edu.wiki.dbexplorer.pages.tables;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;

import edu.wiki.api.concept.IConceptIterator;
import edu.wiki.api.concept.IConceptVector;
import edu.wiki.concept.TroveConceptVector;
import edu.wiki.search.ESASearcher;
import edu.wiki.util.WikiprepESAdb;

public class Clusters {
	@Property
	@PageActivationContext
	private Integer clusterId;

	@Property
	private boolean displayTermView;

	@Property
	private IConceptVector centroid;
	
	@Property
	private IConceptVector memberConcepts;
	
	@Property IConceptVector combined;
	@Property IConceptVector tamed;
	
	public void setupRender() throws SQLException, ClassNotFoundException, IOException {
		if (clusterId != null) {
			populateTermView();
			populateConceptList();
			populateCombined();
			populateTamed();
		}
	}
	
	public Object onSuccessFromSearch()
    {
        return this;
    }

	public void populateTermView() throws SQLException, ClassNotFoundException, IOException {
		PreparedStatement pstmt = WikiprepESAdb.getInstance().getConnection()
				.prepareStatement("SELECT id, vector FROM large_vectors_sampled_clusters WHERE id=?");
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
	}

	public void populateConceptList() throws SQLException, ClassNotFoundException, IOException {
		
		PreparedStatement pstmt = WikiprepESAdb.getInstance().getConnection()
				.prepareStatement("SELECT concept, distance FROM large_vectors_sampled_document_cluster WHERE cluster=?");
		pstmt.setInt(1, clusterId);
		pstmt.execute();
		memberConcepts = new TroveConceptVector(100);
		ResultSet rs = pstmt.getResultSet();
		while(rs.next()){
			memberConcepts.add(rs.getInt(1), -rs.getDouble(2));
		}
		displayTermView = true;
	}

	public void populateTamed() {
		IConceptVector c = new TroveConceptVector(100);
		c.add(centroid);
		tamed = new TroveConceptVector(100);
		IConceptIterator it = c.iterator();
		while(it.next()) {
			if (-memberConcepts.get(it.getId()) > 0.00001) {
				tamed.add(it.getId(), it.getValue() * -memberConcepts.get(it.getId()));
			}
		}
	}
	public void populateCombined() throws ClassNotFoundException, IOException {
		IConceptVector c = new TroveConceptVector(100);
		c.add(centroid);
		ESASearcher s = new ESASearcher();
		c = s.getCombinedVector(c, 500);
		combined = new TroveConceptVector(100);
		IConceptIterator it = c.iterator();
		while(it.next()) {
			if (-memberConcepts.get(it.getId()) > 0.00001) {
				combined.add(it.getId(), it.getValue() * -memberConcepts.get(it.getId()));
			}
		}
	}
	
}
