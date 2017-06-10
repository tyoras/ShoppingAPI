package io.tyoras.shopping.user;

import static io.tyoras.shopping.user.ProfileVisibility.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.Test;

import io.tyoras.shopping.user.ProfileVisibility;
import io.tyoras.shopping.user.User;

public class UserTest {

	@Test(expected = NullPointerException.class)
	public void user_should_fail_without_Id() {
		//given
		UUID nullId = null;
		
		//when
		try {
			new User(nullId, "name", "mail", PUBLIC, LocalDateTime.now(), LocalDateTime.now());
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
			new User(UUID.randomUUID(), blankName, "mail", PUBLIC, LocalDateTime.now(), LocalDateTime.now());
		} catch(IllegalArgumentException iae) {
		//then
			assertThat(iae.getMessage()).isEqualTo("Invalid user name");
			throw iae;
		}
	}
	
	@Test(expected = NullPointerException.class)
	public void user_should_fail_with_null_profile_visibility() {
		//given
		ProfileVisibility nullProfileVisibility = null;
		
		//when
		try {
			new User(UUID.randomUUID(), "name", "mail", nullProfileVisibility, LocalDateTime.now(), LocalDateTime.now());
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("Profile visibility is mandatory");
			throw npe;
		}
	}
	
	@Test(expected = NullPointerException.class)
	public void user_should_fail_without_creation_date() {
		//given
		LocalDateTime nullDate = null;
		
		//when
		try {
			new User(UUID.randomUUID(), "name", "mail", PUBLIC, nullDate, LocalDateTime.now());
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("Creation date is mandatory");
			throw npe;
		}
	}
	
	@Test(expected = NullPointerException.class)
	public void user_should_fail_without_last_update_date() {
		//given
		LocalDateTime nullDate = null;
		
		//when
		try {
			new User(UUID.randomUUID(), "name", "mail", PUBLIC, LocalDateTime.now(), nullDate);
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("Last update date is mandatory");
			throw npe;
		}
	}
}
