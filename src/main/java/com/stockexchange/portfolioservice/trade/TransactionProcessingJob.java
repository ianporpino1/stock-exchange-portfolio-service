package com.stockexchange.portfolioservice.trade;

import com.stockexchange.portfolioservice.portfolio.PortfolioService;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class TransactionProcessingJob {

    private final TransactionRepository transactionRepository;
    private final PortfolioService portfolioService;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TransactionProcessingJob.class);

    public TransactionProcessingJob(TransactionRepository transactionRepository, PortfolioService portfolioService) {
        this.transactionRepository = transactionRepository;
        this.portfolioService = portfolioService;
    }

    @Scheduled(fixedDelay = 5000)
    public void processPendingTransactions() {
        log.info("JOB: Iniciando busca por transacoes pendentes...");

        transactionRepository.findTop100PendingForUpdate()
                .collectList()
                .flatMapMany(transactions -> {
                    if (transactions.isEmpty()) {
                        log.info("JOB: Nenhuma transacao pendente encontrada.");
                        return Flux.empty();
                    }

                    log.info("JOB: {} transacoes encontradas. Processando...", transactions.size());
                    return Flux.fromIterable(transactions);
                })
                .flatMap(portfolioService::applyTransactionToPortfolio, 1)
                .doOnError(error -> log.error("JOB: Erro: ", error))
                .then()
                .block();
    }


}
