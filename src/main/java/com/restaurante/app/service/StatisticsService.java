package com.restaurante.app.service;

import com.restaurante.app.models.Invoice;
import com.restaurante.app.models.ProductSalesDTO;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Produces the sales statistics shown on the admin dashboard (revenue, best/worst selling products).
 *
 * <p>Free of Swing dependencies. Delegates data access to {@link InvoiceService}.
 */
@Service
public class StatisticsService {

    private final InvoiceService invoiceService;

    public StatisticsService(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    /**
     * @return invoices billed between the two instants
     */
    public List<Invoice> findInvoicesBetween(LocalDateTime from, LocalDateTime to) {
        return invoiceService.findByDateRange(from, to);
    }

    /**
     * @return the best-selling product for the range, or an {@code N/A} placeholder when there are no sales
     */
    public ProductSalesDTO findBestSellingProduct(LocalDateTime from, LocalDateTime to) {
        String name = invoiceService.findBestSellingProductName(from, to);
        return new ProductSalesDTO(name != null ? name : "N/A", "más vendido");
    }

    /**
     * @return the worst-selling product for the range, or an {@code N/A} placeholder when there are no sales
     */
    public ProductSalesDTO findWorstSellingProduct(LocalDateTime from, LocalDateTime to) {
        String name = invoiceService.findWorstSellingProductName(from, to);
        return new ProductSalesDTO(name != null ? name : "N/A", "menos vendido");
    }

    /**
     * Aggregates the dashboard figures for the given date range.
     *
     * @return a map with keys {@code facturas}, {@code totalIngresos}, {@code masVendido} and
     *         {@code menosVendido}
     */
    public Map<String, Object> generateStatistics(LocalDateTime from, LocalDateTime to) {
        List<Invoice> invoices = findInvoicesBetween(from, to);
        double totalRevenue = invoices.stream().mapToDouble(Invoice::getTotal).sum();

        Map<String, Object> data = new HashMap<>();
        data.put("facturas", invoices);
        data.put("totalIngresos", totalRevenue);
        data.put("masVendido", findBestSellingProduct(from, to));
        data.put("menosVendido", findWorstSellingProduct(from, to));
        return data;
    }
}
