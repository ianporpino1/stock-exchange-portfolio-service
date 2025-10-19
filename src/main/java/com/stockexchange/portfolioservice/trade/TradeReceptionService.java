package com.stockexchange.portfolioservice.trade;

import com.stockexchange.portfolioservice.portfolio.domain.OrderType;
import com.stockexchange.portfolioservice.trade.dto.TradeListResponse;
import com.stockexchange.portfolioservice.trade.dto.TradeResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Stream;

@Service
public class TradeReceptionService {

    private final TransactionRepository transactionRepository;

    public TradeReceptionService( TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Mono<Void> createPendingTransactionsForTrades(TradeListResponse trades) {
        List<Transaction> allTransactions = trades.trades().stream()
                .flatMap(trade -> {
                    Transaction buyTransaction = Transaction.create(
                            trade.tradeId(),
                            trade.symbol(),
                            trade.quantity(),
                            trade.price(),
                            OrderType.BUY,
                            trade.executedAt(),
                            trade.buyerUserId()
                    );
                    Transaction sellTransaction = Transaction.create(
                            trade.tradeId(),
                            trade.symbol(),
                            trade.quantity(),
                            trade.price(),
                            OrderType.SELL,
                            trade.executedAt(),
                            trade.sellerUserId()
                    );
                    return Stream.of(buyTransaction, sellTransaction);
                })
                .toList();

        if (allTransactions.isEmpty()) {
            return Mono.empty();
        }

        return transactionRepository.saveAll(allTransactions).then();
    }

}
