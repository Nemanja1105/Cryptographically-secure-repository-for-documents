import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class ClientFileIO
{
    public static final String CLIENT_DIR="Klijenti";
    private static final String CLIENT_FILE_PATH=CLIENT_DIR+File.separator+"klijenti.txt";

    static
    {
        var file=new File(CLIENT_DIR);
        if(!file.exists())
            file.mkdir();
    }

    public static void writeClient(Client client)throws IOException
    {
        try(PrintWriter writer=new PrintWriter(new FileWriter(CLIENT_FILE_PATH,true)))
        {
            writer.println(client);
            writer.flush();
            writer.close();
        }
    }

    public static void createClientDirectory(Client client)throws IOException
    {
        var clientDir=new File(CLIENT_DIR+File.separator+client.getUsername());
        if(!clientDir.exists())
            clientDir.mkdir();
    }

    public static HashMap<String,Client> readAllClients()throws IOException
    {
        var lines= Files.readAllLines(Paths.get(CLIENT_FILE_PATH));
        HashMap<String,Client> clients=new HashMap<>();
        for(var line:lines) {
            try {
                var tmp = line.split(" ");
                clients.put(tmp[0], new Client(tmp[0], tmp[1], tmp[2], tmp[3], tmp[4], tmp[5], tmp[6], tmp[7]));
            } catch (Exception e) { //skipamo liniju}
            }
        }
        return clients;
    }

    public static void writeAllClients(HashMap<String,Client> clients)throws IOException
    {
        try(PrintWriter writer=new PrintWriter(new FileWriter(CLIENT_FILE_PATH,true)))
        {
            clients.keySet().forEach((c)->{writer.println(c);});
            writer.flush();
            writer.close();
        }
    }
}
