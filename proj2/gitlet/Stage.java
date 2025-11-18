package gitlet;

import java.io.Serializable;
import java.util.*;

public class Stage implements Serializable {
    /**
     * <file name, blob ID>
     */
    private Map<String, String> added = new TreeMap<>();

    /**
     * <file name>
     */
    private Set<String> removed = new TreeSet<>();

    public Stage() {
        this.added = new TreeMap<>();
        this.removed = new TreeSet<>();
    }

    public void addFile(String fileName, String blobID) {
        added.put(fileName, blobID);
        removed.remove(fileName);
    }

    public void removeFile(String fileName) {
        removed.add(fileName);
        added.remove(fileName);
    }

    public Map<String, String> getAdded() {
        return added;
    }

    public Set<String> getRemoved() {
        return removed;
    }

    public boolean empty() {
        return added.isEmpty() && removed.isEmpty();
    }
}
