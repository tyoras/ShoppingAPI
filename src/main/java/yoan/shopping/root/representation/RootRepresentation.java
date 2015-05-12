/**
 * 
 */
package yoan.shopping.root.representation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import yoan.shopping.infra.rest.Link;
import yoan.shopping.infra.rest.RestRepresentation;
import yoan.shopping.root.BuildInfo;

import com.google.common.base.MoreObjects;

/**
 * Rest Root representation
 * @author yoan
 */
@XmlRootElement(name = "root")
public class RootRepresentation extends RestRepresentation {
	/** Current build version */
	private String version;
	/** Maven build date */
	private LocalDateTime buildDate;
	
	public RootRepresentation() {
		super();
		this.version = "version";
		this.buildDate = LocalDateTime.now();
	}
	
	public RootRepresentation(BuildInfo buildInfo, List<Link> links) {
		super(links);
		if (buildInfo != null) {
			this.version = buildInfo.getVersion();
			this.buildDate = buildInfo.getBuildDate();
		}
	}

	@XmlElement(name = "version")
	public String getVersion() {
		return version;
	}

	@XmlElement(name = "buildDate")
	public LocalDateTime getBuildDate() {
		return buildDate;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), version, buildDate);
	}

	@Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RootRepresentation that = (RootRepresentation) obj;
        return super.equals(obj) && Objects.equals(this.version, that.version)
                && Objects.equals(this.buildDate, that.buildDate);
    }
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("version", version)
											   .add("buildDate", buildDate)
											   .toString();
	}
}
