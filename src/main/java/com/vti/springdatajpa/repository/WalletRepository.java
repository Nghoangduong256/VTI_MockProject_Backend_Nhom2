package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.dto.WalletSelectDTO;
import com.vti.springdatajpa.entity.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Integer> {

    // Dùng cho các nghiệp vụ tiền (deposit / withdraw)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from Wallet w where w.id = :id")
    Optional<Wallet> findByIdForUpdate(@Param("id") Integer id);

    // Lấy ví theo user
    Optional<Wallet> findByUserId(Integer userId);

    // Bảo mật: đảm bảo ví thuộc user
    Optional<Wallet> findByIdAndUserId(Integer id, Integer userId);

    Optional<Wallet> findByUser_UserName(String username);  

    // Lấy ví theo code
    Optional<Wallet> findByCode(String code);

    @Query("""
    select new com.vti.springdatajpa.dto.WalletSelectDTO(
        w.id,
        u.id,
        u.fullName,
        w.accountNumber
    )
    from Wallet w
    join w.user u
    where u.phone = :phone
    and u.id <> :currentUserId
    """)
    List<WalletSelectDTO> searchOtherWalletsByPhone(
            @Param("currentUserId") Integer currentUserId,
            @Param("phone") String phone
    );

}
