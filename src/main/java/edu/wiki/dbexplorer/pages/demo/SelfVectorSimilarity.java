package edu.wiki.dbexplorer.pages.demo;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;

import edu.wiki.api.concept.IConceptVector;
import edu.wiki.search.ESASearcher;
import edu.wiki.util.db.ConceptESAVectorQueryOptimizer;

public class SelfVectorSimilarity {
	@Property
	@Persist
	private Integer doc1;

	@Property
	@Persist
	private Integer doc2;

	@Property
	private Double similarity;

	public void setupRender() {
		if (doc1 != null && doc2 != null) {
			
			byte[] b1 = ConceptESAVectorQueryOptimizer.getInstance().doQuery(doc1);
			byte[] b2 = ConceptESAVectorQueryOptimizer.getInstance().doQuery(doc2);
			
			ESASearcher esa = new ESASearcher();
			if (b1 != null && b2 != null) {
				IConceptVector v1 = esa.getConceptESAVector(b1);
				IConceptVector v2 = esa.getConceptESAVector(b2);
				similarity = esa.getRelatedness(v1, v2);
			}
		}
	}
}
