package client;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SignupController {

    @FXML private TextField userName_sign;
    @FXML private PasswordField password_sign;

    public void signUp(){
        if (userName_sign.getText() != null && password_sign.getText() != null) {

            Main.client.out.println("signup#"+userName_sign.getText()+"#"+password_sign.getText());
        }
    }

}
