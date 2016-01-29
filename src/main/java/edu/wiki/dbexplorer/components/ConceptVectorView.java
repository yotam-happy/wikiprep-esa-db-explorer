package edu.wiki.dbexplorer.components;

import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

import edu.wiki.api.concept.IConceptIterator;
import edu.wiki.api.concept.IConceptVector;
import edu.wiki.util.WikiprepESAdb;

public class ConceptVectorView {
	@Parameter(defaultPrefix=BindingConstants.PROP)
	@Property
	private IConceptVector vector;

	@Property
	private VectorElement vectorElement;

	public List<VectorElement> getVectorElements() {
		List<VectorElement> res = new ArrayList<VectorElement>();
		
		IConceptIterator iter = vector.orderedIterator();
		int c = 0;
		while(iter.next() && c < 100) {
			res.add(new VectorElement(
					iter.getId(), 
					getConceptTitle(iter.getId()),
					(float)iter.getValue()));
		}
		return res;
	}
	
	private String getConceptTitle(int id) {
		try{
		PreparedStatement pstmt = WikiprepESAdb.getInstance().getConnection()
				.prepareStatement("SELECT title FROM article WHERE id=?");
		pstmt.setInt(1, id);
		pstmt.execute();
		ResultSet rs = pstmt.getResultSet();
		if (rs.next()) {
			return new String(rs.getBytes(1), "UTF-8");
		}
		pstmt.close();
		} catch (SQLException | UnsupportedEncodingException e){
			throw new RuntimeException(e);
		}
		return "n/a";
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
