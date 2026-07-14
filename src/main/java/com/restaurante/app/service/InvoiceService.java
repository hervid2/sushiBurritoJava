package com.restaurante.app.service;

import com.restaurante.app.models.Invoice;
import com.restaurante.app.repository.InvoiceRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Business rules for invoices: persisting a generated invoice and answering sales queries.
 *
 * <p>Free of Swing dependencies; persistence goes through {@link InvoiceRepository}.
 */
@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    /**
     * Persists an invoice. The monetary amounts are stored as a snapshot at billing time.
     *
     * @param invoice the invoice to store
     */
    @Transactional
    public void save(Invoice invoice) {
        invoiceRepository.save(invoice);
    }

    /**
     * @param orderId the order id
     * @return the invoice for the order, or {@code null} if it has not been billed
     */
    public Invoice findByOrderId(int orderId) {
        return invoiceRepository.findByOrderId(orderId).orElse(null);
    }

    /**
     * @param from inclusive lower bound of the billing date range
     * @param to   inclusive upper bound of the billing date range
     * @return invoices billed within the range
     */
    public List<Invoice> findByDateRange(LocalDateTime from, LocalDateTime to) {
        return invoiceRepository.findByInvoicedAtBetween(from, to);
    }

    /**
     * @return the name of the best-selling product in the range, or {@code null} if there are no sales
     */
    public String findBestSellingProductName(LocalDateTime from, LocalDateTime to) {
        List<String> ranked = invoiceRepository.findProductNamesRankedBySales(from, to);
        return ranked.isEmpty() ? null : ranked.get(0);
    }

    /**
     * @return the name of the worst-selling product in the range, or {@code null} if there are no sales
     */
    public String findWorstSellingProductName(LocalDateTime from, LocalDateTime to) {
        List<String> ranked = invoiceRepository.findProductNamesRankedBySales(from, to);
        return ranked.isEmpty() ? null : ranked.get(ranked.size() - 1);
    }
}
