package com.jewelry.billing.repository;

import com.jewelry.billing.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("SELECT i FROM Invoice i LEFT JOIN FETCH i.items LEFT JOIN FETCH i.customer ORDER BY i.invoiceDate DESC")
    List<Invoice> findAllWithDetails();

    @Query("SELECT i FROM Invoice i LEFT JOIN FETCH i.items LEFT JOIN FETCH i.customer WHERE i.id = :id")
    Optional<Invoice> findByIdWithDetails(Long id);
}
