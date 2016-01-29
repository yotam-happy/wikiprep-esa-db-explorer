package edu.wiki.dbexplorer.pages.demo;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;

import edu.wiki.api.concept.IConceptVector;
import edu.wiki.search.ESASearcher;
import edu.wiki.search.ESASearcherClusters;

public class UsingClusters {
	@Property
	@Persist
	private String text;

	@Property
	private IConceptVector vector;

	@Property
	private IConceptVector clusterVector1;

	@Property
	private IConceptVector clusterVector2;

	public void setupRender() {
		if (text != null) {
//			ClusterCentroidsQueryOptimizer.getInstance().loadAll();

			ESASearcher searcher = new ESASearcher();
			vector = searcher.getConceptVector(text);
//			vector = searcher.getNormalVector(vector, 800);
			//ESASearcherClustersNew s2 = new ESASearcherClustersNew();
			ESASearcherClusters s3 = new ESASearcherClusters();
			clusterVector1 = s3.ClustersFeatureVectorByCentroid(vector);
			clusterVector2 = s3.ClustersFeatureVectorByMembership(vector, 2);
//			vector = s2.filterByClusters(new ArrayListConceptVector(vector), 100);
			vector = searcher.getCombinedVector(vector, 2000);
		}
	}
}
