import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileRepository {


    private static class SegmentInfo
    {
        public String dirName;
        public String key;

        public SegmentInfo(String dirName,String key)
        {
            this.dirName=dirName;
            this.key=key;
        }

        @Override
        public String toString()
        {
            return this.dirName+" "+this.key;
        }
    }
    private static final int MAX_N = 10;
    private static final Random random = new Random();


    public static void uploadFile(Client client, String path,String name,String clientKey) throws IOException {
       var fileDir= madeFileDirectory(path, client.getUsername(),name);

        var bytes = Files.readAllBytes(Paths.get(path));
        int partSize, n, len;
        do {
            n = random.nextInt(MAX_N - 3) + 4;
            len = partSize =(int)Math.ceil(((double)bytes.length / n));
        } while (partSize == 0);

        var segmentInfoList=new ArrayList<SegmentInfo>();
        for (int i = 0; i < n; i++) {
            var partPathDir=createFilePartPath(client.getUsername(),name);
            if (i == n - 1 && bytes.length % n != 0)
                len = bytes.length-i*partSize;
            //kreiranje fajla
            File segmentPath=new File(partPathDir+File.separator+"segment.bin");
            try (BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(segmentPath))) {
                output.write(bytes, i * partSize, len);
                output.flush();
            }
            String key=OpenSSLUtil.generateRandomKey();
            //enkripcija
            OpenSSLUtil.encryptFile(segmentPath.toString(),partPathDir+File.separator+"segment.enc",key);
            //potpisivanje
            OpenSSLUtil.digitalSignFile(segmentPath.toString(),partPathDir+File.separator+"digitalSign.sign",clientKey);
            //brisanje
            segmentPath.delete();
            segmentInfoList.add(new SegmentInfo(partPathDir.getName(),key));
        }
        writeDocInfo(fileDir,segmentInfoList,clientKey);
    }

    private static void writeDocInfo(File fileDir,ArrayList<SegmentInfo> data,String clientKey)throws IOException
    {
        File docInfoPath=new File(fileDir+File.separator+"temp.txt");
        try(PrintWriter writer=new PrintWriter(new FileWriter(docInfoPath)))
        {
            data.forEach((s)->{writer.println(s);});
            writer.flush();
        }
        OpenSSLUtil.encryptFileWithRsa(docInfoPath.toString(),fileDir+File.separator+"docInfo.enc",clientKey);
        docInfoPath.delete();
    }

    private static ArrayList<SegmentInfo> readDocInfo(Path path,String clientKey)throws IOException
    {
        ArrayList<SegmentInfo> list=new ArrayList<>();
        Path tmpPath=Paths.get(path.getParent().toString(),"tmp.txt");
        var str=OpenSSLUtil.decryptFileWithRsa(path.toString(),tmpPath.toString(),clientKey);
        var lines=Files.readAllLines(tmpPath);
        for(var line:lines)
        {
            var tmp=line.split(" ");
            list.add(new SegmentInfo(tmp[0],tmp[1]));
        }
        Files.delete(tmpPath);
        return list;
    }

    public static void downloadFile(Client client,String name,String clientKey,String outputPath)throws IOException,ViolationOfIntegrityException
    {
        File destFile=new File(ClientFileIO.CLIENT_DIR+File.separator+client.getUsername()+File.separator+name);
        if(!destFile.exists())
            throw new IllegalArgumentException("Fajl sa datim imenom ne postoji u repozitorijumu!!");
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        var fileOrder=readDocInfo(Paths.get(destFile.toString(),"docInfo.enc"),clientKey);
        for(var segment:fileOrder)
        {
            var path=Paths.get(destFile.toString(), segment.dirName,"segment.enc");
            var segmentPath=Paths.get(destFile.toString(), segment.dirName,"segment.bin");
            var segmentSignPath=Paths.get(destFile.toString(),segment.dirName,"digitalSign.sign");
            //enkripcija
            OpenSSLUtil.decryptFile(path.toString(),segmentPath.toString(),segment.key);
            //validacija potpisa
            if(!OpenSSLUtil.verifyDigitalSign(segmentPath.toString(),segmentSignPath.toString(),clientKey))
            {
                Files.delete(segmentPath);
                throw new ViolationOfIntegrityException();
            }

            byte[]tmp=Files.readAllBytes(segmentPath);
            bytes.write(tmp);
            Files.delete(segmentPath);
        }

        try(BufferedOutputStream output=new BufferedOutputStream(new FileOutputStream(outputPath+File.separator+name)))
        {
            output.write(bytes.toByteArray());
            output.flush();
        }
    }

    public static List<String> getClientStoredDocuments(Client c)
    {
        ArrayList<String> documents=new ArrayList<>();
        File destFile=new File(ClientFileIO.CLIENT_DIR+File.separator+c.getUsername());
        var files=destFile.listFiles();
        for(var file:files)
            documents.add(file.getName());
        return documents;
    }



    private static File createFilePartPath(String username,String fileName)
    {
        var uid=UUID.randomUUID().toString().replace("-","");//.substring(0,7);
        var file= new File(ClientFileIO.CLIENT_DIR+File.separator+username+File.separator+fileName+File.separator+uid);
        file.mkdir();
        return file;
    }

    private static File madeFileDirectory(String path, String username,String name) {
        File outputDir = new File(ClientFileIO.CLIENT_DIR + File.separator + username + File.separator + name);
        if (outputDir.exists())
            throw new IllegalArgumentException("Fajl sa datim imenom vec postoji u repozitorijumu!!");
        outputDir.mkdir();
        return outputDir;
    }


}
