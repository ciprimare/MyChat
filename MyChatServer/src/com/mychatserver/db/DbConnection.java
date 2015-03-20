package com.mychatserver.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by ciprian.mare on 3/19/2015.
 */
public class DbConnection {

    private Connection connection = null;

    private static DbConnection dbConnection = new DbConnection();

    public static DbConnection getInstance(){
        return dbConnection;
    }

    /**
     * return a single connection
     * @return
     */
    public Connection getConnection(){
        if(!isConnected()){
            connect();
        }

        return connection;
    }

    /**
     * connect to a particular sqlite db
     */
    private void connect(){
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:D://mychat.db");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }

    /**
     * check to see if a connection already created
     * @return
     */
    private boolean isConnected(){
        if (connection == null)
            return false;
        try {
            if(connection.isClosed()){
                return  false;
            }

            //TODO: maybe to check if connection is valid
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * closing the connection
     */
    public void closeConnection(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            connection = null;
        }
    }
}
