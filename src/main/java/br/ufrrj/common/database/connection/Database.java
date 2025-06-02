package br.ufrrj.common.database.connection;

import java.sql.SQLException;

import br.ufrrj.common.database.StatementExecutor;
import br.ufrrj.common.database.ResultSetMapper;

public interface Database {

    boolean startConnection() throws SQLException;

    void closeConnection();

    boolean isConnected();

    int executeStatement(String query, StatementExecutor executor);

    <T> T executeQuery(String query, StatementExecutor executor, ResultSetMapper<T> function);

    <T> T executeQuery(String query, ResultSetMapper<T> function);

}
