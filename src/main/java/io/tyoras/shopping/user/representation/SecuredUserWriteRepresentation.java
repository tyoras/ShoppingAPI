package io.tyoras.shopping.user.representation;

import io.swagger.annotations.ApiModel;

import javax.xml.bind.annotation.XmlElement;

/**
 * User with security infos Rest Representation
 *
 * @author yoan
 */
@ApiModel(value = "Secured user write")
public class SecuredUserWriteRepresentation extends UserWriteRepresentation {
    /**
     * User password
     */
    private String password;

    public SecuredUserWriteRepresentation() {
        super();
    }

    /**
     * Test Purpose only
     */
    @Deprecated
    public SecuredUserWriteRepresentation(String name, String email, String profileVisibility, String password) {
        super(name, email, profileVisibility);
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
