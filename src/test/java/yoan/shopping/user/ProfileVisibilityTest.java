package yoan.shopping.user;

import static org.assertj.core.api.Assertions.assertThat;
import static yoan.shopping.user.ProfileVisibility.PUBLIC;

import org.junit.Test;

public class ProfileVisibilityTest {
	
	@Test
	public void valueOfOrNull_should_return_null_with_invalid_code() {
		//given
		String invalidCode = "invalid";
		
		//when
		ProfileVisibility result = ProfileVisibility.valueOfOrNull(invalidCode);
		
		//then
		assertThat(result).isNull();
	}
	
	@Test
	public void valueOfOrNull_should_work_with_valid_code() {
		//given
		String validCode = "PUBLIC";
		
		//when
		ProfileVisibility result = ProfileVisibility.valueOfOrNull(validCode);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(PUBLIC);
	}

}
