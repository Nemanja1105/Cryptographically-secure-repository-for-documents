import java.io.File;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ClientStartDialog {
    public static Client login() {
        System.out.println("=============================================================================");
        System.out.println("-==Prijava==-");
        System.out.println("=============================================================================");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Unesite putanju do vaseg sertifikata:");
        String certPath = scanner.nextLine();
        if (!(CertificateUtil.verifyCertificate(certPath) && CertificateUtil.certificateContainInCerts(certPath))) {
            System.out.println("Sertifikat nije validan. Pristup odbijen!!!");
            System.exit(-1);
        }
        if (CertificateUtil.checkCertificateIsSuspended(certPath)) {
            System.out.println("Sertifikat je suspendovan. Pristup odbijen!!!");
            return null;
        }
        System.out.println("Sertifikat validan!!");
        int i = 1;
        while (i <= 3) {
            System.out.println("Username:");
            String username = scanner.nextLine();
            System.out.println("Password:");
            String password = scanner.nextLine();
            if (!CertificateUtil.checkCertificateOwner(username, certPath)) {
                System.out.println("Sertifikat nije izdat na dato ime!!");
                i++;
                continue;
            }
            /*var client=Main.clients.get(username);
            if(client!=null)
            {
                var passwdHash=CertificateUtil.getPasswordHash(password);
                if(client.getPasswordHash().equals(passwdHash))*/
            var client = checkLogin(username, password);
            if (client != null) {
                System.out.println("=============================================================================");
                System.out.println("\tPrijava uspjesna!!");
                System.out.println("=============================================================================");

                System.out.println("Unesite putanju do vaseg para kljuceva:");
                String keyPath = scanner.nextLine();

                client.setStoredDocuments(FileRepository.getClientStoredDocuments(client));
                client.setClientKeyPath(keyPath);
                return client;
            }
            System.out.println("Neispravni kredencijali. Pokusajte ponovo");
            i++;
        }
        if (i == 4) {
            suspendCertificateDialog(certPath);
        }
        return null;
    }

    private static Client checkLogin(String username, String password) {
        var client = Main.clients.get(username);
        if (client != null) {
            var passwdHash = CertificateUtil.getPasswordHash(password);
            if (client.getPasswordHash().equals(passwdHash))
                return client;
        }
        return null;
    }

    private static void suspendCertificateDialog(String certPath) {
        System.out.println("=============================================================================");
        System.out.println("Previse puta ste pogrijesili kredencijale!!\n" +
                "Vrsi se suspenzija vaseg sertifikata!!");
        System.out.println("=============================================================================");
        CertificateUtil.suspendCertificate(certPath);
        Scanner scanner = new Scanner(System.in);
        int opcija;
        while (true) {
            System.out.println("Opcije:");
            System.out.println("\tPokusaj reaktivacije(1)");
            System.out.println("\tPovratak na pocetak(2)");
            try {
                System.out.println("Unesite opciju:");
                opcija = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Pogresna opcija!!");
                scanner.nextLine();
                continue;
            }
            if (opcija != 1 && opcija != 2) {
                System.out.println("Pogresna opcija!!");
                continue;
            }
            break;
        }
        if (opcija == 1) {
            System.out.println("=============================================================================");
            System.out.println("\tImate jedan pokusaj za reaktivaciju");
            System.out.println("=============================================================================");
            System.out.println("Username:");
            scanner.nextLine();
            String username = scanner.nextLine();
            System.out.println("Password:");
            String password = scanner.nextLine();
            if (!CertificateUtil.checkCertificateOwner(username, certPath)) {
                System.out.println("Neuspjesna reaktivacija!!");
                return;
            }
            var client = checkLogin(username, password);
            if (client == null) {
                System.out.println("Neuspjesna reaktivacija!!");
                return;
            } else {
                try {
                    CertificateUtil.reactivateCertificate(certPath);
                    System.out.println("=============================================================================");
                    System.out.println("\tSertifikat uspjesno reaktiviran!!");
                    System.out.println("=============================================================================");
                    Main.main(null);
                } catch (IOException e) {
                    System.out.println("Greska na I/O podsistemu");
                    return;
                }

            }


        } else
            Main.main(null);
    }

    public static void register() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=============================================================================");
        System.out.println("-==Registracija==-");
        System.out.println("=============================================================================");
        String username, password;
        while (true) {
            System.out.println("Username:");
            username = scanner.nextLine();
            if (Main.clients.containsKey(username))
                System.out.println("Username vec postoji u sistemu. Pokusajte ponovo!");
            else
                break;
        }
        while (true) {
            System.out.println("Password:");
            password = scanner.nextLine();
            if (password.length() < 8)
                System.out.println("Lozinka mora biti duza od 8 karaktera");
            else
                break;
        }
        System.out.println("Drzava porijekla:");
        String country = scanner.nextLine();
        System.out.println("Entiten/provincija:");
        String state = scanner.nextLine();
        System.out.println("Lokacija boravka:");
        String city = scanner.nextLine();
        System.out.println("Naziv vase organizacije:");
        String organizationName = scanner.nextLine();
        System.out.println("Naziv organizacione jedinice");
        String organizationalUnitName = scanner.nextLine();
        System.out.println("Email:");
        String email = scanner.nextLine();

        String keyPath = "";
        while (true) {
            System.out.println("Unesite putanju do vaseg para kljuceva!!");
            keyPath = scanner.nextLine();
            if (!(new File(keyPath)).exists())
                System.out.println("Unesite validnu putanju do kljuca!!");
            else break;
        }

        System.out.println("Unesite lokaciju gdje zelite da sacuvate sertifikat za prijavu:");
        String certOutPath = scanner.nextLine();

        Client client = new Client(username, CertificateUtil.getPasswordHash(password), country, state, city, organizationName, organizationalUnitName, email);
        try {
            ClientFileIO.writeClient(client);
            ClientFileIO.createClientDirectory(client);
            Main.clients.put(client.getUsername(), client);
            CertificateUtil.createCertificate(client, certOutPath, keyPath);
            System.out.println("Uspjesno ste se registrovali");
        } catch (Exception e) {
            System.out.println("Greska na ulazno izlaznom podsistemu. Pokusajte registraciju ponovo");
            return;
        }
        Main.main(null);
    }

}
