import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ClientRepositoryDialog
{
    public static void clientRepositoryStartDialog(Client c)
    {
        for(int i=0;i<100;i++)
            System.out.println();
        System.out.println("=============================================================================");
        System.out.println("\t\t\t-=Dobro dosli u repozitorijum=-");
        System.out.println("=============================================================================");
        Scanner scanner=new Scanner(System.in);
        int opcija;
        while(true) {
            System.out.println("Opcije:");
            System.out.println("\tPregled sacuvanih dokumenata(1)");
            System.out.println("\tUpload dokumenta(2)");
            System.out.println("\tDownload dokumenta(3)");
            System.out.println("\tKraj(4)");
            try {
                System.out.println("Unesite opciju:");
                opcija = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Pogresna opcija!!");
                scanner.nextLine();
                continue;
            }
            if(opcija==1)
            {
                documentView(c);
                scanner.nextLine();
                scanner.nextLine();
            }
            else if(opcija==2)
            {
                documentUpload(c);
            }
            else if(opcija==3)
            {
                documentDownload(c);
            }
            else if(opcija==4)
            {
                System.exit(0);
            }
            else
            {
                System.out.println("Pogresna opcija!!");
                continue;
            }

        }
    }

    private static void documentView(Client c)
    {
        System.out.println("=============================================================================");
        System.out.println("\t-=Pregled sacuvanih dokumenata=-");
        System.out.println("=============================================================================");
        for(var doc:c.getStoredDocuments())
            System.out.println(doc);
        System.out.println("=============================================================================");
    }

    private static void documentDownload(Client c)
    {
        System.out.println("=============================================================================");
        System.out.println("\t-=Download dokumenta=-");
        System.out.println("=============================================================================");
        Scanner scanner=new Scanner(System.in);
        for(var doc:c.getStoredDocuments())
            System.out.println(doc);
        System.out.println("Unesite naziv dokumenta koji zelite da preuzmete:");
        String name=scanner.nextLine();
        System.out.println("Unesite putanju na kojoj zelite sacuvati dokument:");
        String destPath=scanner.nextLine();
        try
        {
            FileRepository.downloadFile(c,name,c.getClientKeyPath(),destPath);
            System.out.println("Dokument uspjesno preuzet!!");
            System.out.println("=============================================================================");
        }
        catch(IOException e)
        {
            System.out.println("Greska na I/O podsistemu!!");
            return;
        }
        catch(ViolationOfIntegrityException e)
        {
            System.out.println(e.getMessage());
            return;
        }
        catch(IllegalArgumentException e)
        {
            System.out.println(e.getMessage());
            return;
        }

    }

    private static void documentUpload(Client c)
    {
        System.out.println("=============================================================================");
        System.out.println("\t-=Upload dokumenta=-");
        System.out.println("=============================================================================");
        Scanner scanner=new Scanner(System.in);
        System.out.println("Unesite putanju do dokumenta za upload:");
        String docPath=scanner.nextLine();
        System.out.println("Unesite naziv(sa ekstenzijom) pod kojim ce se dokument sacuvati:");
        String name=scanner.nextLine();
        try
        {
            FileRepository.uploadFile(c,docPath,name,c.getClientKeyPath());
            c.getStoredDocuments().add(name);
            System.out.println("Dokument uspjesno sacuvan!!");
            System.out.println("=============================================================================");
        }
        catch(IOException e)
        {
            System.out.println("Greska na I/O podsistemu!!:"+e.getMessage());
            return;
        }
        catch(IllegalArgumentException e)
        {
            System.out.println(e.getMessage());
            return;
        }
    }
}
