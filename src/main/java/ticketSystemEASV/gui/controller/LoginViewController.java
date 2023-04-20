package ticketSystemEASV.gui.controller;

import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import ticketSystemEASV.Main;
import ticketSystemEASV.bll.util.AlertManager;
import ticketSystemEASV.gui.model.UserModel;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static javafx.scene.input.KeyCode.ENTER;

public class LoginViewController implements Initializable {
    private final UserModel userModel = new UserModel();
    @FXML private MFXTextField emailInput;
    @FXML private MFXPasswordField passwordInput;
    private Parent root;
    private FXMLLoader fxmlLoader;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(this::setEnterKeyAction);
        //TODO delete
        emailInput.setText("admin");
        passwordInput.setText("admin");

        try {
            fxmlLoader = new FXMLLoader(Main.class.getResource("/views/Root.fxml"));
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loginUser(Event event) throws IOException {
        long timeMilis = System.currentTimeMillis();
        if(userModel.logIn(emailInput.getText(), passwordInput.getText())) {
            userModel.setLoggedInUser(userModel.getUserByEmail(emailInput.getText()));
            ((RootController) fxmlLoader.getController()).setUserModel(userModel);
            System.out.println("Login time: " + (System.currentTimeMillis() - timeMilis) + "ms");

            Stage stage = (Stage) emailInput.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        }
        else
            AlertManager.getInstance().getAlert(Alert.AlertType.ERROR, "Invalid username or password.", event);
    }

    private void setEnterKeyAction() {
        Scene scene = emailInput.getScene();
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == ENTER) {
                try {
                    loginUser(new Event(passwordInput, passwordInput, Event.ANY));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
