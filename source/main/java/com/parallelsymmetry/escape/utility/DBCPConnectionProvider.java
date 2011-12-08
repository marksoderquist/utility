package com.parallelsymmetry.escape.utility;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Environment;
import org.hibernate.connection.ConnectionProvider;
import org.hibernate.connection.ConnectionProviderFactory;

import com.parallelsymmetry.escape.utility.log.Log;

public class DBCPConnectionProvider implements ConnectionProvider {

	private static final String PREFIX = "hibernate.dbcp.";

	private BasicDataSource datasource;

	public void configure( Properties properties ) {
		if( properties == null ) throw new HibernateException( "DBCP Connection Provider requires properties." );

		try {
			Log.write( Log.DEBUG, "Configure DBCPConnectionProvider" );

			// DBCP properties used to create the data source.
			Properties dbcpProperties = new Properties();

			// DriverClass and URL.
			String jdbcDriverClass = properties.getProperty( Environment.DRIVER );
			String jdbcUrl = properties.getProperty( Environment.URL );
			dbcpProperties.put( "driverClassName", jdbcDriverClass );
			dbcpProperties.put( "url", jdbcUrl );

			// Username and password.
			String username = properties.getProperty( Environment.USER );
			String password = properties.getProperty( Environment.PASS );
			dbcpProperties.put( "username", username );
			dbcpProperties.put( "password", password );

			// Isolation level
			String isolationLevel = properties.getProperty( Environment.ISOLATION );
			if( ( isolationLevel != null ) && ( isolationLevel.trim().length() > 0 ) ) dbcpProperties.put( "defaultTransactionIsolation", isolationLevel );

			// Pool size
			String poolSize = properties.getProperty( Environment.POOL_SIZE );
			if( ( poolSize != null ) && ( poolSize.trim().length() > 0 ) && ( Integer.parseInt( poolSize ) > 0 ) ) dbcpProperties.put( "maxActive", poolSize );

			// Copy all "driver" properties into "connection" properties.
			Properties driverProperties = ConnectionProviderFactory.getConnectionProperties( properties );
			if( driverProperties.size() > 0 ) {
				StringBuffer connectionProperties = new StringBuffer();
				for( Iterator<Map.Entry<Object, Object>> iterator = driverProperties.entrySet().iterator(); iterator.hasNext(); ) {
					Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>)iterator.next();
					connectionProperties.append( entry.getKey().toString() ).append( '=' ).append( entry.getValue().toString() );
					if( iterator.hasNext() ) connectionProperties.append( ';' );
				}
				dbcpProperties.put( "connectionProperties", connectionProperties.toString() );
			}

			// Copy all DBCP properties removing the prefix.
			for( Iterator<Map.Entry<Object, Object>> iter = properties.entrySet().iterator(); iter.hasNext(); ) {
				Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>)iter.next();
				String key = (String)entry.getKey();
				if( key.startsWith( PREFIX ) ) dbcpProperties.put( key.substring( PREFIX.length() ), entry.getValue().toString() );
			}

			// Some debug info
			if( Log.isActive( Log.DEBUG ) ) {
				StringWriter writer = new StringWriter();
				dbcpProperties.list( new PrintWriter( writer, true ) );
				Log.write( Log.DEBUG, writer.toString() );
			}

			// Let the factory create the pool
			datasource = (BasicDataSource)BasicDataSourceFactory.createDataSource( dbcpProperties );

			// The BasicDataSource has lazy initialization
			// borrowing a connection will start the DataSource
			// and make sure it is configured correctly.
			Connection connection = datasource.getConnection();
			connection.close();

			// Log pool statistics before continuing.
			logStatistics();
		} catch( Exception exception ) {
			if( datasource != null ) {
				try {
					datasource.close();
				} catch( Exception closeException ) {
					// Intentionally ignore exception.
				}
				datasource = null;
			}
			throw new HibernateException( "Could not create DBCP connection provider", exception );
		}
		Log.write( Log.DEBUG, "Configure DBCPConnectionProvider complete" );
	}

	public Connection getConnection() throws SQLException {
		Connection connection = null;
		try {
			connection = datasource.getConnection();
		} finally {
			logStatistics();
		}
		return connection;
	}

	public void closeConnection( Connection connection ) throws SQLException {
		try {
			connection.close();
		} finally {
			logStatistics();
		}
	}

	public void close() throws HibernateException {
		Log.write( Log.DEBUG, "Close DBCPConnectionProvider" );
		logStatistics();
		try {
			if( datasource != null ) {
				datasource.close();
				datasource = null;
			} else {
				Log.write( Log.WARN, "Cannot close DBCP pool (not initialized)" );
			}
		} catch( Exception e ) {
			throw new HibernateException( "Could not close DBCP pool", e );
		}
		Log.write( Log.DEBUG, "Close DBCPConnectionProvider complete" );
	}

	protected void logStatistics() {
		Log.write( Log.INFO, "active: " + datasource.getNumActive() + " (max: " + datasource.getMaxActive() + ")   " + "idle: " + datasource.getNumIdle() + "(max: " + datasource.getMaxIdle() + ")" );
	}

	public boolean supportsAggressiveRelease() {
		return false;
	}
}
