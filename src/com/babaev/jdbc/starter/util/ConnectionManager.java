package com.babaev.jdbc.starter.util;

public final class ConnectionManager {
    private ConnectionManager() {
    }
    private static final String URL_KEY = "db.url";
    private static final String USERNAME_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";
    private static final String POOL_SIZE_KEY = "db.pool.size";
}
