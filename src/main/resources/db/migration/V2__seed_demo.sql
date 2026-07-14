-- Reproducible demo seed (separate from the schema in V1). Kept small and consistent for the video
-- demo; Iteration 8 rounds it out. Passwords are SHA-256 hashes of the plaintext noted beside each
-- user (the current scheme; Iteration 6 migrates to BCrypt).

-- Users: one per role. Plaintext -> admin123 / waiter123 / cook123.
INSERT INTO users (id, name, role, email, password) VALUES
    (1, 'Admin Demo',  'administrador', 'admin@sushiburrito.com',  '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9'),
    (2, 'Waiter Demo', 'mesero',        'waiter@sushiburrito.com', '1a303e87599632e4a19d3603ca36a6762dea330dccb8017796d6a3b9f82154ac'),
    (3, 'Cook Demo',   'cocinero',      'cook@sushiburrito.com',   'ca51f29a25456be733c3e7c48861c5e5693fc9d45d51dd5167483adb572a0b7b');

INSERT INTO categories (id, name) VALUES
    (1, 'Sushi'),
    (2, 'Burritos'),
    (3, 'Bebidas'),
    (4, 'Postres');

INSERT INTO products (id, name, ingredients, net_price, sale_price, tax, category_id) VALUES
    (1, 'California Roll',   'Arroz, alga nori, surimi, aguacate, pepino', 12000, 18000, 1440, 1),
    (2, 'Salmon Nigiri',     'Arroz, salmón fresco',                       10000, 16000, 1280, 1),
    (3, 'Burrito Teriyaki',  'Tortilla, pollo teriyaki, arroz, vegetales', 14000, 21000, 1680, 2),
    (4, 'Burrito Tempura',   'Tortilla, camarón tempura, arroz, aguacate', 16000, 24000, 1920, 2),
    (5, 'Té Verde',          'Té verde frío',                               3000,  6000,  480, 3),
    (6, 'Limonada de Coco',  'Limón, leche de coco',                        4000,  8000,  640, 3),
    (7, 'Mochi de Mango',    'Masa de arroz, helado de mango',              5000, 10000,  800, 4);

-- One in-progress order taken by the waiter, so the kitchen queue and v_order_summary are non-empty.
INSERT INTO orders (id, user_id, table_number, status, created_at, updated_at) VALUES
    (1, 2, 5, 'pendiente', '2026-07-14 12:30:00', '2026-07-14 12:30:00');

INSERT INTO order_items (id, order_id, product_id, quantity, notes) VALUES
    (1, 1, 1, 2, 'Sin pepino'),
    (2, 1, 3, 1, NULL),
    (3, 1, 5, 2, 'Con hielo');
