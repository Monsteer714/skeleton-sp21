package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.*;

public class Blob implements Serializable {
    private String fileName;

    private byte[] content;

    private String blobID;

    public Blob(String fileName) {
        this.fileName = fileName;
    }

    public Blob(File file) {
        this.fileName = file.getName();
        String fileContent = readContentsAsString(file);
        this.content = serialize(fileContent);
        this.blobID = sha1(fileName + fileContent);
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getContent() {
        return content;
    }

    public String getBlobID() {
        return blobID;
    }
}
