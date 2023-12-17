INSERT INTO t_user(id, c_username) VALUES (1, 'dbuser');

INSERT INTO t_user_password(id_user, c_password) VALUES (1, '{noop}password'); -- {noop} - Пароль не шифруется

-- Если у пользователя не будет хотя бы одной роли то Spring Security не даст зайти, так как у него нет ни каких прав то и делать он ничего не может
INSERT INTO t_user_authority(id_user, c_authority) VALUES (1, 'ROLE_DB_USER'), (1, 'ROLE_USER');