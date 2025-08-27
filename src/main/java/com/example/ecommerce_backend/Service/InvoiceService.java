package com.example.ecommerce_backend.Service;



import com.example.ecommerce_backend.Modal.Order;
import com.example.ecommerce_backend.Modal.User;
import com.example.ecommerce_backend.Repo.OrderRepository;
import com.example.ecommerce_backend.Repo.UserRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
public class InvoiceService {

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private UserRepository userRepo;

    public ByteArrayInputStream generateInvoice(String orderId) throws Exception {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        User user = userRepo.findById(order.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Document document = new Document(PageSize.A4, 40, 40, 40, 40);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);

        document.open();

        // ---------- HEADER ----------
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Color.BLUE);
        Paragraph company = new Paragraph("Devwerx Software Solution", titleFont);
        company.setAlignment(Element.ALIGN_CENTER);
        document.add(company);

        Paragraph tagline = new Paragraph("E-commerce Solutions & Services", FontFactory.getFont(FontFactory.HELVETICA, 12, Color.DARK_GRAY));
        tagline.setAlignment(Element.ALIGN_CENTER);
        document.add(tagline);

        document.add(new Paragraph(" "));
        LineSeparator ls = new LineSeparator();
        document.add(ls);
        document.add(new Paragraph(" "));

        // ---------- CUSTOMER + INVOICE INFO ----------
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);

        // Customer block
        PdfPCell customerCell = new PdfPCell();
        customerCell.setBorder(Rectangle.NO_BORDER);
        customerCell.addElement(new Paragraph("Bill To:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        
        customerCell.addElement(new Paragraph(user.getFullName()));
        customerCell.addElement(new Paragraph(user.getEmail()));
        customerCell.addElement(new Paragraph(user.getPhone()));
        customerCell.addElement(new Paragraph(user.getAddress() + ", " + user.getDistrict() + ", " + user.getState() + " - " + user.getPincode()));
        infoTable.addCell(customerCell);

        // Invoice block
        PdfPCell invoiceCell = new PdfPCell();
        invoiceCell.setBorder(Rectangle.NO_BORDER);
        invoiceCell.addElement(new Paragraph("Invoice No: " + order.getId(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        invoiceCell.addElement(new Paragraph("Date: " + order.getOrderDate().toLocalDate()));
        invoiceCell.addElement(new Paragraph("Payment ID: " + order.getPaymentId()));
        invoiceCell.addElement(new Paragraph("Payment Type: " + order.getPaymentType()));
        invoiceCell.addElement(new Paragraph("Status: " + order.getStatus()));
        infoTable.addCell(invoiceCell);

        document.add(infoTable);
        document.add(new Paragraph(" "));

        // ---------- PRODUCT TABLE ----------
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{3, 2, 2, 2});

        // Table header
        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        PdfPCell h1 = new PdfPCell(new Phrase("Product", headFont));
        h1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(h1);

        PdfPCell h2 = new PdfPCell(new Phrase("Quantity", headFont));
        h2.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(h2);

        PdfPCell h3 = new PdfPCell(new Phrase("Price (₹)", headFont));
        h3.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(h3);

        PdfPCell h4 = new PdfPCell(new Phrase("Total (₹)", headFont));
        h4.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(h4);

        // Table row
        table.addCell(order.getProductName());
        table.addCell(String.valueOf(order.getQuantity()));
        table.addCell(String.valueOf(order.getPrice()));
        table.addCell(String.valueOf(order.getPrice() * order.getQuantity()));

        document.add(table);

        document.add(new Paragraph(" "));
        LineSeparator ls2 = new LineSeparator();
        document.add(ls2);
        document.add(new Paragraph(" "));

        // ---------- TOTAL AMOUNT ----------
        Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.RED);
        Paragraph total = new Paragraph("Grand Total: ₹" + (order.getPrice() * order.getQuantity()), totalFont);
        total.setAlignment(Element.ALIGN_RIGHT);
        document.add(total);

        document.add(new Paragraph(" "));
        document.add(new Paragraph("Thank you for shopping with us!", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.GRAY)));

        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }
}

