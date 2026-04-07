# Device Maintenance System - Backend

## Tech Stack
- Java 17, Spring Boot 3.2.0
- Spring Security + JWT
- Spring Data JPA + MySQL 8
- Swagger/OpenAPI 3
- Lombok, ModelMapper

---

## Setup & Chay Project

### 1. Tao Database MySQL
```sql
CREATE DATABASE device_maintenance_db 
  CHARACTER SET utf8mb4 
  COLLATE utf8mb4_unicode_ci;
```

### 2. Cau hinh application.properties
Chinh sua file `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/device_maintenance_db?...
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

### 3. Chay project
```bash
cd device-maintenance
mvn spring-boot:run
```
Server khoi dong tai: `http://localhost:8080/api`

### 4. Du lieu mau tu dong tao
Khi khoi dong lan dau, he thong tu dong tao:
| Account     | Password     | Role            |
|-------------|--------------|-----------------|
| admin       | admin123     | ROLE_ADMIN      |
| manager     | manager123   | ROLE_MANAGER    |
| tech1       | tech123      | ROLE_TECHNICIAN |
| tech2       | tech123      | ROLE_TECHNICIAN |
| user1       | user123      | ROLE_USER       |

Va 4 thiet bi, 3 su co, 3 lich bao tri mau.

---

## Test API voi Postman

1. Import file `DeviceMaintenanceAPI.postman_collection.json` vao Postman
2. Chon request **"Login - Admin"** va Send
3. Token tu dong luu vao bien `{{token}}`
4. Tat ca request khac tu dong dung token nay

---

## API Endpoints tong quan

### Authentication (`/auth`)
| Method | Path             | Mo ta                  | Auth |
|--------|------------------|------------------------|------|
| POST   | /login           | Dang nhap              | No   |
| POST   | /register        | Dang ky                | No   |
| POST   | /refresh         | Lam moi token          | No   |

### Users (`/users`)
| Method | Path                   | Mo ta                      | Role           |
|--------|------------------------|----------------------------|----------------|
| GET    | /me                    | Xem thong tin ca nhan      | All            |
| PUT    | /me                    | Cap nhat ho so             | All            |
| POST   | /me/change-password    | Doi mat khau               | All            |
| POST   | /me/avatar             | Upload avatar              | All            |
| GET    | /                      | Danh sach users            | Admin/Manager  |
| GET    | /{id}                  | User theo ID               | Admin/Manager  |
| POST   | /admin/create          | Tao tai khoan              | Admin          |
| PATCH  | /{id}/toggle-status    | Bat/Tat tai khoan          | Admin          |
| DELETE | /{id}                  | Xoa user                   | Admin          |
| GET    | /technicians           | Danh sach ky thuat vien    | All            |

### Devices (`/devices`)
| Method | Path           | Mo ta                   | Role           |
|--------|----------------|-------------------------|----------------|
| GET    | /              | Danh sach (co loc)      | All            |
| GET    | /active        | Thiet bi dang hoat dong | All            |
| GET    | /{id}          | Chi tiet theo ID        | All            |
| GET    | /code/{code}   | Tim theo ma thiet bi    | All            |
| POST   | /              | Them moi                | Admin/Manager  |
| PUT    | /{id}          | Cap nhat                | Admin/Manager  |
| PATCH  | /{id}/status   | Cap nhat trang thai     | Admin/Mgr/Tech |
| POST   | /{id}/image    | Upload anh              | Admin/Manager  |
| DELETE | /{id}          | Xoa thiet bi            | Admin/Manager  |

### Maintenance Schedules (`/maintenance-schedules`)
| Method | Path              | Mo ta               | Role           |
|--------|-------------------|---------------------|----------------|
| GET    | /                 | Danh sach (co loc)  | All            |
| GET    | /upcoming         | Lich sap toi        | All            |
| GET    | /overdue          | Lich qua han        | All            |
| GET    | /{id}             | Chi tiet            | All            |
| POST   | /                 | Tao lich moi        | Admin/Manager  |
| PUT    | /{id}             | Cap nhat lich       | Adm/Mgr/Tech   |
| PATCH  | /{id}/complete    | Hoan thanh bao tri  | Adm/Mgr/Tech   |
| DELETE | /{id}             | Xoa lich            | Admin/Manager  |

### Incidents (`/incidents`)
| Method | Path              | Mo ta               | Role           |
|--------|-------------------|---------------------|----------------|
| GET    | /                 | Danh sach (co loc)  | All            |
| GET    | /{id}             | Chi tiet            | All            |
| POST   | /                 | Bao cao su co       | All            |
| PUT    | /{id}             | Cap nhat su co      | All            |
| PATCH  | /{id}/assign      | Phan cong tech      | Admin/Manager  |
| PATCH  | /{id}/resolve     | Giai quyet su co    | All            |
| POST   | /{id}/image       | Upload anh          | All            |
| DELETE | /{id}             | Xoa su co           | Admin/Manager  |

### Repair History (`/repair-histories`)
| Method | Path                        | Mo ta            | Role           |
|--------|-----------------------------|------------------|----------------|
| GET    | /                           | Danh sach        | All            |
| GET    | /{id}                       | Chi tiet         | All            |
| GET    | /device/{id}/total-cost     | Tong chi phi     | All            |
| POST   | /                           | Ghi nhan sua     | All            |
| PUT    | /{id}                       | Cap nhat         | All            |
| DELETE | /{id}                       | Xoa              | Admin/Manager  |

### Forum (`/forum`)
| Method | Path                     | Mo ta           | Role |
|--------|--------------------------|-----------------|------|
| GET    | /posts                   | Danh sach bai   | All  |
| GET    | /posts/{id}              | Chi tiet bai    | All  |
| POST   | /posts                   | Tao bai viet    | All  |
| PUT    | /posts/{id}              | Cap nhat bai    | All  |
| DELETE | /posts/{id}              | Xoa bai         | All  |
| GET    | /posts/{id}/comments     | Binh luan       | All  |
| POST   | /posts/{id}/comments     | Them binh luan  | All  |
| PUT    | /comments/{id}           | Sua binh luan   | All  |
| DELETE | /comments/{id}           | Xoa binh luan   | All  |

### Support Tickets (`/support-tickets`)
| Method | Path              | Mo ta           | Role           |
|--------|-------------------|-----------------|----------------|
| GET    | /                 | Tat ca tickets  | Admin/Manager  |
| GET    | /my-tickets       | Ticket cua toi  | All            |
| GET    | /{id}             | Chi tiet        | All            |
| POST   | /                 | Tao yeu cau     | All            |
| PUT    | /{id}/respond     | Admin phan hoi  | Admin/Manager  |
| DELETE | /{id}             | Xoa ticket      | Admin          |

### Dashboard (`/dashboard`)
| Method | Path  | Mo ta          | Role          |
|--------|-------|----------------|---------------|
| GET    | /     | Thong ke TQ    | Admin/Manager |

---

## Query Parameters pho bien

### Phan trang
- `page` (default: 0) - So trang bat dau tu 0
- `size` (default: 10) - So phan tu moi trang
- `sortBy` - Truong sap xep
- `direction` - asc hoac desc

### Tim kiem thiet bi
- `keyword` - Tim theo ten, ma, loai thiet bi
- `status` - ACTIVE | INACTIVE | UNDER_MAINTENANCE | BROKEN | DISPOSED
- `location` - Tim theo vi tri

### Tim kiem su co
- `keyword` - Tim theo tieu de
- `status` - REPORTED | IN_PROGRESS | RESOLVED | CLOSED
- `severity` - LOW | MEDIUM | HIGH | CRITICAL
- `deviceId` - Loc theo thiet bi

---

## Swagger UI
Sau khi chay: `http://localhost:8080/api/swagger-ui.html`

## Auto Scheduler
- **8:00 AM hang ngay**: Tu dong tao lich bao tri cho cac thiet bi co chu ky
- **Moi gio**: Cap nhat trang thai OVERDUE cho lich qua han
