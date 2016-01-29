package edu.wiki.dbexplorer.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

import edu.wiki.api.concept.IConceptIterator;
import edu.wiki.api.concept.IConceptVector;

public class ClusterVectorView {
	@Parameter(defaultPrefix=BindingConstants.PROP)
	@Property
	private IConceptVector vector;

	@Parameter(defaultPrefix=BindingConstants.LITERAL)
	@Property
	private IConceptVector table;
	
	@Property
	private VectorElement vectorElement;

	public List<VectorElement> getVectorElements() {
		List<VectorElement> res = new ArrayList<VectorElement>();
		
		IConceptIterator iter = vector.orderedIterator();
		int c = 0;
		while(iter.next() && c < 100) {
			res.add(new VectorElement(
					iter.getId(), 
					null,
					(float)iter.getValue()));
		}
		return res;
	}
	
	static public class VectorElement {
		int id;
		String title;
		float score;
		
		public VectorElement(int id, String title, float score) {
			this.id = id;
			this.score = score;
			this.title = title;
		}
		
		public int getId() {
			return id;
		}
		public float getScore() {
			return score;
		}
		public String getTitle() {
			return title;
		}
	}
}
