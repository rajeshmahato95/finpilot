package in.finpilot.financialaccount.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FinancialAccountTest {

    @Test
    void createsAccountWithProvidedBusinessFields() {
        UUID userId = UUID.randomUUID();

        FinancialAccount account = new FinancialAccount(
                userId,
                "Primary savings",
                AccountType.SAVINGS,
                "INR",
                "MANUAL_IMPORT");

        assertThat(account.getUserId()).isEqualTo(userId);
        assertThat(account.getDisplayName()).isEqualTo("Primary savings");
        assertThat(account.getAccountType()).isEqualTo(AccountType.SAVINGS);
        assertThat(account.getCurrency()).isEqualTo("INR");
        assertThat(account.getDataSource()).isEqualTo("MANUAL_IMPORT");
    }
}
