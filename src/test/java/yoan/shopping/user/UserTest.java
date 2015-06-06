package yoan.shopping.user;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.Test;

public class UserTest {

	@Test(expected = NullPointerException.class)
	public void user_should_fail_without_Id() {
		//given
		UUID nullId = null;
		
		//when
		try {
			new User(nullId, "name", "mail");
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("User Id is mandatory");
			throw npe;
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void user_should_fail_with_blank_name() {
		//given
		String blankName = "  ";
		
		//when
		try {
			new User(UUID.randomUUID(), blankName, "mail");
		} catch(IllegalArgumentException iae) {
		//then
			assertThat(iae.getMessage()).isEqualTo("Invalid user name");
			throw iae;
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void user_should_fail_with_blank_mail() {
		//given
		String blankMail = "  ";
		
		//when
		try {
			new User(UUID.randomUUID(), "name", blankMail);
		} catch(IllegalArgumentException iae) {
		//then
			assertThat(iae.getMessage()).isEqualTo("Invalid user email");
			throw iae;
		}
	}
	
}
