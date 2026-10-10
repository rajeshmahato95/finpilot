package in.finpilot.financialaccount.infrastructure;

import in.finpilot.financialaccount.domain.FinancialAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FinancialAccountRepository extends JpaRepository<FinancialAccount, UUID> {

    List<FinancialAccount> findAllByUserId(UUID userId);
}
