package in.finpilot.financialaccount.api;

import in.finpilot.financialaccount.domain.FinancialAccount;
import in.finpilot.financialaccount.infrastructure.FinancialAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/financial-accounts")
public class FinancialAccountController {

    private final FinancialAccountRepository repository;

    public FinancialAccountController(FinancialAccountRepository repository) {
        this.repository = repository;
    }

    /**
     * Temporary development endpoint to verify persistence.
     * Authentication and user-scoped authorization must be implemented before exposing
     * any real financial data. Never trust a caller-provided userId in a production API.
     */
    @PostMapping("/_dev")
    @ResponseStatus(HttpStatus.CREATED)
    public FinancialAccount createDevelopmentAccount(@RequestBody CreateAccountRequest request) {
        return repository.save(new FinancialAccount(
                request.userId(),
                request.displayName(),
                request.accountType(),
                request.currency(),
                request.dataSource()));
    }

    /**
     * Temporary development endpoint. Replace with authenticated principal scoping
     * before handling real user data.
     */
    @GetMapping("/_dev/by-user/{userId}")
    public List<FinancialAccount> getDevelopmentAccounts(@PathVariable UUID userId) {
        return repository.findAllByUserId(userId);
    }

    public record CreateAccountRequest(
            UUID userId,
            String displayName,
            in.finpilot.financialaccount.domain.AccountType accountType,
            String currency,
            String dataSource) {
    }
}
