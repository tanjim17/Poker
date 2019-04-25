package client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController{

    @FXML private TextField userName_log;
    @FXML private PasswordField password_log;

    public void logIn() {
        if (userName_log.getText() != null && password_log.getText() != null) {

           Main.client.out.println("login#"+userName_log.getText()+"#"+password_log.getText());
        }
    }

    public void createAccount(){

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("signupScreen.fxml"));
            Parent root = loader.load();

            SignupController sc = loader.getController();
            Main.client.setSignUpController(sc);

            Main.stage.close();
            Main.stage.setTitle("Online Poker - Sign Up");
            Main.stage.setScene(new Scene(root, 950, 520));
            Main.stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}


