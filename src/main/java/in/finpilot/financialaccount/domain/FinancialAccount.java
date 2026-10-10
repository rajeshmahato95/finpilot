package in.finpilot.financialaccount.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "financial_account")
public class FinancialAccount {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @NotBlank
    @Column(name = "display_name", nullable = false, length = 120)
    private String displayName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 32)
    private AccountType accountType;

    @NotBlank
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @NotBlank
    @Column(name = "data_source", nullable = false, length = 40)
    private String dataSource;

    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected FinancialAccount() {
        // Required by JPA.
    }

    public FinancialAccount(
            UUID userId,
            String displayName,
            AccountType accountType,
            String currency,
            String dataSource) {
        this.userId = userId;
        this.displayName = displayName;
        this.accountType = accountType;
        this.currency = currency;
        this.dataSource = dataSource;
    }

    @PrePersist
    void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public String getCurrency() {
        return currency;
    }

    public String getDataSource() {
        return dataSource;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
