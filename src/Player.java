public class Player {
    private String name;
    private Card[] hand;
    private int handSize;
    private int points;

    public Player(String name, int maxHandSize) {
        this.name = name;
        this.hand = new Card[maxHandSize];
        this.handSize = 0;
        this.points = 0;
    }

    public int getPoints() {
        return points;
    }

    public Card[] getHand() {
        return hand;
    }

    public String getName() {
        return name;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    // adds card to players hand
    public void addCard(Card card) {
        if (handSize < hand.length) {
            hand[handSize++] = card;
        }
    }

    public String toString() {
        String handDescription = "";
        for (int i = 0; i < handSize; i++){
            handDescription += hand[i];
            if (i < handSize - 1) {
                handDescription += ", ";
            }
        }
        return name + " has " + points + " points\n" + name + "'s cards: " + handDescription;
    }
}
