package org.example.stock.facade;

import org.example.stock.domain.Stock;
import org.example.stock.repository.StockRepository;
import org.example.stock.service.StockService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class OptimisticLockStockFacadeTest {

    @Autowired
    private OptimisticLockStockFacade optimisticLockStockFacade;
    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    public void beforeEach() {
        stockRepository.save(new Stock(1L, 100L));
    }

    @AfterEach
    public void afterEach() {
        stockRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("Optimistic Lock Test")
    void optimisticLockStockService() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    optimisticLockStockFacade.decrease(1L, 1L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();

        // when
        Stock stock = stockRepository.findById(1L).orElseThrow();

        // then
        assertEquals(0, stock.getQuantity());
    }
}