package com.stockexchange.portfolioservice.trade;

import com.stockexchange.portfolioservice.portfolio.domain.OrderType;
import com.stockexchange.portfolioservice.trade.dto.TradeListResponse;
import com.stockexchange.portfolioservice.trade.dto.TradeResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TradeReceptionService {

    private final TransactionRepository transactionRepository;


    public TradeReceptionService( TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void createPendingTransactionsForTrades(TradeListResponse trades) {
        for (TradeResponse trade : trades.trades()) {
            Transaction buyTransaction = new Transaction(
                    trade.tradeId(),
                    trade.symbol(),
                    trade.quantity(),
                    trade.price(),
                    OrderType.BUY,
                    trade.executedAt(),
                    trade.buyerUserId()
            );

            Transaction sellTransaction = new Transaction(
                    trade.tradeId(),
                    trade.symbol(),
                    trade.quantity(),
                    trade.price(),
                    OrderType.SELL,
                    trade.executedAt(),
                    trade.sellerUserId()
            );

            transactionRepository.saveAll(List.of(buyTransaction, sellTransaction));
        }
    }

}
