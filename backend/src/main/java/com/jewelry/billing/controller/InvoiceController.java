package com.jewelry.billing.controller;

import com.jewelry.billing.dto.InvoiceRequest;
import com.jewelry.billing.dto.InvoiceResponse;
import com.jewelry.billing.entity.User;
import com.jewelry.billing.repository.UserRepository;
import com.jewelry.billing.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final UserRepository userRepository;

    @PostMapping
    public InvoiceResponse createInvoice(@Valid @RequestBody InvoiceRequest request, Authentication auth) {
        Long userId = userRepository.findByUsername(auth.getName()).map(User::getId).orElse(null);
        return invoiceService.createInvoice(request, userId);
    }

    @GetMapping
    public List<InvoiceResponse> getAllInvoices() {
        return invoiceService.getAllInvoices();
    }

    @GetMapping("/{id}")
    public InvoiceResponse getInvoice(@PathVariable Long id) {
        return invoiceService.getInvoice(id);
    }
}
