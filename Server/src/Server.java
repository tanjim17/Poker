import java.io.*;
import java.net.*;
import java.util.*;

public class Server implements Runnable{

    private BufferedReader in;
    public PrintWriter out;

    private boolean isLoggedIn;
    public boolean isTurn;
    public boolean isFolded;

    public Hand hand;
    public String username;
    public int chips;
    public int selfBet;
    public int tableID;

    private static List<Server> loggedInUsers;
    public static List<Server> waitingUsers;
    private static Table table1;
    private static Table table2;
    public Table table;


    Server(Socket socket){

        isLoggedIn = false;
        isFolded = true;
        username = null;
        chips = 0;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(),true);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String [] args){

        loggedInUsers = new ArrayList<>();
        waitingUsers = new ArrayList<>();
        table1 = new Table();
        table2 = new Table();

        try {
            ServerSocket ss = new ServerSocket(7777);
            while (true){
                Socket s = ss.accept();
                System.out.println("connected.");
                new Thread(new Server(s)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        while(true){
            try {
                String data = in.readLine();
                System.out.println(data);
                parseData(data);
            } catch (IOException e) {
                continue;
            }
        }
    }

    private void parseData(String data) {

        String [] message = data.split("#");

        switch (message[0]){

            case "login":
                if(isLoggedIn)
                    break;

                if(validateLogIn(message[1],message[2])){
                    initiateUserEntry(message[1]);
                }
                else out.println("decline"); // invalid message
                break;

            case "signup":
                if(isLoggedIn)
                    break;

                if(validateSignUp(message[1],message[2])){
                    initiateUserEntry(message[1]);
                }
                else out.println("decline"); // invalid message
                break;

            case "call":
                table.sendDataToAll("move#" + username + "#Call");
                decreaseChips(table.currentBet - selfBet);
                table.pot += table.currentBet - selfBet;
                selfBet = table.currentBet;

                table.sendDataToAll("pot#" + String.valueOf(table.pot));
                table.changeTurn();
                isTurn = false;
                break;

            case "check":
                table.sendDataToAll("move#" + username + "#Check");
                table.checkNumber++;
                table.changeTurn();
                if(table.checkNumber == table.inGamePlayers.size())
                    table.changeRound();
                isTurn = false;
                break;

            case "raise":
                table.sendDataToAll("move#" + username + "#Raise " + message[1]);
                table.checkNumber = 0;

                table.currentBet = Integer.parseInt(message[1]);
                decreaseChips(table.currentBet - selfBet);
                table.pot += table.currentBet - selfBet;
                selfBet = Integer.parseInt(message[1]);

                table.sendDataToAll("pot#" + String.valueOf(table.pot));
                table.sendDataToAll("currentbet#" + String.valueOf(table.currentBet));
                isTurn = false;
                table.changeTurn();
                break;

            case "fold":
                table.sendDataToAll("move#" + username + "#Fold");
                isFolded = true;
                isTurn = false;

                table.changeTurn();
                table.inGamePlayers.remove(this);
                table.whichPlayerTurn--;

                if(table.checkNumber == table.inGamePlayers.size())
                    table.changeRound();

                if(table.inGamePlayers.size() == 1){
                    table.Reset();
                }
                break;

            case "logout":
                updateChips();
                table.sendDataToAll("logout#" + username);

                if(isTurn){
                    isTurn = false;
                    table.changeTurn();
                    table.inGamePlayers.remove(this);
                    table.players.remove(this);
                    loggedInUsers.remove(this);
                    table.whichPlayerTurn--;

                    if(table.checkNumber == table.inGamePlayers.size())
                        table.changeRound();

                    if(table.inGamePlayers.size() == 1){
                        table.Reset();
                    }
                }

                else {
                    if(!isFolded){
                        table.inGamePlayers.remove(this);
                        table.players.remove(this);
                        loggedInUsers.remove(this);

                        table.whichPlayerTurn--;
                        if(table.inGamePlayers.size() == 1){
                            table.Reset();
                        }
                    }
                    else {
                        table.players.remove(this);
                        loggedInUsers.remove(this);
                    }
                }
                break;

            case "logoutwait":
                loggedInUsers.remove(this);
                waitingUsers.remove(this);
                break;
        }
    }

    private void updateChips(){

        File originalFile = new File("data.txt"); // original file
        FileReader fin = null;
        try {
            fin = new FileReader(originalFile);
        } catch (FileNotFoundException e) {
            System.err.println("couldn't find file");
        }
        Scanner in = new Scanner(fin);

        File tempFile = new File("tempdata.txt"); // temporary file
        PrintWriter pw = null;

        try {
            pw = new PrintWriter(new FileWriter(tempFile),true);
        } catch (IOException e) {
            System.err.println("IO error in temp file");
        }

        String line;
        String [] entry;

        while(true){
            entry = in.nextLine().split("#");

            if(entry[0].equals(username)){
                entry[2]=String.valueOf(chips);
            }

            line = entry[0]+ "#"+ entry[1]+ "#"+ entry[2];
            System.out.println(line);

            pw.println(line);

            if(!in.hasNextLine()) break;
        }

        in.close();
        pw.close();

        if (!originalFile.delete()) {
            System.out.println("Could not delete file");
            return;
        }

        if (!tempFile.renameTo(originalFile))
            System.out.println("Could not rename file");
    }

    private boolean validateLogIn(String username, String password) {

        FileReader fin = null;
        try {
            fin = new FileReader("data.txt");
        } catch (FileNotFoundException e) {
            System.err.println("couldn't read file");
        }

        Scanner in = new Scanner(fin);
        String [] entry;

        while(in.hasNextLine()){
            entry = in.nextLine().split("#");

            if(entry[0].equals(username) && entry[1].equals(password)){
                in.close();
                this.chips = Integer.parseInt(entry[2]);
                return true;
            }
        }

        in.close();
        return false;
    }

    private boolean validateSignUp(String username, String password) {

        // checking whether same username already exists
        FileReader fin = null;
        try {
            fin = new FileReader("data.txt");
        } catch (FileNotFoundException e) {
            System.err.println("couldn't read file");
        }

        Scanner in = new Scanner(fin);
        String [] entry;

        while(in.hasNextLine()){
            entry = in.nextLine().split("#");

            if(entry[0].equals(username)){
                in.close();
                return false;
            }
        }
        in.close();

        // adding new member info to file
        try {
            FileWriter fout = new FileWriter("data.txt",true);
            fout.write("\n"+username+"#"+password+"#1000");
            this.chips = 10000;
            fout.close();
        } catch (IOException e) {
            System.err.println("couldn't open file to read");
        }

        return true;
    }

    private void initiateUserEntry(String username){
        isLoggedIn = true;
        this.username = username;
        loggedInUsers.add(this);
        out.println("login done#"+username+"#"+String.valueOf(chips));

        if(!table1.isStarted){
            table = table1;
            table.players.add(this);

            if(table.players.size()==2){
                table.startGame();
                table.isStarted = true;
            }

        }

       else if(!table2.isStarted){
            table = table2;
            table.players.add(this);

            if(table.players.size()==2){
                table.startGame();
                table.isStarted = true;
            }
        }
        else {
            out.println("wait");
            waitingUsers.add(this);
        }
    }

    public void decreaseChips(int i) {

        chips -= i;
        table.sendDataToAll("chips#" + this.username+ "#" +String.valueOf(chips));
    }

    public void sleep(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            System.err.println("couldn't sleep");
        }
    }


}

