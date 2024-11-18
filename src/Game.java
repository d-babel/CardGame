import java.util.Scanner;

public class Game {
    private Deck deck;
    private Player player1;
    private Player player2;
    private Card[] communityCards;
    private int communityCardCount;

    public Game() {
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        String[] suits = {"Hearts", "Clubs", "Diamonds", "Spades"};
        int[] values = {2, 3, 4, 5, 6 , 7, 8, 9, 10, 10, 10, 10, 11};

        deck = new Deck(ranks, suits, values);
        communityCards = new Card[5];
        communityCardCount = 0;

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

        for (int i = 0; i < 2; i++){
            player1.addCard(deck.deal());
            player2.addCard(deck.deal());
        }

        System.out.println(player1);
        System.out.println(player2);

        dealFlop();
        System.out.println("Flop: " + getCommunityCardsString());

        dealTurn();
        System.out.println("Turn: " + getCommunityCardsString());

        dealRiver();
        System.out.println("River: " + getCommunityCardsString());

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

    public static void main(String[] args) {
        Game game = new Game();
        game.playGame();
    }


}
