public class Card{

    private short rank, suit;

    private static String[] suits = { "hearts", "spades", "diamonds", "clubs" };
    private static String[] ranks  = { "ace" ,"2", "3", "4", "5", "6", "7", "8", "9", "10", "jack", "queen", "king"};

    public static String rankAsString( int __rank ) {
           if(__rank == 14)return ranks[0];
          return ranks[__rank-1];
    }

    Card(short suit, short rank)
    {
        this.suit=suit;
        this.rank=rank;
    }

    public @Override String toString()
    {
        return ranks[rank] + "_of_" + suits[suit];
    }

    public short getRank() {
        return rank;
    }

    public short getSuit() {
        return suit;
    }


    public static void main(String[] args) {

        short a = 2;
        short b = 5;
        Card c = new Card(a,b);
        System.out.println(c);
    }
}
