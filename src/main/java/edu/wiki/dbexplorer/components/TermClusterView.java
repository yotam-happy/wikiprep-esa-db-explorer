package edu.wiki.dbexplorer.components;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class TermClusterView {
	@Parameter(defaultPrefix=BindingConstants.PROP)
	@Property
	private Map<String,Double> termDistances;

	@Property
	private Entry<String,Double> term;
	
	public List<Entry<String,Double>> getVectorElements() {
		return termDistances.entrySet().stream().sorted(new Comparator<Entry<String,Double>>() {

			@Override
			public int compare(Entry<String,Double> o1, Entry<String,Double> o2) {
				return Double.compare(o1.getValue(), o2.getValue());
			}
		}).collect(Collectors.toList());
	}
}
