package de.tud.stg.tigerseye.eclipse.core.utils;

import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.tud.stg.tigerseye.utils.LMTransformer;
import de.tud.stg.tigerseye.utils.ListBuilder;
import de.tud.stg.tigerseye.utils.ListMap;
import static junit.framework.Assert.*;

public class ListMapTest {

	@Mock
	LMTransformer<String, Integer> sToI;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testTransformEmptyList() throws Exception {
		List<Integer> result = ListMap.map(new ArrayList<String>(), sToI);
		assertEquals(0,result.size());
	}

	@Test
	public void testTransformertransforms() throws Exception {

		LMTransformer<Integer, String> t = new LMTransformer<Integer, String>() {

			@Override
			public String transform(Integer input) {
				return input.toString();
			}

		};

		String transform = t.transform(2);
		assertEquals("2",transform);

	}

	@Test
	public void testTransformSomeStringsToInteger() throws Exception {
		ArrayList<String> arrayList = new ArrayList<String>();
		Collections.addAll(arrayList, "1", "2", "3", "4");
		List<Integer> result = ListMap.map(arrayList,
				new LMTransformer<String, Integer>() {
					@Override
					public Integer transform(String input) {
						return Integer.parseInt(input);
					}
				});
		assertTrue(result.containsAll(ListBuilder.begin(1).add(2).add(3).add(4).toList()));
	}

}
