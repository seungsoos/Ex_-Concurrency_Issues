package org.example.stock.facade;

import lombok.RequiredArgsConstructor;
import org.example.stock.repository.RedisLockRepository;
import org.example.stock.service.StockService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LettuceLockFacade {

    private final RedisLockRepository lockRepository;
    private final StockService stockService;

    public void decrease(Long id, Long quantity) throws InterruptedException {
        while (!lockRepository.lock(id)) {
            Thread.sleep(100);
        }
        try {
            stockService.decrease(id,quantity);
        } finally {
            lockRepository.unlock(id);
        }
    }
}