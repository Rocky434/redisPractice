CREATE TABLE users.role (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    describe VARCHAR(100) NOT NULL
);

CREATE TABLE users.permission (
    id SERIAL PRIMARY KEY,
    name VARCHAR(30) UNIQUE NOT NULL,
    describe VARCHAR(100) NOT NULL
);

CREATE TABLE users.role_permissions (
    id SERIAL PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    FOREIGN KEY (role_id) REFERENCES users.role(id),
    FOREIGN KEY (permission_id) REFERENCES users.permission(id)
);

CREATE TABLE users."user" (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE users.user_roles (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users."user"(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES users.role(id)
);