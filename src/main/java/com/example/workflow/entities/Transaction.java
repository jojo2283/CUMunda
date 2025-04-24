package com.example.workflow.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    @Column(name = "points_used", nullable = false)
    private int pointsUsed;
    private boolean refunded;
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    public Transaction() {
    }

    public Transaction(User user, Product product, int pointsUsed) {
        this.user = user;
        this.product = product;
        this.pointsUsed = pointsUsed;
        this.refunded = false;
        this.transactionDate = LocalDateTime.now();
    }


}
