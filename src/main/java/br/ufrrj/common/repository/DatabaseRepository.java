package br.ufrrj.common.repository;

import br.ufrrj.common.database.connection.Database;
import lombok.Getter;

@Getter
public abstract class DatabaseRepository<T> {

    protected final Database database;

    public DatabaseRepository(Database database) {
        this.database = database;
    }

}
