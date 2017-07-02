package io.tyoras.shopping.infra;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class ShoppingApiApplicationTest {

	private ShoppingApiApplication tested = new ShoppingApiApplication();
	
	@Test
	public void getName_should_return_expected() {
		//given
		String expectedName = "Shopping API";
		
		//when
		String result = tested.getName();
		
		//then
		assertThat(result).isEqualTo(expectedName);
	}
}
