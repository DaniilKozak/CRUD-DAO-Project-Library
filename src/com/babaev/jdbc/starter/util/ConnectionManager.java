package com.babaev.jdbc.starter.util;

import com.babaev.jdbc.starter.exception.DaoException;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public final class ConnectionManager {
    private ConnectionManager() {
    }

    static {
        loadDriver();
        initConnectionPool();
    }

    private static final String URL_KEY = "db.url";
    private static final String USERNAME_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";
    private static final String POOL_SIZE_KEY = "db.pool.size";
    private static final int DEFAULT_POOL_SIZE = 10;
    private static BlockingQueue<Connection> pool;
    private static List<Connection> connectionList;

    private static void initConnectionPool() {
        var poolSize = PropertiesUtil.getPropertyValue(POOL_SIZE_KEY);
        var size = poolSize == null ? DEFAULT_POOL_SIZE : Integer.parseInt(poolSize);
        pool = new ArrayBlockingQueue<>(size);
        connectionList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            var connection = open();
            var proxyConnection = (Connection) Proxy.newProxyInstance(
                    ConnectionManager.class.getClassLoader(),
                    new Class<?>[]{Connection.class},
                    (proxy, method, args) ->{
                        if(method.getName().equals("close")){
                            pool.add((Connection) proxy);
                            return null;
                        }
                        return method.invoke(connection, args);
                    }
            );
            pool.add(proxyConnection);
            connectionList.add(connection);
        }
    }

    public static Connection get(){
        try {
            return pool.take();
        }catch (InterruptedException e){
            throw new DaoException(e);
        }
    }

    private static Connection open(){
        try {
            return DriverManager.getConnection(
                    PropertiesUtil.getPropertyValue(URL_KEY),
                    PropertiesUtil.getPropertyValue(USERNAME_KEY),
                    PropertiesUtil.getPropertyValue(PASSWORD_KEY)
            );
        } catch (SQLException e){
            throw new DaoException(e);
        }
    }

    public static void closePool(){
        try {
            var size = connectionList.size();
            for (int i = 0; i < size; i++) {
                connectionList.get(i).close();
            }
        } catch (SQLException e){
            throw new DaoException(e);
        }
    }

    private static void loadDriver(){
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        }
    }
}
