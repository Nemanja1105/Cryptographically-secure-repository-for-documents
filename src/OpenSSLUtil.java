public class OpenSSLUtil
{
    private static final String DEFAULT_ALG="aes256";

    public static String generateRandomKey()
    {
        String command="openssl rand -base64 16";
        return OpenSSL.executeCommand(command,null);
    }

    public static void encryptFile(String in,String out,String key)
    {
        String command="openssl enc -"+DEFAULT_ALG+" -in \""+in+"\" -out \""+out+"\" -k "+key;
        OpenSSL.executeCommand(command,null);
    }

    public static void decryptFile(String in,String out,String key)
    {
        String command="openssl enc -"+DEFAULT_ALG+" -d -in \""+in+"\" -out \""+out+"\" -k "+key;
        OpenSSL.executeCommand(command,null);
    }

    public static void digitalSignFile(String file,String out,String clientKey)
    {
        String command="openssl dgst -sign \""+clientKey+"\" -sha512 -out \""+out+"\" \""+file+"\"";
        OpenSSL.executeCommand(command,null);
    }

    public static boolean verifyDigitalSign(String file,String sign,String clientKey)
    {
        String command="openssl dgst -prverify \""+clientKey+"\" -sha512 -signature \""+sign+"\" \""+file+"\"";
        String result=OpenSSL.executeCommand(command,null);
        return result.contains("OK");
    }

    public static void encryptFileWithRsa(String in,String out,String inkey )
    {
        String command="openssl rsautl -encrypt -in \""+in+"\" -out \""+out+"\" -inkey \""+inkey+"\"";
        OpenSSL.executeCommand(command,null);
    }

    public static String decryptFileWithRsa(String in,String out,String inkey)
    {
        String command="openssl rsautl -decrypt -in \""+in+"\" -inkey \""+inkey+"\" -out \""+out+"\"";
        return OpenSSL.executeCommand(command,null);
    }


}
