package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.ProjectProperties;
import ee.ut.math.tvt.salessystem.ui.SalesSystemUI;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

import static ee.ut.math.tvt.salessystem.ui.SalesSystemUI.showErrorDialog;

public class TeamController implements Initializable {

    private static final Logger log = LogManager.getLogger(TeamController.class);

    @FXML
    private Text name;
    @FXML
    private Text leader;
    @FXML
    private Text email;
    @FXML
    private Text members;

    @FXML
    private ImageView logo;

    private final Properties properties;


    public TeamController() {
        properties = ProjectProperties.get();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (properties == null) {
            log.error("Team properties file not found");
            showErrorDialog(SalesSystemUI.ErrorType.STARTUP, "Could not load application properties file");
            return;
        }
        name.setText(properties.getProperty("name"));
        leader.setText(properties.getProperty("leader"));
        email.setText(properties.getProperty("email"));
        members.setText(properties.getProperty("members"));

        logo.setImage(new Image(properties.getProperty("logo")));

        log.info("TeamController initialized");
    }
}
