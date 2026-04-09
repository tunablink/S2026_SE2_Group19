# 🚀 Dự án Web Blog

## 🛠 Công nghệ sử dụng

- **Backend:** Node.js, Express.js
- **Database:** Microsoft SQL Server (MSSQL)
- **Tools:** Azure Data Studio / SSMS
- **Frontend:** HTML5, Fetch API

---

## 📂 Hướng dẫn cài đặt Database

### 1. Tạo cấu trúc bảng

Mở công cụ quản lý SQL và chạy script sau để khởi tạo Database và dữ liệu mẫu:

```sql
-- 1. Tạo Database hỗ trợ tiếng Việt (utf8mb4)
CREATE DATABASE IF NOT EXISTS blogweb
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE blogweb;

-- 2. Bảng roles (Quyền hạn)
CREATE TABLE roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
) ENGINE=InnoDB;

-- 3. Bảng users (Người dùng)
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    role_id INT,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- 4. Bảng modules (Danh mục bài học)
CREATE TABLE modules (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    thumbnail VARCHAR(255)
) ENGINE=InnoDB;

-- 5. Bảng lessons (Nội dung bài học)
CREATE TABLE lessons (
    id INT PRIMARY KEY AUTO_INCREMENT,
    module_id INT,
    title VARCHAR(255) NOT NULL,
    content LONGTEXT,
    FOREIGN KEY (module_id) REFERENCES modules(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 6. Chèn dữ liệu mẫu
-- Thêm Roles
INSERT INTO roles (name, description) VALUES ('Admin', 'Quản trị hệ thống'), ('User', 'Người dùng');

-- Thêm Users (Mật khẩu nên được mã hóa khi làm thật, ở đây để text thô để test)
INSERT INTO users (username, password, address, role_id)
VALUES ('hai_admin', '123456Abc@', 'Thái Bình', 1);

-- Thêm Modules
INSERT INTO modules (title, description)
VALUES ('OWASP Top 10', '10 lỗ hổng bảo mật web phổ biến nhất');

-- Thêm Lessons cho Module 1
INSERT INTO lessons (module_id, title, content)
VALUES (1, 'SQL Injection', 'Lý thuyết về cách tấn công và phòng chống SQLi');
```

### 2. Cấu hình MySQL

Để Node.js có thể kết nối thành công, bạn chỉ cần đảm bảo các điều kiện sau:

1.  **Khởi động MySQL Service**:
    - **Nếu dùng XAMPP**: Mở _XAMPP Control Panel_ và nhấn **Start** tại dòng MySQL.
    - **Nếu dùng Laragon**: Nhấn **Start All**.
    - Đảm bảo cổng (Port) hiển thị là `3306`.
2.  **Tạo Tài khoản Truy cập**:
    - Mặc định MySQL thường có user là `root` và mật khẩu để trống (hoặc `123456Abc@` tùy bạn đặt lúc cài).
    - Đảm bảo User này có quyền truy cập vào Database `blogweb`.
3.  **Cấu hình file `.env`**:
    - Tạo file `.env` trong thư mục gốc của project Backend và điền thông số sau:

```env
DB_HOST=localhost
DB_PORT=3306
DB_USER=root
DB_PASSWORD=123456Abc@
DB_NAME=blogweb
```

### 3. Khởi động server backend Nodejs

Chạy 2 lệnh sau

```nodejs
npm install
node server-my-sql.js
```

File index.html và test-api.js chỉ để kiểm tra api hoạt động hay chưa, không ảnh hưởng đến backend
