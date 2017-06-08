package yoan.shopping.infra.rest;

import static java.util.Objects.requireNonNull;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

/**
 * Basic representation with the minimum fields
 * @author yoan
 */
public class RestRepresentation {
	/** Rest navigation links */
	protected final List<Link> links;
	
	public RestRepresentation() {
		links = Lists.newArrayList();
	}
	
	public RestRepresentation(List<Link> links) {
		this.links = requireNonNull(links, "A rest representation should always have navigation links");
	}
	
	@JsonProperty("links")
	public List<Link> getLinks() {
		return links;
	}
	
}
