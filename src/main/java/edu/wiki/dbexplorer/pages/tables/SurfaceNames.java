package edu.wiki.dbexplorer.pages.tables;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;

import edu.wiki.api.concept.IConceptVector;
import edu.wiki.concept.TroveConceptVector;
import edu.wiki.index.WikipediaAnalyzer;
import edu.wiki.util.WikiprepESAdb;

public class SurfaceNames {
	@Property
	@PageActivationContext
	private String surfaceName;

	@Property
	private IConceptVector vector;

	public void setupRender() {
		if (surfaceName != null) {
			populateConceptView();
		}
	}
	
	public Object onSuccessFromSearch()
    {
        return this;
    }

	public String convertSurfaceName(String n) {
		StringBuffer res = new StringBuffer();
		WikipediaAnalyzer analyzer = new WikipediaAnalyzer();
		TokenStream ts = analyzer.tokenStream("contents",new StringReader(n));
    	res.append("$");
        try {
			while (ts.incrementToken()) { 
	            TermAttribute t = ts.getAttribute(TermAttribute.class);
	            res.append('_');
	            res.append(t.term());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
        analyzer.close();
		return res.toString();
	}
	public void populateConceptView() {
		try{
			PreparedStatement pstmt = WikiprepESAdb.getInstance().getConnection()
					.prepareStatement("SELECT concept_id FROM surface_names WHERE name=?");
			PreparedStatement pstmt2 = WikiprepESAdb.getInstance().getConnection()
					.prepareStatement("SELECT inlink FROM inlinks WHERE target_id=?");
			String s = convertSurfaceName(surfaceName);
			pstmt.setBytes(1, s.getBytes("UTF-8"));
			pstmt.execute();
			ResultSet rs = pstmt.getResultSet();
			if (!rs.next()) {
				vector = null;
				return;
			}
			vector = new TroveConceptVector(100);
			do {
				pstmt2.setInt(1, rs.getInt(1));
				pstmt2.execute();
				ResultSet rs2 = pstmt2.getResultSet();
				double k = 0;
				if (rs2.next()) {
					k = rs2.getInt(1);
				}
				vector.add(rs.getInt(1), k);
			} while(rs.next());
		}catch(SQLException | UnsupportedEncodingException e){
			throw new RuntimeException(e);
		}
	}
}
