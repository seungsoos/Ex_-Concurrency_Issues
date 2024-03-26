package org.example.stock.facade;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.stock.repository.LockRepository;
import org.example.stock.service.StockService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NamedLockStockFacade {

    private final LockRepository lockRepository;
    private final StockService stockService;

    @Transactional
    public void decrease(Long id, Long quantity) {
        try {
            lockRepository.getLock(id.toString());
            stockService.decrease(id, quantity);
        } finally {
            lockRepository.releaseLock(id.toString());
        }
    }

}
