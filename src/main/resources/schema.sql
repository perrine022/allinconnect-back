CREATE TABLE IF NOT EXISTS subscription_plans (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255),
    description TEXT,
    price DOUBLE,
    category VARCHAR(50),
    duration VARCHAR(50),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS cards (
    id BIGINT NOT NULL AUTO_INCREMENT,
    card_number VARCHAR(255) NOT NULL,
    type VARCHAR(50),
    owner_id BIGINT,
    PRIMARY KEY (id),
    UNIQUE (card_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    address VARCHAR(255),
    city VARCHAR(255),
    latitude DOUBLE,
    longitude DOUBLE,
    birth_date DATE,
    user_type VARCHAR(50),
    has_connected_before BOOLEAN DEFAULT FALSE,
    subscription_type VARCHAR(50),
    subscription_plan_id BIGINT,
    subscription_date DATETIME,
    renewal_date DATETIME,
    subscription_amount DOUBLE,
    profession VARCHAR(255),
    category VARCHAR(50),
    establishment_name VARCHAR(255),
    establishment_description TEXT,
    phone_number VARCHAR(50),
    website VARCHAR(255),
    instagram VARCHAR(255),
    opening_hours VARCHAR(255),
    reset_token VARCHAR(255),
    reset_token_expiry DATETIME,
    referral_code VARCHAR(255),
    referrer_id BIGINT,
    card_id BIGINT,
    wallet_balance DOUBLE DEFAULT 0.0,
    PRIMARY KEY (id),
    UNIQUE (email),
    CONSTRAINT FK_users_subscription_plan FOREIGN KEY (subscription_plan_id) REFERENCES subscription_plans (id),
    CONSTRAINT FK_users_card FOREIGN KEY (card_id) REFERENCES cards (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE users ADD CONSTRAINT FK_users_referrer FOREIGN KEY (referrer_id) REFERENCES users (id) ON DELETE SET NULL;

CREATE TABLE IF NOT EXISTS user_favorites (
    user_id BIGINT NOT NULL,
    favorite_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, favorite_id),
    CONSTRAINT FK_user_favorites_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT FK_user_favorites_favorite FOREIGN KEY (favorite_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS ratings (
    id BIGINT NOT NULL AUTO_INCREMENT,
    score INTEGER,
    comment TEXT,
    rater_id BIGINT NOT NULL,
    rated_id BIGINT NOT NULL,
    created_at DATETIME,
    PRIMARY KEY (id),
    UNIQUE KEY UK_rater_rated (rater_id, rated_id),
    CONSTRAINT FK_ratings_rater FOREIGN KEY (rater_id) REFERENCES users (id),
    CONSTRAINT FK_ratings_rated FOREIGN KEY (rated_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS savings (
    id BIGINT NOT NULL AUTO_INCREMENT,
    shop_name VARCHAR(255),
    description TEXT,
    amount DOUBLE,
    date DATETIME,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_savings_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS wallet_history (
    id BIGINT NOT NULL AUTO_INCREMENT,
    amount DOUBLE NOT NULL,
    description TEXT,
    date DATETIME NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_wallet_history_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS wallet_requests (
    id BIGINT NOT NULL AUTO_INCREMENT,
    total_amount DOUBLE NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL,
    user_id BIGINT NOT NULL,
    professionals TEXT,
    PRIMARY KEY (id),
    CONSTRAINT FK_wallet_requests_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS card_invited_emails (
    card_id BIGINT NOT NULL,
    email VARCHAR(255) NOT NULL,
    PRIMARY KEY (card_id, email),
    CONSTRAINT FK_card_invited_emails_card FOREIGN KEY (card_id) REFERENCES cards (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE cards ADD CONSTRAINT FK_cards_owner FOREIGN KEY (owner_id) REFERENCES users (id);

CREATE TABLE IF NOT EXISTS offers (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    price DOUBLE,
    start_date DATETIME,
    end_date DATETIME,
    image_url VARCHAR(255),
    type VARCHAR(50),
    status VARCHAR(50),
    is_featured BOOLEAN DEFAULT FALSE,
    professional_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_offers_professional FOREIGN KEY (professional_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS payments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    amount DOUBLE NOT NULL,
    payment_date DATETIME NOT NULL,
    status VARCHAR(50),
    stripe_payment_intent_id VARCHAR(255),
    user_id BIGINT NOT NULL,
    subscription_plan_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT FK_payments_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT FK_payments_subscription_plan FOREIGN KEY (subscription_plan_id) REFERENCES subscription_plans (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
