# Environment Setup Instructions

## 📋 Cách setup môi trường development

### 1. Clone repository
```bash
git clone <repository-url>
cd themoviedb
```

### 2. Tạo file cấu hình
```bash
# Copy template file
cp src/main/resources/application.properties.template src/main/resources/application.properties

# Copy environment example
cp .env.example .env
```

### 3. Cấu hình database
Cập nhật file `.env` với thông tin database của bạn:
```env
DB_HOST=localhost
DB_PORT=3306
DB_NAME=themoviedb
DB_USERNAME=your_username
DB_PASSWORD=your_password
```

### 4. Cấu hình JWT
Tạo JWT secret key mới (khuyến nghị cho production):
```bash
# Tạo random secret key
echo "JWT_SECRET=$(openssl rand -base64 64)" >> .env
```

### 5. Cấu hình Cloudinary
Cập nhật thông tin Cloudinary trong file `.env`:
```env
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
```

### 6. Chạy ứng dụng
```bash
mvn spring-boot:run
```

## ⚠️ Lưu ý bảo mật

- **KHÔNG** commit file `.env` và `application.properties` lên Git
- **THAY ĐỔI** JWT secret key trong production
- **SỬ DỤNG** database credentials mạnh trong production
- **KIỂM TRA** .gitignore đã chứa các file nhạy cảm

## 🔧 Troubleshooting

### Lỗi MySQL connection
- Kiểm tra MySQL server đã chạy
- Xác nhận database `themoviedb` đã được tạo
- Kiểm tra username/password trong file `.env`

### Lỗi JWT
- Đảm bảo `JWT_SECRET` có đủ độ dài (tối thiểu 32 characters)
- Kiểm tra format base64 của secret key

### Lỗi Cloudinary
- Verify API credentials trên Cloudinary dashboard
- Kiểm tra network connection