package de.tud.stg.tigerseye.eclipse.core.utils;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.tud.stg.tigerseye.util.ListBuilder;

public class ListBuilderTest {

	@Test
	public void testListBuilderConstructContainsEl() {
		List<Integer> builder = new ListBuilder<Integer>(2).toList();
		assertTrue(builder.contains(2));
	}
	
	@Test
	public void testListBuilderConstructNull() {
		List<Integer> builder = new ListBuilder<Integer>(null).toList();
		assertTrue(builder.contains(null));
	}

	@Test
	public void addShouldaddElement() {
		List<Integer> builder = new ListBuilder<Integer>(1).add(2).toList();
		assertTrue(builder.contains(1));
		assertTrue(builder.contains(2));
	}

	@Test
	public void testCreateWorksLikeConstructor() {
		List<Integer> list = ListBuilder.newList(1).toList();
		assertTrue(list.contains(1));
	}
	
	@Test
	public void singleShouldReturnListWithElement() throws Exception {
		List<String> single = ListBuilder.single("T");
		assertEquals("T",single.get(0));
		assertEquals(1,single.size());
	}

}
