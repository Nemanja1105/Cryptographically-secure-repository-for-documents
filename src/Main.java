import java.io.IOException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static HashMap<String, Client> clients;

    static {
        try {
            clients = ClientFileIO.readAllClients();
        } catch (IOException e) {
            System.out.println("Greska prilikom ucitavanje podataka o klijentima!!");
            System.exit(-1);
        }
    }

    public static void main(String[] args) {


        System.out.println("=============================================================================");
        System.out.println("\t\t\t-=Dobro dosli u sigurni repozitorijum=-");
        System.out.println("=============================================================================");
        Scanner scanner = new Scanner(System.in);
        int opcija = 0;
        while (true) {
            System.out.println("Opcije:");
            System.out.println("\tRegistracija(1)");
            System.out.println("\tPrijava(2)");
            System.out.println("\tKraj(3)");
            try {
                System.out.println("Unesite opciju:");
                opcija = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Pogresna opcija!!");
                scanner.nextLine();
                continue;
            }
            if (opcija != 1 && opcija != 2 && opcija != 3) {
                System.out.println("Pogresna opcija!!");
                continue;
            }
            break;
        }
        if (opcija == 1)
            ClientStartDialog.register();
        else if (opcija == 2) {
            Client client = ClientStartDialog.login();
            if (client != null)
                ClientRepositoryDialog.clientRepositoryStartDialog(client);
        } else
            System.exit(0);


    }
}