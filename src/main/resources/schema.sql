CREATE TABLE IF NOT EXISTS task
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title       VARCHAR(300) NOT NULL,
    description VARCHAR(300) NOT NULL,
    user_id     BIGINT       NOT NULL,
    status      VARCHAR(50)  NOT NULL
);