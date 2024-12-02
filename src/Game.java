import java.util.ArrayList;
import java.util.Scanner;

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
    private Scanner scanner;

    public Game() {
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        String[] suits = {"Hearts", "Clubs", "Diamonds", "Spades"};
        int[] values = {2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10, 11};

        deck = new Deck(ranks, suits, values);
        communityCards = new Card[5];
        communityCardCount = 0;
        player1Folded = false;
        player2Folded = false;
        pot = 0;
        player1Currency = 1000;
        player2Currency = 1000;
        currentBet = 0;

        scanner = new Scanner(System.in);

        System.out.print("Enter Player 1's name: ");
        player1 = new Player(scanner.nextLine(), 5);

        System.out.print("Enter Player 2's name: ");
        player2 = new Player(scanner.nextLine(), 5);
    }

    public void printInstructions() {
        System.out.println("Welcome to Texas Hold'em Poker!");
        System.out.println("[RULES]");
        System.out.println("each player starts with 1000 chips. the game proceeds through the following phases: pre-flop, flop, turn, and river.");
        System.out.println("you can bet, call, or fold during each betting round.");
        System.out.println("the player with the best 5-card hand at the end wins the pot.");
    }

    // main game loop
    public void playGame() {
        boolean keepPlaying = true;
        while (keepPlaying) {
            resetGame();
            printInstructions();

            // deal initial two cards to each player
            for (int i = 0; i < 2; i++) {
                player1.addCard(deck.deal());
                player2.addCard(deck.deal());
            }

            System.out.println(player1);
            System.out.println(player2);

            // betting rounds and reveal community cards progressively
            bettingRound();
            if (player1Folded || player2Folded) {
                determineWinner();
                continue;
            }
            dealFlop();
            System.out.println("Flop: " + getCommunityCardsString());

            bettingRound();
            if (player1Folded || player2Folded) {
                determineWinner();
                continue;
            }

            dealTurn();
            System.out.println("Turn: " + getCommunityCardsString());

            bettingRound();
            if (player1Folded || player2Folded) {
                determineWinner();
                continue;
            }

            dealRiver();
            System.out.println("River: " + getCommunityCardsString());

            bettingRound();
            if (player1Folded || player2Folded) {
                determineWinner();
                continue;
            }

            determineWinner();

            // play again?
            System.out.print("Do you want to play again? (y/n): ");
            keepPlaying = getYesNoResponse();
        }
    }

    // resets the game state for a new round
    public void resetGame() {
        deck.shuffle();
        communityCardCount = 0;
        player1Folded = false;
        player2Folded = false;
        pot = 0;
        currentBet = 0;
        player1.resetHand();
        player2.resetHand();
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

    // betting round
    public void bettingRound() {
        // player 1 action
        if (!playerAction(player1)) {
            player1Folded = true;
            return;
        }
        // player 2 action
        if (!playerAction(player2)) {
            player2Folded = true;
        }
    }

    // players action (bet, call, or fold)
    public boolean playerAction(Player player) {
        System.out.print(player.getName() + ", do you want to (1) Bet, (2) Fold" + (currentBet > 0 ? ", (3) Call" : "") + "? Enter choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();  // clear buffer
        if (choice == 2) { // fold
            return false;
        } else if (choice == 3 && currentBet > 0) { // call
            if (currentBet > player.getCurrency()) {
                System.out.println("you don't have enough currency to call. you must fold.");
                return false;
            } else {
                player.decreaseCurrency(currentBet);
                pot += currentBet;
            }
        } else if (choice == 1) { // bet
            int bet = nextBet(player.getCurrency());
            while (bet < currentBet || bet > player.getCurrency()) {
                if (bet > player.getCurrency()) {
                    System.out.println("bet exceeds your current currency. you have: " + player.getCurrency());
                } else {
                    System.out.println("your bet must be at least the current bet of: " + currentBet);
                }
                bet = nextBet(player.getCurrency());
            }
            player.decreaseCurrency(bet);
            pot += bet;
            currentBet = bet;
        }
        return true;
    }

    // get player next bet amount
    public int nextBet(int maxCurrency) {
        while (true) {
            System.out.print("enter your bet amount (or enter a percentage, e.g., 10%): ");
            String input = scanner.nextLine();
            try {
                if (input.endsWith("%")) { // percent bet of current wealth
                    int percentage = Integer.parseInt(input.replace("%", "").trim());
                    if (percentage < 0 || percentage > 100) {
                        System.out.println("invalid percentage. enter a value between 0 and 100.");
                    } else {
                        return (maxCurrency * percentage) / 100;
                    }
                } else { // bet by exact amount
                    int bet = Integer.parseInt(input.trim());
                    if (bet < 0) {
                        System.out.println("bet cannot be negative.");
                    } else {
                        return bet;
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("invalid input. please enter a valid number or percentage.");
            }
        }
    }

    // determine the winner of the game
    public void determineWinner() {
        if (player1Folded) { // player 1 folded, player 2 wins
            System.out.println(player2.getName() + " wins because " + player1.getName() + " folded!");
            System.out.println(player2.getName() + " wins the pot of " + pot + " chips!");
            player2.increaseCurrency(pot);
        } else if (player2Folded) { // player 2 folded, player 1 wins
            System.out.println(player1.getName() + " wins because " + player2.getName() + " folded!");
            System.out.println(player1.getName() + " wins the pot of " + pot + " chips!");
            player1.increaseCurrency(pot);
        } else { // both players still in, compare hand values
            int player1Points = evaluateHand(player1);
            int player2Points = evaluateHand(player2);

            if (player1Points > player2Points) {
                System.out.println(player1.getName() + " wins with " + player1Points + " points!");
                System.out.println(player1.getName() + " wins the pot of " + pot + " chips!");
                player1.increaseCurrency(pot);
            } else if (player1Points < player2Points) {
                System.out.println(player2.getName() + " wins with " + player2Points + " points!");
                System.out.println(player2.getName() + " wins the pot of " + pot + " chips!");
                player2.increaseCurrency(pot);
            } else { // tie
                System.out.println("it's a tie!");
                System.out.println("the pot of " + pot + " chips is split!");
                player1.increaseCurrency(pot / 2);
                player2.increaseCurrency(pot / 2);
            }
        }
        pot = 0;
        currentBet = 0;
    }

    // find best possible hand for the player
    public int evaluateHand(Player player) {
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

        // find all possible 5-card combinations
        for (int i = 0; i < combinedCards.size(); i++) {
            for (int j = i + 1; j < combinedCards.size(); j++) {
                ArrayList<Card> tempHand = new ArrayList<>(combinedCards);
                tempHand.remove(i);
                tempHand.remove(j - 1);
                int handValue = calculateHandValue(tempHand);
                if (handValue > bestValue) {
                    bestValue = handValue;
                }
            }
        }
        return bestValue;
    }

    // calc value of a given hand
    private int calculateHandValue(ArrayList<Card> hand) {
        // sort cards to simplify finding straights and flushes
        hand.sort((c1, c2) -> Integer.compare(c2.getValue(), c1.getValue()));

        boolean flush = true;
        boolean straight = true;
        String suit = hand.get(0).getSuit();
        int previousValue = hand.get(0).getValue();
        int valueSum = hand.get(0).getValue();

        // check flush and straight
        for (int i = 1; i < hand.size(); i++) {
            Card card = hand.get(i);
            if (!card.getSuit().equals(suit)) {
                flush = false;
            }
            if (card.getValue() != previousValue - 1) {
                straight = false;
            }
            previousValue = card.getValue();
            valueSum += card.getValue();
        }

        // assign hand value based on poker rules
        if (flush && straight) {
            return 800 + valueSum;  // straight flush
        } else if (flush) {
            return 500 + valueSum;  // flush
        } else if (straight) {
            return 400 + valueSum;  // straight
        } else {
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

    // utility to handle yes/no input
    private boolean getYesNoResponse() {
        while (true) {
            String response = scanner.nextLine().trim().toLowerCase();
            if (response.equals("y") || response.equals("yes")) {
                return true;
            } else if (response.equals("n") || response.equals("no")) {
                return false;
            } else {
                System.out.print("invalid response. please enter 'y' or 'n': ");
            }
        }
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.playGame();
    }
}
