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
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "sales")
@Data
@NoArgsConstructor
public class Sale implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "invoiceId")
    private Invoice invoice;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "productId")
    private Product product;

    @Column(name = "quantity")
    private double quantity;
    @Column(name = "price")
    private double price;
    @Column(name = "total")
    private double total;
    @Column(name = "datetime", insertable=false)
    private String date;
    @Column(name = "rrn")
    private String rrn;
    @Column(name = "description")
    private String description;
    @Column(name = "reference")
    private String reference;
    @Column(name = "imei")
    private String imei;
    @Column(name = "pan")
    private String pan;
    @Column(name = "channel")
    private String channel;

    public Sale(Invoice invoice, Product product, double quantity, double price, double total) {
        this.invoice = invoice;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
        this.channel = "CASH";
    }

    public Sale(Invoice invoice, Product product, double quantity, double price, double total, String rrn, String description, String reference, String imei, String pan, String channel) {
        this.invoice = invoice;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
        this.rrn = rrn;
        this.description = description;
        this.reference = reference;
        this.imei = imei;
        this.pan = pan;
        this.channel = channel;
    }

    public Sale(long id, Invoice invoice, Product product, double quantity, double price, double total, String date) {
        this.id = id;
        this.invoice = invoice;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
        this.date = date;
        this.channel = "CASH";
    }
}
