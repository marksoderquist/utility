package com.parallelsymmetry.utility.data;

import com.parallelsymmetry.utility.JavaUtil;

public abstract class DataEvent {
	
	public enum Type {
		DATA_CHANGED, META_ATTRIBUTE, DATA_ATTRIBUTE, DATA_CHILD
	}

	public enum Action {
		MODIFY, INSERT, REMOVE
	}

	private Type type;
	
	private Action action;
	
	// FIXME E - Rename data to sender.
	private DataNode data;
	
	public DataEvent( Type type, Action action, DataNode sender ) {
		this.type = type;
		this.action = action;
		this.data = sender;
	}
	
	public Type getType() {
		return type;
	}

	public Action getAction() {
		return action;
	}
	
	public DataNode getData() {
		return data;
	}
	
	public abstract DataEvent cloneWithNewSender( DataNode parent );
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( JavaUtil.getClassName( getClass() ) );
		builder.append( "  type: " );
		builder.append( type.name() );
		builder.append( "  action: " );
		builder.append( action.name() );
		builder.append( "  sender: " );
		builder.append( data.toString() );
		
		return builder.toString();
	}

}
