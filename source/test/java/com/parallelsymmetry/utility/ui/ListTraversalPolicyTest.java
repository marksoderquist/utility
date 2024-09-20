package com.parallelsymmetry.utility.ui;

import com.parallelsymmetry.utility.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ListTraversalPolicyTest extends BaseTestCase {

	private Container container;

	private ArrayList<Component> order;

	private ListTraversalPolicy policy;

	@BeforeEach
	@Override
	public void setup() {
		JTextField field0 = new JTextField( "field0" );
		JTextField field1 = new JTextField( "field1" );
		JTextField field2 = new JTextField( "field2" );
		JTextField field3 = new JTextField( "field3" );
		JTextField field4 = new JTextField( "field4" );

		container = new Container();
		container.add( field0 );
		container.add( field1 );
		container.add( field2 );
		container.add( field3 );
		container.add( field4 );

		order = new ArrayList<>();
		order.add( field0 );
		order.add( field1 );
		order.add( field2 );
		order.add( field3 );
		order.add( field4 );

		policy = new ListTraversalPolicy( order );
	}

	@Test
	public void testGetDefaultComponent() {
		assertEquals( order.get( 0 ), policy.getDefaultComponent( container ) );
	}

	@Test
	public void testGetFirstComponent() {
		assertEquals( order.get( 0 ), policy.getFirstComponent( container ) );
	}

	@Test
	public void testGetComponentAfter() {
		assertEquals( order.get( 1 ), policy.getComponentAfter( container, order.get( 0 ) ) );
		assertEquals( order.get( 2 ), policy.getComponentAfter( container, order.get( 1 ) ) );
		assertEquals( order.get( 3 ), policy.getComponentAfter( container, order.get( 2 ) ) );
		assertEquals( order.get( 4 ), policy.getComponentAfter( container, order.get( 3 ) ) );
		assertEquals( order.get( 0 ), policy.getComponentAfter( container, order.get( 4 ) ) );
	}

	@Test
	public void testGetComponentBefore() {
		assertEquals( order.get( 4 ), policy.getComponentBefore( container, order.get( 0 ) ) );
		assertEquals( order.get( 0 ), policy.getComponentBefore( container, order.get( 1 ) ) );
		assertEquals( order.get( 1 ), policy.getComponentBefore( container, order.get( 2 ) ) );
		assertEquals( order.get( 2 ), policy.getComponentBefore( container, order.get( 3 ) ) );
		assertEquals( order.get( 3 ), policy.getComponentBefore( container, order.get( 4 ) ) );
	}

	@Test
	public void testGetLastComponent() {
		assertEquals( order.get( 4 ), policy.getLastComponent( container ) );
	}

}
