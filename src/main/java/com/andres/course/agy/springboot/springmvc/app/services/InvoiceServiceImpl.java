package com.andres.course.agy.springboot.springmvc.app.services;

import com.andres.course.agy.springboot.springmvc.app.models.Invoice;
import com.andres.course.agy.springboot.springmvc.app.models.InvoiceItem;
import com.andres.course.agy.springboot.springmvc.app.models.Product;
import com.andres.course.agy.springboot.springmvc.app.models.User;
import com.andres.course.agy.springboot.springmvc.app.repositories.InvoiceRepository;
import com.andres.course.agy.springboot.springmvc.app.repositories.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ProductRepository productRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, ProductRepository productRepository) {
        this.invoiceRepository = invoiceRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Invoice> findAll(Pageable pageable) {
        return invoiceRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Invoice> findByUser(User user, Pageable pageable) {
        return invoiceRepository.findByUser(user, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Invoice> findById(Long id) {
        return invoiceRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Invoice> findInvoiceWithDetails(Long id) {
        return invoiceRepository.fetchByIdWithUserWithInvoiceItemWithProduct(id);
    }

    @Override
    @Transactional
    public Invoice save(Invoice invoice) {
        if (invoice.getItems() != null && !invoice.getItems().isEmpty()) {
            for (InvoiceItem item : invoice.getItems()) {
                if (item.getProduct() != null && item.getProduct().getId() != null) {
                    Product dbProduct = productRepository.findById(item.getProduct().getId())
                            .orElseThrow(() -> new IllegalArgumentException("El producto especificado no existe."));

                    if (item.getQuantity() > dbProduct.getStock()) {
                        throw new IllegalArgumentException("El producto '" + dbProduct.getName() + 
                                "' no cuenta con suficiente stock disponible. Stock actual: " + 
                                dbProduct.getStock() + ", Solicitado: " + item.getQuantity());
                    }

                    // Deduct stock
                    dbProduct.setStock(dbProduct.getStock() - item.getQuantity());
                    productRepository.save(dbProduct);
                    item.setProduct(dbProduct);
                }
            }
        }
        return invoiceRepository.save(invoice);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Optional<Invoice> invoiceOpt = invoiceRepository.fetchByIdWithUserWithInvoiceItemWithProduct(id);
        if (invoiceOpt.isPresent()) {
            Invoice invoice = invoiceOpt.get();
            if (invoice.getItems() != null) {
                for (InvoiceItem item : invoice.getItems()) {
                    if (item.getProduct() != null && item.getQuantity() != null) {
                        Product product = item.getProduct();
                        product.setStock(product.getStock() + item.getQuantity());
                        productRepository.save(product);
                    }
                }
            }
            invoiceRepository.deleteById(id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findProductByName(String term) {
        return productRepository.findByNameContainingIgnoreCase(term);
    }
}
