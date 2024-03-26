package org.example.stock.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.stock.domain.Stock;
import org.example.stock.repository.StockRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PessimisticLockStockService {

    private final StockRepository stockRepository;

    @Transactional
    public void decrease(Long id, Long quantity) {
        Stock stock = stockRepository.findByIdWithPessimisticLock(id);
        stock.decrease(quantity);
        stockRepository.saveAndFlush(stock);
    }

}
