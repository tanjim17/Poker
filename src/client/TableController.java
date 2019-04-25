package client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class TableController{

    @FXML public TextField p1;
    @FXML public TextField p2;
    @FXML public TextField p3;
    @FXML public TextField p4;
    @FXML public TextField p5;
    @FXML public TextField c1;
    @FXML public TextField c2;
    @FXML public TextField c3;
    @FXML public TextField c4;
    @FXML public TextField c5;
    @FXML private TextField raisebox;
    @FXML public TextField whichPturn;

    @FXML public  ImageView card1;
    @FXML public  ImageView card2;

    public Circle circle;
    public Circle circle2;
    public Circle circle3;
    public Circle circle4;
    public Circle circle5;
    public Circle pot1;
    public Circle pot2;
    public Circle pot3;
    public Circle pot4;
    public Circle pot5;
    public Circle pot6;
    public Circle avatar1;
    public Circle avatar2;
    public Circle avatar3;
    public Circle avatar4;
    public Circle avatar5;

    @FXML public ImageView cc1;
    @FXML public ImageView cc2;
    @FXML public ImageView cc3;
    @FXML public ImageView cc4;
    @FXML public ImageView cc5;
    @FXML public ImageView card21;
    @FXML public ImageView card22;
    @FXML public ImageView card31;
    @FXML public ImageView card32;
    @FXML public ImageView card41;
    @FXML public ImageView card42;
    @FXML public ImageView card51;
    @FXML public ImageView card52;

    @FXML public Text bet;
    @FXML public Text action;
    @FXML public Text pot;
    @FXML public Text message;
    @FXML public Text action2;
    @FXML public Text action3;
    @FXML public Text action4;
    @FXML public Text action5;

    public void call(){

          message.setText(" ");

          if(Main.client.isTurn){
              if(Main.client.currentbet - Main.client.selfBet<= Main.client.chips){
                  if(Main.client.currentbet != Main.client.selfBet){

                      Main.client.out.println("call");
                      Main.client.selfBet = Main.client.currentbet;
                      Main.client.isTurn = false;
                  }
                  else message.setText("You can't call now. Check, raise or fold.");
              }
              else {
                  message.setText("You need to fold now!"); // today

                  Main.client.out.println("fold");
                  Main.client.isTurn = false;
                  card1.setImage(new Image("resources/download.jpg"));
                  card2.setImage(new Image("resources/download.jpg"));
              }
          }
          else message.setText("Not Your Turn!");
      }

    public void check(){

        message.setText(" ");

          if(Main.client.isTurn){
              if(Main.client.currentbet == Main.client.selfBet){
                  Main.client.out.println("check");
                  Main.client.isTurn = false;
              }
              else message.setText("You need to call the current bet!");
          }
          else message.setText("Not Your Turn!");
    }

    public void raise() {

        message.setText(" ");

        try{
            if (Main.client.isTurn) {

                if (Integer.parseInt(raisebox.getText()) - Main.client.selfBet <= Main.client.chips) {

                    if(Integer.parseInt(raisebox.getText()) > Main.client.currentbet){

                        Main.client.out.println("raise#" + raisebox.getText());
                        Main.client.selfBet = Integer.parseInt(raisebox.getText());
                        raisebox.clear();
                        Main.client.isTurn = false;
                    }
                    else message.setText("You can't raise lower than current bet");
                }
                else message.setText("Not Enough Chips!");
            }
            else message.setText("Not Your Turn!");

        }catch (NumberFormatException e){
            message.setText("Please input Numbers");
        }
    }

    public void fold(){

        message.setText(" ");

        if(Main.client.isTurn){
            Main.client.out.println("fold");
            Main.client.isTurn = false;

            card1.setImage(new Image("resources/download.jpg"));
            card2.setImage(new Image("resources/download.jpg"));
        }
    }

    public void logout(){

        if(Main.client.canLogout){
            Main.client.out.println("logout");
            Platform.exit();
            System.exit(0);
        }

    }

}