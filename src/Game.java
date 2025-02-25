// Drew Babel
// 12/3/2024

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
    private int currentBet;
    private Scanner scanner;

    private String difficulty = "easy"; // default if not set

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
        currentBet = 0;

        scanner = new Scanner(System.in);

        System.out.print("Enter Player 1's name: ");
        player1 = new Player(scanner.nextLine(), 5);

        System.out.print("Enter Player 2's name: ");
        player2 = new Player(scanner.nextLine(), 5);
    }

    public void setDifficulty(String diff) {
        difficulty = diff;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Deck getDeck() {
        return deck;
    }

    public Card[] getCommunityCards() {
        return communityCards;
    }

    public int getCommunityCardCount() {
        return communityCardCount;
    }

    public boolean isPlayer1Folded() {
        return player1Folded;
    }

    public boolean isPlayer2Folded() {
        return player2Folded;
    }

    public int getPot() {
        return pot;
    }

    public int getCurrentBet() {
        return currentBet;
    }

    public void printInstructions() {
        System.out.println("Welcome to Texas Hold'em Poker!");
        System.out.println("[RULES]");
        System.out.println("each player starts with 1000 chips. the game proceeds through the following phases: pre-flop, flop, turn, and river.");
        System.out.println("you can bet, call, or fold during each betting round.");
        System.out.println("the player with the best 5-card hand at the end wins the pot.");
    }

    public void playGameConsole() {
        boolean keepPlaying = true;
        while (keepPlaying) {
            resetGame();
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

            System.out.print("Do you want to play again? (y/n): ");
            keepPlaying = getYesNoResponse();
        }
    }

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

    // deals 3 community cards
    public void dealFlop() {
        for (int i = 0; i < 3; i++) {
            communityCards[communityCardCount++] = deck.deal();
        }
    }

    // deals 1 community card
    public void dealTurn() {
        communityCards[communityCardCount++] = deck.deal();
    }

    // deals 1 community card
    public void dealRiver() {
        communityCards[communityCardCount++] = deck.deal();
    }

    public String getCommunityCardsString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < communityCardCount; i++){
            result.append(communityCards[i]);
            if (i < communityCardCount - 1) {
                result.append(", ");
            }
        }
        return result.toString();
    }

    public void addToPot(int amount) {
        pot += amount;
    }

    public void setCurrentBet(int amount) {
        currentBet = amount;
    }

    public void foldPlayer1() {
        player1Folded = true;
    }

    public void foldPlayer2() {
        player2Folded = true;
    }

    public void bettingRound() {
        if (!playerAction(player1)) {
            player1Folded = true;
            return;
        }
        if (!playerActionAI(player2)) {
            player2Folded = true;
        }
    }

    public boolean playerAction(Player player) {
        System.out.print(player.getName() + ", do you want to (1) Bet, (2) Fold"
                + (currentBet > 0 ? ", (3) Call" : "") + "? Enter choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        if (choice == 2) {
            return false;
        } else if (choice == 3 && currentBet > 0) {
            if (currentBet > player.getCurrency()) {
                System.out.println("you don't have enough currency to call. you must fold.");
                return false;
            } else {
                player.decreaseCurrency(currentBet);
                pot += currentBet;
            }
        } else if (choice == 1) {
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

    public boolean playerActionAI(Player aiPlayer) {
        if (difficulty.equalsIgnoreCase("easy")) {
            return aiActionEasy(aiPlayer);
        } else if (difficulty.equalsIgnoreCase("medium")) {
            return aiActionMedium(aiPlayer);
        } else {
            return aiActionHard(aiPlayer);
        }
    }

    private boolean aiActionEasy(Player aiPlayer) {
        int aiCurrency = aiPlayer.getCurrency();
        if (currentBet == 0) {
            int bet = (int)(Math.random() * 50);
            if (bet > aiCurrency) bet = aiCurrency;
            aiPlayer.decreaseCurrency(bet);
            pot += bet;
            currentBet = bet;
        } else {
            double chance = Math.random();
            if (chance < 0.3) {
                return false;
            } else {
                if (currentBet > aiCurrency) {
                    return false;
                } else {
                    aiPlayer.decreaseCurrency(currentBet);
                    pot += currentBet;
                }
            }
        }
        return true;
    }

    private boolean aiActionMedium(Player aiPlayer) {
        int aiCurrency = aiPlayer.getCurrency();
        if (currentBet == 0) {
            int bet = (int)(Math.random() * 100);
            if (bet > aiCurrency) bet = aiCurrency;
            aiPlayer.decreaseCurrency(bet);
            pot += bet;
            currentBet = bet;
        } else {
            double chance = Math.random();
            if (chance < 0.1) {
                return false;
            } else {
                if (currentBet > aiCurrency) {
                    return false;
                } else {
                    aiPlayer.decreaseCurrency(currentBet);
                    pot += currentBet;
                }
            }
        }
        return true;
    }

    private boolean aiActionHard(Player aiPlayer) {
        int aiCurrency = aiPlayer.getCurrency();
        if (currentBet == 0) {
            int bet = (int)(Math.random() * 200) + 50;
            if (bet > aiCurrency) bet = aiCurrency;
            aiPlayer.decreaseCurrency(bet);
            pot += bet;
            currentBet = bet;
        } else {
            double chance = Math.random();
            if (chance < 0.05) {
                return false;
            } else if (chance < 0.5) {
                if (currentBet > aiCurrency) {
                    return false;
                } else {
                    aiPlayer.decreaseCurrency(currentBet);
                    pot += currentBet;
                }
            } else {
                int additionalBet = (int)(Math.random() * 200);
                int newBet = currentBet + additionalBet;
                if (newBet > aiCurrency) newBet = aiCurrency;
                aiPlayer.decreaseCurrency(newBet);
                pot += newBet;
                currentBet = newBet;
            }
        }
        return true;
    }

    public int nextBet(int maxCurrency) {
        while (true) {
            System.out.print("enter your bet amount (or enter a percentage, e.g., 10%): ");
            String input = scanner.nextLine();
            try {
                if (input.endsWith("%")) {
                    int percentage = Integer.parseInt(input.replace("%", "").trim());
                    if (percentage < 0 || percentage > 100) {
                        System.out.println("invalid percentage. enter a value between 0 and 100.");
                    } else {
                        return (maxCurrency * percentage) / 100;
                    }
                } else {
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

    public void determineWinner() {
        if (player1Folded) {
            System.out.println(player2.getName() + " wins because " + player1.getName() + " folded!");
            System.out.println(player2.getName() + " wins the pot of " + pot + " chips!");
            player2.increaseCurrency(pot);
        } else if (player2Folded) {
            System.out.println(player1.getName() + " wins because " + player2.getName() + " folded!");
            System.out.println(player1.getName() + " wins the pot of " + pot + " chips!");
            player1.increaseCurrency(pot);
        } else {
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
            } else {
                System.out.println("it's a tie!");
                System.out.println("the pot of " + pot + " chips is split!");
                player1.increaseCurrency(pot / 2);
                player2.increaseCurrency(pot / 2);
            }
        }
        pot = 0;
        currentBet = 0;
    }

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

    private int calculateHandValue(ArrayList<Card> hand) {
        hand.sort((c1, c2) -> Integer.compare(c2.getValue(), c1.getValue()));

        boolean flush = true;
        boolean straight = true;
        String suit = hand.get(0).getSuit();
        int previousValue = hand.get(0).getValue();
        int valueSum = hand.get(0).getValue();

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

        if (flush && straight) {
            return 800 + valueSum;
        } else if (flush) {
            return 500 + valueSum;
        } else if (straight) {
            return 400 + valueSum;
        } else {
            int[] valueCount = new int[15];
            for (Card c : hand) {
                valueCount[c.getValue()]++;
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
                return 700 + valueSum;
            } else if (threeOfKind > 0 && pairs > 0) {
                return 600 + valueSum;
            } else if (threeOfKind > 0) {
                return 300 + valueSum;
            } else if (pairs > 1) {
                return 200 + valueSum;
            } else if (pairs == 1) {
                return 100 + valueSum;
            } else {
                return valueSum;
            }
        }
    }

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
        new CardView();
    }
}