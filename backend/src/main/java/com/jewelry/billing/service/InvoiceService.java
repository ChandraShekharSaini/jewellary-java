package com.jewelry.billing.service;

import com.jewelry.billing.dto.*;
import com.jewelry.billing.entity.*;
import com.jewelry.billing.exception.ResourceNotFoundException;
import com.jewelry.billing.repository.CustomerRepository;
import com.jewelry.billing.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final ProductService productService;
    private final DailyRateService dailyRateService;
    private final BillingCalculator billingCalculator;

    @Value("${app.billing.default-gst-percent}")
    private BigDecimal defaultGstPercent;

    @Transactional
    public InvoiceResponse createInvoice(InvoiceRequest request, Long userId) {
        Customer customer = resolveCustomer(request);
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal taxTotal = BigDecimal.ZERO;
        List<InvoiceItem> items = new ArrayList<>();
        Long rateSnapshotId = null;

        for (InvoiceLineRequest line : request.getItems()) {
            Product product = productService.getProduct(line.getProductId());
            DailyRate rate = dailyRateService.getActiveRate(product.getMetalType(), product.getPurity());
            if (rateSnapshotId == null) {
                rateSnapshotId = rate.getId();
            }

            BillingCalculator.LineCalculation calc = billingCalculator.calculateLine(
                    product, line.getWeightGrams(), rate.getRatePerGram(), defaultGstPercent);
            InvoiceItem item = billingCalculator.toInvoiceItem(product, calc, rate.getId());
            items.add(item);
            subtotal = subtotal.add(calc.getLineSubtotal());
            taxTotal = taxTotal.add(calc.getTaxAmount());
        }

        Invoice invoice = Invoice.builder()
                .customer(customer)
                .rateSnapshotId(rateSnapshotId)
                .subtotal(subtotal)
                .taxAmount(taxTotal)
                .totalAmount(subtotal.add(taxTotal))
                .gstPercent(defaultGstPercent)
                .createdBy(userId)
                .build();

        items.forEach(invoice::addItem);
        return toResponse(invoiceRepository.save(invoice));
    }

    @Transactional(readOnly = true)
    public InvoiceResponse getInvoice(Long id) {
        Invoice invoice = invoiceRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + id));
        return toResponse(invoice);
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> getAllInvoices() {
        return invoiceRepository.findAllWithDetails().stream()
                .map(this::toResponse)
                .toList();
    }

    private Customer resolveCustomer(InvoiceRequest request) {
        if (request.getCustomerId() != null) {
            return customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        }
        if (request.getCustomerName() != null && !request.getCustomerName().isBlank()) {
            return customerRepository.save(Customer.builder()
                    .name(request.getCustomerName().trim())
                    .phone(request.getCustomerPhone())
                    .build());
        }
        return null;
    }

    private InvoiceResponse toResponse(Invoice invoice) {
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .invoiceDate(invoice.getInvoiceDate())
                .customer(invoice.getCustomer() != null ? CustomerResponse.builder()
                        .id(invoice.getCustomer().getId())
                        .name(invoice.getCustomer().getName())
                        .phone(invoice.getCustomer().getPhone())
                        .email(invoice.getCustomer().getEmail())
                        .address(invoice.getCustomer().getAddress())
                        .build() : null)
                .subtotal(invoice.getSubtotal())
                .taxAmount(invoice.getTaxAmount())
                .totalAmount(invoice.getTotalAmount())
                .gstPercent(invoice.getGstPercent())
                .items(invoice.getItems().stream().map(item -> InvoiceItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                        .productName(item.getProductName())
                        .weightGrams(item.getWeightGrams())
                        .rateApplied(item.getRateApplied())
                        .makingApplied(item.getMakingApplied())
                        .metalAmount(item.getMetalAmount())
                        .makingAmount(item.getMakingAmount())
                        .lineSubtotal(item.getLineSubtotal())
                        .taxAmount(item.getTaxAmount())
                        .lineTotal(item.getLineTotal())
                        .dailyRateId(item.getDailyRateId())
                        .build()).toList())
                .build();
    }
}
