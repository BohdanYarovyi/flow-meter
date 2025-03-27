INSERT INTO t_accounts (c_created_at, c_updated_at, c_deleted,
                        c_login, c_email, c_password, c_firstname,
                        c_lastname, c_patronymic, c_date_of_birth, c_phone)
VALUES (now(), now(), false, 'maybyes', 'bogdan.yarovoy.01@gmail.com', '$2y$10$BcrFGOoSDDsuYfcGZRCJXu90XJjmsxWQM94jv9zpoQRcJ1lvIc5Lq',
        'Bohdan', 'Yarovyi', 'Serhiyovich','2001-11-07', '+380971773437'), -- password

       (now(), now(), false, 'alex_smith', 'alex.smith@example.com', '$2y$10$QHYWHikW1/P9S2ULHYquHuKwIXh8NZi7aQg/.PzRe.9WJXWXCBz0K',
        'Alex', 'Smith', 'Johnson', '1990-03-15', '+380971234567'),  -- password123

       (now(), now(), false, 'irina_kovalska', 'irina.kovalska@email.com', '$2y$10$noW4biH.qdB5zE3WSzYva.t6N6sE3wFsM0DJB448UQ6CdzEqoUu12',
        'Irina', 'Kovalska', 'Ivanivna', '1995-07-22', '+380972345678'), -- mypassword

       (now(), now(), false, 'maxim_tarasov', 'maxim.tarasov@mail.com', '$2y$10$T59gDlY2CASpVcG7.UilXu8Dd.Ecy3rusm1ZA68F4TGrVOBx5c88u',
        'Maxim', 'Tarasov', 'Petrovych', '1987-01-10', '+380973456789'), -- 1234password

       (now(), now(), false, 'yana_petrova', 'yana.petrova@gmail.com', '$2y$10$VBN96i1NgEJaopbT2P5tHeO0lNaKHjvy4KrERf6cOVLwsoUiTOgUm',
        'Yana', 'Petrova', 'Andriivna', '1992-05-30', '+380974567890'); -- securepassword

--      Login:           |   Password:       |
---------------------------------------------|
--      maybyes          |   password        |
--      alex_smith       |   password123     |
--      irina_kovalska   |   mypassword      |
--      maxim_tarasov    |   1234password    |
--      yana_petrova     |   securepassword  |
--      john_doe         |   SecurePass123   |

INSERT INTO t_roles ( c_name)
VALUES ('ADMIN'),
       ('USER');

INSERT INTO t_accounts_roles (account_id, role_id)
VALUES (1, 1),
       (1, 2),
       (2, 2),
       (3, 1),
       (3, 2),
       (4, 2),
       (5, 2);

