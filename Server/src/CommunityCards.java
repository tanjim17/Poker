
public class CommunityCards {

    public Card[] cards;

    CommunityCards()
    {
        cards = new Card[5];
    }

    public void flop(Deck d){
        for (int x = 0; x < 3; x++) {
            cards[x] = d.drawFromDeck();

        }
    }

    public void turn(Deck d){
        cards[3] = d.drawFromDeck();
    }

    public void river(Deck d){
            cards[4] = d.drawFromDeck();
    }
}