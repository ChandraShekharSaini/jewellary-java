package com.jewelry.billing.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class InvoiceResponse {
    private Long id;
    private Instant invoiceDate;
    private CustomerResponse customer;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private BigDecimal gstPercent;
    private List<InvoiceItemResponse> items;
}
