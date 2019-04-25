package client;

import javafx.application.Platform;

public class WaitController {

    public void logoutFromWait(){

        Main.client.out.println("logoutwait");
        Platform.exit();
        System.exit(0);
    }
}
