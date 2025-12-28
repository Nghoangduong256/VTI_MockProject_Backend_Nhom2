# Tài liệu API (API Documentation)

Tài liệu này tổng hợp danh sách các API hiện có trong hệ thống, bao gồm endpoint, dữ liệu đầu vào/đầu ra, và logic xử lý.

**Lưu ý**: Tất cả các lỗi code đã được fix và cập nhật:
- Import errors đã được sửa
- Method names đã được chuẩn hóa
- Interface-implementation mismatch đã được fix
- Package structure đã được đồng bộ
- Dependency injection style đã được chuẩn hóa

---

## 1. Authentication (Xác thực)

### Đăng ký tài khoản (Register)
- **Mô tả**: Tạo tài khoản người dùng mới.
- **Endpoint**: `POST /api/auth/register`
- **Đầu vào (Request Body)**:
  ```json
  {
    "userName": "newuser",
    "email": "newuser@example.com",
    "phone": "0987654321",
    "fullName": "Nguyen Van New",
    "passwordHash": "123456" 
  }
  ```
  *(Lưu ý: Trường `passwordHash` chứa mật khẩu dạng raw text từ client, server sẽ mã hóa sau)*
- **Đầu ra**: 200 OK (Empty Body)
- **Logic**:
  - Kiểm tra trùng username, email, phone.
  - Mã hóa mật khẩu.
  - Tạo User mới với role USER.

### Đăng nhập (Login)
- **Mô tả**: Xác thực người dùng và trả về JWT token để truy cập các API khác.
- **Endpoint**: `POST /api/auth/login`
- **Đầu vào (Request Body)**:
  ```json
  {
    "userName": "user",
    "password": "123456"
  }
  ```
- **Đầu ra (Response Body)**:
  ```json
  {
    "token": "eyJhbGciOiJIUz...",
    "type": "Bearer",
    "expiresIn": 86400000,
    "userName": "user",
    "email": "user@example.com",
    "fullName": "Nguyen Van User",
    "roles": ["USER"]
  }
  ```
- **Logic**: 
  - Kiểm tra userName và password trong database.
  - Nếu đúng, sinh JWT token.
  - Nếu sai, trả về lỗi 401.

---

## 2. User (Người dùng)

### Lấy thông tin cá nhân
- **Mô tả**: Lấy thông tin chi tiết của người dùng đang đăng nhập để hiển thị trên Dashboard.
- **Endpoint**: `GET /api/user/profile`
- **Đầu vào**: Header `Authorization: Bearer <token>`
- **Đầu ra**:
  ```json
  {
    "fullName": "Nguyen Van User",
    "email": "user@vti.com",
    "userName": "user",
    "avatarUrl": "https://i.pravatar.cc/150?u=user",
    "membership": "Gold"
  }
  ```
- **Logic**:
  - Lấy username từ Security Context (token).
  - Truy vấn bảng `users` để lấy thông tin.

---

## 3. Wallet (Ví điện tử)

### Xem số dư
- **Mô tả**: Lấy số dư hiện tại của ví và phần trăm thay đổi (dummy) để hiển thị biểu đồ.
- **Endpoint**: `GET /api/wallet/balance`
- **Đầu vào**: Header `Authorization: Bearer <token>`
- **Đầu ra**:
  ```json
  {
    "balance": 1500000.0,
    "monthlyChangePercent": 2.5
  }
  ```
- **Logic**:
  - Tìm ví (`wallets`) thuộc về người dùng hiện tại.
  - Trả về `balance` và số liệu thống kê.

---

## 4. Cards (Thẻ ngân hàng)

### Danh sách thẻ
- **Mô tả**: Lấy danh sách các thẻ/tài khoản ngân hàng đã liên kết.
- **Endpoint**: `GET /api/cards`
- **Đầu vào**: Header `Authorization: Bearer <token>`
- **Đầu ra**:
  ```json
  [
    {
      "id": "uuid...",
      "last4": "3210",
      "holderName": "NGUYEN VAN USER",
      "type": "Debit",
      "bankName": "TPBank",
      "status": "ACTIVE",
      "expirationDate": "12/25"
    }
  ]
  ```
- **Logic**:
  - Truy vấn bảng `cards`/`bank_accounts` theo user name.

### Thêm thẻ mới
- **Mô tả**: Liên kết thẻ/tài khoản ngân hàng mới.
- **Endpoint**: `POST /api/cards`
- **Đầu vào (Request Body)**:
  ```json
  {
    "cardNumber": "1234567890123456",
    "holderName": "NGUYEN VAN USER",
    "expiryDate": "12/25",
    "cvv": "123",
    "type": "Debit",
    "bankName": "TPBank"
  }
  ```
- **Đầu ra**: 200 OK với thông tin thẻ đã tạo.
- **Logic**:
  - Lưu thông tin thẻ vào database.

---

## 5. Contacts (Danh bạ thụ hưởng)

### Danh sách chuyển tiền nhanh
- **Mô tả**: Lấy danh sách người nhận thường xuyên (Frequent Contacts) cho chức năng Quick Transfer.
- **Endpoint**: `GET /api/contacts/frequent?limit=5`
- **Đầu vào**: 
  - Query param `limit` (mặc định 5).
  - Header `Authorization: Bearer <token>`
- **Đầu ra**:
  ```json
  [
    {
      "id": "uuid...",
      "name": "Friend One",
      "avatarUrl": "https://..."
    }
  ]
  ```
- **Logic**:
  - Truy vấn bảng `contacts` của user, giới hạn số lượng trả về.

---

## 6. Transactions (Giao dịch)

### Lịch sử giao dịch
- **Mô tả**: Lấy danh sách giao dịch có phân trang.
- **Endpoint**: `GET /api/transactions?page=0&size=10`
- **Đầu vào**: Params `page`, `size`.
- **Đầu ra**:
  ```json
  {
    "content": [
      {
        "id": "uuid...",
        "type": "DEPOSIT",
        "category": "General",
        "amount": 500000.0,
        "date": "2024-12-20T...",
        "status": "COMPLETED",
        "direction": "IN"
      }
    ],
    ...
  }
  ```
- **Logic**:
  - Lấy tất cả giao dịch trong bảng `transactions` thuộc ví của user.
  - Sắp xếp theo ngày tạo mới nhất.

### Chuyển tiền (Transfer)
- **Mô tả**: Chuyển tiền từ ví người dùng sang người dùng khác trong hệ thống.
- **Endpoint**: `POST /api/transactions/transfer`
- **Đầu vào**:
  ```json
  {
    "toUserId": 123,
    "amount": 50000.0,
    "note": "Tien an trua"
  }
  ```
  *(Lưu ý: `toUserId` là ID (Integer) của người nhận, không phải username)*
- **Đầu ra**: 200 OK (Empty Body)
- **Logic**:
  - Kiểm tra số dư ví người gửi (`balance` >= `amount`).
  - Trừ tiền ví người gửi, cộng tiền ví người nhận.
  - Tạo 2 bản ghi `Transaction`: 1 bản ghi `TRANSFER_OUT` cho người gửi, 1 bản ghi `TRANSFER_IN` cho người nhận.
  - Sử dụng `@Transactional`.

### Nạp tiền (Topup)
- **Mô tả**: Nạp tiền vào ví từ thẻ liên kết.
- **Endpoint**: `POST /api/transactions`
- **Đầu vào**:
  ```json
  {
    "type": "topup",
    "amount": 100000.0,
    "sourceCardId": "uuid-card"
  }
  ```
- **Đầu ra**: 200 OK
- **Logic**:
  - Cộng tiền vào ví, tạo bản ghi `Transaction` loại `DEPOSIT`.
  - Chỉ hỗ trợ `type`: "topup" hiện tại.

---

## 7. E-Wallet Operations (Ví điện tử)

### Nạp tiền vào ví (Deposit)
- **Mô tả**: Nạp tiền vào ví từ nguồn bên ngoài.
- **Endpoint**: `POST /api/E-Wallet/deposits`
- **Đầu vào (Request Body)**:
  ```json
  {
    "walletId": 1,
    "amount": 100000.0
  }
  ```
- **Đầu ra**:
  ```json
  {
    "message": "Deposit successful",
    "newBalance": 1600000.0
  }
  ```
- **Logic**: 
  - Cộng tiền vào ví balance
  - Tạo transaction record loại DEPOSIT

### Xem thông tin ví
- **Mô tả**: Lấy thông tin chi tiết của ví theo ID.
- **Endpoint**: `GET /api/E-Wallet/deposits/wallet/{id}`
- **Đầu ra**:
  ```json
  {
    "id": 1,
    "code": "WALLET001",
    "currency": "VND",
    "balance": 1500000.0,
    "availableBalance": 1500000.0,
    "status": "ACTIVE"
  }
  ```

### Lịch sử nạp tiền gần đây
- **Mô tả**: Lấy lịch sử các giao dịch nạp tiền gần đây của ví.
- **Endpoint**: `GET /api/E-Wallet/deposits/wallet/{id}/recent-deposits`
- **Đầu ra**:
  ```json
  [
    {
      "id": 1,
      "amount": 500000.0,
      "referenceId": "DEMO_DEPOSIT_1640995200000",
      "status": "COMPLETED",
      "createdAt": "2024-01-15T10:30:00"
    }
  ]
  ```

---

## 8. User Profile & Account Management

### Lấy thông tin người dùng hiện tại
- **Mô tả**: Lấy thông tin chi tiết của người dùng đang đăng nhập.
- **Endpoint**: `GET /api/me`
- **Đầu vào**: Header `Authorization: Bearer <token>`
- **Đầu ra**:
  ```json
  {
    "id": 2,
    "email": "user@vti.com",
    "fullName": "Nguyen Van User",
    "phone": "0987654321",
    "username": "user",
    "wallet": {
      "id": 2,
      "balance": 1500000.0,
      "currency": "VND",
      "status": "ACTIVE"
    }
  }
  ```

---

## 9. Bank Account Management

### Lấy danh sách tài khoản ngân hàng
- **Mô tả**: Lấy danh sách các tài khoản ngân hàng của người dùng theo userId.
- **Endpoint**: `GET /api/bank-account?userId={userId}`
- **Đầu ra**:
  ```json
  [
    {
      "id": 1,
      "code": "BANK001",
      "bankCode": "TPB",
      "bankName": "TPBank",
      "accountNumber": "9876543210",
      "accountName": "NGUYEN VAN USER",
      "status": "ACTIVE",
      "createdAt": "2024-01-01T00:00:00"
    }
  ]
  ```

---

## 10. Error Responses

### Common Error Format
- **Đầu ra (Error Response)**:
  ```json
  {
    "timestamp": "2024-01-15T10:30:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Invalid username or password",
    "path": "/api/auth/login"
  }
  ```

### Common Error Codes
- `400 Bad Request`: Dữ liệu đầu vào không hợp lệ
- `401 Unauthorized`: Token không hợp lệ hoặc hết hạn
- `403 Forbidden`: Không có quyền truy cập
- `404 Not Found`: Resource không tồn tại
- `500 Internal Server Error`: Lỗi server

---

## 11. Security Notes

- **JWT Token**: Có hiệu lực 15 ngày
- **Password Encryption**: Sử dụng BCrypt
- **PIN Generation**: Tự động tạo 6 chữ số ngẫu nhiên khi đăng ký
- **Role-based Access**: USER, ADMIN, SUPPORT
- **Transaction Security**: Sử dụng @Transactional và pessimistic locking

---

## 12. Database Schema Summary

### Main Entities:
- **users**: Thông tin người dùng
- **wallets**: Ví điện tử (1-1 với users)
- **transactions**: Lịch sử giao dịch
- **bank_accounts**: Tài khoản ngân hàng liên kết
- **cards**: Thẻ tín dụng/ghi nợ
- **contacts**: Danh bạ chuyển tiền

### Enums:
- **Role**: USER, ADMIN, SUPPORT
- **WalletStatus**: ACTIVE, FROZEN, CLOSED
- **TransactionType**: DEPOSIT, WITHDRAW, TRANSFER_IN, TRANSFER_OUT
- **TransactionStatus**: PENDING, COMPLETED, FAILED
- **TransactionDirection**: IN, OUT
- **BankAccountStatus**: ACTIVE, PENDING, REVOKED
- **CardStatus**: ACTIVE, INACTIVE, LOCKED

---

