package client;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Client{

    public Socket socket;
    private BufferedReader in;
    public PrintWriter out;
    public String username;
    public int chips;
    public int selfBet;
    public int currentbet;

    private boolean isLoggedIn;
    public boolean isTurn;
    public boolean canLogout;

    private LoginController lc;
    private SignupController sc;
    private TableController tc;
    private WaitController wc;
    public List<Player> players;

    Client(){
        isLoggedIn = false;
        isTurn = false;
        canLogout = true;

        try {
            socket = new Socket("localhost",7777);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(),true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(()->receive(),"client receive thread").start();
    }


    private void receive() {

        while(true){
            try {
                String data = in.readLine();
                System.out.println(data);
                Platform.runLater(
                        ()-> parseData(data)
                );


            } catch (IOException e) {
                continue;
            }
        }
    }

    private void parseData(String data){

        String [] message = data.split("#");

        switch (message[0]){

            case "login done":
                username = message[1];
                chips = Integer.parseInt(message[2]);

                players = new ArrayList<>();
                players.add(new Player(username,chips));

                openTable();
                tc.message.setText("Waiting for players...");
                break;

            case "wait":
                openWait();
                break;

            case "opponentAdded":
                players.add(new Player(message[1],Integer.parseInt(message[2])));

                if(players.size()==2) {

                    tc.p2.setText(message[1]);
                    tc.c2.setText(String.valueOf(message[2]));
                    tc.circle2.setVisible(true);
                    tc.circle2.setFill(new ImagePattern(new Image("/resources/blackChip2.jpg")));
                    tc.avatar2.setFill(new ImagePattern(new Image("/resources/face3.jpg")));
                }
                else if(players.size()==3){
                    tc.p3.setText(message[1]);
                    tc.c3.setText(String.valueOf(message[2]));
                    tc.circle3.setVisible(true);
                    tc.circle3.setFill(new ImagePattern(new Image("/resources/blackChip2.jpg")));
                    tc.avatar3.setFill(new ImagePattern(new Image("/resources/face5.png")));
                }
                else if(players.size()==4){
                    tc.p4.setText(message[1]);
                    tc.c4.setText(String.valueOf(message[2]));
                    tc.circle4.setVisible(true);
                    tc.circle4.setFill(new ImagePattern(new Image("/resources/blackChip2.jpg")));
                    tc.avatar4.setFill(new ImagePattern(new Image("/resources/face4.jpg")));
                }
                else if(players.size()==5){
                    tc.p5.setText(message[1]);
                    tc.c5.setText(String.valueOf(message[2]));
                    tc.circle5.setVisible(true);
                    tc.circle5.setFill(new ImagePattern(new Image("/resources/blackChip2.jpg")));
                    tc.avatar5.setFill(new ImagePattern(new Image("/resources/face6.png")));
                }
                break;

            case "card":
                tc.message.setText(" ");
                tc.card1.setImage(new Image("/resources/" + message[1] + ".png"));
                tc.card2.setImage(new Image("/resources/" + message[2] + ".png"));
                break;

            case "chips":
                for(int i=0; i<players.size(); i++){

                    if(message[1].equals(players.get(i).username)){

                        if(i==0){
                            tc.c1.setText(message[2]);
                            chips = Integer.parseInt(message[2]);
                        }

                        if(i==1) tc.c2.setText(message[2]);
                        if(i==2) tc.c3.setText(message[2]);
                        if(i==3) tc.c4.setText(message[2]);
                        if(i==4) tc.c5.setText(message[2]);
                    }
                }
                break;

            case "currentbet":
                currentbet = Integer.parseInt(message[1]);
                tc.bet.setText(message[1]);
                break;

            case "selfbet":
                selfBet = Integer.parseInt(message[1]);
                break;

            case "pot":
                tc.pot.setText(message[1]);
                break;

            case "flop":
                tc.cc1.setImage(new Image("/resources/" + message[1] + ".png"));
                tc.cc2.setImage(new Image("/resources/" + message[2] + ".png"));
                tc.cc3.setImage(new Image("/resources/" + message[3] + ".png"));
                break;

            case "turn":
                tc.cc4.setImage(new Image("/resources/" + message[1] + ".png"));
                break;

            case "river":
                tc.cc5.setImage(new Image("/resources/" + message[1] + ".png"));
                break;

            case "whichPturn":
                for(Player player: players){
                    if(message[1].equals(player.username)){
                        if(message[1].equals(username)){
                            isTurn = true;
                        }
                        tc.whichPturn.setText(message[1]+"'s turn!!");
                    }
                }
                break;

            case "winner":
                tc.action.setText(message[1]);
                break;

            case "round":
                tc.action.setText(message[1]);
                break;

            case "move":
                if(tc.p2.getText().equals(message[1])) {
                    tc.action2.setText(message[2]);
                    System.out.println(message[2] + " " + tc.p2.getText());
                }
                else if(tc.p3.getText().equals(message[1]))
                {
                    tc.action3.setText(message[2]);
                    System.out.println(message[2] + " " + tc.p3.getText());
                }
                else if(tc.p4.getText().equals(message[1]))
                {
                    tc.action4.setText(message[2]);
                    System.out.println(message[2] + " " + tc.p4.getText());
                }
                else if(tc.p5.getText().equals(message[1]))
                {
                    tc.action5.setText(message[2]);
                    System.out.println(message[2] + " " + tc.p5.getText());
                }

                if(message[2].equals("Fold")){
                    for(int i=0; i<players.size(); i++){
                        if(players.get(i).username.equals(message[1])){
                            players.remove(i);
                        }
                    }
                }
                break;

            case "cardReset":
                tc.card1.setImage(new Image("/resources/download.jpg"));
                tc.card2.setImage(new Image("/resources/download.jpg"));

                tc.cc1.setImage(new Image("/resources/download.jpg"));
                tc.cc2.setImage(new Image("/resources/download.jpg"));
                tc.cc3.setImage(new Image("/resources/download.jpg"));
                tc.cc4.setImage(new Image("/resources/download.jpg"));
                tc.cc5.setImage(new Image("/resources/download.jpg"));

                tc.card21.setImage(new Image("/resources/download.jpg"));
                tc.card22.setImage(new Image("/resources/download.jpg"));
                tc.card31.setImage(new Image("/resources/download.jpg"));
                tc.card32.setImage(new Image("/resources/download.jpg"));
                tc.card41.setImage(new Image("/resources/download.jpg"));
                tc.card42.setImage(new Image("/resources/download.jpg"));
                tc.card51.setImage(new Image("/resources/download.jpg"));
                tc.card52.setImage(new Image("/resources/download.jpg"));
                tc.action.setText(" ");

                players = new ArrayList<>();
                players.add(new Player(username,chips));
                break;

            case "cardshow":
                for(int i = 0;i<players.size();i++){

                    if(message[1].equals(username))continue;

                    if(tc.p2.getText().equals(message[1])){
                        tc.card21.setImage(new Image("/resources/" + message[2] + ".png"));
                        tc.card22.setImage(new Image("/resources/" + message[3] + ".png"));
                    }
                    else  if(tc.p3.getText().equals(message[1])){
                        tc.card31.setImage(new Image("/resources/" + message[2] + ".png"));
                        tc.card32.setImage(new Image("/resources/" + message[3] + ".png"));
                    }
                    else if(tc.p4.getText().equals(message[1])){
                        tc.card41.setImage(new Image("/resources/" + message[2] + ".png"));
                        tc.card42.setImage(new Image("/resources/" + message[3] + ".png"));
                    }
                    else if(tc.p5.getText().equals(message[1])){
                        tc.card51.setImage(new Image("/resources/" + message[2] + ".png"));
                        tc.card52.setImage(new Image("/resources/" + message[3] + ".png"));
                    }

                }
                break;

            case "removeaction":
                tc.action2.setText(" ");
                tc.action3.setText(" ");
                tc.action4.setText(" ");
                tc.action5.setText(" ");
                break;

            case "logout":
                for(int i=0; i<players.size(); i++){
                    if(players.get(i).username.equals(message[1])){
                        players.remove(i);
                        break;
                    }
                }

                if(tc.p2.getText().equals(message[1])) {
                    tc.p2.setText("Empty");
                    tc.c2.setText("-");
                    tc.avatar2.setFill(null);
                    tc.action2.setText(" ");
                }
                else if(tc.p3.getText().equals(message[1]))
                {
                    tc.p3.setText("Empty");
                    tc.c3.setText("-");
                    tc.avatar3.setFill(null);
                    tc.action3.setText(" ");
                }
                else if(tc.p4.getText().equals(message[1]))
                {
                    tc.p4.setText("Empty");
                    tc.c4.setText("-");
                    tc.avatar4.setFill(null);
                    tc.action4.setText(" ");
                }
                else if(tc.p5.getText().equals(message[1]))
                {
                    tc.p5.setText("Empty");
                    tc.c5.setText("-");
                    tc.avatar5.setFill(null);
                    tc.action5.setText(" ");
                }
                break;

            case "sleep":
                canLogout=false;
                break;

            case "alive":
                canLogout=true;
                break;
        }
    }

    private void openWait(){

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("wait.fxml"));
            Parent root = loader.load();

            WaitController wc = loader.getController();
            setWaitController(wc);

            Main.stage.close();
            Main.stage.setTitle("WAITING ROOM");
            Main.stage.setScene(new Scene(root));
            Main.stage.show();
        }
        catch (IOException e){
            e.printStackTrace();
            System.err.println("error in loading wait");
        }
    }

    private void openTable(){

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Table.fxml"));
            Parent root = loader.load();

            TableController tc = loader.getController();
            setTableController(tc);

            Main.stage.close();
            Main.stage.setTitle("TABLE");
            Main.stage.setScene(new Scene(root));
            Main.stage.show();

            tc.p1.setText(username);
            tc.c1.setText(String.valueOf(chips));

            tc.circle.setFill(new ImagePattern(new Image("/resources/blackChip2.jpg")));
            tc.pot1.setFill(new ImagePattern(new Image("/resources/redChip.png")));
            tc.pot2.setFill(new ImagePattern(new Image("/resources/redChip.png")));
            tc.pot3.setFill(new ImagePattern(new Image("/resources/redChip.png")));
            tc.pot4.setFill(new ImagePattern(new Image("/resources/redChip.png")));
            tc.pot5.setFill(new ImagePattern(new Image("/resources/redChip.png")));
            tc.pot6.setFill(new ImagePattern(new Image("/resources/redChip.png")));
            tc.avatar1.setFill(new ImagePattern(new Image("/resources/face2.jpg")));
        }
        catch (IOException e){
            e.printStackTrace();
            System.err.println("error in loading table");
        }
    }

    public void setLogInController(LoginController lc) {
        this.lc = lc;
    }

    public void setSignUpController(SignupController sc) {
        this.sc = sc;
    }

    public void setTableController(TableController tc){
        this.tc = tc;
    }

    public void setWaitController(WaitController wc){
        this.wc = wc;
    }
}





