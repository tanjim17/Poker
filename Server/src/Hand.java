
public class Hand {
    public Card[] cards;
    private int[] value;

    Hand(Deck d)
    {
        value = new int[8];//why 6
        cards = new Card[7];

        for (int x=0; x<2; x++) {
            cards[x] = d.drawFromDeck();
        }

    }

    Hand(){

        cards = new Card[7];
        Card[] cards2 = new Card[7];
        value = new int[8];
        short a = 1;
        short b = 2;
        short g = 7;
        short c = 3;
        short d = 4;
        short e = 5;
        short f = 6;
        short h = 9;
        short x = 0;
        short x2 = 11;
        short x3 = 12;
        short x4 = 10;

        cards[0] = new Card(x,x3);
        cards[1] = new Card(a,x3);
        cards[2] = new Card(b,x3);
        cards[3] = new Card(c,x2);
        cards[4] = new Card(x,x2);
        cards[5] = new Card(b,h);
        cards[6] = new Card(c,x4);

       /* cards2[0] = new Card(x,x);
        cards2[1] = new Card(a,x2);
        cards2[2] = new Card(x,x3);
        cards2[3] = new Card(c,c);
        cards2[4] = new Card(x,d);
        cards2[5] = new Card(b,h);
        cards2[6] = new Card(c,x4);*/

        for(int i = 0;i<cards.length;i++){
            System.out.println(cards[i].toString());
        }


    }

    public static void main(String[] args) {

        Hand h = new Hand();
       // System.out.println(h.cards[0].getRank());
        h.evaluateHand();
        System.out.println(h.display());
    }


    public String display()
    {
        String s;
        switch( value[0] )
        {

            case 1:
                //System.out.println("========================================value[1]: " + value[1]);
                s="high card " + Card.rankAsString(value[1]);
                break;
            case 2:
                s="pair of " + Card.rankAsString(value[1]) + "\'s";
                break;
            case 3:
                s="two pair " + Card.rankAsString(value[1]) + " " + Card.rankAsString(value[2]);
                break;
            case 4:
                s="three of a kind " + Card.rankAsString(value[1]) + "\'s";
                break;
            case 5:
                s=Card.rankAsString(value[1]) + " high straight";
                break;
            case 6:
                s="flush";
                break;
            case 7:
                s="full house " + Card.rankAsString(value[1]+1) + " over " + Card.rankAsString(value[2]+1);
                break;
            case 8:
                s="four of a kind " + Card.rankAsString(value[1]);
                break;
            case 9:
                s="straight flush " + Card.rankAsString(value[1]) + " high";
                break;
            default:
                s="error in Hand.display: value[0] contains invalid value";
        }

        return s;
    }


    int compareTo(Hand that)
    {
        for (int x=0; x<8; x++)
        {
            if (this.value[x]>that.value[x])
                return 1;
            else if (this.value[x]<that.value[x])
                return -1;
        }
        return 0; //if hands are equal
    }

    public void getCommunityCards(Card[] c){
        for(int i = 0;i<c.length;i++){
            cards[i+2] = c[i];
        }

        evaluateHand();

    }

    private void evaluateHand() {

        int[] ranks = new int[14];
        int[] orderedRanks = new int[7];  //miscellaneous cards that are not otherwise significant
        boolean flush = false, straight = false;

        int sameCards = 1, sameCards2 = 1;
        int largeGroupRank = 0, smallGroupRank = 0;
        int index = 0;
        int topStraightValue = 0;

        for (int x = 0; x <= 13; x++) {
            ranks[x] = 0;
        }
        for (int x = 0; x < 7; x++) {
            ranks[cards[x].getRank()+1]++;

        }

       /* for(int x:ranks)
            System.out.print(x + " || ");
        System.out.println();*/

        for(int i = 0;i<4;i++){
          int temp = 0;
        for (int x = 0; x < 7; x++) {

            if(cards[x].getSuit() == i)temp++;
        }
        if(temp >4){
            flush = true;
            break;
        }
    }
        for (int x=13; x>=1; x--)
        {
            if (ranks[x] > sameCards)
            {
                if (sameCards == 1)  //if sameCards was not assigned to already
                {
                    largeGroupRank = x;

                }
                else {
                sameCards2 = sameCards; //if sameCards was assigned to, write data from
                smallGroupRank = x;          //top group to low group

            }
                sameCards = ranks[x];         //update sameCards to new greatest sameCards value in ranks

            } else if (ranks[x] > sameCards2)

            {
                sameCards2 = ranks[x];
                smallGroupRank = x;
            }
        }


        if (ranks[1]==1) //if ace, run this before because ace is highest card
        {
            orderedRanks[index]=14;
            index++;
        }

        for (int x=13; x>=2; x--)
        {
            if (ranks[x]==1)
            {
                orderedRanks[index]=x; //if ace
                index++;
            }
        }


        for (int x=1; x<=9; x++) //can't have straight with lowest value of more than 10
        {

            if (ranks[x]>0 && ranks[x+1]>0 && ranks[x+2]>0 && ranks[x+3]>0 && ranks[x+4]>0)
            {
                straight=true;
                topStraightValue=x+4; //4 above bottom value
                break;
            }
        }

        if (ranks[10]>0 && ranks[11]>0 && ranks[12]>0 && ranks[13]>0 && ranks[1]>0) //ace high
        {
            straight=true;
            topStraightValue=14; //higher than king
        }

        for (int x=0; x<=5; x++)
        {
            value[x]=0;
        }

        //start hand evaluation
        if ( sameCards==1 ) {  // high card
            value[0]=1;
            value[1]=orderedRanks[0];
            value[2]=orderedRanks[1];
            value[3]=orderedRanks[2];
            value[4]=orderedRanks[3];
            value[5]=orderedRanks[4];
            value[6]=orderedRanks[5];
            value[7]=orderedRanks[6];
        }

        if (sameCards==2 && sameCards2==1)
        {
            value[0]=2;
            value[1]=largeGroupRank; //rank of pair
            value[2]=orderedRanks[0];
            value[3]=orderedRanks[1];
            value[4]=orderedRanks[2];
            value[5]=orderedRanks[3];
            value[6]=orderedRanks[4];
        }

        if (sameCards==2 && sameCards2==2) //two pair
        {
            value[0]=3;
            value[1]= largeGroupRank>smallGroupRank ? largeGroupRank : smallGroupRank; //rank of greater pair
            value[2]= largeGroupRank<smallGroupRank ? largeGroupRank : smallGroupRank;
            value[3]=orderedRanks[0];  //extra card
            value[4]=orderedRanks[1];  //extra card
            value[5]=orderedRanks[2];  //extra card
        }

        if (sameCards==3 && sameCards2!=2){ // three of a kind
            value[0]=4;
            value[1]= largeGroupRank;
            value[2]=orderedRanks[0];
            value[3]=orderedRanks[1];
            value[4]=orderedRanks[2];
            value[5]=orderedRanks[3];
        }

        if (straight && !flush)
        {
            value[0]=5;
            value[1]=topStraightValue;
        }

        if (flush && !straight)
        {
            value[0]=6;
            value[1]=orderedRanks[0]; //tie determined by ranks of cards
            value[2]=orderedRanks[1];
            value[3]=orderedRanks[2];
            value[4]=orderedRanks[3];
            value[5]=orderedRanks[4];
            value[6]=orderedRanks[5];
            value[7]=orderedRanks[6];
        }

        if (sameCards==3 && sameCards2==2) // full house
        {
            value[0]=7;
            System.out.println("In FUll House     -------------`````` " + largeGroupRank + " " + smallGroupRank);
            value[1]=largeGroupRank-1;
            value[2]=smallGroupRank-1;

        }

        if (sameCards==4) // four of a kind
        {
            value[0]=8;
            value[1]=largeGroupRank;
            value[2]=orderedRanks[0];
        }

        if (straight && flush) // straight flush
        {
            value[0]=9;
            value[1]=topStraightValue;

            if(topStraightValue==14)  // royal flush
                value[0]=10;
        }

    }

}
