import java.util.ArrayList;
import java.util.List;

public class Table{

    public List<Server> inGamePlayers;
    public List<Server> players;
    private CommunityCards communityCards;
    private Deck deck;
    public int whichPlayerTurn;
    private int round;
    public int pot;
    public int currentBet;
    public int checkNumber;
    public boolean isStarted;

    Table(){
        players = new ArrayList<>();
        isStarted = false;
    }

    public void startGame() {
        inGamePlayers = new ArrayList<>();
        deck = new Deck();
        communityCards = new CommunityCards();
        currentBet = 0;
        pot = 0;

        for(int i=0; i<players.size(); i++){
            inGamePlayers.add(players.get(i));
        }


        for(int i = 0; i< inGamePlayers.size(); i++){

            Server player = inGamePlayers.get(i);

            player.isFolded = false;

            player.hand = new Hand(deck);
            player.out.println("card#" +  player.hand.cards[0].toString() +
                    "#"+ player.hand.cards[1].toString());

            for(int j = 0; j< inGamePlayers.size(); j++){

                if(j == i) continue;

                inGamePlayers.get(j).out.println("opponentAdded#"+ player.username+
                        "#"+String.valueOf(player.chips) );
            }
        }

        /**pre flop starts**/

        sendDataToAll("round#" + "pre flop");
        round = 1;
        whichPlayerTurn = 0;
        initiateNewRound();

        currentBet = 10;

        inGamePlayers.get(0).decreaseChips(currentBet);
        inGamePlayers.get(0).selfBet = currentBet;
        inGamePlayers.get(0).out.println("selfbet#" + String.valueOf(inGamePlayers.get(0).selfBet));
        pot += currentBet;
        sendDataToAll("pot#"+ String.valueOf(pot));
        sendDataToAll("currentbet#" + String.valueOf(currentBet));

        changeTurn();
    }

    public void changeTurn(){

        if(whichPlayerTurn==inGamePlayers.size()-1){
            whichPlayerTurn = 0;
        }

        else whichPlayerTurn++;

        inGamePlayers.get(whichPlayerTurn).isTurn = true;
        sendDataToAll("whichPturn#"+ inGamePlayers.get(whichPlayerTurn).username);

    }


    public void changeRound(){

        sendDataToAll("removeaction");

        if(round == 4) compareAndReset();

        else round++;

        if(round==2){                        //flop
            communityCards.flop(deck);

            initiateNewRound();

            sendDataToAll("flop#" + communityCards.cards[0].toString() + "#" +
                    communityCards.cards[1].toString() + "#" + communityCards.cards[2].toString()
            );
            sendDataToAll("round#" + "flop round");
        }

        if(round==3){                        //turn
            communityCards.turn(deck);

            initiateNewRound();

            sendDataToAll("turn#" + communityCards.cards[3].toString());
            sendDataToAll("round#" + "turn round");
        }

        if(round==4){                       //river
            communityCards.river(deck);

            initiateNewRound();

            sendDataToAll("river#" + communityCards.cards[4].toString());
            sendDataToAll("round#" + "river round");
        }
    }

    public void sendDataToAll(String data){
        for(Server player: players){
            player.out.println(data);
        }
    }

    public void sendDataToAllActive(String data){
        for(Server player: inGamePlayers){
            player.out.println(data);
        }
    }

    private void initiateNewRound(){
        checkNumber = 0;
        currentBet = 0;
        sendDataToAll("currentbet#" + String.valueOf(currentBet));

        for(int i = 0; i<inGamePlayers.size(); i++){
            inGamePlayers.get(i).selfBet = 0;
            inGamePlayers.get(i).out.println("selfbet#" +
                    String.valueOf(inGamePlayers.get(i).selfBet));
        }
    }

    public void compareAndReset(){

        inGamePlayers.get(0).isTurn = false;

        for (int j = 0; j < inGamePlayers.size(); j++) {        //getting community cards
            inGamePlayers.get(j).hand.getCommunityCards(communityCards.cards);
        }

        /* start comparing*/

        Server temp = inGamePlayers.get(0);
        for (int j = 0; j < inGamePlayers.size(); j++) {

            if (temp.hand.compareTo(inGamePlayers.get(j).hand) == -1)
                temp = inGamePlayers.get(j);

            sendDataToAll("cardshow#" + inGamePlayers.get(j).username + "#" +
                    inGamePlayers.get(j).hand.cards[0].toString() + "#"
                    + inGamePlayers.get(j).hand.cards[1].toString());
        }

        sendDataToAll("winner#" + "\" " + temp.username + " Won with a " + temp.hand.display() + "!\"");
        temp.chips += pot;
        pot = 0;

        sendDataToAll("pot#" + String.valueOf(pot));
        sendDataToAll("chips#" + temp.username + "#" + String.valueOf(temp.chips));

        /*wait in the interval**/
        for (Server player : players) {
            player.sleep();
        }

        /* getting new player from waiting room**/

        while (true) {
            getNewPlayers();

            if(players.size()>1){
                sendDataToAll("cardReset");
                startGame();
                break;
            }
            else continue;
        }

    }

    private void getNewPlayers() {
        while(players.size() < 5 && Server.waitingUsers.size() > 0) {

            players.add(Server.waitingUsers.get(0));

            int j = players.size()-1;
            players.get(j).table = this;

            players.get(j).out.println("login done#" +
                    players.get(j).username + "#" +
                    String.valueOf(players.get(j).chips));

            Server.waitingUsers.remove(0);
        }
    }

    public void Reset(){

        Server player = inGamePlayers.get(0);
        player.isTurn = false;

        sendDataToAll("winner#" + "\" Muck! " + player.username + " Won"+ "!\"");
        player.chips += pot;
        pot = 0;

        sendDataToAll("pot#" + String.valueOf(pot));
        sendDataToAll("chips#" + player.username + "#" + String.valueOf(player.chips));

        /*wait in the interval*/
        for (Server playah : players) {
            playah.out.println("sleep");
            playah.sleep();
            playah.out.println("alive");

        }


        /* getting new player from waiting room**/

        while (true) {
            getNewPlayers();

            if(players.size()>1){
                sendDataToAll("cardReset");
                startGame();
                break;
            }
            else continue;
        }
    }

}




