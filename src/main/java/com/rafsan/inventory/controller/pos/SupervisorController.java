package com.rafsan.inventory.controller.pos;

import com.rafsan.inventory.entity.*;
import com.rafsan.inventory.ipos.IPOSTransaction;
import com.rafsan.inventory.model.EmployeeModel;
import com.rafsan.inventory.model.InvoiceModel;
import com.rafsan.inventory.model.ProductModel;
import com.rafsan.inventory.model.SalesModel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;

import java.net.URL;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

@Data
public class SupervisorController implements Initializable {

    @FXML
    private TextField supervisorCodeTextField;
    @FXML
    private Label errorLabel;
    private EmployeeModel model;

    public boolean authorised = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        model = new EmployeeModel();
        enterPressed();
        supervisorCodeTextFieldRequestFocus();
    }

    private void enterPressed() {

        supervisorCodeTextField.setOnKeyPressed((KeyEvent ke) -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                try {
                    authenticate(ke);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
    }

    @FXML
    public void confirmAction(ActionEvent event) throws Exception {
        if (validateInput()) {

            String supervisorCode = supervisorCodeTextField.getText().trim();

            if (model.checkSupervisorUser(supervisorCode)) {

                authorised = true;
                ((Node) (event.getSource())).getScene().getWindow().hide();


            } else {
                authorised = false;
                resetFields();
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid Credentials");
                alert.setContentText("The Supervisor code input does not exist");
                alert.showAndWait();
                supervisorCodeTextFieldRequestFocus();
            }
        }
    }

    @FXML
    public void closeAction(ActionEvent event) {
        ((Node) (event.getSource())).getScene().getWindow().hide();
    }

    private void authenticate(Event event) throws Exception {
        if (validateInput()) {

            String supervisorCode = supervisorCodeTextField.getText().trim();

            if (model.checkSupervisorUser(supervisorCode)) {

                ((Node) (event.getSource())).getScene().getWindow().hide();

            } else {
                resetFields();
                errorLabel.setText("User doesn't exist!");
            }
        }
    }

    private void resetFields() {
        supervisorCodeTextField.setText("");
    }

    private boolean validateInput() {

        String errorMessage = "";

        if (supervisorCodeTextField.getText() == null || supervisorCodeTextField.getText().length() == 0) {
            errorMessage += "Please enter credentials!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            errorLabel.setText(errorMessage);
            return false;
        }
    }

    public void supervisorCodeTextFieldRequestFocus(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                supervisorCodeTextField.requestFocus();
            }
        });
    }
}
