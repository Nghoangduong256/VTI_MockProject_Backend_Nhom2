-- Drop the database if it already exists
DROP DATABASE IF EXISTS bank_db;

CREATE DATABASE IF NOT EXISTS bank_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE bank_db;

CREATE TABLE users (
  id CHAR(36) PRIMARY KEY,
  username VARCHAR(50) UNIQUE,
  email VARCHAR(100) UNIQUE NOT NULL,
  phone VARCHAR(20) UNIQUE,
  full_name VARCHAR(100),
  date_of_birth DATE,
  address VARCHAR(255),
  password_hash TEXT NOT NULL,
  pin_hash TEXT,
  role VARCHAR(20) NOT NULL,
  is_active BOOLEAN DEFAULT TRUE,
  is_verified BOOLEAN DEFAULT FALSE,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE wallets (
  id CHAR(36) PRIMARY KEY,
  user_id CHAR(36) NOT NULL,
  currency CHAR(3) DEFAULT 'VND',
  balance BIGINT DEFAULT 0,
  available_balance BIGINT DEFAULT 0,
  status ENUM('ACTIVE','LOCKED') DEFAULT 'ACTIVE',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_wallet_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE bank_accounts (
  id CHAR(36) PRIMARY KEY,
  user_id CHAR(36) NOT NULL,
  bank_code VARCHAR(20),
  bank_name VARCHAR(100),
  account_number VARCHAR(50),
  account_name VARCHAR(100),
  status ENUM('ACTIVE','INACTIVE') DEFAULT 'ACTIVE',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_bank_user FOREIGN KEY (user_id) REFERENCES users(id)
);


CREATE TABLE transactions (
  id CHAR(36) PRIMARY KEY,
  wallet_id CHAR(36) NOT NULL,
  type ENUM('DEPOSIT','WITHDRAW','TRANSFER') NOT NULL,
  direction ENUM('IN','OUT') NOT NULL,
  amount BIGINT NOT NULL,
  fee BIGINT DEFAULT 0,
  balance_before BIGINT,
  balance_after BIGINT,
  status ENUM('PENDING','SUCCESS','FAILED') DEFAULT 'PENDING',
  reference_id VARCHAR(100),
  idempotency_key VARCHAR(100),
  related_tx_id CHAR(36),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_tx_wallet FOREIGN KEY (wallet_id) REFERENCES wallets(id)
);

CREATE TABLE transfer_details (
  id CHAR(36) PRIMARY KEY,
  transaction_id CHAR(36) UNIQUE,
  counterparty_wallet_id CHAR(36),
  counterparty_user_id CHAR(36),
  note VARCHAR(255),
  method ENUM('QR','MANUAL'),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_transfer_tx FOREIGN KEY (transaction_id) REFERENCES transactions(id)
);


CREATE TABLE qr_codes (
  id CHAR(36) PRIMARY KEY,
  wallet_id CHAR(36) NOT NULL,
  code_value TEXT NOT NULL,
  type ENUM('STATIC','DYNAMIC'),
  expires_at DATETIME,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_qr_wallet FOREIGN KEY (wallet_id) REFERENCES wallets(id)
);


CREATE TABLE sessions (
  id CHAR(36) PRIMARY KEY,
  user_id CHAR(36) NOT NULL,
  device_info VARCHAR(255),
  ip_address VARCHAR(50),
  refresh_token_hash TEXT,
  revoked BOOLEAN DEFAULT FALSE,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  last_active_at DATETIME,
  CONSTRAINT fk_session_user FOREIGN KEY (user_id) REFERENCES users(id)
);


CREATE TABLE otp_requests (
  id CHAR(36) PRIMARY KEY,
  user_id CHAR(36) NOT NULL,
  purpose ENUM('LOGIN','RESET_PASSWORD','CHANGE_PIN'),
  otp_code VARCHAR(10),
  is_used BOOLEAN DEFAULT FALSE,
  expires_at DATETIME,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_otp_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE notifications (
  id CHAR(36) PRIMARY KEY,
  user_id CHAR(36) NOT NULL,
  type ENUM('SYSTEM','TRANSACTION','SECURITY'),
  title VARCHAR(255),
  content TEXT,
  is_read BOOLEAN DEFAULT FALSE,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_notify_user FOREIGN KEY (user_id) REFERENCES users(id)
);


CREATE TABLE admin_actions (
  id CHAR(36) PRIMARY KEY,
  admin_id CHAR(36) NOT NULL,
  action_type ENUM('LOCK_USER','UNLOCK_USER','RESET_PASSWORD','RESET_PIN'),
  target_type VARCHAR(50),
  target_id CHAR(36),
  reason VARCHAR(255),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_admin_user FOREIGN KEY (admin_id) REFERENCES users(id)
);


CREATE TABLE balance_change_logs (
  id CHAR(36) PRIMARY KEY,
  wallet_id CHAR(36) NOT NULL,
  transaction_id CHAR(36),
  delta BIGINT,
  balance_before BIGINT,
  balance_after BIGINT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_balance_wallet FOREIGN KEY (wallet_id) REFERENCES wallets(id),
  CONSTRAINT fk_balance_tx FOREIGN KEY (transaction_id) REFERENCES transactions(id)
);

ALTER TABLE users
MODIFY id CHAR(36) NOT NULL;
