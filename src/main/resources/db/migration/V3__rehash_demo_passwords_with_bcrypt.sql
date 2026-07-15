-- Re-hashes the demo users seeded in V2 (which used unsalted SHA-256) with BCrypt.
--
-- V2 is immutable because it is already applied, so the new hashes are written here instead. The
-- plaintext passwords are unchanged (admin123 / waiter123 / cook123), so the demo script still works;
-- only the stored representation is hardened.
--
-- The "{bcrypt}" prefix is the algorithm identifier read by DelegatingPasswordEncoder: it is what
-- lets a future migration recognise and upgrade these hashes in turn. Accounts created outside this
-- seed keep their SHA-256 hash and are upgraded transparently on their next successful login.

UPDATE users SET password = '{bcrypt}$2a$10$UzNCJE/BaytQy5WN/QAjoeGgYW9gI.GnB/Iiu7b7JratTX4mq/sqC'
WHERE email = 'admin@sushiburrito.com';

UPDATE users SET password = '{bcrypt}$2a$10$QvHS5KY6.m6t.O9T9XDOIuiO8TQzOZH27.ix27Pkdkg6Hs8Tr/UJ2'
WHERE email = 'waiter@sushiburrito.com';

UPDATE users SET password = '{bcrypt}$2a$10$Pol14KZ7FFuFKUEJTwrfQubsl8L5xDUnsIi9anjid8FDzZNLS6pTy'
WHERE email = 'cook@sushiburrito.com';
