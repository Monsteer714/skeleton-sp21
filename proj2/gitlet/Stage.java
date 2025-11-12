package gitlet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Stage implements Serializable  {
    /** <file name, blob ID> */
    private Map<String, String> added = new HashMap<>();

    /** <file name> */
    private Set<String> removed = new HashSet<>();

    public Stage() {
        this.added = new HashMap<>();
        this.removed = new HashSet<>();
    }

    public boolean addFile(String fileName, String blobID) {
        boolean isAdded = added.containsKey(fileName);
        added.put(fileName, blobID);
        removed.remove(fileName);
        return isAdded;
    }

    public void removeFile(String fileName) {
        removed.add(fileName);
        added.remove(fileName);
    }

    public Map<String, String> getAdded(){
        return added;
    }

    public Set<String> getRemoved(){
        return removed;
    }

    public boolean empty(){
        return added.isEmpty() && removed.isEmpty();
    }
}
