package com.stockexchange.portfolioservice.portfolio;

import com.stockexchange.portfolioservice.exception.ErrorException;
import com.stockexchange.portfolioservice.portfolio.domain.OrderType;
import com.stockexchange.portfolioservice.portfolio.domain.Portfolio;
import com.stockexchange.portfolioservice.portfolio.event.OrderEvent;
import com.stockexchange.portfolioservice.position.Position;
import com.stockexchange.portfolioservice.position.PositionRepository;
import com.stockexchange.portfolioservice.position.PositionResponse;
import com.stockexchange.portfolioservice.trade.Transaction;
import com.stockexchange.portfolioservice.portfolio.dto.PortfolioResponse;
import com.stockexchange.portfolioservice.trade.TransactionRepository;
import com.stockexchange.portfolioservice.trade.TransactionStatus;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.*;

@Service
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final TransactionRepository transactionRepository;
    private final PositionRepository positionRepository;
    Logger log = org.slf4j.LoggerFactory.getLogger(PortfolioService.class);

    public PortfolioService(PortfolioRepository portfolioRepository, TransactionRepository transactionRepository, PositionRepository positionRepository) {
        this.portfolioRepository = portfolioRepository;
        this.transactionRepository = transactionRepository;
        this.positionRepository = positionRepository;
    }

    public Mono<PortfolioResponse> getPortfolioByUserId(UUID userId) {
        return portfolioRepository.findByUserId(userId)
                .switchIfEmpty(Mono.error(new ErrorException("Portfólio não encontrado para o usuário: " + userId)))
                .flatMap(portfolio -> {
                    Mono<List<PositionResponse>> positionsMono = positionRepository
                            .findByPortfolioId(portfolio.getPortfolioId())
                            .map(PositionResponse::new)
                            .collectList()
                            .defaultIfEmpty(Collections.emptyList());
                    return Mono.just(portfolio)
                            .zipWith(positionsMono, PortfolioResponse::new);
                });
    }

    @Transactional
    public Mono<Void> applyTransactionToPortfolio(Transaction transaction) {
        UUID userId = transaction.getUserId();
        String symbol = transaction.getSymbol();

        Mono<Portfolio> portfolioMono = getOrCreatePortfolio(userId);

        return portfolioMono.flatMap(portfolio -> {
                    Mono<Position> positionMono = positionRepository
                            .findByPortfolioIdAndSymbol(portfolio.getPortfolioId(), symbol)
                            .switchIfEmpty(Mono.just(Position.create(symbol, portfolio.getPortfolioId())));

                    return Mono.zip(Mono.just(portfolio), positionMono);
                })
                .flatMap(tuple -> {
                    Portfolio portfolio = tuple.getT1();
                    Position position = tuple.getT2();

                    return switch (transaction.getOrderType()) {
                        case BUY -> applyBuyLogic(transaction, portfolio, position);
                        case SELL -> applySellLogic(transaction, portfolio, position);
                    };
                })
                .flatMap(processedTransaction ->
                        transactionRepository.save(processedTransaction.withStatus(TransactionStatus.COMPLETED))
                )
                .then();
    }


    private Mono<Transaction> applyBuyLogic(Transaction transaction, Portfolio portfolio, Position position) {
        BigDecimal totalCost = transaction.getPrice().multiply(BigDecimal.valueOf(transaction.getQuantity()));

        Portfolio updatedPortfolio = portfolio.withBuySettled(totalCost);
        Position updatedPosition = position.withBuy(transaction.getQuantity(), transaction.getPrice());

        return portfolioRepository.save(updatedPortfolio)
                .then(positionRepository.save(updatedPosition))
                .thenReturn(transaction);
    }

    private Mono<Transaction> applySellLogic(Transaction transaction, Portfolio portfolio, Position position) {
        BigDecimal totalCredit = transaction.getPrice().multiply(BigDecimal.valueOf(transaction.getQuantity()));

        Portfolio updatedPortfolio = portfolio.withSellSettled(totalCredit);

        Position updatedPosition = position.withSell(transaction.getQuantity());

        Mono<Void> positionPersistence = updatedPosition.getQuantity() == 0
                ? positionRepository.delete(position)
                : positionRepository.save(updatedPosition).then();

        return portfolioRepository.save(updatedPortfolio)
                .then(positionPersistence)
                .thenReturn(transaction);
    }

    public Mono<Portfolio> getOrCreatePortfolio(UUID userId) {
        return portfolioRepository.findByUserId(userId)
                .switchIfEmpty(
                        portfolioRepository.save(Portfolio.create(userId))
                                .onErrorResume(org.springframework.dao.DuplicateKeyException.class,
                                        e -> portfolioRepository.findByUserId(userId))
                );
    }

    public Mono<Boolean> hasBalance(OrderEvent.OrderCreated order) {
        BigDecimal requiredAmount = order.price().multiply(BigDecimal.valueOf(order.quantity()));

        if (order.orderType() == OrderType.BUY) {
            return getOrCreatePortfolio(order.userId())
                    .flatMap(_ ->
                            portfolioRepository.reserveCash(order.userId(), requiredAmount)
                    )
                    .map(rowsUpdated -> rowsUpdated > 0)
                    .defaultIfEmpty(false);
        } else {
            return Mono.just(true);
        }
    }

    public Mono<Void> refund(OrderEvent.OrderRejected event) {
        if (event.orderType() == OrderType.BUY) {
            BigDecimal amountToRefund = event.price().multiply(BigDecimal.valueOf(event.quantity()));
            return portfolioRepository.refundCash(event.userId(), amountToRefund).then();
        }
        return Mono.empty();
    }

//
//    private Mono<Boolean> checkStockPosition(UUID userId, String symbol, int quantityToSell) {
//        return portfolioRepository.findByUserId(userId)
//                .flatMap(portfolio -> positionRepository.findByPortfolioIdAndSymbol(portfolio.getPortfolioId(), symbol))
//                .map(position -> position.getQuantity() >= quantityToSell)
//                .defaultIfEmpty(false);
//    }
}
