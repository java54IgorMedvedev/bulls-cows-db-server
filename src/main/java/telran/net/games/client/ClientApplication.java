package telran.net.games.client;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import telran.net.games.proxy.BullsCowsProxy;
import telran.net.games.service.BullsCowsService;
import telran.net.games.model.MoveData;

public class ClientApplication {
    private static BullsCowsService bcService;

    public static void main(String[] args) {
        try {
            bcService = (BullsCowsService) new BullsCowsProxy("localhost", 5000); 
            runMainMenu(); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void runMainMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Main menu:");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    login(scanner); 
                    break;
                case 2:
                    register(scanner); 
                    break;
                case 3:
                    System.exit(0);
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void login(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.next();
        String gamer = bcService.loginGamer(username);
        if (gamer != null) {
            System.out.println("Logged in as: " + gamer);
            runGameMenu(scanner);  
        } else {
            System.out.println("Login failed.");
        }
    }

    private static void register(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.next();
        System.out.print("Enter birthdate (yyyy-mm-dd): ");
        String birthdate = scanner.next();
        bcService.registerGamer(username, LocalDate.parse(birthdate));
        System.out.println("Registered: " + username);
        runGameMenu(scanner);  
    }

    private static void runGameMenu(Scanner scanner) {
        while (true) {
            System.out.println("Game menu:");
            System.out.println("1. Start game");
            System.out.println("2. Continue game");
            System.out.println("3. Join game");
            System.out.println("4. Back to main menu");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    startGame();
                    break;
                case 2:
                    continueGame(scanner);
                    break;
                case 3:
                    joinGame(scanner);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void startGame() {
        long gameId = bcService.createGame();
        List<String> gamers = bcService.startGame(gameId);
        System.out.println("Game started with ID: " + gameId);
        System.out.println("Gamers: " + gamers);
    }

    private static void continueGame(Scanner scanner) {
        System.out.print("Enter game ID: ");
        long gameId = scanner.nextLong();
        System.out.print("Enter your sequence: ");
        String sequence = scanner.next();
        System.out.print("Enter username: ");
        String username = scanner.next();
        List<MoveData> moves = bcService.moveProcessing(sequence, gameId, username);
        moves.forEach(move -> System.out.println("Sequence: " + move.sequence() + 
                ", Bulls: " + move.bulls() + ", Cows: " + move.cows()));
    }

    private static void joinGame(Scanner scanner) {
        System.out.print("Enter game ID: ");
        long gameId = scanner.nextLong();
        System.out.print("Enter username: ");
        String username = scanner.next();
        bcService.gamerJoinGame(gameId, username);
        System.out.println(username + " joined game ID: " + gameId);
    }
}
