package edu.wiki.dbexplorer.pages.tables;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;

import edu.wiki.api.concept.IConceptVector;
import edu.wiki.search.ESASearcher;
import edu.wiki.util.WikiprepESAdb;

public class Concepts {
	@Property
	@PageActivationContext
	private Integer conceptId;

	@Property
	private boolean displayConceptView;

	@Property
	private IConceptVector vector;

	@Property
	private Integer cluster;
	
	public void setupRender() {
		if (conceptId != null) {
			populateConceptView();
			populateClusterList();
		}
	}
	
	public Object onSuccessFromSearch()
    {
        return this;
    }

	public void populateConceptView() {
		try{
			PreparedStatement pstmt = WikiprepESAdb.getInstance().getConnection()
					.prepareStatement("SELECT id, vector FROM concept_esa_vectors WHERE id=?");
			pstmt.setInt(1, conceptId);
			pstmt.execute();
			ResultSet rs = pstmt.getResultSet();
			if (!rs.next()) {
				displayConceptView = false;
				return;
			}
			displayConceptView = true;
			ESASearcher searcher = new ESASearcher();
			vector = searcher.getConceptESAVector(rs.getBytes(2));
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
	}

	public void populateClusterList() {
		try{
			PreparedStatement pstmt = WikiprepESAdb.getInstance().getConnection()
					.prepareStatement("SELECT cluster FROM large_vectors_sampled_document_cluster WHERE concept=?");
			pstmt.setInt(1, conceptId);
			pstmt.execute();
			ResultSet rs = pstmt.getResultSet();
			if (rs.next()) {
				cluster = rs.getInt(1);
			}
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
	}
}
