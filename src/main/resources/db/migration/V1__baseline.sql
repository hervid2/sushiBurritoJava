-- Baseline schema for Sushi Burrito, recreated from scratch on Railway.
--
-- The schema is renamed to English (snake_case) so it maps 1:1 to the JPA entities with no @Column
-- overrides. Normalisation fixes baked in here (vs. the old Spanish schema):
--   * orders drops the denormalised text summaries "producto"/"producto_categoria" (they violated 1NF
--     and duplicated order_items); they are reconstructed on demand by the v_order_summary view below.
--   * orders consolidates the old "hora_entrada" and "fecha_creacion" into a single created_at.
--   * invoices keeps subtotal/total_tax/tip/total as a legitimate accounting snapshot (frozen at
--     billing time), which is not problematic denormalisation.

CREATE TABLE users (
    id       INT          NOT NULL AUTO_INCREMENT,
    name     VARCHAR(100) NOT NULL,
    role     VARCHAR(20)  NOT NULL,
    email    VARCHAR(150) NOT NULL,
    password VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_user_email UNIQUE (email)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE categories (
    id   INT          NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE products (
    id          INT            NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100)   NOT NULL,
    ingredients VARCHAR(500),
    net_price   DECIMAL(10, 2) NOT NULL,
    sale_price  DECIMAL(10, 2) NOT NULL,
    tax         DECIMAL(10, 2) NOT NULL DEFAULT 0,
    category_id INT            NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE orders (
    id           INT         NOT NULL AUTO_INCREMENT,
    user_id      INT         NOT NULL,
    table_number INT         NOT NULL,
    status       VARCHAR(20) NOT NULL,
    created_at   DATETIME    NOT NULL,
    updated_at   DATETIME,
    PRIMARY KEY (id),
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE order_items (
    id         INT NOT NULL AUTO_INCREMENT,
    order_id   INT NOT NULL,
    product_id INT NOT NULL,
    quantity   INT NOT NULL,
    notes      VARCHAR(500),
    PRIMARY KEY (id),
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT fk_order_item_product FOREIGN KEY (product_id) REFERENCES products (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE invoices (
    id          INT            NOT NULL AUTO_INCREMENT,
    order_id    INT            NOT NULL,
    subtotal    DECIMAL(10, 2) NOT NULL,
    total_tax   DECIMAL(10, 2) NOT NULL,
    tip         DECIMAL(10, 2) NOT NULL DEFAULT 0,
    total       DECIMAL(10, 2) NOT NULL,
    invoiced_at DATETIME       NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_invoice_order UNIQUE (order_id),
    CONSTRAINT fk_invoice_order FOREIGN KEY (order_id) REFERENCES orders (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- Replaces the removed orders.producto / orders.producto_categoria columns: rebuilds the product and
-- category summaries from order_items at query time, so there is a single source of truth.
CREATE VIEW v_order_summary AS
SELECT o.id                                                                    AS order_id,
       GROUP_CONCAT(CONCAT(oi.quantity, ' ', p.name) ORDER BY oi.id SEPARATOR ', ') AS product_summary,
       GROUP_CONCAT(DISTINCT c.name ORDER BY c.name SEPARATOR ', ')            AS category_summary
FROM orders o
         LEFT JOIN order_items oi ON oi.order_id = o.id
         LEFT JOIN products p ON p.id = oi.product_id
         LEFT JOIN categories c ON c.id = p.category_id
GROUP BY o.id;
