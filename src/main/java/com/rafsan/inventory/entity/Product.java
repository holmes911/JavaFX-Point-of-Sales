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
@Table(name = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product implements Serializable {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "name")
    private String productName;
    @Column(name = "price")
    private double price;
    @Column(name = "quantity")
    private double quantity;
    @Column(name = "description")
    private String description;
    @Column(name = "barcode")
    private String barcode;
    
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "categoryId")
    private Category category;
    
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "supplierId")
    private Supplier supplier;

    public Product(long id, String productName, double price,
            double quantity, String description, Category category, Supplier supplier, String barcode) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.description = description;
        this.category = category;
        this.supplier = supplier;
        this.barcode = barcode;
    }

    public Product(String productName, double price,
            double quantity, String description, Category category, Supplier supplier, String barcode) {
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.description = description;
        this.category = category;
        this.supplier = supplier;
        this.barcode = barcode;
    }
}
