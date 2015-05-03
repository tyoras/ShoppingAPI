/**
 * 
 */
package yoan.shopping.infra.rest;

import static java.util.Objects.requireNonNull;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Hypermedia link
 * @author yoan
 */
@XmlRootElement(name = "link")
public class Link {
	public static final String SELF_REL = "self";
	
	private final String href;
    private final String rel;
    
    public Link() { 
    	href = null;
    	rel = null;
    }
    
    public Link(String rel, String href) {
        this.href = requireNonNull(href);
        this.rel = requireNonNull(rel);
    }

    @XmlElement(name = "href")
	public String getHref() {
		return href;
	}

    @XmlElement(name = "rel")
	public String getRel() {
		return rel;
	}
    
    public static Link self(String url) {
        return new Link(SELF_REL, url);
    }
}
