package com.parallelsymmetry.escape.utility.data;

public class SetAttributeAction extends Action {

	private String name;

	private Object oldValue;

	private Object newValue;

	public SetAttributeAction( DataNode data, String name, Object oldValue, Object newValue ) {
		super( data );
		this.name = name;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	@Override
	protected ActionResult process() {
		ActionResult result = new ActionResult( this );

		getData().doSetAttribute( name, oldValue, newValue );

		DataEvent.Type type = DataEvent.Type.MODIFY;
		type = oldValue == null ? DataEvent.Type.INSERT : type;
		type = newValue == null ? DataEvent.Type.REMOVE : type;
		result.addEvent( new DataAttributeEvent( type, getData(), name, oldValue, newValue ) );

		return result;
	}

}
