package yoan.shopping.user.representation;

import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;

import io.swagger.annotations.ApiModel;

/**
 * User with security infos Rest Representation
 * @author yoan
 */
@ApiModel(value = "Secured user write")
public class SecuredUserWriteRepresentation extends UserWriteRepresentation {
	/** User password */
	private String password;
	
	public SecuredUserWriteRepresentation() {
		super();
	}
	
	/** Test Purpose only */
	@Deprecated 
	public SecuredUserWriteRepresentation(UUID id, String name, String email, String password) {
		super(id, name, email);
		this.password = password;
	}
	
	@XmlElement(name = "password")
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
}
