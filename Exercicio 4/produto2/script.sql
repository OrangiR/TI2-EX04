-- Exercício 4 - Produto 2
-- Executado no Azure Database for PostgreSQL (servidor flexível) pg-raphael-puc

CREATE TABLE jogos (
  id SERIAL PRIMARY KEY,
  nome VARCHAR(100),
  genero VARCHAR(50),
  ano INT
);

INSERT INTO jogos (nome, genero, ano) VALUES
  ('Silent Hill 2', 'Survival Horror', 2001),
  ('Signalis', 'Survival Horror', 2022),
  ('Resident Evil 4', 'Ação/Horror', 2005);

SELECT * FROM jogos;
