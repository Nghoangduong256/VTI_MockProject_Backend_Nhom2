# API Documentation - E-Wallet Backend System

**Last Updated**: 2025-01-01
**Version**: 2.2

**Code Fixes Applied**:
- Fixed ambiguous mapping error between AuthController and RegisterController
- Removed duplicate RegisterController, kept AuthController as single entry point
- Updated register endpoint to use RegisterForm DTO instead of User entity
- Added ModelMapper for DTO to Entity conversion
- **Fixed QrWithAmountRequest - removed note field as requested**
- **Fixed POST /api/qr/wallet/with-amount to return proper DTO instead of String**
- **Ensured phone number = account number logic throughout system**
- **QR Base64 format standardized with data:image/png prefix**
- Package structure đã được đồng bộ
- Dependency injection style đã được chuẩn hóa

**New APIs Added**: QR Code APIs, Wallet Info APIs, Incoming Transactions APIs
- QR Code generation và download
- Wallet information display
- Transaction history for widgets
- QR image reading to JSON conversion

**Key Features**:
- **Phone Number = Account Number** throughout entire system
- **QR Code contains accountNumber and amount (no note)**
- **Base64 QR format**: `data:image/png;base64,iVBORw0KGgo...`
- **Consistent DTO responses across all APIs**

---

## API Summary (Tổng quan tất cả APIs)

### Authentication APIs
- `POST /api/auth/register` - Đăng ký tài khoản
- `POST /api/auth/login` - Đăng nhập

### User & Profile APIs  
- `GET /api/user/profile` - Lấy thông tin cá nhân
- `GET /api/me` - Lấy thông tin người dùng hiện tại

### Wallet APIs
- `GET /api/wallet/balance` - Xem số dư
- `GET /api/wallet/me` - Lấy thông tin ví đầy đủ **[NEW]**

### QR Code APIs **[NEW]**
- `GET /api/qr/wallet` - Tạo QR Code cho ví (Base64)
- `GET /api/qr/wallet/download` - Tải QR Image
- `POST /api/qr/wallet/with-amount` - Tạo QR với số tiền cố định
- `POST /api/qr/resolve` - Giải mã QR Payload
- `POST /api/qr/read-image` - Đọc ảnh QR thành JSON **[NEW]**

### Transaction APIs
- `GET /api/transactions` - Lịch sử giao dịch (phân trang)
- `GET /api/transactions/incoming` - Giao dịch đến gần đây **[NEW]**
- `POST /api/transactions/transfer` - Chuyển tiền
- `POST /api/transactions` - Nạp tiền (topup)

### E-Wallet Operations
- `POST /api/E-Wallet/deposits` - Nạp tiền vào ví
- `GET /api/E-Wallet/deposits/wallet/{id}` - Xem thông tin ví
- `GET /api/E-Wallet/deposits/wallet/{id}/recent-deposits` - Lịch sử nạp tiền gần đây

### Card & Bank Account APIs
- `GET /api/cards` - Danh sách thẻ
- `POST /api/cards` - Thêm thẻ mới
- `GET /api/bank-account` - Danh sách tài khoản ngân hàng

### Contact APIs
- `GET /api/contacts/frequent` - Danh sách chuyển tiền nhanh

---

## 1. Authentication (Xác thực)

### Đăng ký tài khoản (Register)
- **Mô tả**: Tạo tài khoản người dùng mới với số tài khoản tự động từ số điện thoại.
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
- **Đầu ra**:
  ```json
  {
    "message": "User registered successfully",
    "userId": 123,
    "accountNumber": "0987654321",
    "walletId": "WALLET123"
  }
  ```
- **Logic**:
  - Kiểm tra trùng username, email, phone.
  - Mã hóa mật khẩu.
  - Tạo User mới với role USER.
  - **Tự động tạo số tài khoản = số điện thoại đăng ký**
  - Tạo Wallet liên kết với User.
  - Trả về thông tin tài khoản vừa tạo.
- **Data Transfer**: Sử dụng `RegisterForm` DTO thay vì `User` entity trực tiếp.

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

## 4. QR Code APIs

### Lấy thông tin ví đầy đủ
- **Mô tả**: Lấy thông tin chi tiết ví của người dùng hiện tại.
- **Endpoint**: `GET /api/wallet/me`
- **Đầu vào**: Header `Authorization: Bearer <token>`
- **Đầu ra**:
  ```json
  {
    "walletId": "WALLET001",
    "accountName": "Nguyen Van User",
    "accountNumber": "0987654321",
    "currency": "VND",
    "balance": 1500000.0
  }
  ```
- **Logic**:
  - Lấy username từ SecurityContext
  - Join users → wallets
  - Mapping DTO với account number (số điện thoại đăng ký)
  - Không trả internal ID

### Tạo QR Code cho ví (Base64)
- **Mô tả**: Tạo QR code chứa thông tin nhận tiền, trả về dạng Base64.
- **Endpoint**: `GET /api/qr/wallet`
- **Đầu vào**: Header `Authorization: Bearer <token>`
- **QR Payload Format**:
  ```
  walletapp://pay?version=1&walletId=WALLET001&name=Nguyen%20Van%20User&accountNumber=0987654321
  ```
- **Đầu ra**:
  ```json
  {
    "walletId": "WALLET001",
    "accountName": "Nguyen Van User",
    "accountNumber": "0987654321",
    "qrBase64": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA..."
  }
  ```
- **Logic**:
  - Auth user từ JWT token
  - Lấy wallet information với account number (số điện thoại)
  - Build payload string bao gồm accountNumber
  - Generate QR image và encode Base64 với prefix `data:image/png;base64,`

### Tải QR Image
- **Mô tả**: Tải QR code dạng file ảnh PNG.
- **Endpoint**: `GET /api/qr/wallet/download`
- **Đầu ra**: 
  - Content-Type: `image/png`
  - Content-Disposition: `attachment; filename="wallet-qr.png"`
  - Binary image data
- **Logic**:
  - Reuse logic tạo QR
  - Stream image về client (không Base64)

### Tạo QR với số tiền cố định
- **Mô tả**: Tạo QR code có kèm số tiền.
- **Endpoint**: `POST /api/qr/wallet/with-amount`
- **Đầu vào (Request Body)**:
  ```json
  {
    "amount": 50000
  }
  ```
- **QR Payload**:
  ```
  walletapp://pay?version=1&walletId=WALLET001&name=Nguyen%20Van%20User&accountNumber=0987654321&amount=50000
  ```
- **Đầu ra**:
  ```json
  {
    "qrBase64": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA..."
  }
  ```
- **Logic**:
  - Validate amount > 0
  - **Không cần ghi chú (note)**
  - Generate QR runtime với accountNumber và amount
  - Return proper DTO with qrBase64 field

### Giải mã QR Payload
- **Mô tả**: Giải mã và xác thực thông tin từ QR code khi scan.
- **Endpoint**: `POST /api/qr/resolve`
- **Đầu vào (Request Body)**:
  ```json
  {
    "qrPayload": "walletapp://pay?walletId=WALLET001&accountNumber=0987654321&amount=50000"
  }
  ```
- **Đầu ra**:
  ```json
  {
    "walletId": "WALLET001",
    "receiverName": "Nguyen Van User",
    "accountNumber": "0987654321",
    "amount": 50000,
    "currency": "VND",
    "valid": true
  }
  ```
- **Logic**:
  - Parse payload parameters
  - Validate scheme và version
  - Check wallet tồn tại và ACTIVE
  - Return info bao gồm accountNumber

### Đọc ảnh QR thành JSON
- **Mô tả**: Đọc file ảnh QR (.png) và chuyển thành thông tin JSON để chuyển khoản.
- **Endpoint**: `POST /api/qr/read-image`
- **Đầu vào**: Multipart form data với file ảnh QR
  ```
  Content-Type: multipart/form-data
  file: [QR image file .png]
  ```
- **Đầu ra**:
  ```json
  {
    "walletId": "WALLET001",
    "receiverName": "Nguyen Van User",
    "accountNumber": "0987654321",
    "amount": 50000,
    "currency": "VND",
    "valid": true,
    "transferReady": true
  }
  ```
- **Logic**:
  - Nhận file ảnh QR từ client
  - Decode QR image để extract payload
  - Parse và validate payload
  - Return JSON thông tin sẵn sàng để chuyển khoản
  - Nếu amount có sẵn, set transferReady = true

---

## 5. Incoming Transactions (Widget)

### Lấy giao dịch đến gần đây
- **Mô tả**: Lấy danh sách giao dịch đến cho widget bên phải.
- **Endpoint**: `GET /api/transactions/incoming?limit=5`
- **Đầu vào**: 
  - Query param `limit` (mặc định 5)
  - Header `Authorization: Bearer <token>`
- **Đầu ra**:
  ```json
  [
    {
      "id": 1,
      "type": "TRANSFER_IN",
      "amount": 50000.0,
      "date": "2024-01-15T10:30:00",
      "status": "COMPLETED",
      "description": "Chuyển tiền vào"
    },
    {
      "id": 2,
      "type": "DEPOSIT",
      "amount": 100000.0,
      "date": "2024-01-14T15:20:00",
      "status": "COMPLETED",
      "description": "Nạp tiền vào ví"
    }
  ]
  ```
- **Logic**:
  - Get user from JWT token
  - Find wallet by user id
  - Filter transactions with direction = IN
  - Order by createdAt DESC
  - Apply limit parameter
  - Map description theo transaction type:
    - `TRANSFER_IN` → "Chuyển tiền vào"
    - `DEPOSIT` → "Nạp tiền vào ví"
    - Default → "Giao dịch vào"

---

## 6. Cards (Thẻ ngân hàng)

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

## 7. Contacts (Danh bạ thụ hưởng)

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

## 8. Transactions (Giao dịch)

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

## 9. E-Wallet Operations (Ví điện tử)

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

## 10. User Profile & Account Management

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

## 11. Bank Account Management

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

## 12. Error Responses

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

## 13. Security Notes

- **JWT Token**: Có hiệu lực 15 ngày
- **Password Encryption**: Sử dụng BCrypt
- **PIN Generation**: Tự động tạo 6 chữ số ngẫu nhiên khi đăng ký
- **Role-based Access**: USER, ADMIN, SUPPORT
- **Transaction Security**: Sử dụng @Transactional và pessimistic locking

---

## 14. Database Schema Summary

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

