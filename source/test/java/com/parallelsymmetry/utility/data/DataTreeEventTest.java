package com.parallelsymmetry.utility.data;

public abstract class DataTreeEventTest extends DataTestCase {

	protected DataNode grandparent;

	protected DataNode parent;

	protected DataNode child;

	protected DataEventWatcher grandparentWatcher;

	protected DataEventWatcher parentWatcher;

	protected DataEventWatcher childWatcher;

	@Override
	public void setUp() {
		super.setUp();
		DataNode localGrandparent = createNode( "grandparent" );
		DataNode localParent = createNode( "parent" );
		DataNode localChild = createNode( "child" );

		DataEventWatcher localGrandparentWatcher = ( (WatchedMockData)localGrandparent ).getDataEventWatcher();
		DataEventWatcher localParentWatcher = ( (WatchedMockData)localParent ).getDataEventWatcher();
		DataEventWatcher localChildWatcher = ( (WatchedMockData)localChild ).getDataEventWatcher();

		assertFalse( localGrandparent.isModified() );
		assertFalse( localParent.isModified() );
		assertFalse( localChild.isModified() );

		addParent( localGrandparent, localParent );
		assertTrue( localGrandparent.isModified() );
		assertFalse( localParent.isModified() );
		assertFalse( localChild.isModified() );

		addChild( localParent, localChild );
		assertTrue( localGrandparent.isModified() );
		assertTrue( localParent.isModified() );
		assertFalse( localChild.isModified() );

		localGrandparent.setModified( false );

		assertNodeState( localGrandparent, false, 0 );
		assertNodeState( localParent, false, 0 );
		assertNodeState( localChild, false, 0 );

		localGrandparentWatcher.reset();
		localParentWatcher.reset();
		localChildWatcher.reset();

		assertEventCounts( localGrandparentWatcher, 0, 0, 0 );
		assertEventCounts( localParentWatcher, 0, 0, 0 );
		assertEventCounts( localChildWatcher, 0, 0, 0 );

		grandparentWatcher = localGrandparentWatcher;
		parentWatcher = localParentWatcher;
		childWatcher = localChildWatcher;

		grandparent = localGrandparent;
		parent = localParent;
		child = localChild;
	}

	protected abstract DataNode createNode( String name );

	protected abstract void addParent( DataNode grandparent, DataNode parent );

	protected abstract void addChild( DataNode parent, DataNode child );

	public void testInitialState() {
		assertFalse( grandparent.isModified() );
		assertFalse( parent.isModified() );
		assertFalse( child.isModified() );
		assertEventCounts( grandparentWatcher, 0, 0, 0 );
		assertEventCounts( parentWatcher, 0, 0, 0 );
		assertEventCounts( childWatcher, 0, 0, 0 );
	}

	public void testSetAttributeOnGrandparent() {
		grandparent.setAttribute( "a", "1" );
		assertTrue( grandparent.isModified() );
		assertFalse( parent.isModified() );
		assertFalse( child.isModified() );
		assertEventCounts( grandparentWatcher, 1, 1, 1 );
		assertEventState( grandparentWatcher, 0, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.INSERT, grandparent, grandparent, "a", null, "1" );
		assertEventState( grandparentWatcher, 1, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, grandparent, DataNode.MODIFIED, false, true );
		assertEventState( grandparentWatcher, 2, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, grandparent );
		assertEventCounts( parentWatcher, 0, 0, 0 );
		assertEventCounts( childWatcher, 0, 0, 0 );
	}

	public void testSetAttributeOnParent() {
		parent.setAttribute( "a", "1" );
		assertTrue( grandparent.isModified() );
		assertTrue( parent.isModified() );
		assertFalse( child.isModified() );
		assertEventCounts( grandparentWatcher, 1, 1, 1 );
		assertEventState( grandparentWatcher, 0, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.INSERT, grandparent, parent, "a", null, "1" );
		assertEventState( grandparentWatcher, 1, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, grandparent, DataNode.MODIFIED, false, true );
		assertEventState( grandparentWatcher, 2, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, grandparent );
		assertEventCounts( parentWatcher, 1, 1, 1 );
		assertEventState( parentWatcher, 0, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.INSERT, parent, parent, "a", null, "1" );
		assertEventState( parentWatcher, 1, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, parent, DataNode.MODIFIED, false, true );
		assertEventState( parentWatcher, 2, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, parent );
		assertEventCounts( childWatcher, 0, 0, 0 );
	}

	public void testSetAttributeOnChild() {
		child.setAttribute( "a", "1" );
		assertTrue( grandparent.isModified() );
		assertTrue( parent.isModified() );
		assertTrue( child.isModified() );
		assertEventCounts( grandparentWatcher, 1, 1, 1 );
		assertEventState( grandparentWatcher, 0, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.INSERT, grandparent, child, "a", null, "1" );
		assertEventState( grandparentWatcher, 1, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, grandparent, DataNode.MODIFIED, false, true );
		assertEventState( grandparentWatcher, 2, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, grandparent );
		assertEventCounts( parentWatcher, 1, 1, 1 );
		assertEventState( parentWatcher, 0, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.INSERT, parent, child, "a", null, "1" );
		assertEventState( parentWatcher, 1, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, parent, DataNode.MODIFIED, false, true );
		assertEventState( parentWatcher, 2, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, parent );
		assertEventCounts( childWatcher, 1, 1, 1 );
		assertEventState( childWatcher, 0, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.INSERT, child, child, "a", null, "1" );
		assertEventState( childWatcher, 1, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, child, DataNode.MODIFIED, false, true );
		assertEventState( childWatcher, 2, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, child );
	}

	public void testResetAttributeOnGrandparent() {
		testSetAttributeOnGrandparent();
		resetWatchers();

		grandparent.setAttribute( "a", null );
		assertNodeState( grandparent, false, 0 );
		assertNodeState( parent, false, 0 );
		assertNodeState( child, false, 0 );
		assertEventCounts( grandparentWatcher, 1, 1, 1 );
		assertEventState( grandparentWatcher, 0, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.REMOVE, grandparent, grandparent, "a", "1", null );
		assertEventState( grandparentWatcher, 1, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, grandparent, DataNode.MODIFIED, true, false );
		assertEventState( grandparentWatcher, 2, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, grandparent );
		assertEventCounts( parentWatcher, 0, 0, 0 );
		assertEventCounts( childWatcher, 0, 0, 0 );
	}

	public void testResetAttributeOnParent() {
		testSetAttributeOnParent();
		resetWatchers();

		parent.setAttribute( "a", null );
		assertNodeState( grandparent, false, 0 );
		assertNodeState( parent, false, 0 );
		assertNodeState( child, false, 0 );
		assertEventCounts( grandparentWatcher, 1, 1, 1 );
		assertEventState( grandparentWatcher, 0, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.REMOVE, grandparent, parent, "a", "1", null );
		assertEventState( grandparentWatcher, 1, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, grandparent, DataNode.MODIFIED, true, false );
		assertEventState( grandparentWatcher, 2, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, grandparent );
		assertEventCounts( parentWatcher, 1, 1, 1 );
		assertEventState( parentWatcher, 0, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.REMOVE, parent, parent, "a", "1", null );
		assertEventState( parentWatcher, 1, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, parent, DataNode.MODIFIED, true, false );
		assertEventState( parentWatcher, 2, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, parent );
		assertEventCounts( childWatcher, 0, 0, 0 );
	}

	public void testResetAttributeOnChild() {
		testSetAttributeOnChild();
		resetWatchers();

		child.setAttribute( "a", null );
		assertNodeState( grandparent, false, 0 );
		assertNodeState( parent, false, 0 );
		assertNodeState( child, false, 0 );
		assertEventCounts( grandparentWatcher, 1, 1, 1 );
		assertEventState( grandparentWatcher, 0, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.REMOVE, grandparent, child, "a", "1", null );
		assertEventState( grandparentWatcher, 1, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, grandparent, DataNode.MODIFIED, true, false );
		assertEventState( grandparentWatcher, 2, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, grandparent );
		assertEventCounts( parentWatcher, 1, 1, 1 );
		assertEventState( parentWatcher, 0, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.REMOVE, parent, child, "a", "1", null );
		assertEventState( parentWatcher, 1, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, parent, DataNode.MODIFIED, true, false );
		assertEventState( parentWatcher, 2, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, parent );
		assertEventCounts( childWatcher, 1, 1, 1 );
		assertEventState( childWatcher, 0, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.REMOVE, child, child, "a", "1", null );
		assertEventState( childWatcher, 1, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, child, DataNode.MODIFIED, true, false );
		assertEventState( childWatcher, 2, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, child );
	}

	public void testUnmodifyGrandparent() {
		testSetAttributeOnGrandparent();
		resetWatchers();

		grandparent.setModified( false );
		assertNodeState( grandparent, false, 0 );
		assertNodeState( parent, false, 0 );
		assertNodeState( child, false, 0 );
		assertEventCounts( grandparentWatcher, 1, 1, 0 );
		assertEventState( grandparentWatcher, 0, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, grandparent, DataNode.MODIFIED, true, false );
		assertEventState( grandparentWatcher, 1, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, grandparent );
		assertEventCounts( parentWatcher, 0, 0, 0 );
		assertEventCounts( childWatcher, 0, 0, 0 );
	}

	public void testUnmodifyParent() {
		testSetAttributeOnParent();
		resetWatchers();

		parent.setModified( false );
		assertNodeState( grandparent, false, 0 );
		assertNodeState( parent, false, 0 );
		assertNodeState( child, false, 0 );
		assertEventCounts( grandparentWatcher, 1, 1, 0 );
		assertEventState( grandparentWatcher, 0, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, grandparent, DataNode.MODIFIED, true, false );
		assertEventState( grandparentWatcher, 1, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, grandparent );
		assertEventCounts( parentWatcher, 1, 1, 0 );
		assertEventState( parentWatcher, 0, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, parent, DataNode.MODIFIED, true, false );
		assertEventState( parentWatcher, 1, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, parent );
		assertEventCounts( childWatcher, 0, 0, 0 );
	}

	public void testUnmodifyChild() {
		testSetAttributeOnChild();
		resetWatchers();

		child.setModified( false );
		assertNodeState( grandparent, false, 0 );
		assertNodeState( parent, false, 0 );
		assertNodeState( child, false, 0 );
		assertEventCounts( grandparentWatcher, 1, 1, 0 );
		assertEventState( grandparentWatcher, 0, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, grandparent, DataNode.MODIFIED, true, false );
		assertEventState( grandparentWatcher, 1, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, grandparent );
		assertEventCounts( parentWatcher, 1, 1, 0 );
		assertEventState( parentWatcher, 0, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, parent, DataNode.MODIFIED, true, false );
		assertEventState( parentWatcher, 1, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, parent );
		assertEventCounts( childWatcher, 1, 1, 0 );
		assertEventState( childWatcher, 0, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, child, DataNode.MODIFIED, true, false );
		assertEventState( childWatcher, 1, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, child );
	}

	public void testModifyParentSetGrandparentUnmodified() {
		testSetAttributeOnParent();
		resetWatchers();

		grandparent.setModified( false );
		assertNodeState( grandparent, false, 0 );
		assertNodeState( parent, false, 0 );
		assertNodeState( child, false, 0 );
		assertEventCounts( grandparentWatcher, 1, 1, 0 );
		assertEventState( grandparentWatcher, 0, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, grandparent, DataNode.MODIFIED, true, false );
		assertEventState( grandparentWatcher, 1, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, grandparent );
		assertEventCounts( parentWatcher, 1, 1, 0 );
		assertEventState( parentWatcher, 0, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, parent, DataNode.MODIFIED, true, false );
		assertEventState( parentWatcher, 1, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, parent );
		assertEventCounts( childWatcher, 0, 0, 0 );
	}

	public void testModifyChildSetGrandparentUnmodified() {
		testSetAttributeOnChild();
		resetWatchers();

		grandparent.setModified( false );
		assertNodeState( grandparent, false, 0 );
		assertNodeState( parent, false, 0 );
		assertNodeState( child, false, 0 );
		assertEventCounts( grandparentWatcher, 1, 1, 0 );
		assertEventState( grandparentWatcher, 0, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, grandparent, grandparent, DataNode.MODIFIED, true, false );
		assertEventState( grandparentWatcher, 1, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, grandparent );
		assertEventCounts( parentWatcher, 1, 1, 0 );
		assertEventState( parentWatcher, 0, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, parent, grandparent, DataNode.MODIFIED, true, false );
		assertEventState( parentWatcher, 1, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, parent );
		assertEventCounts( childWatcher, 1, 1, 0 );
		assertEventState( childWatcher, 0, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, child, grandparent, DataNode.MODIFIED, true, false );
		assertEventState( childWatcher, 1, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, child );
	}

	public void testModifyChildSetParentUnmodified() {
		testSetAttributeOnChild();
		resetWatchers();

		parent.setModified( false );
		assertNodeState( grandparent, false, 0 );
		assertNodeState( parent, false, 0 );
		assertNodeState( child, false, 0 );
		assertEventCounts( grandparentWatcher, 1, 1, 0 );
		assertEventState( grandparentWatcher, 0, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, grandparent, DataNode.MODIFIED, true, false );
		assertEventState( grandparentWatcher, 1, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, grandparent );
		assertEventCounts( parentWatcher, 1, 1, 0 );
		assertEventState( parentWatcher, 0, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, parent, DataNode.MODIFIED, true, false );
		assertEventState( parentWatcher, 1, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, parent );
		assertEventCounts( childWatcher, 1, 1, 0 );
		assertEventState( childWatcher, 0, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, child, DataNode.MODIFIED, true, false );
		assertEventState( childWatcher, 1, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, child );
	}

	private void resetWatchers() {
		grandparentWatcher.reset();
		parentWatcher.reset();
		childWatcher.reset();
	}

}
