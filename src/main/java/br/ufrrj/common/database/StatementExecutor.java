package br.ufrrj.common.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface StatementExecutor {
    void execute(PreparedStatement statement) throws SQLException;
}
