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
    private int pot;
    private int player1Currency;
    private int player2Currency;
    private int currentBet;

    public Game() {
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        String[] suits = {"Hearts", "Clubs", "Diamonds", "Spades"};
        int[] values = {2, 3, 4, 5, 6 , 7, 8, 9, 10, 10, 10, 10, 11};

        deck = new Deck(ranks, suits, values);
        communityCards = new Card[5];
        communityCardCount = 0;
        player1Folded = false;
        player2Folded = false;
        pot = 0;
        player1Currency = 1000;
        player2Currency = 1000;
        currentBet = 0;

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

        System.out.print(player1.getName() + ", do you want to (1) Bet, (2) Fold" + (currentBet > 0 ? ", (3) Call" : "") + "? Enter choice: "); // modified: only allow call if currentBet > 0
        int choice1 = scanner.nextInt();
        if (choice1 == 2){
            player1Folded = true;
            return;
        } else if (choice1 == 3 && currentBet > 0) {
            if (currentBet > player1Currency) {
                System.out.println("You don't have enough currency to call. You must fold.");
                player1Folded = true;
                return;
            } else {
                player1Currency -= currentBet;
                pot += currentBet;
            }
        } else {
            int bet1 = nextBet(player1Currency);
            while (bet1 < currentBet || bet1 > player1Currency) {
                if (bet1 > player1Currency) {
                    System.out.println("Bet exceeds your current currency. You have: " + player1Currency);
                } else {
                    System.out.println("Your bet must be at least the current bet of: " + currentBet);
                }
                bet1 = nextBet(player1Currency);
            }
            player1Currency -= bet1;
            pot += bet1;
            currentBet = bet1;
        }

        System.out.print(player2.getName() + ", do you want to (1) Bet, (2) Fold" + (currentBet > 0 ? ", (3) Call" : "") + "? Enter choice: "); // modified: only allow call if currentBet > 0
        int choice2 = scanner.nextInt();
        if (choice2 == 2){
            player2Folded = true;
            return;
        } else if (choice2 == 3 && currentBet > 0) {
            if (currentBet > player2Currency) {
                System.out.println("You don't have enough currency to call. You must fold.");
                player2Folded = true;
                return;
            } else {
                player2Currency -= currentBet;
                pot += currentBet;
            }
        } else {
            int bet2 = nextBet(player2Currency);
            while (bet2 < currentBet || bet2 > player2Currency) {
                if (bet2 > player2Currency) {
                    System.out.println("Bet exceeds your current currency. You have: " + player2Currency);
                } else {
                    System.out.println("Your bet must be at least the current bet of: " + currentBet);
                }
                bet2 = nextBet(player2Currency);
            }
            player2Currency -= bet2;
            pot += bet2;
            currentBet = bet2;
        }
    }

    public int nextBet(int maxCurrency) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter your bet amount (or enter a percentage, e.g., 10%): ");
            String input = scanner.nextLine();
            try {
                if (input.endsWith("%")) {
                    int percentage = Integer.parseInt(input.replace("%", "").trim());
                    if (percentage < 0 || percentage > 100) {
                        System.out.println("Invalid percentage. Enter a value between 0 and 100.");
                    } else {
                        return (maxCurrency * percentage) / 100;
                    }
                } else {
                    int bet = Integer.parseInt(input.trim());
                    if (bet < 0) {
                        System.out.println("Bet cannot be negative.");
                    } else {
                        return bet;
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number or percentage.");
            }
        }
    }

    public void determineWinner() {
        if (player1Folded) {
            System.out.println(player2.getName() + " wins because " + player1.getName() + " folded!");
            System.out.println(player2.getName() + " wins the pot of " + pot + " chips!");
            player2Currency += pot;
        } else if (player2Folded) {
            System.out.println(player1.getName() + " wins because " + player2.getName() + " folded!");
            System.out.println(player1.getName() + " wins the pot of " + pot + " chips!");
            player1Currency += pot;
        } else {
            int player1Points = evaluateHand(player1);
            int player2Points = evaluateHand(player2);

            if (player1Points > player2Points) {
                System.out.println(player1.getName() + " wins with " + player1Points + " points!");
                System.out.println(player1.getName() + " wins the pot of " + pot + " chips!");
                player1Currency += pot;
            } else if (player1Points < player2Points) {
                System.out.println(player2.getName() + " wins with " + player2Points + " points!");
                System.out.println(player2.getName() + " wins the pot of " + pot + " chips!");
                player2Currency += pot;
            } else {
                System.out.println("It's a tie!");
                System.out.println("The pot of " + pot + " chips is split!");
                player1Currency += pot / 2;
                player2Currency += pot / 2;
            }
        }
        pot = 0;
        currentBet = 0;
    }

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

        // evaluate all possible 5 card combinations
        for (int i = 0; i < combinedCards.size(); i++){
            for (int j = i + 1; j < combinedCards.size(); j++) {
                ArrayList<Card> tempHand = new ArrayList<>(combinedCards);
                tempHand.remove(i);
                tempHand.remove(j - 1);
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
