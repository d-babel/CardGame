import java.util.Scanner;

public class Game {
    private Deck deck;
    private Player player1;
    private Player player2;

    public Game() {
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        String[] suits = {"Hearts", "Clubs", "Diamonds", "Spades"};
        int[] values = {2, 3, 4, 5, 6 , 7, 8, 9, 10, 10, 10, 10, 11};

        deck = new Deck(ranks, suits, values);
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
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.playGame();
    }


}
