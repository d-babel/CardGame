import java.util.Scanner;

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
            player1Folded = true;
            return;
        }
    }

    public void determineWinner() {
        if (player1Folded) {
            System.out.println(player2.getName() + " wins because " + player1.getName() + "folded!");
        } else if (player2Folded) {
            System.out.println(player1.getName() + " wins because " + player2.getName() + "folded!");
        } else {
            // placeholder for logic based winner determination
            int player1Points = player1.getPoints();
            int player2Points = player2.getPoints();

            if (player1Points > player2Points) {
                System.out.println(player1.getName() + " wins with " + player1Points + " points!");
            } else if (player1Points < player2Points) {
                System.out.println(player2.getName() + " wins with " + player2Points + " points!");
            } else {
                System.out.println("It's a tie!");

            }
        }
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.playGame();
    }
}
