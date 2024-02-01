package com.example.demo;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;

@Entity
@Table(name = "Cash_card")
public class CashCard implements Serializable {
   
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name = "amount", nullable = false )
    private Double amount;

    public CashCard() {
        super();
    }

    public CashCard(Long id, Double amount) {
        super();
        this.id = id;
        this.amount = amount;
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
    @Override
    public String toString() {
        return "CashCard [id=" + id + ", amount=" + amount + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((amount == null) ? 0 : amount.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
        return true;
    if (obj == null)
        return false;
    if (getClass() != obj.getClass())
        return false;
    CashCard other = (CashCard) obj;
    if (amount == null) {
        if (other.amount != null)
            return false;
    } else if (!amount.equals(other.amount))
        return false;
    if (id != other.id)
        return false;
    return true;
    }

}
