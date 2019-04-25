package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static Stage stage;
    public static Client client;

    @Override
    public void start(Stage primaryStage) throws Exception{

        client = new Client();

        this.stage = primaryStage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("loginScreen.fxml"));
        Parent root = loader.load();

        LoginController lc = loader.getController();
        client.setLogInController(lc);

        stage.setTitle("Online Poker - Log In");
        stage.setScene(new Scene(root, 1000, 650));
        stage.show();

    }
    public static void main(String[] args) {
        launch(args);
    }

}
