package yoan.shopping.infra.util;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.fest.assertions.api.Assertions.assertThat;
import static yoan.shopping.infra.rest.error.Level.ERROR;
import static yoan.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static yoan.shopping.infra.util.error.CommonErrorMessage.INVALID;
import static yoan.shopping.test.TestHelper.assertWebApiException;

import java.util.UUID;

import org.junit.Test;

import yoan.shopping.infra.rest.error.WebApiException;

public class ResourceUtilTest {
	
	@Test
	public void getIdfromParam_should_not_fail_with_null_paramName_and_valid_param() {
		//given
		UUID expectedUUID = UUID.randomUUID();
		String validParam = expectedUUID.toString();
		
		//when
		UUID result = ResourceUtil.getIdfromParam(null, validParam);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(expectedUUID);
	}
	
	@Test(expected = WebApiException.class)
	public void getIdfromParam_should_fail_with_clean_WebApiException_when_null_paramName_and_invalid_param() {
		//given
		String invalidParam = "invalid param";
		String expectedMessage = INVALID.getHumanReadableMessage("Unknown param : " + invalidParam);
		
		//when
		try {
			ResourceUtil.getIdfromParam(null, invalidParam);
		} catch(WebApiException wae) {
		//then
			assertWebApiException(wae, BAD_REQUEST, ERROR, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void getIdfromParam_should_fail_with_clean_WebApiException_when_blank_paramName_and_invalid_param() {
		//given
		String invalidParam = "invalid param";
		String expectedMessage = INVALID.getHumanReadableMessage("Unknown param : " + invalidParam);
		
		//when
		try {
			ResourceUtil.getIdfromParam("  ", invalidParam);
		} catch(WebApiException wae) {
		//then
			assertWebApiException(wae, BAD_REQUEST, ERROR, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void getIdfromParam_should_fail_with_null_param() {
		//given
		String paramName = "nullParam";
		String expectedMessage = INVALID.getHumanReadableMessage("Param named " + paramName + " : " + null);
		
		//when
		try {
			ResourceUtil.getIdfromParam(paramName, null);
		} catch(WebApiException wae) {
		//then
			assertWebApiException(wae, BAD_REQUEST, ERROR, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void getIdfromParam_should_fail_with_blank_param() {
		//given
		String paramName = "blankParam";
		String expectedMessage = INVALID.getHumanReadableMessage("Param named " + paramName + " :   ");
		
		//when
		try {
			ResourceUtil.getIdfromParam(paramName, "  ");
		} catch(WebApiException wae) {
		//then
			assertWebApiException(wae, BAD_REQUEST, ERROR, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void getIdfromParam_should_fail_with_invalid_param() {
		//given
		String invalidParam = "invalid param";
		String expectedMessage = INVALID.getHumanReadableMessage("Param named paramName : " + invalidParam);
		
		//when
		try {
			ResourceUtil.getIdfromParam("paramName", invalidParam);
		} catch(WebApiException wae) {
		//then
			assertWebApiException(wae, BAD_REQUEST, ERROR, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test
	public void getIdfromParam_should_work() {
		//given
		UUID expectedUUID = UUID.randomUUID();
		String validParam = expectedUUID.toString();
		
		//when
		UUID result = ResourceUtil.getIdfromParam("paramName", validParam);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(expectedUUID);
	}
}
