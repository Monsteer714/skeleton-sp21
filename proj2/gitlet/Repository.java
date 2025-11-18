package gitlet;

import java.io.File;
import java.io.IOException;
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
        REF_DIR = join(GITLET_DIRS, "refs");
        HEADS_DIR = join(REF_DIR, "heads");
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
     */

    public File GITLET_DIRS;

    public File STAGING_DIR;
    public File STAGE;

    public File BLOBS_DIR;
    public File COMMITS_DIR;

    public File HEAD;

    public File REF_DIR;
    public File HEADS_DIR;

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
        REF_DIR.mkdirs();
        HEADS_DIR.mkdirs();
        writeObject(STAGE, new Stage());
        File masterHead = join(HEADS_DIR, "master");
        writeObject(HEAD, "master");

        Commit initialCommit = new Commit();
        writeCommitToFile(initialCommit);
        writeObject(masterHead, initialCommit);
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
        Commit head = getHead();
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
            File fileToDelete = join(STAGING_DIR, blobToDeleteId);
            fileToDelete.delete();
        }

        /** Add the blob to STAGING_DIR. */
        String blobID = thisBlob.getBlobID();
        File stageBlob = join(STAGING_DIR, blobID);
        writeObject(stageBlob, thisBlob);

        stage.addFile(fileName, blobID);
        writeObject(STAGE, stage);
    }

    /** Remove */
    public void rm(String fileName) {
        Commit head = getHead();
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
            File fileToDelete = join(STAGING_DIR, blobToDeleteId);
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
        Commit head = getHead();
        Stage stage = readObject(STAGE, Stage.class);
        if (stage.empty()) {
           System.out.println("No changes added to the commit.");
           System.exit(0);
        }
        ArrayList<Commit> parents = new ArrayList<>();
        parents.add(head);
        Commit commit = new Commit(message, parents, stage);
        writeCommitToFile(commit);
        writeCommitToBranch(commit);

        moveStagingToBlob();
        emptyStagingArea();
    }

    /** Checkout file with only fileName */
    public void checkoutFile(String fileName) {
        Commit head = getHead();
        String commitUID = head.getUID();
        checkoutFile(commitUID, fileName);
    }

    /** Checkout file with UID and fileName */
    public void checkoutFile(String commitUID, String fileName) {
        File checkoutCommitFile = join(COMMITS_DIR, commitUID);
        if(!checkoutCommitFile.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit commit = readObject(checkoutCommitFile, Commit.class);
        checkoutHelper(commit, fileName);
    }

    /** Checkout branch with branchName */
    public void checkoutBranch(String branchName) {
        if(!checkBranchExists(branchName)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        if(checkIsCurrentBranch(branchName)) {
            System.out.println("No need to checkout current branch.");
            System.exit(0);
        }

    }

    /** Print out log */
    public void log() {
        Commit cur = getHead();
        while(true) {
            printLog(cur);
            if(cur.getParents().isEmpty()) {
                break;
            }
            List<String> parents = cur.getParents();
            String parentId = parents.get(0);
            File parentCommit = join(COMMITS_DIR, parentId);
            cur = readObject(parentCommit, Commit.class);
        }
    }

    /** Print out global-log. */
    public void globalLog() {
        List<String> commitsIDs = plainFilenamesIn(COMMITS_DIR);
        for(String commitID : commitsIDs){
            File commitFile = join(COMMITS_DIR, commitID);
            Commit thisCommit = readObject(commitFile, Commit.class);
            printLog(thisCommit);
        }
    }

    /** Find the commit id(s) by
     * the given commit message. */
    public void find(String commitMessage) {
        List<String> commitsIDs = plainFilenamesIn(COMMITS_DIR);
        boolean found = false;
        for(String commitID : commitsIDs){
            File commitFile = join(COMMITS_DIR, commitID);
            Commit thisCommit = readObject(commitFile, Commit.class);
            if(commitMessage.equals(thisCommit.getMessage())) {
                found = true;
                System.out.println(thisCommit.getUID());
            }
        }
        if(!found) {
            System.out.println("Found no commit with that message.");
        }
    }

    /** Create branch at current head. */
    public void branch(String branchName) {
        File branchHead = join(HEADS_DIR, branchName);
        if(branchHead.exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        Commit currentCommit = getHead();
        writeObject(branchHead, currentCommit);
    }

    /** Remove the branch pointer with the given
     * branch name.
     */
    public void rmBranch(String branchName) {
        if(!checkBranchExists(branchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if(checkIsCurrentBranch(branchName)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        File branchHeadFile = join(HEADS_DIR, branchName);
        branchHeadFile.delete();
    }

    /** Write commit to the system. */
    public void writeCommitToFile(Commit c) {
        String UID = c.getUID();
        File thisCommit = join(COMMITS_DIR, UID);
        writeObject(thisCommit, c);
    }

    /** Get the head of current branch. */
    public Commit getHead() {
        String branchName = readObject(HEAD, String.class);
        File branchHeadFile = join(HEADS_DIR, branchName);
        Commit branchHead = readObject(branchHeadFile, Commit.class);
        return branchHead;
    }

    /** Write commit to the current branch head. */
    public void writeCommitToBranch(Commit c) {
        String currentBranchName = readObject(HEAD, String.class);
        File currentBranchHeadFile = join(HEADS_DIR, currentBranchName);
        writeObject(currentBranchHeadFile, c);
    }

    public void checkoutHelper(Commit head, String fileName){
        String blobId = head.getBlob(fileName);
        if(blobId == null) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        File checkBlobFile = join(BLOBS_DIR, blobId);
        Blob checkBlob = readObject(checkBlobFile, Blob.class);
        byte[] checkBlobSerializedContent = checkBlob.getContent();
        File cwdFile = join(CWD, fileName);
        writeContents(cwdFile, checkBlobSerializedContent);
        String checkBlobContent = readObject(cwdFile, String.class);
        writeContents(cwdFile, checkBlobContent);
    }

    public void printLog(Commit commit) {
        String UID = commit.getUID();
        String timestamp = commit.getFormattedTimestamp();
        String message = commit.getMessage();
        System.out.println("===");
        System.out.println("commit " + UID);
        System.out.println("Date: " + timestamp);
        System.out.println(message);
        System.out.println();
    }

    /** Check if the branch with the given name exist. */
    public boolean checkBranchExists(String branchName) {
        File currentBranchHeadFile = join(HEADS_DIR, branchName);
        if(!currentBranchHeadFile.exists()) {
            return false;
        }
        return true;
    }

    /** Check if the input branch name is current branch. */
    public boolean checkIsCurrentBranch(String branchName) {
        String currentBranchName = readObject(HEAD, String.class);
        if(currentBranchName.equals(branchName)) {
            return true;
        }
        return false;
    }

    /** Move everything in staging area to BLOB_DIR. */
    public void moveStagingToBlob(){
        List<String> blobIDs = plainFilenamesIn(STAGING_DIR);
        List<File> blobFiles = new ArrayList<>();

        if (blobIDs != null) {
            for(String blobID : blobIDs) {
                File blobFile = join(STAGING_DIR, blobID);
                blobFiles.add(blobFile);
            }
        }

        for(File blobFile : blobFiles){
            Blob blob = readObject(blobFile, Blob.class);
            File commitBlob = join(BLOBS_DIR, blob.getBlobID());
            writeObject(commitBlob, blob);
        }
    }

    /** Empty the staging area. */
    public void emptyStagingArea(){
        List<String> blobIDs = plainFilenamesIn(STAGING_DIR);
        List<File> blobFiles = new ArrayList<>();

        if (blobIDs != null) {
            for(String blobID : blobIDs) {
                File blobFile = join(STAGING_DIR, blobID);
                blobFiles.add(blobFile);
            }
        }

        for(File blobFile : blobFiles){
            blobFile.delete();
        }
        writeObject(STAGE, new Stage());
    }
}
