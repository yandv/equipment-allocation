CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL
);

CREATE TABLE equipamentos (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL
);

CREATE TYPE status_reserva AS ENUM ('ATIVO', 'FINALIZADO');

CREATE TABLE reservas (
    usuario_id INTEGER NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    equipamento_id INTEGER NOT NULL REFERENCES equipamentos(id) ON DELETE CASCADE,
    data_inicio TIMESTAMP WITH TIME ZONE NOT NULL,
    data_fim TIMESTAMP WITH TIME ZONE NOT NULL,
    status status_reserva NOT NULL DEFAULT 'ATIVO',
    CONSTRAINT valid_dates CHECK (data_inicio < data_fim)
);  

CREATE INDEX idx_reservas_usuario_id ON reservas(usuario_id);
CREATE INDEX idx_reservas_equipamento_id ON reservas(equipamento_id);
CREATE INDEX idx_reservas_data ON reservas(data_inicio, data_fim); 