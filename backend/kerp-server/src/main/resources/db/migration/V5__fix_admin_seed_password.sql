UPDATE sys_user
SET password_hash = '$2a$10$ctFGhfDkjVA58RuHDlvnaO6aOeNF/yT5szrjNALtNdGmxjLe0jL12'
WHERE username = 'admin'
  AND password_hash = '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi6C9T7VDaRao7IhiHBpjz2uVH54cam';
