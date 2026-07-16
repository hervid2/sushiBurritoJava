-- Rounds out the demo data (Iteration 8): adds one already-paid order with its invoice so the
-- statistics dashboard and the sales history are not empty when the video is recorded. V2 keeps a
-- single in-progress order to drive the kitchen flow; this adds completed history alongside it.
--
-- Amounts mirror the app's invoice logic (GenerarFacturaView): subtotal = sum(sale_price * quantity),
-- tax = 8% of subtotal, tip = suggested 10% of subtotal, total = subtotal + tax + tip.
--   2 x California Roll (18000) + 1 x Limonada de Coco (8000) = 44000 subtotal
--   tax = 3520, tip = 4400, total = 51920

INSERT INTO orders (id, user_id, table_number, status, created_at, updated_at) VALUES
    (2, 2, 8, 'pagado', '2026-07-15 13:00:00', '2026-07-15 13:45:00');

INSERT INTO order_items (id, order_id, product_id, quantity, notes) VALUES
    (4, 2, 1, 2, NULL),
    (5, 2, 6, 1, 'Sin azúcar');

INSERT INTO invoices (id, order_id, subtotal, total_tax, tip, total, invoiced_at) VALUES
    (1, 2, 44000, 3520, 4400, 51920, '2026-07-15 13:45:00');
