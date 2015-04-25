/**
 * 
 */
package yoan.shopping.infra.rest;

import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * Basic representation with the minimum fields
 * @author yoan
 */
public abstract class BasicRepresentation {
	//TODO ajouter self link + utilisation
	protected final List<Link> links;

	protected BasicRepresentation(List<Link> links) {
		this.links = links;
	}
	
	@XmlElementWrapper(name = "links")
	public List<Link> getLinks() {
		return links;
	}
}
