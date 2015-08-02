package yoan.shopping.list;

import static org.fest.assertions.api.Assertions.assertThat;
import static yoan.shopping.list.ItemState.TO_BUY;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.Test;

public class ShoppingItemTest {
	@Test(expected = NullPointerException.class)
	public void shoppingItem_should_fail_without_Id() {
		//given
		UUID nullId = null;
		
		//when
		try {
			new ShoppingItem(nullId, "name", LocalDateTime.now(), LocalDateTime.now(), 1, TO_BUY);
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("Item Id is mandatory");
			throw npe;
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shoppingItem_should_fail_with_blank_name() {
		//given
		String blankName = "  ";
		
		//when
		try {
			new ShoppingItem(UUID.randomUUID(), blankName, LocalDateTime.now(), LocalDateTime.now(), 1, TO_BUY);
		} catch(IllegalArgumentException iae) {
		//then
			assertThat(iae.getMessage()).isEqualTo("Invalid item name");
			throw iae;
		}
	}
	
	@Test(expected = NullPointerException.class)
	public void shoppingItem_should_fail_without_state() {
		//given
		ItemState nullState = null;
		
		//when
		try {
			new ShoppingItem(UUID.randomUUID(), "name", LocalDateTime.now(), LocalDateTime.now(), 1, nullState);
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("Invalid item state");
			throw npe;
		}
	}
	
	@Test(expected = NullPointerException.class)
	public void shoppingItemshould_fail_without_creation_date() {
		//given
		LocalDateTime nullDate = null;
		
		//when
		try {
			new ShoppingItem(UUID.randomUUID(), "name", nullDate, LocalDateTime.now(), 1, TO_BUY);
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("Creation date is mandatory");
			throw npe;
		}
	}
	
	@Test(expected = NullPointerException.class)
	public void shoppingItem_should_fail_without_last_update_date() {
		//given
		LocalDateTime nullDate = null;
		
		//when
		try {
			new ShoppingItem(UUID.randomUUID(), "name", LocalDateTime.now(), nullDate, 1, TO_BUY);
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("Last update date is mandatory");
			throw npe;
		}
	}
}
