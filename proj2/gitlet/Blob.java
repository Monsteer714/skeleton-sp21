package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.*;

public class Blob implements Serializable {
    private String fileName;

    private byte[] content;

    private String blobId;

    public Blob(String fileName) {
        this.fileName = fileName;
    }

    public Blob(File file) {
        this.fileName = file.getName();
        String fileContent = readContentsAsString(file);
        this.content = serialize(fileContent);
        this.blobId = sha1(fileName + fileContent);
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getContent() {
        return content;
    }

    public String getBlobId() {
        return blobId;
    }
}
