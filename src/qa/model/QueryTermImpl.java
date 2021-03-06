package qa.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class QueryTermImpl implements QueryTerm, Serializable {
	private String text;
	
	public QueryTermImpl(String text) {
		this.text = text;
	}

	@Override
	public String getText() {
		return text;
	}

}
