CREATE TABLE IF NOT EXISTS user_products (
    id UUID PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    product_id INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_products_user_id ON user_products(user_id);
CREATE INDEX idx_user_products_product_id ON user_products(product_id);

-- Agregar la columna product_id si no existe
DO $$ 
BEGIN 
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name='user_products' AND column_name='product_id'
    ) THEN
        ALTER TABLE user_products ADD COLUMN product_id INTEGER;
    END IF;
END $$;

-- Crear el índice si no existe
CREATE INDEX IF NOT EXISTS idx_user_products_product_id ON user_products(product_id);