package com.stockexchange.portfolioservice.portfolio;

import com.stockexchange.portfolioservice.exception.ErrorException;
import com.stockexchange.portfolioservice.portfolio.domain.OrderType;
import com.stockexchange.portfolioservice.portfolio.domain.Portfolio;
import com.stockexchange.portfolioservice.trade.Transaction;
import com.stockexchange.portfolioservice.portfolio.dto.PortfolioResponse;
import com.stockexchange.portfolioservice.trade.TransactionRepository;
import com.stockexchange.portfolioservice.trade.TransactionStatus;
import com.stockexchange.portfolioservice.trade.dto.TradeListResponse;
import com.stockexchange.portfolioservice.trade.dto.TradeResponse;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.stream.Stream;

@Service
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionTemplate transactionTemplate;

    public PortfolioService(PortfolioRepository portfolioRepository, TransactionRepository transactionRepository, TransactionTemplate transactionTemplate) {
        this.portfolioRepository = portfolioRepository;
        this.transactionRepository = transactionRepository;
        this.transactionTemplate = transactionTemplate;
    }

    public PortfolioResponse getPortfolioByUserId(UUID userId) {
        Portfolio portfolio = portfolioRepository.findByUserId(userId)
                .orElseThrow(() -> new ErrorException("Portfolio não encontrado para o usuário com ID: " + userId));

        return new PortfolioResponse(portfolio);
    }

    @Transactional
    public void applyTransactionToPortfolio(Transaction transaction) {
        UUID userId = transaction.getUserId();
        Portfolio portfolio = getOrCreatePortfolio(userId);
        portfolio.applyTransaction(transaction.getSymbol(), transaction.getQuantity(), transaction.getPrice(), transaction.getOrderType());

        transaction.setStatus(TransactionStatus.COMPLETED);
        transactionRepository.save(transaction);
    }

//
//    public void updatePortfoliosFromTrades(TradeListResponse trades) {
//        for (var trade : trades.trades()) {
//            transactionTemplate.executeWithoutResult(_ -> processSingleTrade(trade));
//        }
//    }

    //
//    public void processSingleTrade(TradeResponse trade) {
//        UUID buyerUserId = trade.buyerUserId();
//        UUID sellerUserId = trade.sellerUserId();
//
//        List<UUID> sortedUserIds = Stream.of(buyerUserId, sellerUserId)
//                .sorted()
//                .toList();
//        long key1 = sortedUserIds.get(0).hashCode();
//        long key2 = sortedUserIds.get(1).hashCode();
//        portfolioRepository.acquireAdvisoryLock(key1);
//        portfolioRepository.acquireAdvisoryLock(key2);
//
//        Portfolio firstPortfolio = getOrCreatePortfolio(sortedUserIds.get(0));
//        Portfolio secondPortfolio = getOrCreatePortfolio(sortedUserIds.get(1));
//
//        Portfolio buyerPortfolio = firstPortfolio.getUserId().equals(buyerUserId) ? firstPortfolio : secondPortfolio;
//        Portfolio sellerPortfolio = firstPortfolio.getUserId().equals(sellerUserId) ? firstPortfolio : secondPortfolio;
//
//        Transaction buyTransaction = new Transaction(
//                buyerPortfolio,
//                trade.tradeId(),
//                trade.symbol(),
//                trade.quantity(),
//                trade.price(),
//                OrderType.BUY,
//                trade.executedAt(),
//                trade.buyOrderId()
//        );
//
//        Transaction sellTransaction = new Transaction(
//                sellerPortfolio,
//                trade.tradeId(),
//                trade.symbol(),
//                trade.quantity(),
//                trade.price(),
//                OrderType.SELL,
//                trade.executedAt(),
//                trade.sellOrderId()
//        );
//
//        buyerPortfolio.addTransaction(buyTransaction);
//        buyerPortfolio.applyTransaction(trade.symbol(), trade.quantity(), trade.price(), OrderType.BUY);
//
//        sellerPortfolio.addTransaction(sellTransaction);
//        sellerPortfolio.applyTransaction(trade.symbol(), trade.quantity(), trade.price(), OrderType.SELL);
//
//        transactionRepository.saveAll(List.of(buyTransaction, sellTransaction));
//    }
    @Transactional
    public Portfolio getOrCreatePortfolio(UUID userId) {
        Optional<Portfolio> portfolioOpt = portfolioRepository.findByUserId(userId);

        if (portfolioOpt.isPresent()) {
            return portfolioOpt.get();
        }

        try {
            Portfolio newPortfolio = new Portfolio(userId);
            return portfolioRepository.save(newPortfolio);
        } catch (DataIntegrityViolationException e) {
            return portfolioRepository.findByUserId(userId)
                    .orElseThrow(() -> new IllegalStateException("Falha crítica ao buscar portfólio para o usuário: " + userId, e));
        }
    }

}
