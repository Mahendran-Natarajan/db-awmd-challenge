package com.db.awmd.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class MoneyTransfer {

    public String getAccountFrom() {
        return accountFrom;
    }

    public String getAccountTo() {
        return accountTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @NotNull
    @NotEmpty
    private final String accountFrom;

    @NotNull
    @NotEmpty
    private final String accountTo;

    @NotNull
    @NotEmpty
    private BigDecimal amount;

    @JsonCreator
    public MoneyTransfer(@JsonProperty("accountFrom") String accountFrom,
                         @JsonProperty("accountTo") String accountTo,
                         @JsonProperty("amount") BigDecimal amount) {
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
    }
}
