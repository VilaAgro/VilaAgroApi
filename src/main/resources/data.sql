-- Seeds Vila Agro API (Senha: admin123)
-- Apenas insere se não existir (evita duplicação)

-- Limpar dados antigos (ordem correta respeitando foreign keys)
DELETE FROM course_presence;
DELETE FROM attractions;
DELETE FROM justification_for_absence;
DELETE FROM absence;
DELETE FROM statement;
DELETE FROM notifications;
DELETE FROM course;
DELETE FROM artist;
DELETE FROM fairs;
DELETE FROM sale_point;
DELETE FROM address;
DELETE FROM users;

-- Usuários
INSERT INTO users (id, email, password, name, cpf, sale_point_id, type, documents_status, created_at, updated_at)
VALUES (RANDOM_UUID(), 'admin@vilaagro.com', '$2a$10$N9qo8uLOickgx2ZrVzY3COHxlNjZlxyqCBfC4jJ5vIo.KJLLLHHqG', 'Administrador', '12345678901', NULL, 'ADMIN', 'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO users (id, email, password, name, cpf, sale_point_id, type, documents_status, created_at, updated_at)
VALUES (RANDOM_UUID(), 'joao@email.com', '$2a$10$N9qo8uLOickgx2ZrVzY3COHxlNjZlxyqCBfC4jJ5vIo.KJLLLHHqG', 'João Silva', '11122233344', NULL, 'PRODUTOR_RURAL', 'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO users (id, email, password, name, cpf, sale_point_id, type, documents_status, created_at, updated_at)
VALUES (RANDOM_UUID(), 'maria@email.com', '$2a$10$N9qo8uLOickgx2ZrVzY3COHxlNjZlxyqCBfC4jJ5vIo.KJLLLHHqG', 'Maria Oliveira', '55566677788', NULL, 'GASTRONOMO', 'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Feiras
INSERT INTO fairs (id, date, start_time, end_time, expected_merchants, notes, status, created_at, updated_at)
VALUES (RANDOM_UUID(), '2025-11-15', '08:00', '14:00', 50, 'Feira de Novembro', 'scheduled', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO fairs (id, date, start_time, end_time, expected_merchants, notes, status, created_at, updated_at)
VALUES (RANDOM_UUID(), '2025-11-22', '08:00', '14:00', 45, 'Feira Regular', 'scheduled', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO fairs (id, date, start_time, end_time, expected_merchants, notes, status, created_at, updated_at)
VALUES (RANDOM_UUID(), '2025-12-06', '08:00', '14:00', 60, 'Feira de Dezembro', 'scheduled', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Artistas (sem banner, faça upload depois via API)
INSERT INTO artist (id, name, genre, banner, created_at, updated_at)
VALUES (RANDOM_UUID(), 'Banda Sertanejo Raiz', 'Sertanejo', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO artist (id, name, genre, banner, created_at, updated_at)
VALUES (RANDOM_UUID(), 'Grupo de Forró', 'Forró', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO artist (id, name, genre, banner, created_at, updated_at)
VALUES (RANDOM_UUID(), 'DJ Eletrônico', 'Eletrônica', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Atrações (precisa dos IDs das feiras e artistas, então faremos via subquery)
INSERT INTO attractions (id, artist_id, fair_id, time_start, time_end)
SELECT RANDOM_UUID(), 
       (SELECT id FROM artist WHERE name = 'Banda Sertanejo Raiz' LIMIT 1),
       (SELECT id FROM fairs WHERE notes = 'Feira de Novembro' LIMIT 1),
       '10:00:00', '12:00:00'
WHERE EXISTS (SELECT 1 FROM artist WHERE name = 'Banda Sertanejo Raiz')
  AND EXISTS (SELECT 1 FROM fairs WHERE notes = 'Feira de Novembro');

INSERT INTO attractions (id, artist_id, fair_id, time_start, time_end)
SELECT RANDOM_UUID(), 
       (SELECT id FROM artist WHERE name = 'Grupo de Forró' LIMIT 1),
       (SELECT id FROM fairs WHERE notes = 'Feira de Novembro' LIMIT 1),
       '13:00:00', '14:30:00'
WHERE EXISTS (SELECT 1 FROM artist WHERE name = 'Grupo de Forró')
  AND EXISTS (SELECT 1 FROM fairs WHERE notes = 'Feira de Novembro');

INSERT INTO attractions (id, artist_id, fair_id, time_start, time_end)
SELECT RANDOM_UUID(), 
       (SELECT id FROM artist WHERE name = 'DJ Eletrônico' LIMIT 1),
       (SELECT id FROM fairs WHERE notes = 'Feira Regular' LIMIT 1),
       '11:00:00', '13:00:00'
WHERE EXISTS (SELECT 1 FROM artist WHERE name = 'DJ Eletrônico')
  AND EXISTS (SELECT 1 FROM fairs WHERE notes = 'Feira Regular');

-- Endereços
INSERT INTO address (id, street, number, neighborhood, city, cep, reference, created_at, updated_at)
VALUES (RANDOM_UUID(), 'Rua das Flores', '123', 'Centro', 'São Paulo', '01234-567', 'Próximo ao mercado', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO address (id, street, number, neighborhood, city, cep, reference, created_at, updated_at)
VALUES (RANDOM_UUID(), 'Av. Principal', '456', 'Jardim Botânico', 'São Paulo', '04567-890', 'Em frente à praça', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Pontos de Venda
INSERT INTO sale_point (id, name, address_id, created_at, updated_at)
SELECT RANDOM_UUID(), 'Ponto Central', 
       (SELECT id FROM address WHERE street = 'Rua das Flores' LIMIT 1),
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM address WHERE street = 'Rua das Flores');

INSERT INTO sale_point (id, name, address_id, created_at, updated_at)
SELECT RANDOM_UUID(), 'Ponto Jardim', 
       (SELECT id FROM address WHERE street = 'Av. Principal' LIMIT 1),
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM address WHERE street = 'Av. Principal');

-- Cursos
INSERT INTO course (id, title, description, datetime, address_id, created_at, updated_at)
SELECT RANDOM_UUID(), 'Cultivo Orgânico', 'Aprenda técnicas de cultivo orgânico', 
       TIMESTAMP '2025-11-20 14:00:00',
       (SELECT id FROM address WHERE street = 'Rua das Flores' LIMIT 1),
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM address WHERE street = 'Rua das Flores');

INSERT INTO course (id, title, description, datetime, address_id, created_at, updated_at)
SELECT RANDOM_UUID(), 'Queijos Artesanais', 'Workshop de produção de queijos', 
       TIMESTAMP '2025-11-25 09:00:00',
       (SELECT id FROM address WHERE street = 'Av. Principal' LIMIT 1),
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM address WHERE street = 'Av. Principal');
