package ticketSystemEASV.gui.controller;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import ticketSystemEASV.gui.model.EventModel;
import ticketSystemEASV.gui.model.TicketModel;
import ticketSystemEASV.gui.model.UserModel;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RootController implements Initializable {
    @FXML
    private GridPane gridPane;
    @FXML
    private MFXTextField searchBar;
    private final TicketModel ticketModel = new TicketModel();
    private UserModel userModel;
    private final EventModel eventModel = new EventModel();
    private Node eventsScene, coordinatorsScene;
    private MainViewController mainViewController;
    private ManageCoordinatorsController manageCoordinatorsController;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            FXMLLoader eventsLoader = new FXMLLoader(getClass().getResource("/views/MainView.fxml"));
            eventsScene = eventsLoader.load();
            mainViewController = eventsLoader.getController();

            FXMLLoader coordinatorsLoader = new FXMLLoader(getClass().getResource("/views/ManageCoordinatorsView.fxml"));
            coordinatorsScene = coordinatorsLoader.load();
            manageCoordinatorsController = coordinatorsLoader.getController();

            gridPane.add(eventsScene, 1, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void manageCoordinatorsAction(ActionEvent actionEvent) throws IOException {
        switchView(coordinatorsScene);
    }

    public void myEventsAction(ActionEvent actionEvent) {
        switchView(eventsScene);
    }

    private void switchView(Node scene) {
        if (scene == eventsScene && !gridPane.getChildren().contains(eventsScene)){
            gridPane.getChildren().remove(coordinatorsScene);
            gridPane.add(eventsScene, 1, 0);
        }
        else if (scene == coordinatorsScene && !gridPane.getChildren().contains(coordinatorsScene)){
            gridPane.getChildren().remove(eventsScene);
            gridPane.add(coordinatorsScene, 1, 0);
        }
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
        mainViewController.setModels(ticketModel, eventModel);
        manageCoordinatorsController.setModels(ticketModel, eventModel, userModel);
    }
}
