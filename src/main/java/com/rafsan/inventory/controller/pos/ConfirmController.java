package com.rafsan.inventory.controller.pos;

import com.rafsan.inventory.entity.Item;
import com.rafsan.inventory.pdf.PrintInvoice;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.scene.control.TextArea;

public class ConfirmController implements Initializable {

    @FXML
    private TextArea billingArea;
    @FXML
    private Label retailLabel;
    private double retail;
    private ObservableList<Item> items;
    private String barcode;
    DecimalFormat df =new DecimalFormat("0.00");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        retailLabel.setText("Change: $" + df.format(retail));

        String name;
        StringBuilder details = new StringBuilder("Item Name\t\t" + "Cost\t\t" + "Quantity\t\t" + "Total\n");

        for (Item i : items) {
            name = i.getItemName();
            if (name.length() > 7){
                name = name.substring(0, 7);
            }else if (name.length() < 7){
                for (int x = name.length(); x < 7; x++){
                    name = name + " ";
                }
            }
            details.append(name)
                    .append("\t\t\t")
                    .append(df.format(i.getUnitPrice()))
                    .append("\t\t\t")
                    .append((int)i.getQuantity())
                    .append("\t\t\t")
                    .append(df.format(i.getTotal()))
                    .append("\n");
        }

        billingArea.setText(details.toString());
    }

    public void setData(double retail, ObservableList<Item> items, String barcode) {
        this.retail = retail;
        this.items = FXCollections.observableArrayList(items);
        this.barcode = barcode;
    }

    @FXML
    public void doneAction(ActionEvent event) {
        billingArea.setText("");
        PrintInvoice pi = new PrintInvoice(items, barcode);
        pi.generateReport();
        ((Node) (event.getSource())).getScene().getWindow().hide();
    }
}
