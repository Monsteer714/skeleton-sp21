package gitlet;

import java.io.Serializable;
import java.util.Date;
import java.util.Formatter;
import java.util.*;

import static gitlet.Utils.*;

/**
 * Represents a gitlet commit object.
 *
 * @author Monsteer714
 */
public class Commit implements Serializable {
    /**
     * <p>
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    private String message;

    private Date timestamp;

    private String UID;

    /**
     * <SHA1 ID>
     */
    private List<String> parents;

    /**
     * <file name,blob id>
     */
    private Map<String, String> blobs;

    public Commit() {
        this.message = "initial commit";
        this.timestamp = new Date(0);
        this.UID = sha1(this.message + this.timestamp.toString());
        this.parents = new ArrayList<>();
        this.blobs = new HashMap<>();
    }

    public Commit(String message, List<Commit> parents, Stage stage) {
        this.message = message;
        this.timestamp = new Date();
        this.UID = sha1(this.message + this.timestamp.toString());
        this.parents = new ArrayList<>();
        for (Commit parent : parents) {
            this.parents.add(parent.getUID());
        }
        this.blobs = new HashMap<>();
        Map<String, String> added = stage.getAdded();
        Set<String> removed = stage.getRemoved();
        if (parents.size() == 1) {
            Commit head = parents.get(0);
            Map<String, String> headBlobs = head.getBlobs();
            this.blobs.putAll(headBlobs);
            for (Map.Entry<String, String> entry : added.entrySet()) {
                this.blobs.put(entry.getKey(), entry.getValue());
            }
            for (String entry : removed) {
                this.blobs.remove(entry);
            }
        } else if (parents.size() == 2) {

        }

    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getFormattedTimestamp() {
        Date date = this.timestamp;
        Formatter formatter = new Formatter();
        formatter.format(Locale.ENGLISH, "%ta %tb %td %tT %tY %tz", date, date, date, date, date, date);
        String res = formatter.toString();
        formatter.close();
        return res;
    }

    public String getUID() {
        return UID;
    }

    public List<String> getParents() {
        return parents;
    }

    public Map<String, String> getBlobs() {
        return blobs;
    }

    public String getBlob(String fileName) {
        return blobs.get(fileName);
    }
}
