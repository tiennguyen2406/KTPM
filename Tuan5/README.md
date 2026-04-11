# Docker Compose - Tuần 5

Hướng dẫn chạy từng bài tập Docker Compose

---

## Bài 4: Chạy ứng dụng Node.js với Express

```bash
cd Bai4
docker-compose up -d
```

**Truy cập:** http://localhost:3000

**Dừng:**
```bash
docker-compose down
```

---

## Bài 5: Chạy Redis

```bash
cd Bai5
docker-compose up -d
```

**Kết nối:** localhost:6379

**Kiểm tra:**
```bash
docker exec -it redis-server redis-cli ping
```

**Dừng:**
```bash
docker-compose down
```

---

## Bài 6: Chạy WordPress với MySQL

```bash
cd Bai6
docker-compose up -d
```

**Truy cập WordPress:** http://localhost:8080

**Cài đặt ban đầu:**
- Database: wordpress
- User: wordpress
- Password: wordpress
- Host: mysql

**Dừng:**
```bash
docker-compose down
```

---

## Bài 7: Chạy MongoDB với Mongo Express

```bash
cd Bai7
docker-compose up -d
```

**Mongo Express:** http://localhost:8081
- Username: admin
- Password: admin123

**Kết nối MongoDB:** mongodb://admin:admin123@localhost:27017/

**Dừng:**
```bash
docker-compose down
```

---

## Bài 8: Chạy Node.js kết nối với MySQL

```bash
cd Bai8
docker-compose up -d
```

**Truy cập API:** http://localhost:3000

**Kiểm tra health:** http://localhost:3000/health

**Dừng:**
```bash
docker-compose down
```

---

## Bài 9: Chạy ứng dụng Python Flask

```bash
cd Bai9
docker-compose up -d
```

**Truy cập:** http://localhost:5000

**Kiểm tra health:** http://localhost:5000/health

**Dừng:**
```bash
docker-compose down
```

---

## Bài 10: Lưu trữ dữ liệu với Docker Volumes

```bash
cd Bai10
docker-compose up -d
```

**Kết nối MySQL:**
- Host: localhost
- Port: 3306
- User: root / user
- Password: rootpassword / password
- Database: mydb

**Dừng:**
```bash
docker-compose down
```

**Dữ liệu được lưu trong volume `mysql_data`**

---

## Bài 11: Chạy PostgreSQL với Adminer

```bash
cd Bai11
docker-compose up -d
```

**Adminer:** http://localhost:8083

**Cấu hình kết nối:**
- System: PostgreSQL
- Server: postgres
- Username: user
- Password: password
- Database: mydb

**Dừng:**
```bash
docker-compose down
```

---

## Bài 12: Giám sát container với Prometheus và Grafana

```bash
cd Bai12
docker-compose up -d
```

**Prometheus:** http://localhost:9090

**Grafana:** http://localhost:3000
- Username: admin
- Password: admin

**Thêm data source trong Grafana:**
1. Configuration → Data Sources
2. Add data source: Prometheus
3. URL: http://prometheus:9090
4. Save & Test

**Dừng:**
```bash
docker-compose down
```

---

## Bài 13: Chạy ứng dụng React với Nginx

```bash
cd Bai13
docker-compose up -d
```

**Truy cập:** http://localhost:8080

**Dừng:**
```bash
docker-compose down
```

---

## Bài 14: Cấu hình mạng riêng giữa các container

```bash
cd Bai14
docker-compose up -d
```

**Kiểm tra mạng:**
```bash
docker network ls
docker network inspect bai14_private-network
```

**Test kết nối giữa container:**
```bash
docker exec -it network-client ping api-server
docker exec -it network-client ping web-server
```

**Dừng:**
```bash
docker-compose down
```

---

## Bài 15: Giới hạn tài nguyên cho container

```bash
cd Bai15
docker-compose up -d
```

**Kiểm tra giới hạn tài nguyên:**
```bash
docker inspect redis-limited --format='{{.HostConfig.Resources}}'
```

**Dừng:**
```bash
docker-compose down
```

---

## Lệnh Docker Compose hữu ích

```bash
# Chạy tất cả services
docker-compose up -d

# Xem logs
docker-compose logs

# Xem logs theo service
docker-compose logs [service_name]

# Dừng và xóa containers
docker-compose down

# Dừng, xóa containers và volumes
docker-compose down -v

# Khởi động lại
docker-compose restart

# Liệt kê containers đang chạy
docker-compose ps

# Kiểm tra trạng thái
docker ps
```
