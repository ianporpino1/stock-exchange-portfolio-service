package com.stockexchange.portfolioservice.trade;

import com.stockexchange.portfolioservice.portfolio.domain.OrderType;
import com.stockexchange.portfolioservice.trade.dto.TradeListInput;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.stream.Stream;

@Service
public class TradeReceptionService {

    private final TransactionRepository transactionRepository;

    public TradeReceptionService( TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Mono<Boolean> createPendingTransactionsForTrades(TradeListInput trades) {
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

        return transactionRepository.saveAll(allTransactions).then(Mono.just(true));
    }

}
