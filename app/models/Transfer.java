package models;

import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity
public class Transfer extends Model {

    @Id
    @GeneratedValue
    public java.util.UUID requestId;

    Long sourceAccountId;

    Long targetAccountId;

    @Column(precision = 15, scale = 10)
    BigDecimal amount;

    Date transferDate;

    public static Finder<Long, Transfer> find = new Finder<>(Transfer.class);

    public Transfer(Account accountFrom, Account accountTo, BigDecimal amount) {
        transferDate = new Date();
        this.sourceAccountId = accountFrom.getId();
        this.targetAccountId = accountTo.getId();
        this.amount = amount;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public Long getSourceAccountId() {
        return sourceAccountId;
    }

    public void setSourceAccountId(Long sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public Long getTargetAccountId() {
        return targetAccountId;
    }

    public void setTargetAccountId(Long targetAccountId) {
        this.targetAccountId = targetAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(Date transferDate) {
        this.transferDate = transferDate;
    }
}
