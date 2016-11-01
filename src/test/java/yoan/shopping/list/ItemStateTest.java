package yoan.shopping.list;

import static org.fest.assertions.api.Assertions.assertThat;
import static yoan.shopping.list.ItemState.TO_BUY;

import org.junit.Test;

public class ItemStateTest {
	
	@Test
	public void valueOfOrNull_should_return_null_with_invalid_code() {
		//given
		String invalidCode = "invalid";
		
		//when
		ItemState result = ItemState.valueOfOrNull(invalidCode);
		
		//then
		assertThat(result).isNull();
	}
	
	@Test
	public void valueOfOrNull_should_work_with_valid_code() {
		//given
		String validCode = "TO_BUY";
		
		//when
		ItemState result = ItemState.valueOfOrNull(validCode);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(TO_BUY);
	}
 
}
