package com.rafsan.inventory.controller.pos;

import com.rafsan.inventory.entity.Invoice;
import com.rafsan.inventory.entity.Item;
import com.rafsan.inventory.entity.Payment;
import com.rafsan.inventory.entity.Product;
import com.rafsan.inventory.entity.Sale;
import com.rafsan.inventory.ipos.IPOSTransaction;
import com.rafsan.inventory.model.EmployeeModel;
import com.rafsan.inventory.model.InvoiceModel;
import com.rafsan.inventory.model.ProductModel;
import com.rafsan.inventory.model.SalesModel;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

public class InvoiceController implements Initializable {

    @FXML
    private TextField totalAmountField, paidAmountField;
    private double netPrice;
    private ObservableList<Item> items;
    private EmployeeModel employeeModel;
    private ProductModel productModel;
    private SalesModel salesModel;
    private InvoiceModel invoiceModel;
    private Payment payment;

    private double xOffset = 0;
    private double yOffset = 0;

    IPOSTransaction iposTransaction;

    DecimalFormat df =new DecimalFormat("0.00");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        productModel = new ProductModel();
        employeeModel = new EmployeeModel();
        salesModel = new SalesModel();
        invoiceModel = new InvoiceModel();
        totalAmountField.setText(String.valueOf(df.format(netPrice)));
    }

    public void setData(double netPrice, ObservableList<Item> items, Payment payment) {

        this.netPrice = netPrice;
        this.items = FXCollections.observableArrayList(items);
        this.payment = payment;
    }

    @FXML
    public void confirmAction(ActionEvent event) throws Exception {
        Sale sale = null;

        if (validateInput()) {
            double paid = Double.parseDouble(paidAmountField.getText().trim());
            double retail = Math.abs(paid - netPrice);

            String invoiceId = String.valueOf(new Timestamp(System.currentTimeMillis()).getTime());

            Invoice invoice = new Invoice(
                    invoiceId,
                    employeeModel.getEmployee(2),
                    payment.getSubTotal(),
                    payment.getVat(),
                    payment.getDiscount(),
                    payment.getPayable(),
                    paid,
                    retail
            );

            invoiceModel.saveInvoice(invoice);

            for (Item i : items) {

                Product p = productModel.getProductByName(i.getItemName());
                double quantity = p.getQuantity() - i.getQuantity();
                p.setQuantity(quantity);
                productModel.decreaseProduct(p);

                sale = new Sale(
                        invoiceModel.getInvoice(invoiceId),
                        productModel.getProductByName(i.getItemName()),
                        i.getQuantity(),
                        i.getUnitPrice(),
                        i.getTotal()
                );

                salesModel.saveSale(sale);
            }

            FXMLLoader loader = new FXMLLoader((getClass().getResource("/fxml/Confirm.fxml")));
            ConfirmController controller = new ConfirmController();
            controller.setData(retail, items, invoiceId, sale);
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            root.setOnMousePressed((MouseEvent e) -> {
                xOffset = e.getSceneX();
                yOffset = e.getSceneY();
            });
            root.setOnMouseDragged((MouseEvent e) -> {
                stage.setX(e.getScreenX() - xOffset);
                stage.setY(e.getScreenY() - yOffset);
            });
            stage.setTitle("Confirm");
            stage.getIcons().add(new Image("/images/logo.png"));
            stage.setScene(scene);
            stage.show();
        }

    }

    @FXML
    public void ecocashTransaction(ActionEvent event) throws Exception {
        paidAmountField.setText(totalAmountField.getText());
        Sale sale = null;

        JSONObject request = new JSONObject();
        request.put("saleAmount", Double.parseDouble(totalAmountField.getText()) * 100);
        request.put("cashBack", "0");
        request.put("posUser", "");
        request.put("tenderType", "MOBILE");
        request.put("currency", "RTGS");
        request.put("transactionId", "0001");

        iposTransaction = new IPOSTransaction();
        JSONObject result = iposTransaction.makeTransaction(request);

        if (result.get("code").equals("00")){
            double paid = Double.parseDouble(paidAmountField.getText().trim());
            double retail = Math.abs(paid - netPrice);

            String invoiceId = String.valueOf(new Timestamp(System.currentTimeMillis()).getTime());

            Invoice invoice = new Invoice(
                    invoiceId,
                    employeeModel.getEmployee(2),
                    payment.getSubTotal(),
                    payment.getVat(),
                    payment.getDiscount(),
                    payment.getPayable(),
                    paid,
                    retail
            );

            invoiceModel.saveInvoice(invoice);

            for (Item i : items) {

                Product p = productModel.getProductByName(i.getItemName());
                double quantity = p.getQuantity() - i.getQuantity();
                p.setQuantity(quantity);
                productModel.decreaseProduct(p);

                sale = new Sale(
                        invoiceModel.getInvoice(invoiceId),
                        productModel.getProductByName(i.getItemName()),
                        i.getQuantity(),
                        i.getUnitPrice(),
                        i.getTotal(),
                        result.getString("rrn"),
                        result.getString("code") + " : " + result.getString("description"),
                        result.getString("ref"),
                        result.getString("imei"),
                        result.getString("pan"),
                        "ECOCASH"
                );

                salesModel.saveSale(sale);
            }

            FXMLLoader loader = new FXMLLoader((getClass().getResource("/fxml/Confirm.fxml")));
            ConfirmController controller = new ConfirmController();
            controller.setData(retail, items, invoiceId, sale);
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            root.setOnMousePressed((MouseEvent e) -> {
                xOffset = e.getSceneX();
                yOffset = e.getSceneY();
            });
            root.setOnMouseDragged((MouseEvent e) -> {
                stage.setX(e.getScreenX() - xOffset);
                stage.setY(e.getScreenY() - yOffset);
            });
            stage.setTitle("Confirm");
            stage.getIcons().add(new Image("/images/logo.png"));
            stage.setScene(scene);
            stage.show();
        }else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Alert");
            alert.setHeaderText("Transaction Failed");
            alert.setContentText("[" + result.get("code") + "] " + result.get("description"));
            alert.showAndWait();
        }
    }

    @FXML
    public void bankCardTransaction(ActionEvent event) throws Exception {
        paidAmountField.setText(totalAmountField.getText());
        Sale sale = null;

        JSONObject request = new JSONObject();
        request.put("saleAmount", Double.parseDouble(totalAmountField.getText()) * 100);
        request.put("cashBack", "0");
        request.put("posUser", "");
        request.put("tenderType", "SWIPE");
        request.put("currency", "RTGS");
        request.put("transactionId", "0001");

        iposTransaction = new IPOSTransaction();
        JSONObject result = iposTransaction.makeTransaction(request);

        if (result.get("code").equals("00")){
            double paid = Double.parseDouble(paidAmountField.getText().trim());
            double retail = Math.abs(paid - netPrice);

            String invoiceId = String.valueOf(new Timestamp(System.currentTimeMillis()).getTime());

            Invoice invoice = new Invoice(
                    invoiceId,
                    employeeModel.getEmployee(2),
                    payment.getSubTotal(),
                    payment.getVat(),
                    payment.getDiscount(),
                    payment.getPayable(),
                    paid,
                    retail
            );

            invoiceModel.saveInvoice(invoice);

            for (Item i : items) {

                Product p = productModel.getProductByName(i.getItemName());
                double quantity = p.getQuantity() - i.getQuantity();
                p.setQuantity(quantity);
                productModel.decreaseProduct(p);

                sale = new Sale(
                        invoiceModel.getInvoice(invoiceId),
                        productModel.getProductByName(i.getItemName()),
                        i.getQuantity(),
                        i.getUnitPrice(),
                        i.getTotal(),
                        result.getString("rrn"),
                        result.getString("code") + " : " + result.getString("description"),
                        result.getString("ref"),
                        result.getString("imei"),
                        result.getString("pan"),
                        "ZIMSWITCH"
                );

                salesModel.saveSale(sale);
            }

            FXMLLoader loader = new FXMLLoader((getClass().getResource("/fxml/Confirm.fxml")));
            ConfirmController controller = new ConfirmController();
            controller.setData(retail, items, invoiceId, sale);
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            root.setOnMousePressed((MouseEvent e) -> {
                xOffset = e.getSceneX();
                yOffset = e.getSceneY();
            });
            root.setOnMouseDragged((MouseEvent e) -> {
                stage.setX(e.getScreenX() - xOffset);
                stage.setY(e.getScreenY() - yOffset);
            });
            stage.setTitle("Confirm");
            stage.getIcons().add(new Image("/images/logo.png"));
            stage.setScene(scene);
            stage.show();
        }else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Alert");
            alert.setHeaderText("Transaction Failed");
            alert.setContentText("[" + result.get("code") + "] " + result.get("description"));
            alert.showAndWait();
        }
    }

    private boolean validateInput() {

        String errorMessage = "";

        if (paidAmountField.getText() == null || paidAmountField.getText().length() == 0) {
            errorMessage += "Invalid Input!\n";
        } else if (Double.parseDouble(paidAmountField.getText()) < netPrice) {
            errorMessage += "Insufficient Input!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Please input the valid amount");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            paidAmountField.setText("");

            return false;
        }
    }

    @FXML
    public void closeAction(ActionEvent event) {
        ((Node) (event.getSource())).getScene().getWindow().hide();
    }
}
