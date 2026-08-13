package com.andres.course.agy.springboot.springmvc.app.services;

import com.andres.course.agy.springboot.springmvc.app.models.Invoice;
import com.andres.course.agy.springboot.springmvc.app.models.InvoiceItem;
import com.andres.course.agy.springboot.springmvc.app.models.Order;
import com.andres.course.agy.springboot.springmvc.app.models.OrderItem;
import com.andres.course.agy.springboot.springmvc.app.models.Product;
import com.andres.course.agy.springboot.springmvc.app.models.User;
import com.andres.course.agy.springboot.springmvc.app.repositories.InvoiceRepository;
import com.andres.course.agy.springboot.springmvc.app.repositories.OrderRepository;
import com.andres.course.agy.springboot.springmvc.app.repositories.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final InvoiceRepository invoiceRepository;
    private final ProductRepository productRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            InvoiceRepository invoiceRepository,
                            ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.invoiceRepository = invoiceRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Order save(Order order) {
        // If it's a new order (checkout), deduct stock for each item
        if (order.getId() == null && order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                if (item.getProduct() != null && item.getQuantity() != null) {
                    Product product = productRepository.findById(item.getProduct().getId())
                            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));
                    
                    if (product.getStock() < item.getQuantity()) {
                        throw new IllegalArgumentException("Stock insuficiente para el producto: " + product.getName());
                    }
                    product.setStock(product.getStock() - item.getQuantity());
                    productRepository.save(product);
                }
            }
        }
        return orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findOrderWithDetails(Long id) {
        return orderRepository.findOrderWithDetails(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> findByUser(User user, Pageable pageable) {
        return orderRepository.findByUser(user, pageable);
    }

    @Override
    @Transactional
    public Invoice convertOrderToInvoice(Long orderId, User issuer) {
        Order order = orderRepository.findOrderWithDetails(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Orden N° " + orderId + " no encontrada."));

        if (order.getInvoice() != null) {
            return order.getInvoice();
        }

        Invoice invoice = new Invoice();
        invoice.setCustomerName(order.getCustomerName());
        invoice.setTaxId(order.getTaxId());
        invoice.setDescription("Factura oficial por Orden de Compra Web N° " + order.getId() + " (" + order.getShippingMethod() + " / " + order.getPaymentMethod() + ")");
        invoice.setObservation("Despacho: " + order.getAddress() + ", " + order.getCity() + " | Contacto: " + order.getPhone() + " (" + order.getEmail() + ")");
        invoice.setUser(issuer);
        invoice.setStatus("FACTURADO");

        List<InvoiceItem> invoiceItems = new ArrayList<>();
        if (order.getItems() != null) {
            for (OrderItem orderItem : order.getItems()) {
                InvoiceItem item = new InvoiceItem();
                item.setProduct(orderItem.getProduct());
                item.setQuantity(orderItem.getQuantity());
                invoiceItems.add(item);
            }
        }
        invoice.setItems(invoiceItems);

        Invoice savedInvoice = invoiceRepository.save(invoice);

        order.setStatus("FACTURADO");
        order.setInvoice(savedInvoice);
        orderRepository.save(order);

        return savedInvoice;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Optional<Order> orderOpt = orderRepository.findOrderWithDetails(id);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            // Restore stock if deleted
            if (order.getItems() != null) {
                for (OrderItem item : order.getItems()) {
                    if (item.getProduct() != null && item.getQuantity() != null) {
                        Product p = item.getProduct();
                        p.setStock(p.getStock() + item.getQuantity());
                        productRepository.save(p);
                    }
                }
            }
            orderRepository.deleteById(id);
        }
    }
}
