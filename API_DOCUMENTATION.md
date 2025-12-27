# Tài liệu API (API Documentation)

Tài liệu này tổng hợp danh sách các API hiện có trong hệ thống, bao gồm endpoint, dữ liệu đầu vào/đầu ra, và logic xử lý.

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

