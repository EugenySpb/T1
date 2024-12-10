INSERT INTO task (title, description, user_id, status)
VALUES ('Задача 1', 'Разработать приложение', 1, 'CREATED'),
       ('Задача 2', 'Протестировать приложение', 2, 'CREATED'),
       ('Задача 3', 'Пофиксить баги', 3, 'CREATED');

INSERT INTO users (login, email, password)
VALUES ('admin', 'admin@mail.ru', '$2a$10$Bpz2wFT0pPs6D9tgN0vza.oFaJQHo.ZqM/iMZ5qtiXMCjYtgWpGXO'),
       ('moderator', 'moderator@mail.ru', '$2a$10$j8MVNzn3ooDYM528aR0osekiRtJK5DpitO9c.eJoOuaRUiD.vYiDa'),
       ('user', 'user@mail.ru', '$2a$10$isqPAfKQDoGbJmx5ptPZbOcjjcKgn1mZczdiu3YF56c3LoGz0IbOS')
ON CONFLICT DO NOTHING;

INSERT INTO role (role)
VALUES ('ROLE_USER'),
       ('ROLE_ADMIN'),
       ('ROLE_MODERATOR')
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
VALUES (1, 2),
       (2, 3),
       (3, 1)
ON CONFLICT DO NOTHING;