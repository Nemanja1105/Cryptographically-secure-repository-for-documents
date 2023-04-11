
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CertificateUtil {
    private static final String DEFAULT_SALT = "6k/73DrglwTDgMh7";
    private static final String CA_TIJELO_PATH = "CA tijelo";
    private static final String CA_TIJELO_CERTS_PATH = "certs" + File.separator;
    private static final String CA_TIJELO_REQUESTS_PATH = "requests" + File.separator;
    private static final String CA_TIJELO_CONFIG_PATH = "openssl.cnf";

    private static final String CA_TIJELO_CA_CERT = "root.pem";
    private static final String CA_TIJELO_CRL = "crl/crl.pem";
    private static final String CA_TIJELO_INDEX = "index.txt";


    private static Runtime runtime = Runtime.getRuntime();


    public static String getFileHash(String path) {
        return OpenSSL.executeCommand("openssl dgst -sha512 \"" + path + "\"", null);
    }

    public static String getPasswordHash(String password) {
        String command = "openssl passwd -6 -salt " + DEFAULT_SALT + " " + password;
        return OpenSSL.executeCommand(command, null);
    }

    public static void createCertificate(Client c, String clientOutput, String clientRSAKeyPath) throws IOException {
        String reqPath = CA_TIJELO_REQUESTS_PATH + "req" + c.getUsername() + ".req";
        String certPath = CA_TIJELO_CERTS_PATH + c.getUsername() + ".crt";
        String request = "openssl req -new -key \"" + clientRSAKeyPath + "\" -out " + reqPath + " -config " + CA_TIJELO_CONFIG_PATH
                + " -subj \"/C=" + c.getCountryName() + "/ST=" + c.getStateName() + "/L=" + c.getCityName() + "/O=" + c.getOrganizationName() + "/OU=" + c.getOrganizationalUnitName()
                + "/CN=" + c.getUsername() + "/emailAddress=" + c.getEmail() + "\"";

        String sign = "openssl ca -in " + reqPath + " -out " + certPath + " -config " + CA_TIJELO_CONFIG_PATH + " -batch";
        OpenSSL.executeCommand(request, new File(CA_TIJELO_PATH));
        OpenSSL.executeCommand(sign, new File(CA_TIJELO_PATH));
        Files.copy(Paths.get(CA_TIJELO_PATH, certPath), Paths.get(clientOutput +File.separator+ c.getUsername() + ".crt"));
    }

    public static boolean checkCertificateOwner(String commonName, String certPath) {
        String command = "openssl x509 -in \"" + certPath + "\" -noout -subject";
        String result = OpenSSL.executeCommand(command, null);
        Pattern pattern = Pattern.compile("CN = ([^,]+)");
        Matcher matcher = pattern.matcher(result);
        matcher.find();
        return matcher.group(1).equals(commonName);
    }

    public static void suspendCertificate(String certificatePath) {
        String command = "openssl ca -revoke \"" + certificatePath + "\" -crl_reason certificateHold -config openssl.cnf";
        OpenSSL.executeCommand(command, new File(CA_TIJELO_PATH));
        command = "openssl ca -gencrl -out " + CA_TIJELO_CRL+" -config openssl.cnf";
        OpenSSL.executeCommand(command, new File(CA_TIJELO_PATH));
    }

    public static String getCertificateSerial(String certificatePath) {
        String command = "openssl x509 -in \"" + certificatePath + "\" -noout -serial";
        String result = OpenSSL.executeCommand(command, null);
        return result.substring(result.indexOf("=") + 1, result.length());
    }

    public static boolean checkCertificateIsSuspended(String certificatePath) {
        String serialNumber = "Serial Number: " + getCertificateSerial(certificatePath);
        String command = "openssl crl -in " + CA_TIJELO_CRL + " -noout -text";
        String result = OpenSSL.executeCommand(command, new File(CA_TIJELO_PATH));
        return result.contains(serialNumber);
    }

    public static void reactivateCertificate(String certificatePath) throws IOException {
        String serialNumber=getCertificateSerial(certificatePath);
        var indexPath = Paths.get(CA_TIJELO_PATH, CA_TIJELO_INDEX);
        //String serialNumber = "01";
        var lines = Files.readAllLines(indexPath);
        try (PrintWriter writer = new PrintWriter(new FileWriter(indexPath.toString()))) {
            for (var line : lines) {
                if (line.startsWith("R")) {
                    var tmp = line.split("\\t");
                    if (serialNumber.equals(tmp[3])) {
                        writer.println("V" + "\t" + tmp[1] + "\t\t" + tmp[3] + "\t" + tmp[4] + "\t" + tmp[5]);
                    }
                }
                else
                    writer.println(line);
            }
        }

        String command = "openssl ca -gencrl -out " + CA_TIJELO_CRL+" -config openssl.cnf";
        OpenSSL.executeCommand(command, new File(CA_TIJELO_PATH));
    }


    public static boolean verifyCertificate(String certificatePath) {
        String command = "openssl verify -CAfile " + CA_TIJELO_CA_CERT + " -verbose \"" + certificatePath + "\"";
        var result = OpenSSL.executeCommand(command, new File(CA_TIJELO_PATH));
        return result.contains("OK");
    }

    public static boolean certificateContainInCerts(String certificatePath) {
        String certHash = getFileHash(certificatePath);
        certHash = certHash.substring(certHash.indexOf("= ") + 2);
        var files = new File(CA_TIJELO_PATH + File.separator + CA_TIJELO_CERTS_PATH).listFiles();
        for (var file : files) {
            var hash = getFileHash(file.getPath());
            hash = hash.substring(hash.indexOf("= ") + 2);
            if (hash.equals(certHash))
                return true;
        }
        return false;
    }

    /*private static String executeCommand(String command, File workingDirectory) {
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
            BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = err.readLine()) != null) {
                System.out.println(line);
            }
        } catch (InterruptedException | IOException e) {}
        return builder.toString();
    }*/

}
