package com.restaurante.app.repository;

import com.restaurante.app.models.Invoice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data repository for {@link Invoice invoices} and the sales queries built on top of them.
 */
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

    /**
     * @param orderId the billed order id
     * @return the invoice for the order, if it has been billed
     */
    Optional<Invoice> findByOrderId(Integer orderId);

    /**
     * @param from inclusive lower bound of the billing date range
     * @param to   inclusive upper bound of the billing date range
     * @return invoices billed within the range
     */
    List<Invoice> findByInvoicedAtBetween(LocalDateTime from, LocalDateTime to);

    /**
     * Product names ranked by quantity sold within the billing date range, best sellers first.
     * The caller takes the first element as the best seller (or the last as the worst).
     *
     * @param from inclusive lower bound of the billing date range
     * @param to   inclusive upper bound of the billing date range
     * @return product names ordered by total quantity sold, descending
     */
    @Query("""
            SELECT p.name FROM OrderItem oi, Product p, Order o, Invoice f
            WHERE oi.productId = p.id AND oi.orderId = o.id AND o.id = f.orderId
              AND f.invoicedAt BETWEEN :from AND :to
            GROUP BY oi.productId, p.name
            ORDER BY SUM(oi.quantity) DESC
            """)
    List<String> findProductNamesRankedBySales(@Param("from") LocalDateTime from,
                                               @Param("to") LocalDateTime to);
}
