package com.rafsan.inventory.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "purchases")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Purchase implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "productId")
    private Product product;
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "supplierId")
    private Supplier supplier;
    @Column(name = "quantity")
    private double quantity;
    @Column(name = "price")
    private double price;
    @Column(name = "total")
    private double total;
    @Column(name = "datetime", insertable=false)
    private String date;

    public Purchase(Product product, Supplier supplier,
            double quantity, double price, double total) {
        this.product = product;
        this.supplier = supplier;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
    }
}
