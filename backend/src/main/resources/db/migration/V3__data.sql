INSERT INTO users.role (name, describe) VALUES
('ROLE_USER', '普通使用者，擁有基本功能權限'),
('ROLE_STAFF', '員工，擁有特定操作權限'),
('ROLE_MANAGER', '經理，擁有部門管理權限'),
('ROLE_DIRECTOR', '總監，擁有高階管理權限');

INSERT INTO users.permission (name, describe) VALUES
('READ', '讀取資料'),
('WRITE', '寫入資料'),
('DELETE', '刪除資料'),
('UPDATE', '更新資料');