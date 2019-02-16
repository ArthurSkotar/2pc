package com.home.task.tm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DbConnections {

    private DbConnections() {}

    public static Connection getHotelConnection() {
        return Handler.HOTEL_CONNECTION;
    }

    public static Connection getPlaneConnection() {
        return Handler.PLANE_CONNECTION;
    }

    public static Connection getAccountConnection() {
        return Handler.AccountConnection;
    }

    private static final class Handler {
        private static final Connection HOTEL_CONNECTION = getConnection("jdbc:postgresql://localhost/hotel");
        private static final Connection PLANE_CONNECTION = getConnection("jdbc:postgresql://localhost/fly");
        private static final Connection AccountConnection = getConnection("jdbc:postgresql://localhost/account");

        private static Connection getConnection(String url) {
            Connection conn = null;
            try {
                conn = DriverManager.getConnection(url, "postgres", "password");
                System.out.println("Connected to the PostgreSQL server successfully.");
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            return conn;
        }
    }
}
