-- Dados de teste para Atrações (com IDs fixos para vincular às feiras)
INSERT INTO attractions (id, name, genre, date, time, image_url, description, created_at, updated_at)
VALUES 
('11111111-1111-1111-1111-111111111111', 'Turma do Pagode', 'Samba/Pagode', '2025-11-08', '22h', 'https://via.placeholder.com/400x300', 'Show especial de pagode', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('22222222-2222-2222-2222-222222222222', 'Banda de Forró', 'Forró', '2025-11-08', '20h', 'https://via.placeholder.com/400x300', 'Forró pé de serra', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('33333333-3333-3333-3333-333333333333', 'Grupo de Samba', 'Samba', '2025-11-15', '21h', 'https://via.placeholder.com/400x300', 'Samba raiz', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('44444444-4444-4444-4444-444444444444', 'DJ Local', 'Eletrônica', '2025-11-15', '23h', 'https://via.placeholder.com/400x300', 'Set de música eletrônica', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Dados de teste para Feiras (com IDs fixos para vincular às atrações)
-- expectedMerchants será calculado automaticamente baseado em usuários ativos
INSERT INTO fairs (id, date, start_time, end_time, expected_merchants, notes, status, created_at, updated_at)
VALUES 
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '2025-11-08', '06:00', '14:00', 0, 'Feira regular de sexta-feira', 'confirmed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '2025-11-15', '06:00', '14:00', 0, 'Feira especial com shows', 'scheduled', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('cccccccc-cccc-cccc-cccc-cccccccccccc', '2025-11-22', '06:00', '14:00', 0, 'Feira normal', 'scheduled', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('dddddddd-dddd-dddd-dddd-dddddddddddd', '2025-11-29', '06:00', '14:00', 0, 'Feira de fim de mês', 'scheduled', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Vincular atrações às feiras
INSERT INTO fair_attractions (fair_id, attraction_id) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111'), -- Feira 08/11 -> Turma do Pagode
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '22222222-2222-2222-2222-222222222222'), -- Feira 08/11 -> Banda de Forró
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '33333333-3333-3333-3333-333333333333'), -- Feira 15/11 -> Grupo de Samba
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '44444444-4444-4444-4444-444444444444'); -- Feira 15/11 -> DJ Local
