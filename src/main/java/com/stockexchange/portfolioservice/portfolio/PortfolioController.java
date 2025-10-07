package com.stockexchange.portfolioservice.portfolio;

import com.stockexchange.portfolioservice.portfolio.dto.PortfolioResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/portfolios")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/{userId}")
    @PreAuthorize("#userId.toString() == principal.subject")
    public PortfolioResponse getPortfolioByUserId(@PathVariable UUID userId) {
        return portfolioService.getPortfolioByUserId(userId);
    }


}
