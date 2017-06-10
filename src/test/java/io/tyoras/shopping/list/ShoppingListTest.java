package io.tyoras.shopping.list;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

import io.tyoras.shopping.list.ShoppingItem;
import io.tyoras.shopping.list.ShoppingList;

public class ShoppingListTest {
	
	@Test(expected = NullPointerException.class)
	public void shoppingList_should_fail_without_Id() {
		//given
		UUID nullId = null;
		
		//when
		try {
			new ShoppingList(nullId, "name", UUID.randomUUID(),LocalDateTime.now(), LocalDateTime.now(), ImmutableList.<ShoppingItem>of());
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("List Id is mandatory");
			throw npe;
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shoppingList_should_fail_with_blank_name() {
		//given
		String blankName = "  ";
		
		//when
		try {
			new ShoppingList(UUID.randomUUID(), blankName, UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now(), ImmutableList.<ShoppingItem>of());
		} catch(IllegalArgumentException iae) {
		//then
			assertThat(iae.getMessage()).isEqualTo("Invalid list name");
			throw iae;
		}
	}
	
	@Test(expected = NullPointerException.class)
	public void shoppingList_should_fail_without_owner_Id() {
		//given
		UUID nullId = null;
		
		//when
		try {
			new ShoppingList(UUID.randomUUID(), "name", nullId, LocalDateTime.now(), LocalDateTime.now(), ImmutableList.<ShoppingItem>of());
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("List owner Id is mandatory");
			throw npe;
		}
	}
	
	@Test(expected = NullPointerException.class)
	public void shoppingList_should_fail_without_creation_date() {
		//given
		LocalDateTime nullDate = null;
		
		//when
		try {
			new ShoppingList(UUID.randomUUID(), "name", UUID.randomUUID(), nullDate, LocalDateTime.now(), ImmutableList.<ShoppingItem>of());
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("Creation date is mandatory");
			throw npe;
		}
	}
	
	@Test(expected = NullPointerException.class)
	public void shoppingList_should_fail_without_last_update_date() {
		//given
		LocalDateTime nullDate = null;
		
		//when
		try {
			new ShoppingList(UUID.randomUUID(), "name", UUID.randomUUID(), LocalDateTime.now(), nullDate, ImmutableList.<ShoppingItem>of());
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("Last update date is mandatory");
			throw npe;
		}
	}
	
	@Test(expected = NullPointerException.class)
	public void shoppingList_should_fail_without_item_list() {
		//given
		ImmutableList<ShoppingItem> nullList = null;
		
		//when
		try {
			new ShoppingList(UUID.randomUUID(), "name", UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now(), nullList);
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("Item list is mandatory");
			throw npe;
		}
	}
}
