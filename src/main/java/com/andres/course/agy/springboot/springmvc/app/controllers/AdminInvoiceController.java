package com.andres.course.agy.springboot.springmvc.app.controllers;

import com.andres.course.agy.springboot.springmvc.app.models.Invoice;
import com.andres.course.agy.springboot.springmvc.app.models.InvoiceItem;
import com.andres.course.agy.springboot.springmvc.app.models.Product;
import com.andres.course.agy.springboot.springmvc.app.models.User;
import com.andres.course.agy.springboot.springmvc.app.services.InvoiceService;
import com.andres.course.agy.springboot.springmvc.app.services.UserService;
import com.andres.course.agy.springboot.springmvc.app.util.paginator.PageRender;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.awt.Color;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/invoices")
public class AdminInvoiceController {

    private final InvoiceService invoiceService;
    private final UserService userService;

    public AdminInvoiceController(InvoiceService invoiceService, UserService userService) {
        this.invoiceService = invoiceService;
        this.userService = userService;
    }

    @GetMapping
    public String list(@RequestParam(name = "page", defaultValue = "0") int page,
                       Authentication authentication,
                       Model model) {

        User currentUser = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Usuario no autenticado."));

        boolean isAdmin = currentUser.getRoles().stream().anyMatch(r -> "ROLE_ADMIN".equals(r.getName()));

        Pageable pageable = PageRequest.of(page, 8, Sort.by("id").descending());
        Page<Invoice> invoicesPage = isAdmin ? 
                invoiceService.findAll(pageable) : 
                invoiceService.findByUser(currentUser, pageable);

        PageRender<Invoice> pageRender = new PageRender<>("/admin/invoices", invoicesPage);

        model.addAttribute("title", "Gestión de Facturas");
        model.addAttribute("invoices", invoicesPage.getContent());
        model.addAttribute("page", pageRender);
        model.addAttribute("isAdmin", isAdmin);

        return "invoices/list";
    }

    @GetMapping("/form")
    public String createForm(Model model) {
        Invoice invoice = new Invoice();

        model.addAttribute("title", "Emitir Nueva Factura");
        model.addAttribute("invoice", invoice);

        return "invoices/form";
    }

    @GetMapping(value = "/load-products/{term}", produces = "application/json")
    @ResponseBody
    public List<Map<String, Object>> loadProducts(@PathVariable String term) {
        List<Product> products = invoiceService.findProductByName(term);
        return products.stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getId());
            map.put("name", p.getName());
            map.put("price", p.getPrice());
            map.put("stock", p.getStock());
            return map;
        }).collect(Collectors.toList());
    }

    @PostMapping("/form")
    public String save(@Valid @ModelAttribute("invoice") Invoice invoice,
                       BindingResult result,
                       @RequestParam(name = "item_id[]", required = false) Long[] itemIds1,
                       @RequestParam(name = "item_id", required = false) Long[] itemIds2,
                       @RequestParam(name = "quantity[]", required = false) Integer[] quantities1,
                       @RequestParam(name = "quantity", required = false) Integer[] quantities2,
                       Authentication authentication,
                       Model model,
                       RedirectAttributes flash) {

        Long[] itemIds = (itemIds1 != null && itemIds1.length > 0) ? itemIds1 : itemIds2;
        Integer[] quantities = (quantities1 != null && quantities1.length > 0) ? quantities1 : quantities2;

        if (itemIds == null || itemIds.length == 0 || quantities == null || quantities.length == 0) {
            model.addAttribute("error", "Error: La factura debe incluir al menos un producto.");
            model.addAttribute("title", "Emitir Nueva Factura");
            return "invoices/form";
        }

        if (result.hasErrors()) {
            model.addAttribute("title", "Emitir Nueva Factura");
            return "invoices/form";
        }

        User currentUser = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Usuario no autenticado."));
        invoice.setUser(currentUser);

        for (int i = 0; i < itemIds.length; i++) {
            Product product = new Product();
            product.setId(itemIds[i]);

            InvoiceItem item = new InvoiceItem();
            item.setProduct(product);
            item.setQuantity(quantities[i]);

            invoice.addItem(item);
        }

        try {
            Invoice savedInvoice = invoiceService.save(invoice);
            flash.addFlashAttribute("success", "Factura N° " + savedInvoice.getId() + " creada con éxito.");
            return "redirect:/admin/invoices/view/" + savedInvoice.getId();
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("title", "Emitir Nueva Factura");
            return "invoices/form";
        }
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable Long id,
                       @RequestParam(name = "format", required = false) String format,
                       Authentication authentication,
                       HttpServletResponse response,
                       Model model,
                       RedirectAttributes flash) {

        Optional<Invoice> invoiceOpt = invoiceService.findInvoiceWithDetails(id);
        if (invoiceOpt.isEmpty()) {
            flash.addFlashAttribute("error", "La factura no existe en el sistema.");
            return "redirect:/admin/invoices";
        }

        Invoice invoice = invoiceOpt.get();
        User currentUser = userService.findByUsername(authentication.getName()).orElse(null);
        boolean isAdmin = currentUser != null && currentUser.getRoles().stream().anyMatch(r -> "ROLE_ADMIN".equals(r.getName()));

        // Security check: Only Admin or Owner can view invoice
        if (!isAdmin && (currentUser == null || invoice.getUser() == null || !invoice.getUser().getId().equals(currentUser.getId()))) {
            flash.addFlashAttribute("error", "Acceso denegado: No tienes permisos para consultar esta factura.");
            return "redirect:/admin/invoices";
        }

        // Export to PDF if requested
        if ("pdf".equalsIgnoreCase(format)) {
            generateInvoicePdf(invoice, response);
            return null;
        }

        model.addAttribute("title", "Detalle de Factura N° " + invoice.getId());
        model.addAttribute("invoice", invoice);

        return "invoices/view";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         Authentication authentication,
                         RedirectAttributes flash) {

        Optional<Invoice> invoiceOpt = invoiceService.findById(id);
        if (invoiceOpt.isPresent()) {
            Invoice invoice = invoiceOpt.get();
            User currentUser = userService.findByUsername(authentication.getName()).orElse(null);
            boolean isAdmin = currentUser != null && currentUser.getRoles().stream().anyMatch(r -> "ROLE_ADMIN".equals(r.getName()));

            if (!isAdmin) {
                flash.addFlashAttribute("error", "Solo los administradores pueden eliminar facturas emitidas.");
                return "redirect:/admin/invoices";
            }

            invoiceService.deleteById(id);
            flash.addFlashAttribute("success", "Factura N° " + id + " eliminada y stock restaurado en catálogo.");
        } else {
            flash.addFlashAttribute("error", "Factura no encontrada.");
        }

        return "redirect:/admin/invoices";
    }

    private void generateInvoicePdf(Invoice invoice, HttpServletResponse response) {
        try {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=factura_" + invoice.getId() + ".pdf");

            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            OutputStream out = response.getOutputStream();
            PdfWriter.getInstance(document, out);

            document.open();

            // Font & Colors
            Font fontTitle = new Font(Font.HELVETICA, 18, Font.BOLD, new Color(30, 41, 59));
            Font fontHeader = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
            Font fontBody = new Font(Font.HELVETICA, 9, Font.NORMAL, new Color(51, 65, 85));
            Font fontBold = new Font(Font.HELVETICA, 10, Font.BOLD, new Color(15, 23, 42));

            // Header Table
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{3, 2});

            PdfPCell cellLeft = new PdfPCell();
            cellLeft.setBorder(PdfPCell.NO_BORDER);
            cellLeft.addElement(new Paragraph("TIENDA ONLINE E-COMMERCE", fontTitle));
            cellLeft.addElement(new Paragraph("RUT Empresa: 76.543.210-9", fontBody));
            cellLeft.addElement(new Paragraph("Dirección: Av. Principal 123, Santiago, Chile", fontBody));
            cellLeft.addElement(new Paragraph("Teléfono: +56 9 1234 5678", fontBody));

            PdfPCell cellRight = new PdfPCell();
            cellRight.setBorder(PdfPCell.BOX);
            cellRight.setBackgroundColor(new Color(241, 245, 249));
            cellRight.setPadding(10);
            cellRight.addElement(new Paragraph("FACTURA ELECTRÓNICA", fontBold));
            cellRight.addElement(new Paragraph("N° " + String.format("%06d", invoice.getId()), fontTitle));
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            cellRight.addElement(new Paragraph("Fecha: " + (invoice.getCreatedAt() != null ? invoice.getCreatedAt().format(fmt) : "N/A"), fontBody));

            headerTable.addCell(cellLeft);
            headerTable.addCell(cellRight);
            document.add(headerTable);

            document.add(new Paragraph("\n"));

            // Customer Info Table
            PdfPTable customerTable = new PdfPTable(2);
            customerTable.setWidthPercentage(100);

            PdfPCell c1 = new PdfPCell(new Paragraph("DATOS DEL CLIENTE / EMPRESA", fontBold));
            c1.setColspan(2);
            c1.setBackgroundColor(new Color(226, 232, 240));
            c1.setPadding(6);
            customerTable.addCell(c1);

            customerTable.addCell(new PdfPCell(new Paragraph("Cliente / Razón Social: " + invoice.getCustomerName(), fontBody)));
            customerTable.addCell(new PdfPCell(new Paragraph("RUT / Id Fiscal: " + (invoice.getTaxId() != null ? invoice.getTaxId() : "N/A"), fontBody)));
            customerTable.addCell(new PdfPCell(new Paragraph("Descripción: " + invoice.getDescription(), fontBody)));
            customerTable.addCell(new PdfPCell(new Paragraph("Emisor Responsable: " + (invoice.getUser() != null ? invoice.getUser().getName() + " (" + invoice.getUser().getUsername() + ")" : "N/A"), fontBody)));

            document.add(customerTable);

            document.add(new Paragraph("\n"));

            // Items Table
            PdfPTable itemsTable = new PdfPTable(4);
            itemsTable.setWidthPercentage(100);
            itemsTable.setWidths(new float[]{4, 2, 1.5f, 2});

            String[] headers = {"PRODUCTO", "PRECIO UNIT.", "CANTIDAD", "SUBTOTAL"};
            for (String h : headers) {
                PdfPCell th = new PdfPCell(new Paragraph(h, fontHeader));
                th.setBackgroundColor(new Color(30, 41, 59));
                th.setPadding(6);
                itemsTable.addCell(th);
            }

            if (invoice.getItems() != null) {
                for (InvoiceItem item : invoice.getItems()) {
                    itemsTable.addCell(new PdfPCell(new Paragraph(item.getProduct() != null ? item.getProduct().getName() : "N/A", fontBody)));
                    itemsTable.addCell(new PdfPCell(new Paragraph(String.format("$%.2f", item.getProduct() != null ? item.getProduct().getPrice() : 0.0), fontBody)));
                    itemsTable.addCell(new PdfPCell(new Paragraph(String.valueOf(item.getQuantity()), fontBody)));
                    itemsTable.addCell(new PdfPCell(new Paragraph(String.format("$%.2f", item.calculateImport()), fontBody)));
                }
            }

            // Total Row
            PdfPCell totalLabelCell = new PdfPCell(new Paragraph("TOTAL FACTURA", fontBold));
            totalLabelCell.setColspan(3);
            totalLabelCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            totalLabelCell.setPadding(6);
            totalLabelCell.setBackgroundColor(new Color(241, 245, 249));

            PdfPCell totalValueCell = new PdfPCell(new Paragraph(String.format("$%.2f", invoice.getTotal()), fontBold));
            totalValueCell.setPadding(6);
            totalValueCell.setBackgroundColor(new Color(241, 245, 249));

            itemsTable.addCell(totalLabelCell);
            itemsTable.addCell(totalValueCell);

            document.add(itemsTable);

            if (invoice.getObservation() != null && !invoice.getObservation().isBlank()) {
                document.add(new Paragraph("\nObservaciones:", fontBold));
                document.add(new Paragraph(invoice.getObservation(), fontBody));
            }

            document.close();
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
