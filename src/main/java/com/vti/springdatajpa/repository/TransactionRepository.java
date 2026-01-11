package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.dto.SpendingActivityDTO;
import com.vti.springdatajpa.entity.Transaction;
import com.vti.springdatajpa.entity.enums.TransactionDirection;
import com.vti.springdatajpa.entity.enums.TransactionStatus;
import com.vti.springdatajpa.entity.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    // dùng cho chống double-submit / retry
    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);

    // dùng cho lịch sử nạp tiền (top N)
    List<Transaction> findTop5ByWallet_IdAndTypeOrderByCreatedAtDesc(
            Integer walletId,
            TransactionType type
    );

    // dùng khi cần full history (không limit)
    List<Transaction> findByWallet_IdAndTypeOrderByCreatedAtDesc(
            Integer walletId,
            TransactionType type
    );

    // dùng cho phân trang giao dịch
    Page<Transaction> findByWalletId(Integer walletId, Pageable pageable);

    // dùng cho incoming transactions (direction = IN)
    @Query("SELECT t FROM Transaction t WHERE t.wallet.id = ?1 AND t.direction = ?2 ORDER BY t.createdAt DESC")
    List<Transaction> findIncomingTransactions(Integer walletId, TransactionDirection direction);

    // dùng cho dashboard analytics - transactions trong khoảng thời gian
    @Query("SELECT t FROM Transaction t WHERE t.wallet.id = ?1 AND t.createdAt BETWEEN ?2 AND ?3 ORDER BY t.createdAt DESC")
    List<Transaction> findByWalletIdAndCreatedAtBetween(Integer walletId, LocalDateTime startDate, LocalDateTime endDate);

    // dùng cho transfer history có phân trang + filter
    // filter theo (walletId), chiều giao dịch (IN/OUT), thời gian (fromDate / toDate)
    @Query("""
            SELECT t FROM Transaction t WHERE t.wallet.id = :walletId
              AND (:direction IS NULL OR t.direction = :direction)
              AND (:fromDate IS NULL OR t.createdAt >= :fromDate)
              AND (:toDate IS NULL OR t.createdAt <= :toDate)
            """)
    Page<Transaction> findTransferHistory(
            @Param("walletId") Integer walletId,
            @Param("direction") TransactionDirection direction,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );

    // Tính Monthly Spending
    @Query("""
                SELECT COALESCE(SUM(t.amount), 0)
                FROM Transaction t
                WHERE t.direction = :direction
                  AND t.status = :status
                  AND t.transactionDate BETWEEN :startDate AND :endDate
            """)
    Double sumAmountByDirectionAndStatusAndDateBetween(
            @Param("direction") TransactionDirection direction,
            @Param("status") TransactionStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Query Spending Activity
    @Query(value = """
                SELECT
                    DATE(t.transaction_date) AS tx_date,
                    SUM(t.amount) AS total_amount
                FROM transactions t
                WHERE t.direction = :direction
                  AND t.status = :status
                  AND t.transaction_date BETWEEN :startDate AND :endDate
                GROUP BY DATE(t.transaction_date)
                ORDER BY DATE(t.transaction_date)
            """, nativeQuery = true)
    List<Object[]> findDailySpendingActivity(
            @Param("direction") String direction,
            @Param("status") String status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Query Spending Breakdown
    @Query(value = """
                SELECT
                    c.name        AS category_name,
                    SUM(t.amount) AS total_amount,
                    c.icon        AS icon,
                    c.color       AS color
                FROM transactions t
                JOIN transaction_categories c
                    ON t.category_id = c.id
                WHERE t.direction = :direction
                  AND t.status = :status
                  AND t.transaction_date BETWEEN :startDate AND :endDate
                GROUP BY c.id, c.name, c.icon, c.color
                ORDER BY total_amount DESC
            """, nativeQuery = true)
    List<Object[]> findSpendingBreakdown(
            @Param("direction") String direction,
            @Param("status") String status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Query Recent Transactions for Dashboard
    @Query(value = """
                SELECT
                    t.transaction_date AS tx_date,
                    c.name             AS category_name,
                    t.metadata         AS description,
                    t.amount           AS amount,
                    c.icon             AS icon,
                    c.color            AS color
                FROM transactions t
                JOIN transaction_categories c
                    ON t.category_id = c.id
                WHERE t.direction = :direction
                  AND t.status = :status
                ORDER BY t.transaction_date DESC
                LIMIT :limit
            """, nativeQuery = true)
    List<Object[]> findRecentSpendingTransactions(
            @Param("direction") String direction,
            @Param("status") String status,
            @Param("limit") int limit
    );

}
