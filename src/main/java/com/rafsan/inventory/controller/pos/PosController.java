package com.rafsan.inventory.controller.pos;

import com.rafsan.inventory.entity.Item;
import com.rafsan.inventory.entity.Payment;
import com.rafsan.inventory.entity.Product;
import com.rafsan.inventory.model.ProductModel;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Formatter;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Node;
import com.rafsan.inventory.interfaces.ProductInterface;
import static com.rafsan.inventory.interfaces.ProductInterface.PRODUCTLIST;
import javafx.scene.input.MouseEvent;
import org.apache.commons.math3.util.Precision;
import javafx.stage.StageStyle;

public class PosController implements Initializable, ProductInterface {

    @FXML
    private TableView<Product> productTableView;
    @FXML
    private TableView<Item> listTableView;
    @FXML
    private TableColumn<Product, String> productColumn;
    @FXML
    private TableColumn<Item, String> itemColumn;
    @FXML
    private TableColumn<Item, Double> priceColumn, quantityColumn, totalColumn;
    @FXML
    private TextField searchField, productField, priceField, quantityField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private TextField subTotalField, discountField, vatField, netPayableField;
    @FXML
    private Button addButton, removeButton, paymentButton;
    @FXML
    private Label quantityLabel;
    @FXML
    private ObservableList<Item> ITEMLIST;
    private ProductModel productModel;
    DecimalFormat df =new DecimalFormat("0.00");
    boolean barcodeRead = false;

    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ITEMLIST = FXCollections.observableArrayList();
        productModel = new ProductModel();

        loadData();

        productColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showDetails(newValue));
        productTableView.setItems(PRODUCTLIST);

        filterData();

        itemColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        listTableView.setItems(ITEMLIST);

        addButton
                .disableProperty()
                .bind(Bindings.isEmpty(productTableView.getSelectionModel().getSelectedItems()));
        removeButton
                .disableProperty()
                .bind(Bindings.isEmpty(listTableView.getSelectionModel().getSelectedItems()));

        searchRequestFocus();
    }

    private void filterData() {
        barcodeRead = false;
        FilteredList<Product> searchedData = new FilteredList<>(PRODUCTLIST, e -> true);

        searchField.setOnKeyReleased(e -> {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                searchedData.setPredicate(product -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    if (product.getProductName().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } else if (product.getDescription().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } else if (product.getSupplier().getName().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } else if (product.getBarcode().toLowerCase().contains(lowerCaseFilter)) {
                        barcodeRead = true;
                        productTableView.getSelectionModel().select(product);
                        showDetails(product);
                        return true;
                    }
                    return false;
                });
            });

            SortedList<Product> sortedData = new SortedList<>(searchedData);
            sortedData.comparatorProperty().bind(productTableView.comparatorProperty());
            productTableView.setItems(sortedData);
        });
    }

    private void loadData() {
        if (!PRODUCTLIST.isEmpty()) {
            PRODUCTLIST.clear();
        }

        PRODUCTLIST.addAll(productModel.getProducts());
    }

    private void showDetails(Product product) {
        if (product != null) {
            quantityField.setDisable(false);
            productField.setText(product.getProductName());
            priceField.setText(String.valueOf(df.format(product.getPrice())));

            double quantity = product.getQuantity();

            if (quantity > 0) {
                quantityField.setEditable(true);
                quantityField.setStyle(null);
                quantityField.setText("1");
            } else {
                quantityField.setEditable(false);
                quantityField.setStyle("-fx-background-color: red;");
            }
            quantityLabel.setText("Stock: " + String.valueOf((int)quantity));
            descriptionArea.setText(product.getDescription());
        } else {
            productField.setText("");
            priceField.setText("");
            quantityLabel.setText("");
            descriptionArea.setText("");
        }
    }

    private void resetProductTableSelection() {
        productTableView.getSelectionModel().clearSelection();
    }

    private void resetItemTable() {
        ITEMLIST.clear();
    }

    private void resetAdd() {
        productField.setText("");
        priceField.setText("");
        quantityField.setText("");
        resetQuantityField();
        quantityLabel.setText("Available: ");
        descriptionArea.setText("");
        searchField.setText(null);
        searchRequestFocus();
    }

    private void resetInvoice() {
        subTotalField.setText("0.00");
        vatField.setText("0.00");
        netPayableField.setText("0.00");
    }

    public void searchRequestFocus(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                searchField.requestFocus();
            }
        });
    }

    private void resetQuantityField() {
        quantityField.setDisable(true);
    }

    private void resetPaymentButton() {
        paymentButton.setDisable(true);
    }

    private void resetInterface() {
        loadData();
        resetPaymentButton();
        resetProductTableSelection();
        resetItemTable();
        resetQuantityField();
        resetAdd();
        resetInvoice();
    }

    @FXML
    public void resetAction(ActionEvent event) {
        resetInterface();
    }

    @FXML
    public void addAction(ActionEvent event) throws IOException {
        double quantity = Double.parseDouble(quantityField.getText());

        if (quantity < 0){
            // validate input for admin
            FXMLLoader loader = new FXMLLoader((getClass().getResource("/fxml/Supervisor.fxml")));
            SupervisorController controller = new SupervisorController();

            loader.setController(controller);
            Parent root = loader.load();
            Stage stage = new Stage();
            root.setOnMousePressed((MouseEvent e) -> {
                xOffset = e.getSceneX();
                yOffset = e.getSceneY();
            });
            root.setOnMouseDragged((MouseEvent e) -> {
                stage.setX(e.getScreenX() - xOffset);
                stage.setY(e.getScreenY() - yOffset);
            });
            Scene scene = new Scene(root);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Payment");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.getIcons().add(new Image("/images/logo.png"));
            stage.setScene(scene);
            stage.showAndWait();

            if (!controller.authorised){
                resetInterface();
            }
        }

        if (validateInput()) {
            String productName = productField.getText();
            double unitPrice = Double.parseDouble(priceField.getText());
            double total = unitPrice * quantity;
            ITEMLIST.add(new Item(productName, Precision.round(unitPrice,2), quantity, Precision.round(total,2)));
            calculation();

            resetAdd();
            productTableView.getSelectionModel().clearSelection();
        }
    }

    private void calculation() {

        double subTotalPrice = 0.0;
        subTotalPrice = listTableView.getItems().stream().map(
                (item) -> item.getTotal()).reduce(subTotalPrice, (accumulator, _item) -> accumulator + _item);

        //if (subTotalPrice > 0)
        {
            paymentButton.setDisable(false);
            //double vat = (double) subTotalPrice * 0.145;
            double vat = (double) subTotalPrice * 0;
            double netPayablePrice = (double) (Math.abs((subTotalPrice)));

            subTotalField.setText(String.valueOf(df.format(subTotalPrice - vat)));
            vatField.setText(String.valueOf(df.format(vat)));
            netPayableField.setText(String.valueOf(df.format(netPayablePrice)));
        }
    }

    @FXML
    public void paymentAction(ActionEvent event) throws Exception {

        Payment payment = new Payment(
                Double.parseDouble(subTotalField.getText().trim()),
                Double.parseDouble(vatField.getText().trim()),
                //Double.parseDouble(discountField.getText().trim()),
                0,
                Double.parseDouble(netPayableField.getText().trim())
        );

        ObservableList<Item> sold = listTableView.getItems();

        FXMLLoader loader = new FXMLLoader((getClass().getResource("/fxml/Invoice.fxml")));
        InvoiceController controller = new InvoiceController();
        loader.setController(controller);
        controller.setData(Double.parseDouble(netPayableField.getText().trim()), sold, payment);
        Parent root = loader.load();
        Stage stage = new Stage();
        root.setOnMousePressed((MouseEvent e) -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });
        root.setOnMouseDragged((MouseEvent e) -> {
            stage.setX(e.getScreenX() - xOffset);
            stage.setY(e.getScreenY() - yOffset);
        });
        Scene scene = new Scene(root);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Payment");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.getIcons().add(new Image("/images/logo.png"));
        stage.setScene(scene);
        stage.showAndWait();

        resetInterface();
    }

    @FXML
    public void removeAction(ActionEvent event) {

        int index = listTableView.getSelectionModel().getSelectedIndex();

        if (index > 0) {
            listTableView.getItems().remove(index);
            calculation();
        } else if (index == 0) {
            listTableView.getItems().remove(index);
            resetInvoice();
        }
    }

    private boolean validateInput() {

        String errorMessage = "";

        if (quantityField.getText() == null || quantityField.getText().length() == 0) {
            errorMessage += "Quantity not supplied!\n";
        } else {
            double quantity = Double.parseDouble(quantityField.getText());
            String available = quantityLabel.getText();
            double availableQuantity = Double.parseDouble(available.substring(7));

            if (quantity > availableQuantity) {
                errorMessage += "Out of Stock!\n";
            }
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Please input the valid number of products");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            quantityField.setText("");

            return false;
        }
    }

    @FXML
    public void logoutAction(ActionEvent event) throws Exception {
        ((Node) (event.getSource())).getScene().getWindow().hide();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
        Stage stage = new Stage();
        root.setOnMousePressed((MouseEvent e) -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });
        root.setOnMouseDragged((MouseEvent e) -> {
            stage.setX(e.getScreenX() - xOffset);
            stage.setY(e.getScreenY() - yOffset);
        });

        Scene scene = new Scene(root);
        stage.setTitle("Inventory:: Version 1.0");
        stage.getIcons().add(new Image("/images/logo.png"));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();
    }
}
