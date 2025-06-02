package br.ufrrj.common.database.connection.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Predicate;

import br.ufrrj.common.database.ResultSetMapper;
import br.ufrrj.common.database.StatementExecutor;
import br.ufrrj.common.database.connection.Database;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PostgresDatabase implements Database {

    private final String databaseUrl;
    private final String username;
    private final String password;

    private Connection connection;

    public PostgresDatabase(String hostname, int port, String username, String password, String databaseName,
            boolean useSSL) {
        this(String.format("jdbc:postgresql://%s:%d/%s?ssl=%s", hostname, port, databaseName, useSSL), username,
                password);
    }

    @Override
    public boolean startConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(databaseUrl, username, password);
            return true;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver not found", e);
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to database", e);
        }
    }

    @Override
    public void closeConnection() {
        try {
            if (this.connection != null) {
                this.connection.close();
                this.connection = null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error closing connection", e);
        }
    }

    @Override
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            throw new RuntimeException("Error checking connection", e);
        }
    }

    @Override
    public int executeStatement(String query, StatementExecutor executor) {
        if (!isConnected()) {
            throw new RuntimeException("Not connected to database");
        }

        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            executor.execute(statement);

            int rowsAffected = statement.executeUpdate();

            statement.close();
            return rowsAffected;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error executing query", e);
        }
    }

    @Override
    public <T> T executeQuery(String query, StatementExecutor executor, ResultSetMapper<T> function) {
        if (!isConnected()) {
            throw new RuntimeException("Not connected to database");
        }

        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            executor.execute(statement);

            ResultSet resultSet = statement.executeQuery();

            T result = function.map(resultSet);

            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error executing query", e);
        }
    }

    @Override
    public <T> T executeQuery(String query, ResultSetMapper<T> function) {
        if (!isConnected()) {
            throw new RuntimeException("Not connected to database");
        }

        try (Statement statement = this.connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);

            T result = function.map(resultSet);

            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error executing query", e);
        }
    }

}
