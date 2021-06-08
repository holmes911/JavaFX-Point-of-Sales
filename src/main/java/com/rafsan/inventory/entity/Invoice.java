package com.rafsan.inventory.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "invoices")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Invoice implements Serializable {
    
    @Id
    @Column(name = "id")
    private String id;
    
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "employeeId")
    private Employee employee;
    
    @Column(name = "total")
    private double total;
    @Column(name = "vat")
    private double vat;
    @Column(name = "discount")
    private double discount;
    @Column(name = "payable")
    private double payable;
    @Column(name = "paid")
    private double paid;
    @Column(name = "returned")
    private double returned;
    @Column(name = "datetime", insertable=false)
    private String date;

    public Invoice(String id, Employee employee, double total, double vat, 
            double discount, double payable, double paid, double returned) {
        this.id = id;
        this.employee = employee;
        this.total = total;
        this.vat = vat;
        this.discount = discount;
        this.payable = payable;
        this.paid = paid;
        this.returned = returned;
    }
}
