package yoan.shopping.tmp;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "Pet")
public class Pet {
	private long id;
	private String name;
	private List<String> photoUrls = new ArrayList<String>();
	private String status;

	@XmlElement(name = "id")
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@XmlElement(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElementWrapper(name = "photoUrls")
	@XmlElement(name = "photoUrl")
	public List<String> getPhotoUrls() {
		return photoUrls;
	}

	public void setPhotoUrls(List<String> photoUrls) {
		this.photoUrls = photoUrls;
	}

	@XmlElement(name = "status")
	@ApiModelProperty(value = "pet status in the store", allowableValues = "available,pending,sold")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
