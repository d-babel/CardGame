import java.util.Scanner;
import java.util.ArrayList;

public class Game {
    private Deck deck;
    private Player player1;
    private Player player2;
    private Card[] communityCards;
    private int communityCardCount;
    private boolean player1Folded;
    private boolean player2Folded;

    public Game() {
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        String[] suits = {"Hearts", "Clubs", "Diamonds", "Spades"};
        int[] values = {2, 3, 4, 5, 6 , 7, 8, 9, 10, 10, 10, 10, 11};

        deck = new Deck(ranks, suits, values);
        communityCards = new Card[5];
        communityCardCount = 0;
        player1Folded = false;
        player2Folded = false;

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Player 1's name: ");
        player1 = new Player(scanner.nextLine(), 5);

        System.out.print("Enter Player 2's name: ");
        player2 = new Player(scanner.nextLine(), 5);
    }

    public void printInstructions() {
        System.out.println("[RULES]");
    }

    public void playGame() {
        printInstructions();

        for (int i = 0; i < 2; i++) {
            player1.addCard(deck.deal());
            player2.addCard(deck.deal());
        }

        System.out.println(player1);
        System.out.println(player2);

        bettingRound();
        if (player1Folded || player2Folded) {
            determineWinner();
            return;
        }
        dealFlop();
        System.out.println("Flop: " + getCommunityCardsString());

        bettingRound();
        if (player1Folded || player2Folded) {
            determineWinner();
            return;
        }

        dealTurn();
        System.out.println("Turn: " + getCommunityCardsString());

        bettingRound();
        if (player1Folded || player2Folded) {
            determineWinner();
            return;
        }

        dealRiver();
        System.out.println("River: " + getCommunityCardsString());

        bettingRound();
        if (player1Folded || player2Folded) {
            determineWinner();
            return;
        }

        determineWinner();
    }

    public void dealFlop() {
        for (int i = 0; i < 3; i++) {
            communityCards[communityCardCount++] = deck.deal();
        }
    }

    public void dealTurn() {
        communityCards[communityCardCount++] = deck.deal();
    }

    public void dealRiver() {
        communityCards[communityCardCount++] = deck.deal();
    }

    public String getCommunityCardsString() {
        String result = "";
        for (int i = 0; i < communityCardCount; i++){
            result += communityCards[i];
            if (i < communityCardCount - 1) {
                result += ", ";
            }
        }
        return result;
    }

    public void bettingRound() {
        Scanner scanner = new Scanner(System.in);

        System.out.print(player1.getName() + ", do you want to (1) Bet, (2) Fold? Enter choice: ");
        int choice1 = scanner.nextInt();
        if (choice1 == 2){
            player1Folded = true;
            return;
        }

        System.out.print(player2.getName() + ", do you want to (1) Bet, (2) Fold? Enter choice: ");
        int choice2 = scanner.nextInt();
        if (choice2 == 2){
            player1Folded = false;
            return;
        }
    }

    public void determineWinner() {
        if (player1Folded) {
            System.out.println(player2.getName() + " wins because " + player1.getName() + "folded!");
        } else if (player2Folded) {
            System.out.println(player1.getName() + " wins because " + player2.getName() + "folded!");
        } else {
            int player1Points = evaluateHand(player1);
            int player2Points = evaluateHand(player2);

            if (player1Points > player2Points) {
                System.out.println(player1.getName() + " wins with " + player1Points + " points!");
            } else if (player1Points < player2Points) {
                System.out.println(player2.getName() + " wins with " + player2Points + " points!");
            } else {
                System.out.println("It's a tie!");

            }
        }
    }

//    public int evaluateHand(Player player ) {
//        Card[] combinedCards = new Card[communityCardCount + player.getHand().length];
//        for (int i = 0; i < player.getHand(); i++){
//            combinedCards[i] = player.getHand()[i];
//        }
//        for (int i = 0; i < communityCardCount; i++){
//            combinedCards[player.getHand().length + i] = communityCards[i];
//        }
//
//        //placeholder
//
//        int totalValue = 0;
//        for (Card card : combinedCards){
//            totalValue += card.getValue();
//        }
//        return totalValue;
//    }

    public int evaluateHand(Player player ) {
        ArrayList<Card> combinedCards = new ArrayList<>();
        for (Card card : player.getHand()) {
            if (card != null) {
                combinedCards.add(card);
            }
        }
        for (Card card : communityCards) {
            if (card != null) {
                combinedCards.add(card);
            }
        }

        int bestValue = 0;
        ArrayList<Card> bestHand = new ArrayList<>();

        // evauluate all possibel 5 card combinations
        for (int i = 0; i < combinedCards.size(); i++){
            for (int j = i + 1; j < combinedCards.size(); j++) {
                ArrayList<Card> tempHand = new ArrayList<>(combinedCards);
                tempHand.remove(i);
                tempHand.remove(j - 1); // adjust index after removing previous card
                int handValue = calculateHandValue(tempHand);
                if (handValue > bestValue) {
                    bestValue = handValue;
                    bestHand = tempHand;
                }
            }
        }
        return bestValue;
    }


    //NOTE: INCREMENT VAR TO NOT REPEAT COMPARISONS
    private int calculateHandValue(ArrayList<Card> hand) {
        for (int i = 0; i < hand.size(); i++) {
            for (int j = 0; j < hand.size(); j++){
                if (hand.get(i).getValue() < hand.get(j).getValue()) {
                    Card temp = hand.get(i);
                    hand.set(i, hand.get(j));
                    hand.set(j, temp);
                }
            }
        }

        boolean flush = true;
        boolean straight = true;
        String suit = hand.get(0).getSuit();
        int previousValue = hand.get(0).getValue();
        int valueSum = hand.get(0).getValue();

        for (int i = 1; i < hand.size(); i++) {
            Card card = hand.get(i);
            if(!card.getSuit().equals(suit)) {
                flush = false;
            }
            if(card.getValue() !=  previousValue - 1) {
                straight = false;
            }
            previousValue = card.getValue();
            valueSum += card.getValue();
        }

        if (flush && straight) {
            return 800 + valueSum;  // straight flush
        } else if (flush) {
            return 500 + valueSum;  // flush
        } else if (straight) {
            return 400 + valueSum;  // straight
        } else {
            // calculate pairs, three of a kind, four of a kind
            int[] valueCount = new int[15]; // to account for ace being both high and low
            for (Card card : hand) {
                valueCount[card.getValue()]++;
            }
            int fourOfKind = 0;
            int threeOfKind = 0;
            int pairs = 0;
            for (int count : valueCount) {
                if (count == 4) {
                    fourOfKind++;
                }
                if (count == 3) {
                    threeOfKind++;
                }
                if (count == 2) {
                    pairs++;
                }
            }
            if (fourOfKind > 0) {
                return 700 + valueSum;  // four of a kind
            } else if (threeOfKind > 0 && pairs > 0) {
                return 600 + valueSum;  // full house
            } else if (threeOfKind > 0) {
                return 300 + valueSum;  // three of a kind
            } else if (pairs > 1) {
                return 200 + valueSum;  // two pair
            } else if (pairs == 1) {
                return 100 + valueSum;  // one pair
            } else {
                return valueSum;  // high card
            }
        }
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.playGame();
    }
}
