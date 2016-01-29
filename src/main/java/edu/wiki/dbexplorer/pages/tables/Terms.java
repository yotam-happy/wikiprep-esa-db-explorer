package edu.wiki.dbexplorer.pages.tables;

import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;

import edu.wiki.api.concept.IConceptVector;
import edu.wiki.search.ESASearcher;
import edu.wiki.util.WikiprepESAdb;

public class Terms {
	@Property
	@PageActivationContext
	private String termName;

	@Property
	private boolean displayTermView;

	@Property
	private String termViewName;

	@Property
	private IConceptVector termViewVector;
	
	public void setupRender() {
		if (termName != null) {
			populateTermView();
		}
	}
	
	public Object onSuccessFromSearch()
    {
        return this;
    }

	public void populateTermView() {
		try{
			PreparedStatement pstmt = WikiprepESAdb.getInstance().getConnection()
					.prepareStatement("SELECT term, vector FROM idx WHERE term=?");
			pstmt.setBytes(1, termName.getBytes("UTF-8"));
			pstmt.execute();
			ResultSet rs = pstmt.getResultSet();
			if (!rs.next()) {
				displayTermView = false;
				return;
			}
			displayTermView = true;
			termViewName = new String(rs.getBytes(1), "UTF-8");
			ESASearcher searcher = new ESASearcher();
			termViewVector = searcher.getConceptESAVector(rs.getBytes(2));
		}catch(SQLException | UnsupportedEncodingException e){
			throw new RuntimeException(e);
		}
	}
}
