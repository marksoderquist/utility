package com.parallelsymmetry.data;

public class DataEvent {

	public static enum Type {
		CHANGE, ATTRIBUTE, METADATA, INSERT, REMOVE
	};

	private Type type;

	private DataNode data;

	private Throwable trace;

	public DataEvent( Type type, DataNode data ) {
		this.type = type;
		this.data = data;
		this.trace = new Throwable( data.toString() );
	}

	public Type getType() {
		return type;
	}

	public DataNode getData() {
		return data;
	}

	public Throwable getTrace() {
		return trace;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append( getClass().getName() );
		builder.append( ": " );
		builder.append( data );

		return builder.toString();
	}

}
