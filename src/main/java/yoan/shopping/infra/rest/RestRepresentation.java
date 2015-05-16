package yoan.shopping.infra.rest;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Lists;

/**
 * Basic representation with the minimum fields
 * @author yoan
 */
@XmlRootElement(name = "root")
public class RestRepresentation {
	
	protected final List<Link> links;
	
	public RestRepresentation() {
		links = Lists.newArrayList();
	}
	
	public RestRepresentation(List<Link> links) {
		this.links = links;
	}
	
	@XmlElementWrapper(name = "links")
	@XmlElement(name = "link")
	public List<Link> getLinks() {
		return links;
	}
	
}
