package com.jewelry.billing.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class InvoiceRequest {
    private Long customerId;
    private String customerName;
    private String customerPhone;

    @NotEmpty
    @Valid
    private List<InvoiceLineRequest> items;
}
