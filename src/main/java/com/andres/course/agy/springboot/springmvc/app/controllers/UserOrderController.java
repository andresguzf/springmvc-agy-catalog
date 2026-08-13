package com.andres.course.agy.springboot.springmvc.app.controllers;

import com.andres.course.agy.springboot.springmvc.app.models.Company;
import com.andres.course.agy.springboot.springmvc.app.models.Invoice;
import com.andres.course.agy.springboot.springmvc.app.models.User;
import com.andres.course.agy.springboot.springmvc.app.services.CompanyService;
import com.andres.course.agy.springboot.springmvc.app.services.InvoiceService;
import com.andres.course.agy.springboot.springmvc.app.services.UserService;
import com.andres.course.agy.springboot.springmvc.app.util.paginator.PageRender;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.OutputStream;
import java.util.Optional;

@Controller
@RequestMapping("/user/orders")
public class UserOrderController {

    private final InvoiceService invoiceService;
    private final UserService userService;
    private final CompanyService companyService;

    public UserOrderController(InvoiceService invoiceService, UserService userService, CompanyService companyService) {
        this.invoiceService = invoiceService;
        this.userService = userService;
        this.companyService = companyService;
    }

    @GetMapping
    public String myOrders(@RequestParam(name = "page", defaultValue = "0") int page,
                           Authentication authentication,
                           Model model) {

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return "redirect:/login";
        }

        User currentUser = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado."));

        Pageable pageable = PageRequest.of(page, 8, Sort.by("id").descending());
        Page<Invoice> invoicesPage = invoiceService.findByUser(currentUser, pageable);
        PageRender<Invoice> pageRender = new PageRender<>("/user/orders", invoicesPage);

        model.addAttribute("title", "Mis Compras e Historial de Órdenes");
        model.addAttribute("invoices", invoicesPage.getContent());
        model.addAttribute("page", pageRender);

        return "user/orders/list";
    }

    @GetMapping("/view/{id}")
    public String viewOrder(@PathVariable Long id,
                            @RequestParam(name = "format", required = false) String format,
                            Authentication authentication,
                            HttpServletResponse response,
                            Model model,
                            RedirectAttributes flash) {

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return "redirect:/login";
        }

        Optional<Invoice> invoiceOpt = invoiceService.findInvoiceWithDetails(id);
        if (invoiceOpt.isEmpty()) {
            flash.addFlashAttribute("error", "La orden de compra no existe.");
            return "redirect:/user/orders";
        }

        Invoice invoice = invoiceOpt.get();
        User currentUser = userService.findByUsername(authentication.getName()).orElse(null);
        boolean isAdmin = currentUser != null && currentUser.getRoles().stream().anyMatch(r -> "ROLE_ADMIN".equals(r.getName()));

        // Security check: Only Admin or the Order Owner can view this order
        if (!isAdmin && (currentUser == null || invoice.getUser() == null || !invoice.getUser().getId().equals(currentUser.getId()))) {
            flash.addFlashAttribute("error", "Acceso denegado: No tienes permisos para ver esta orden de compra.");
            return "redirect:/user/orders";
        }

        Company company = companyService.getCompany();

        if ("pdf".equalsIgnoreCase(format)) {
            generateInvoicePdf(invoice, company, response);
            return null;
        }

        model.addAttribute("title", "Detalle de Orden de Compra N° " + invoice.getId());
        model.addAttribute("invoice", invoice);
        model.addAttribute("company", company);

        return "user/orders/view";
    }

    private void generateInvoicePdf(Invoice invoice, Company company, HttpServletResponse response) {
        try {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=orden_compra_" + invoice.getId() + ".pdf");

            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            OutputStream out = response.getOutputStream();
            PdfWriter.getInstance(document, out);

            document.open();

            Font fontTitle = new Font(Font.HELVETICA, 18, Font.BOLD, new Color(30, 41, 59));
            Font fontHeader = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
            Font fontBody = new Font(Font.HELVETICA, 9, Font.NORMAL, new Color(51, 65, 85));
            Font fontBold = new Font(Font.HELVETICA, 10, Font.BOLD, new Color(15, 23, 42));

            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{3, 2});

            PdfPCell cellLeft = new PdfPCell();
            cellLeft.setBorder(PdfPCell.NO_BORDER);
            cellLeft.addElement(new Paragraph(company != null ? company.getName() : "TIENDA ONLINE E-COMMERCE", fontTitle));
            cellLeft.addElement(new Paragraph("RUT Empresa: " + (company != null ? company.getTaxId() : "76.543.210-9"), fontBody));
            cellLeft.addElement(new Paragraph("Dirección: " + (company != null ? company.getAddress() : "Av. Principal 123, Santiago, Chile"), fontBody));
            cellLeft.addElement(new Paragraph("Teléfono: " + (company != null ? company.getPhone() : "+56 9 1234 5678"), fontBody));

            PdfPCell cellRight = new PdfPCell();
            cellRight.setBorder(PdfPCell.BOX);
            cellRight.setBackgroundColor(new Color(241, 245, 249));
            cellRight.setPadding(10);
            cellRight.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellRight.addElement(new Paragraph("COMPROBANTE DE COMPRA", fontBold));
            cellRight.addElement(new Paragraph("Orden N°: " + String.format("%06d", invoice.getId()), fontBold));
            cellRight.addElement(new Paragraph("Fecha: " + (invoice.getCreatedAt() != null ? invoice.getCreatedAt().toString() : "N/A"), fontBody));

            headerTable.addCell(cellLeft);
            headerTable.addCell(cellRight);
            document.add(headerTable);

            document.add(new Paragraph(" "));

            // Customer Info Table
            PdfPTable clientTable = new PdfPTable(1);
            clientTable.setWidthPercentage(100);
            PdfPCell clientCell = new PdfPCell();
            clientCell.setPadding(8);
            clientCell.setBackgroundColor(new Color(248, 250, 252));
            clientCell.addElement(new Paragraph("DATOS DEL COMPRADOR", fontBold));
            clientCell.addElement(new Paragraph("Cliente: " + invoice.getCustomerName(), fontBody));
            clientCell.addElement(new Paragraph("RUT / Tax ID: " + (invoice.getTaxId() != null ? invoice.getTaxId() : "N/A"), fontBody));
            clientCell.addElement(new Paragraph("Detalles: " + (invoice.getDescription() != null ? invoice.getDescription() : "Sin descripción"), fontBody));
            clientCell.addElement(new Paragraph("Observaciones / Envío: " + (invoice.getObservation() != null ? invoice.getObservation() : "Sin observaciones"), fontBody));
            clientTable.addCell(clientCell);
            document.add(clientTable);

            document.add(new Paragraph(" "));

            // Items Table
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4, 2, 2, 2});

            String[] headers = {"Producto", "Precio Unitario", "Cantidad", "Subtotal"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Paragraph(header, fontHeader));
                cell.setBackgroundColor(new Color(16, 185, 129));
                cell.setPadding(6);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            if (invoice.getItems() != null) {
                for (var item : invoice.getItems()) {
                    PdfPCell c1 = new PdfPCell(new Paragraph(item.getProduct() != null ? item.getProduct().getName() : "Producto", fontBody));
                    c1.setPadding(6);
                    table.addCell(c1);

                    PdfPCell c2 = new PdfPCell(new Paragraph("$" + String.format(java.util.Locale.US, "%.2f", item.getProduct() != null ? item.getProduct().getPrice() : 0.0), fontBody));
                    c2.setPadding(6);
                    c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(c2);

                    PdfPCell c3 = new PdfPCell(new Paragraph(String.valueOf(item.getQuantity()), fontBody));
                    c3.setPadding(6);
                    c3.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(c3);

                    PdfPCell c4 = new PdfPCell(new Paragraph("$" + String.format(java.util.Locale.US, "%.2f", item.calculateImport()), fontBody));
                    c4.setPadding(6);
                    c4.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(c4);
                }
            }

            document.add(table);

            document.add(new Paragraph(" "));
            Paragraph totalPara = new Paragraph("TOTAL PAGADO: $" + String.format(java.util.Locale.US, "%.2f", invoice.getTotal()), fontTitle);
            totalPara.setAlignment(Element.ALIGN_RIGHT);
            document.add(totalPara);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
