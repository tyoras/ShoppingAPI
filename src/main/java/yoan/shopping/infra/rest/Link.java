/**
 * 
 */
package yoan.shopping.infra.rest;

import java.net.URL;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Hypermedia link
 * @author yoan
 */
@XmlRootElement(name = "link")
public class Link {
	public static final String SELF_REL = "self";
	
	private final URL href;
    private final String rel;
    
    public Link(String rel, URL href) {
        this.href = Objects.requireNonNull(href);
        this.rel = Objects.requireNonNull(rel);
    }

    @XmlElement(name = "href")
	public URL getHref() {
		return href;
	}

    @XmlElement(name = "rel")
	public String getRel() {
		return rel;
	}
    
    public static Link self(URL url) {
        return new Link(SELF_REL, url);
    }
}
