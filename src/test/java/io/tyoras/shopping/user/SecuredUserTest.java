package io.tyoras.shopping.user;

import static io.tyoras.shopping.user.ProfileVisibility.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.Test;

import io.tyoras.shopping.user.SecuredUser;
import io.tyoras.shopping.user.User;

public class SecuredUserTest {
	@Test(expected = NullPointerException.class)
	public void securedUser_should_fail_without_Id() {
		//given
		UUID nullId = null;
		
		//when
		try {
			new SecuredUser(nullId, "name", "mail", PUBLIC, LocalDateTime.now(), LocalDateTime.now(), "Password", UUID.randomUUID());
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("User Id is mandatory");
			throw npe;
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void securedUser_should_fail_with_blank_name() {
		//given
		String blankName = "  ";
		
		//when
		try {
			new SecuredUser(UUID.randomUUID(), blankName, "mail", PUBLIC, LocalDateTime.now(), LocalDateTime.now(), "Password", UUID.randomUUID());
		} catch(IllegalArgumentException iae) {
		//then
			assertThat(iae.getMessage()).isEqualTo("Invalid user name");
			throw iae;
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void securedUser_should_fail_with_blank_password() {
		//given
		String blankPassword = "  ";
		
		//when
		try {
			new SecuredUser(UUID.randomUUID(), "name", "mail", PUBLIC, LocalDateTime.now(), LocalDateTime.now(), blankPassword, UUID.randomUUID());
		} catch(IllegalArgumentException iae) {
		//then
			assertThat(iae.getMessage()).isEqualTo("Invalid user password");
			throw iae;
		}
	}
	
	@Test(expected = NullPointerException.class)
	public void securedUser_should_fail_without_Salt() {
		//given
		UUID nullSalt = null;
		
		//when
		try {
			new SecuredUser(UUID.randomUUID(), "name", "mail", PUBLIC, LocalDateTime.now(), LocalDateTime.now(), "Password", nullSalt);
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("The password hash salt is mandatory");
			throw npe;
		}
	}
	
	@Test
	public void createFromUser_should_work() {
		//given
		User user = User.Builder.createDefault().withRandomId()
												.withEmail("mail")
												.withName("name")
												.build();
		String expectedPassword = "expected pass";
		UUID expectedSalt = UUID.randomUUID();
		
		//when
		SecuredUser result = SecuredUser.Builder.createFrom(user)
												.withPassword(expectedPassword)
												.withSalt(expectedSalt)
												.build();
		//then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(user.getId());
		assertThat(result.getName()).isEqualTo(user.getName());
		assertThat(result.getEmail()).isEqualTo(user.getEmail());
		assertThat(result.getPassword()).isEqualTo(expectedPassword);
		assertThat(result.getSalt()).isEqualTo(expectedSalt);
	}
}
