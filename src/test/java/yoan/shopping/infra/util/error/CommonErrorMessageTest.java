/**
 * 
 */
package yoan.shopping.infra.util.error;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class CommonErrorMessageTest {

	@Test
	public void getHumanReadableMessage_should_work_with_varargs() {
		//given
		CommonErrorMessage messageWithVarargs = CommonErrorMessage.INVALID;
		String expectedMessage = "Invalid thing";
		
		//when
		String result = messageWithVarargs.getDevReadableMessage("thing");
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(expectedMessage);
	}
	
	@Test
	public void getHumanReadableMessage_should_not_fail_with_varargs_message_without_param() {
		//given
		CommonErrorMessage messageWithVarargs = CommonErrorMessage.INVALID;
		String expectedMessage = "Invalid %s";
		
		//when
		String result = messageWithVarargs.getDevReadableMessage();
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(expectedMessage);
	}
}
