import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class OpenSSL {
    public static String executeCommand(String command, File workingDirectory) {
        StringBuilder builder = new StringBuilder();
        var processBuilder = new ProcessBuilder(command.split(" "));
        if (workingDirectory != null)
            processBuilder.directory(workingDirectory);
        try {
            var process = processBuilder.start();
            int value = process.waitFor();
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                builder.append(line);
            }

            //debug
            /*BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = err.readLine()) != null) {
                System.out.println(line);
            }*/
        } catch (InterruptedException | IOException e) {}
        return builder.toString();
    }
}
