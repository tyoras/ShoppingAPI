package yoan.shopping.infra.rest;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

public class LinkTest {

	@Test
	public void self_should_return_a_link_with_self_rel() throws URISyntaxException {
		//given
		URI validURL = new URI("http://www.google.com");
		
		//when
		Link result = Link.self(validURL);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result.getRel()).isEqualTo("self");
	}
}
