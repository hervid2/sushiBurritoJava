package com.restaurante.app;

import static org.assertj.core.api.Assertions.assertThat;

import com.restaurante.app.service.InvoiceService;
import com.restaurante.app.service.OrderService;
import com.restaurante.app.service.ProductService;
import com.restaurante.app.service.StatisticsService;
import com.restaurante.app.service.UserService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

/**
 * Smoke test: the whole Spring context (JPA, repositories, services, security beans) starts against
 * an in-memory database, so a wiring or configuration regression fails the build before it reaches
 * the demo. Runs under the {@code test} profile, which disables the Swing UI runner (headless) and
 * swaps MySQL/Flyway for H2 (see {@code application-test.properties}).
 */
@SpringBootTest
@ActiveProfiles("test")
class SushiBurritoApplicationTests {

    @Autowired
    private ApplicationContext context;

    /** The core service beans every demo flow depends on must be present and injectable. */
    @Test
    void contextLoads_andCoreServiceBeansArePresent() {
        assertThat(context.getBean(UserService.class)).isNotNull();
        assertThat(context.getBean(OrderService.class)).isNotNull();
        assertThat(context.getBean(ProductService.class)).isNotNull();
        assertThat(context.getBean(InvoiceService.class)).isNotNull();
        assertThat(context.getBean(StatisticsService.class)).isNotNull();
    }
}
