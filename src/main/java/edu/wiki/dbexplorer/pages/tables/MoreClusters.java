package edu.wiki.dbexplorer.pages.tables;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;

import edu.wiki.api.concept.IConceptVector;
import edu.wiki.concept.TroveConceptVector;
import edu.wiki.search.ESASearcher;
import edu.wiki.util.WikiprepESAdb;
import edu.wiki.util.db.ClusterCentroidsQueryOptimizer;

public class MoreClusters {
	@Property
	@PageActivationContext
	private Integer clusterId;

	@Property
	private IConceptVector members;

	@Property
	private IConceptVector core;

	@Property
	private IConceptVector centroid;
	
	public void setupRender() {
		if (clusterId != null) {
			populateMembers();
			populateCore();
			populateCentroid();
		}
	}
	
	public Object onSuccessFromSearch()
    {
        return this;
    }

	public void populateMembers() {
		try{
			PreparedStatement pstmt = WikiprepESAdb.getInstance().getConnection()
					.prepareStatement("SELECT concept, distance FROM clusters_new WHERE cluster=?");
			pstmt.setInt(1, clusterId);
			pstmt.execute();
			members = new TroveConceptVector(100);
			ResultSet rs = pstmt.getResultSet();
			while(rs.next()){
				members.add(rs.getInt(1), -rs.getDouble(2));
			}
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
	}
	public void populateCore() {
		try{
			PreparedStatement pstmt = WikiprepESAdb.getInstance().getConnection()
					.prepareStatement("SELECT concept, distance FROM clusters_new_cores WHERE cluster=?");
			pstmt.setInt(1, clusterId);
			pstmt.execute();
			core = new TroveConceptVector(100);
			ResultSet rs = pstmt.getResultSet();
			while(rs.next()){
				core.add(rs.getInt(1), -rs.getDouble(2));
			}
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
	}
	public void populateCentroid() {
		ClusterCentroidsQueryOptimizer query = ClusterCentroidsQueryOptimizer.getInstance();
		ESASearcher esa = new ESASearcher();
		centroid = esa.getConceptESAVector(query.doQuery(clusterId));
	}
}
