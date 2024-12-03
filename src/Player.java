// Drew Babel
// 12/3/2024

class Player {
    private String name;
    private Card[] hand;
    private int handSize;
    private int currency;
    private int points;

    // constructor with name only
    public Player(String name, int maxHandSize) {
        this.name = name;
        this.hand = new Card[maxHandSize];
        this.handSize = 0;
        this.currency = 1000;
        this.points = 0;
    }

    // constructor with name and initial hand [REQUIRED: "ensure initialHand length matches maxHandSize to avoid inconsistencies"]
    public Player(String name, Card[] initialHand) {
        this.name = name;
        this.hand = initialHand;
        this.handSize = initialHand.length;
        this.currency = 1000;
        this.points = 0;
    }

    public String getName() {
        return name;
    }

    public Card[] getHand() {
        return hand;
    }

    public int getCurrency() {
        return currency;
    }

    public void decreaseCurrency(int amount) {
        currency -= amount;
    }

    public void increaseCurrency(int amount) {
        currency += amount;
    }

    public void addPoints(int additionalPoints) {
        points += additionalPoints;
    }

    public int getPoints() {
        return points;
    }

    // add card to player's hand [REQUIRED: "consider checking if card is null before adding to avoid null entries"]
    public void addCard(Card card) {
        if (handSize < hand.length) {
            hand[handSize++] = card;
        }
    }

    // reset player's hand [REQUIRED: "should hand size be dynamic or fixed at 5 for all cases?"]
    public void resetHand() {
        hand = new Card[5];
        handSize = 0;
    }

    // player's name, points, and cards
    public String toString() {
        StringBuilder handDescription = new StringBuilder();
        for (int i = 0; i < handSize; i++) {
            handDescription.append(hand[i]);
            if (i < handSize - 1) {
                handDescription.append(", ");
            }
        }
        return name + " has " + points + " points\n" + name + "'s cards: " + handDescription;
    }
}
