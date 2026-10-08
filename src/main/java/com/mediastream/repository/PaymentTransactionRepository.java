package com.mediastream.repository;

import com.mediastream.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    List<PaymentTransaction> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<PaymentTransaction> findAllByOrderByCreatedAtDesc();

    @Query("SELECT COALESCE(SUM(p.amount), 0.0) FROM PaymentTransaction p WHERE p.status = 'SUCCESS'")
    Double calculateTotalRevenue();
}
