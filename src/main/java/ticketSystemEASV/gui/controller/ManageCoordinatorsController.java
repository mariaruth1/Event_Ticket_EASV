package ticketSystemEASV.gui.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import ticketSystemEASV.be.views.CoordinatorView;
import ticketSystemEASV.bll.AlertManager;
import ticketSystemEASV.gui.controller.addController.AddCoordinatorController;
import ticketSystemEASV.gui.model.Model;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ManageCoordinatorsController extends MotherController implements Initializable {
    @FXML
    private ScrollPane coordinatorScrollPane;
    @FXML
    private FlowPane flowPane;
    private final ObservableList<CoordinatorView> coordinatorViews = FXCollections.observableArrayList();
    private final AlertManager alertManager = AlertManager.getInstance();
    private Model model;
    private CoordinatorView lastFocusedCoordinator;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        flowPane.prefHeightProperty().bind(coordinatorScrollPane.heightProperty());
        flowPane.prefWidthProperty().bind(coordinatorScrollPane.widthProperty());
        Bindings.bindContent(flowPane.getChildren(), coordinatorViews);
    }

    public void addCoordinatorAction(ActionEvent actionEvent) throws IOException {
        ((AddCoordinatorController) openNewWindow("/views/add...views/AddCoordinatorView.fxml", Modality.WINDOW_MODAL).getController()).setModel(model);
    }

    public void editCoordinatorAction(ActionEvent actionEvent) throws IOException {
        if (lastFocusedCoordinator == null)
            alertManager.getAlert(Alert.AlertType.ERROR, "No coordinator selected!", actionEvent).showAndWait();
        else {
            FXMLLoader fxmlLoader = openNewWindow("/views/add...views/AddCoordinatorView.fxml", Modality.WINDOW_MODAL);
            AddCoordinatorController addCoordinatorController = fxmlLoader.getController();
            addCoordinatorController.setModel(model);
            addCoordinatorController.setIsEditing(lastFocusedCoordinator.getCoordinator());
        }
    }

    public void deleteCoordinatorAction(ActionEvent actionEvent) {
         if (lastFocusedCoordinator == null)
             alertManager.getAlert(Alert.AlertType.ERROR, "No coordinator selected!", actionEvent).showAndWait();
        else {
             Alert alert = alertManager.getAlert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this coordinator?", actionEvent);
             Optional<ButtonType> result = alert.showAndWait();
             if (result.isPresent() && result.get() == ButtonType.OK){
                 model.deleteCoordinator(lastFocusedCoordinator.getCoordinator());
                 refreshItems();
             }
        }
    }

    public void setModel(Model model) {
        this.model = model;
        refreshItems();
    }

    @Override
    public void refreshItems() {
        //TODO optimize
        coordinatorViews.clear();
        coordinatorViews.addAll(model.getAllEventCoordinators().stream().map(CoordinatorView::new).toList());

        for (var coordinatorView : coordinatorViews) {
            coordinatorView.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) lastFocusedCoordinator = coordinatorView;
            });
        }
    }
}