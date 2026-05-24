CREATE TABLE url (
  id_url INT AUTO_INCREMENT PRIMARY KEY,
  url VARCHAR(255) NOT NULL,
  short_code VARCHAR(10) NOT NULL UNIQUE,
  created_at DATETIME(6),
  updated_at DATETIME(6),
  expiration_date DATETIME(6),
  access_count INT NOT NULL
);