package org.example.stock.service;

import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.example.stock.domain.Stock;
import org.example.stock.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
//@Transactional
class StockServiceTest {

    @Autowired
    private StockService stockService;
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
    @DisplayName("재고의 수량만큼 재고가 감소 테스트")
    void decrease() {
        // given
        stockService.decrease(1L, 1L);
        // when
        Stock stock = stockRepository.findById(1L).orElseThrow();
        // then
        assertEquals(stock.getQuantity(), 99);
    }

    @Test
    @DisplayName("재고의 수량 감소 - 동시성 문제")
    void concurrent_decrease() throws InterruptedException {
        // given
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    stockService.decrease(1L, 1L);
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