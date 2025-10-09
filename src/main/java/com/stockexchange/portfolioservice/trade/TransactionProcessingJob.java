package com.stockexchange.portfolioservice.trade;

import com.stockexchange.portfolioservice.portfolio.PortfolioService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionProcessingJob {

    private final TransactionRepository transactionRepository;
    private final PortfolioService portfolioService;

    public TransactionProcessingJob(TransactionRepository transactionRepository, PortfolioService portfolioService) {
        this.transactionRepository = transactionRepository;
        this.portfolioService = portfolioService;
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processPendingTransactions() {
        System.out.println("JOB: Procurando por transações pendentes...");

        List<Transaction> pendingTransactions = transactionRepository.findTop1000PendingForUpdate();

        if (pendingTransactions.isEmpty()) {
            return;
        }

        System.out.println("JOB: " + pendingTransactions.size() + " transações encontradas. Processando...");

        for (Transaction transaction : pendingTransactions) {
            try {
                portfolioService.applyTransactionToPortfolio(transaction);
            } catch (Exception e) {
                System.err.println("Erro ao processar transação " + transaction.getTransactionId() + ": " + e.getMessage());
            }
        }
    }


}
