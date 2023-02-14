package com.test.task.bankservice.entity;

import com.test.task.bankservice.OperationStatus;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class Operation {
    @Id
    @Column
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(nullable = false)
    private Double amountOfPayment;

    @Column(nullable = false)
    private String status = OperationStatus.FAILED.toString();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Double getAmountOfPayment() {
        return amountOfPayment;
    }

    public void setAmountOfPayment(Double amountOfPayment) {
        this.amountOfPayment = amountOfPayment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
