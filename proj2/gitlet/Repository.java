package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    public File CWD;

    Repository() {
        this.CWD = new File(System.getProperty("user.dir"));
        configDIRS();
    }

    public void configDIRS() {
        GITLET_DIRS = join(CWD, ".gitlet");
        STAGING_DIR = join(GITLET_DIRS, "staging");
        BLOBS_DIR = join(GITLET_DIRS, "blobs");
        COMMITS_DIR = join(GITLET_DIRS, "commits");
        STAGE = join(GITLET_DIRS, "STAGE");
        HEAD = join(GITLET_DIRS, "HEAD");

    }

    /** The .gitlet directories
     * .gitlet
     * --staging
     * [stage]
     * --blobs
     * --commits
     * --refs
     *   --heads
     *     [HEAD]
     * [HEAD]
     *
     */

    public File GITLET_DIRS;

    public File STAGING_DIR;
    public File STAGE;

    public File BLOBS_DIR;
    public File COMMITS_DIR;

    public File HEAD;


    /** Initialize the repository */
    public void init() {
        if(GITLET_DIRS.exists() && GITLET_DIRS.isDirectory()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        GITLET_DIRS.mkdirs();
        STAGING_DIR.mkdirs();
        BLOBS_DIR.mkdirs();
        COMMITS_DIR.mkdirs();
        writeObject(STAGE, new Stage());

        Commit initialCommit = new Commit();
        writeCommitToFile(initialCommit);
        writeObject(HEAD, initialCommit);
    }

    /** Stage the file with the fileName */
    public void add(String fileName) {
        File file = join(CWD, fileName);
        if(!file.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }

        /** Check if version is identical to
         * the version in the current commit.
         * If so do not add and if it is already
         * staged,remove it from staging area.
         */
        Commit head = readObject(HEAD, Commit.class);
        Stage stage = readObject(STAGE, Stage.class);
        Blob thisBlob = new Blob(file);
        Map<String, String> blobs = head.getBlobs();
        if(blobs.containsKey(fileName) &&
                blobs.get(fileName) == thisBlob.getBlobID())  {
            Map<String, String> added = stage.getAdded();
            if(added.containsKey(fileName)) {
                added.remove(fileName);
            }
            return;
        }

        /** Unstage the file if it is
         * currently staged for addition.
         */
        Map<String, String> added = stage.getAdded();
        if(added.containsKey(fileName) &&
                added.get(fileName) != thisBlob.getBlobID()) {
            String blobToDeleteId = added.get(fileName);
            File fileToDelete = new File(STAGING_DIR, blobToDeleteId);
            fileToDelete.delete();
        }

        /** Add the blob to STAGING_DIR. */
        String blobID = thisBlob.getBlobID();
        File stageBlob = new File(STAGING_DIR, blobID);
        writeObject(stageBlob, thisBlob);

        stage.addFile(fileName, blobID);
        writeObject(STAGE, stage);
    }

    /** Remove */
    public void rm(String fileName) {
        Commit head = readObject(HEAD, Commit.class);
        Stage stage = readObject(STAGE, Stage.class);
        Map<String, String> blobs = head.getBlobs();
        Map<String, String> added = stage.getAdded();
        if(!blobs.containsKey(fileName) &&
        !added.containsKey(fileName)) {
            System.out.println("File does not exist.");
            System.exit(0);
        }

        /** Unstage the file if it is
         * currently staged for addition.
         */
        if(added.containsKey(fileName)) {
            String blobToDeleteId = added.get(fileName);
            File fileToDelete = new File(STAGING_DIR, blobToDeleteId);
            fileToDelete.delete();
        }

        /** Remove the file in the
         * working directory.
         */
        restrictedDelete(fileName);
        
        stage.removeFile(fileName);
        writeObject(STAGE, stage);
    }

    /** Commit */
    public void commit(String message) {
        Commit head = readObject(HEAD, Commit.class);
        Stage stage = readObject(STAGE, Stage.class);
        if (stage.empty()) {
           System.out.println("No changes added to the commit.");
           System.exit(0);
        }
        ArrayList<Commit> parents = new ArrayList<>();
        parents.add(head);
        Commit commit = new Commit(message, parents, stage);
        writeCommitToFile(commit);
        writeObject(HEAD, commit);


        /** Move everything in staging area to BLOB_DIR. */
        List<String> blobIDs = plainFilenamesIn(STAGING_DIR);
        for(String blobID : blobIDs){
            File blobFile = join(STAGING_DIR, blobID);
            Blob blob = readObject(blobFile, Blob.class);
            File commitBlob = join(BLOBS_DIR, blobID);
            writeObject(commitBlob, blob);
            blobFile.delete();
        }

        /** Empty the staging area. */
        writeObject(STAGE, new Stage());
    }

    /** Checkout file with only fileName */
    public void checkoutFile(String fileName) {
        Commit head = readObject(HEAD,Commit.class);
        checkoutHelper(head, fileName);
    }

    /** Checkout file with UID and fileName */
    public void checkoutFile(String commitUID, String fileName) {
        File checkoutCommitFile = join(COMMITS_DIR, commitUID);
        Commit commit = readObject(checkoutCommitFile, Commit.class);
        checkoutHelper(commit, fileName);
    }

    /** Checkout branch with branchName */
    public void checkoutBranch(String branchName) {

    }

    /** Print out log */
    public void log() {
        Commit cur = readObject(HEAD, Commit.class);
        while(true) {
            String UID = cur.getUID();
            String timestamp = cur.getFormattedTimestamp();
            String message = cur.getMessage();
            System.out.println("===");
            System.out.println("commit " + UID);
            System.out.println("Date: " + timestamp);
            System.out.println(message);
            System.out.println();
            if(cur.getParents().isEmpty()) {
                break;
            }
            List<String> parents = cur.getParents();
            String parentId = parents.get(0);
            File parentCommit = join(COMMITS_DIR, parentId);
            cur = readObject(parentCommit, Commit.class);
        }
    }

    /** Write commit to the system. */
    public void writeCommitToFile(Commit c) {
        String UID = c.getUID();
        File thisCommit = join(COMMITS_DIR, UID);
        writeObject(thisCommit, c);
    }

    public void checkoutHelper(Commit head, String fileName){
        String blobId = head.getBlob(fileName);
        File checkBlobFile = join(BLOBS_DIR, blobId);
        Blob checkBlob = readObject(checkBlobFile, Blob.class);
        byte[] checkBlobSerializedContent = checkBlob.getContent();
        File cwdFile = join(CWD, fileName);
        writeContents(cwdFile, checkBlobSerializedContent);
        String checkBlobContent = readObject(cwdFile, String.class);
        writeContents(cwdFile, checkBlobContent);
    }
}
