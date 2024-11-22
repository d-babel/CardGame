import org.w3c.dom.ls.LSOutput;

public class Deck {
    private Card[] cards;
    private int cardsLeft;

    public Deck(String[] ranks, String[] suits, int[] values){
        int totalCards = ranks.length * suits.length;
        cards = new Card[totalCards];
        int index = 0;

        for (String suit : suits) {
            for (int i = 0; i < ranks.length; i++) {
                cards[index++] = new Card(ranks[i], suit, values[i]);
            }
        }
        cardsLeft = totalCards;
        shuffle();
    }

    public boolean isEmpty() {
        return cardsLeft == 0;
    }

    public int getCardsLeft() {
        return cardsLeft;
    }

    public Card deal() {
        if (isEmpty()) {
            return null;
        }
        return cards[--cardsLeft];
    }

    public void shuffle() {
        for (int i = cards.length - 1; i > 0; i--) {
            int r = (int) (Math.random() * (i + 1));
            //swap cards[i] with cards[r]
            Card temp = cards[i];
            cards[i] = cards[r];
            cards[r] = temp;
        }
        cardsLeft = cards.length;
    }
}
