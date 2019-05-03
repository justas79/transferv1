package models;

import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Currency;

@Entity
public class Account extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    String name;

    @Column(precision = 15, scale = 10)
    BigDecimal balance = new BigDecimal(0);

    Currency currencyCode;

    Boolean active;

    public static Finder<Long, Account> find = new Finder<>(Account.class);

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Currency getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(Currency currencyCode) {
        this.currencyCode = currencyCode;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Boolean getActive() {
        return active;
    }

    public void withdraw(BigDecimal amount) {
        balance = balance.subtract(amount);
    }

    public void deposit(BigDecimal amount) {
        balance = balance.add(amount);
    }
}
