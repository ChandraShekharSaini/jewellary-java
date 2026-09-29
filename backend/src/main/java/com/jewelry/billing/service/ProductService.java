package com.jewelry.billing.service;

import com.jewelry.billing.dto.ProductRequest;
import com.jewelry.billing.dto.ProductResponse;
import com.jewelry.billing.entity.Product;
import com.jewelry.billing.exception.ResourceNotFoundException;
import com.jewelry.billing.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<ProductResponse> getActiveProducts() {
        return productRepository.findByActiveTrueOrderByNameAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .filter(Product::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .metalType(request.getMetalType())
                .purity(request.getPurity())
                .makingChargePerGram(request.getMakingChargePerGram())
                .wastagePercent(request.getWastagePercent())
                .active(true)
                .build();
        return toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        product.setName(request.getName());
        product.setMetalType(request.getMetalType());
        product.setPurity(request.getPurity());
        product.setMakingChargePerGram(request.getMakingChargePerGram());
        product.setWastagePercent(request.getWastagePercent());
        return toResponse(productRepository.save(product));
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .metalType(product.getMetalType())
                .purity(product.getPurity())
                .makingChargePerGram(product.getMakingChargePerGram())
                .wastagePercent(product.getWastagePercent())
                .active(product.getActive())
                .build();
    }
}
