package gitlet;

import java.io.File;
import java.util.*;

import static gitlet.Utils.*;

/**
 * Represents a gitlet repository.
 *
 * @author Monsteer714
 */
public class Repository {
    /**
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    private File CWD;

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

    /**
     * The .gitlet directories
     * .gitlet
     * --staging
     * [stage]
     * --blobs
     * --commits
     * --refs
     * --heads
     * [HEAD]
     * [HEAD]
     */

    private File GITLET_DIRS;

    private File STAGING_DIR;
    private File STAGE;

    private File BLOBS_DIR;
    private File COMMITS_DIR;

    private File HEAD;

    private File REF_DIR;
    private File HEADS_DIR;

    /**
     * Initialize the repository
     */
    public void init() {
        if (GITLET_DIRS.exists() && GITLET_DIRS.isDirectory()) {
            System.out.println("A Gitlet version-control system "
                    + "already exists in the current directory.");
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

    /**
     * Stage the file with the fileName
     */
    public void add(String fileName) {
        File file = join(CWD, fileName);
        if (!file.exists()) {
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
        Map<String, String> added = stage.getAdded();
        Set<String> removed = stage.getRemoved();
        if (blobs.containsKey(fileName)
                && blobs.get(fileName).equals(thisBlob.getBlobId())) {
            if (added.containsKey(fileName)) {
                String blobToDeleteId = added.get(fileName);
                File fileToDelete = join(STAGING_DIR, blobToDeleteId);
                fileToDelete.delete();
                added.remove(fileName);
            }
            removed.remove(fileName);
            writeObject(STAGE, stage);
            System.exit(0);
        }

        /** Overwrites the file if it is
         * currently staged for addition.
         */
        if (added.containsKey(fileName)
                && !added.get(fileName).equals(thisBlob.getBlobId())) {
            String blobToDeleteId = added.get(fileName);
            File fileToDelete = join(STAGING_DIR, blobToDeleteId);
            fileToDelete.delete();
        }

        /** Add the blob to STAGING_DIR. */
        String blobId = thisBlob.getBlobId();
        File stageBlob = join(STAGING_DIR, blobId);
        writeObject(stageBlob, thisBlob);

        stage.addFile(fileName, blobId);
        writeObject(STAGE, stage);
    }

    /**
     * Remove
     */
    public void rm(String fileName) {
        Commit head = getHead();
        Stage stage = readObject(STAGE, Stage.class);
        Map<String, String> blobs = head.getBlobs();
        Map<String, String> added = stage.getAdded();

        /** The file is neither staged for addition
         * nor tracked by current commit.
         */
        if (!blobs.containsKey(fileName)
                && !added.containsKey(fileName)) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }

        /** Remove the file from addition. */
        if (added.containsKey(fileName)) {
            String blobToDeleteId = added.get(fileName);
            added.remove(fileName);
            File fileToDelete = join(STAGING_DIR, blobToDeleteId);
            fileToDelete.delete();
        }

        /** Delete the file from working directory and
         * stage it to removal if it is tracked by
         * current commit.
         * */
        if (blobs.containsKey(fileName)) {
            restrictedDelete(fileName);
            stage.removeFile(fileName);
        }

        writeObject(STAGE, stage);
    }

    /**
     * Commit
     */
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

    /**
     * Checkout file with only fileName
     */
    public void checkoutFile(String fileName) {
        Commit head = getHead();
        String commitId = head.getId();
        checkoutFile(commitId, fileName);
    }

    /**
     * Checkout file with commitId and fileName
     */
    public void checkoutFile(String commitId, String fileName) {
        if (!checkCommitExists(commitId)) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit commit = getCommit(commitId);
        checkoutHelper(commit, fileName);
    }

    /**
     * Checkout branch with branchName
     */
    public void checkoutBranch(String branchName) {
        if (!checkBranchExists(branchName)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        if (checkIsCurrentBranch(branchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        Commit branchCommit = getBranchHead(branchName);
        resetHelper(branchCommit);

        /** Change the head to the branch with the given branch name. */
        writeObject(HEAD, branchName);

        /** Clear the staging area. */
        emptyStagingArea();
    }

    /**
     * Print out log
     */
    public void log() {
        Commit cur = getHead();
        while (true) {
            printLog(cur);
            if (cur.getParents().isEmpty()) {
                break;
            }
            List<String> parents = cur.getParents();
            String parentId = parents.get(0);
            cur = getCommit(parentId);
        }
    }

    /**
     * Print out global-log.
     */
    public void globalLog() {
        List<String> commitsIds = plainFilenamesIn(COMMITS_DIR);
        for (String commitId : commitsIds) {
            Commit thisCommit = getCommit(commitId);
            printLog(thisCommit);
        }
    }

    /**
     * Find the commit id(s) by
     * the given commit message.
     */
    public void find(String commitMessage) {
        List<String> commitsIds = plainFilenamesIn(COMMITS_DIR);
        boolean found = false;
        for (String commitId : commitsIds) {
            Commit thisCommit = getCommit(commitId);
            if (commitMessage.equals(thisCommit.getMessage())) {
                found = true;
                System.out.println(thisCommit.getId());
            }
        }
        if (!found) {
            System.out.println("Found no commit with that message.");
        }
    }

    /**
     * Create branch at current head.
     */
    public void branch(String branchName) {
        File branchHead = join(HEADS_DIR, branchName);
        if (branchHead.exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        Commit currentCommit = getHead();
        writeObject(branchHead, currentCommit);
    }

    /**
     * Remove the branch pointer
     * with the given branch name.
     */
    public void rmBranch(String branchName) {
        if (!checkBranchExists(branchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (checkIsCurrentBranch(branchName)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        File branchHeadFile = join(HEADS_DIR, branchName);
        branchHeadFile.delete();
    }

    /**
     * Checks out all the files tracked by the given commit.
     */
    public void reset(String commitId) {
        if (!checkCommitExists(commitId)) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit c = getCommit(commitId);
        resetHelper(c);
        writeCommitToBranch(c);
        emptyStagingArea();
    }

    public void status() {
        if (!GITLET_DIRS.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
        System.out.println("=== Branches ===");
        List<String> headsNames = plainFilenamesIn(HEADS_DIR);
        Collections.sort(headsNames);
        for (String headName : headsNames) {
            if (checkIsCurrentBranch(headName)) {
                System.out.print("*");
            }
            System.out.println(headName);
        }
        System.out.println();

        Stage stage = readObject(STAGE, Stage.class);
        System.out.println("=== Staged Files ===");
        Map<String, String> added = stage.getAdded();
        for (String key : added.keySet()) {
            System.out.println(key);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        Set<String> removed = stage.getRemoved();
        for (String key : removed) {
            System.out.println(key);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();

        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    public void merge(String branchName) {
        Stage stage = readObject(STAGE, Stage.class);
        if (!stage.empty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        if (!checkBranchExists(branchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (checkIsCurrentBranch(branchName)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        Commit currentCommit = getHead();
        Commit branchCommit = getBranchHead(branchName);
        if (checkUntrackedFiles(branchCommit)) {
            System.out.println("There is an untracked file in the way;"
                    + " delete it, or add and commit it first.");
            System.exit(0);
        }

        Commit splitPointCommit = getSplitPointCommit(currentCommit, branchCommit);

        if (splitPointCommit.getId().equals(branchCommit.getId())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
        if (splitPointCommit.getId().equals(currentCommit.getId())) {
            checkoutBranch(branchName);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }

        mergeHelper(branchName, currentCommit, branchCommit, splitPointCommit);

    }


    /**
     * Get the commit with the given commit id.
     */
    public Commit getCommit(String commitId) {
        File commitFile = join(COMMITS_DIR, commitId);
        return readObject(commitFile, Commit.class);
    }

    /**
     * Get the head commit of current branch.
     */
    public Commit getHead() {
        String branchName = readObject(HEAD, String.class);
        return getBranchHead(branchName);
    }

    /**
     * Get the head commit of branch with the given branch name
     */
    public Commit getBranchHead(String branchName) {
        File branchHeadFile = join(HEADS_DIR, branchName);
        Commit branchHead = readObject(branchHeadFile, Commit.class);
        return branchHead;
    }

    /**
     * Get the split point commit of two commits.
     */
    public Commit getSplitPointCommit(Commit a, Commit b) {
        Commit p = a;
        Commit q = b;
        Set<String> vis = new HashSet<>();
        Commit res = null;

        Queue<String> visQueue = new LinkedList<>();
        visQueue.add(p.getId());
        vis.add(p.getId());
        while (!visQueue.isEmpty()) {
            String temp = visQueue.poll();
            vis.add(temp);
            Commit tempCommit = getCommit(temp);
            if (tempCommit.getParents().isEmpty()) {
                break;
            }
            for (String parentId : tempCommit.getParents()) {
                Commit parentCommit = getCommit(parentId);
                visQueue.add(parentCommit.getId());
            }
        }

        Queue<String> searchQueue = new LinkedList<>();
        searchQueue.add(q.getId());
        while (!searchQueue.isEmpty()) {
            String temp = searchQueue.poll();
            Commit tempCommit = getCommit(temp);
            if (vis.contains(temp)) {
                return getCommit(temp);
            }
            for (String parentId : tempCommit.getParents()) {
                Commit parentCommit = getCommit(parentId);
                searchQueue.add(parentCommit.getId());
            }
        }

        return res;
    }

    public List<String> getAllFileNames(Commit a, Commit b, Commit c) {
        List<String> fileNames = new ArrayList<>();
        Map<String, String> aBlobs = a.getBlobs();
        Map<String, String> bBlobs = b.getBlobs();
        Map<String, String> cBlobs = c.getBlobs();
        Set<String> set = new HashSet<>();
        for (String fileName : aBlobs.keySet()) {
            set.add(fileName);
        }
        for (String fileName : bBlobs.keySet()) {
            set.add(fileName);
        }
        for (String fileName : cBlobs.keySet()) {
            set.add(fileName);
        }
        for (String fileName : set) {
            fileNames.add(fileName);
        }
        return fileNames;
    }

    /**
     * Write commit to the system.
     */
    public void writeCommitToFile(Commit c) {
        String uid = c.getId();
        File thisCommit = join(COMMITS_DIR, uid);
        writeObject(thisCommit, c);
    }

    /**
     * Write commit to the current branch head.
     */
    public void writeCommitToBranch(Commit c) {
        String currentBranchName = readObject(HEAD, String.class);
        File currentBranchHeadFile = join(HEADS_DIR, currentBranchName);
        writeObject(currentBranchHeadFile, c);
    }

    public void checkoutHelper(Commit head, String fileName) {
        String blobId = head.getBlobId(fileName);
        if (blobId == null) {
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

    public void resetHelper(Commit c) {
        Commit currentHeadCommit = getHead();
        Map<String, String> currentCommitMap = currentHeadCommit.getBlobs();
        Map<String, String> branchCommitMap = c.getBlobs();

        if (checkUntrackedFiles(c)) {
            System.out.println("There is an untracked file in the way;"
                    + " delete it, or add and commit it first.");
            System.exit(0);
        }

        /** Compares files in current head with the branch head,
         * takes it if cur don't have and branch have,
         * overwrites it if cur have and branch have,
         * deletes it if cur have and branch don't have.
         */
        for (String fileName : currentCommitMap.keySet()) {
            if (!branchCommitMap.containsKey(fileName)) {
                File checkoutFile = join(CWD, fileName);
                checkoutFile.delete();
            }
        }
        for (String fileName : branchCommitMap.keySet()) {
            checkoutHelper(c, fileName);// both take and overwrite
        }
    }

    public void mergeHelper(String branchName, Commit currentCommit,
                            Commit branchCommit, Commit splitPointCommit) {
        List<String> fileNames = getAllFileNames(currentCommit, branchCommit, splitPointCommit);
        Stage stage = readObject(STAGE, Stage.class);

        boolean conflictExists = false;

        for (String fileName : fileNames) {
            boolean inCurrentCommit = currentCommit.checkBlobExists(fileName);
            boolean inBranchCommit = branchCommit.checkBlobExists(fileName);
            boolean inSplitPointCommit = splitPointCommit.checkBlobExists(fileName);
            String currentBlobId = currentCommit.getBlobId(fileName);
            String branchBlobId = branchCommit.getBlobId(fileName);
            String splitPointBlobId = splitPointCommit.getBlobId(fileName);

            /**
             * Only exists in given branch commit.
             */
            if (!inSplitPointCommit
                    && !inCurrentCommit
                    && inBranchCommit) {
                checkoutHelper(branchCommit, fileName);
                stage.addFile(fileName,branchBlobId);
                continue;
            }

            /**
             * Only exists in current commit.
             */
            if (!inSplitPointCommit
                    && inCurrentCommit
                    && !inBranchCommit) {
                continue;
            }

            if (inSplitPointCommit
                    && inCurrentCommit
                    && splitPointBlobId.equals(currentBlobId)
                    && !inBranchCommit) {
                restrictedDelete(fileName);
                stage.removeFile(fileName);
                continue;
            }

            if (inSplitPointCommit
                    && inBranchCommit
                    && splitPointBlobId.equals(branchBlobId)
                    && !inCurrentCommit) {
                stage.removeFile(fileName);
                continue;
            }

            if (inSplitPointCommit
                    && inCurrentCommit
                    && inBranchCommit
                    && splitPointBlobId.equals(currentBlobId)
                    && !splitPointBlobId.equals(branchBlobId)) {
                checkoutHelper(branchCommit, fileName);
                stage.addFile(fileName,branchBlobId);
                continue;
            }

            if (inSplitPointCommit
                    && inCurrentCommit
                    && inBranchCommit
                    && !splitPointBlobId.equals(currentBlobId)
                    && splitPointBlobId.equals(branchBlobId)) {
                continue;
            }

            if (!inCurrentCommit
                    && !inBranchCommit) {
                continue;
            }

            if (inCurrentCommit
                    && inBranchCommit
                    && currentBlobId.equals(branchBlobId)) {
                continue;
            }
            /**
             * Exist in split point commit,
             * modified in current commit,
             * and modified in given branch commit in different ways,
             * conflict.
             */
            if (inSplitPointCommit
                    && inCurrentCommit
                    && inBranchCommit
                    && !splitPointBlobId.equals(currentBlobId)
                    && !splitPointBlobId.equals(branchBlobId)
                    && !currentBlobId.equals(branchBlobId)) {
                conflictExists = true;
                conflictHelper(currentCommit, branchCommit, fileName);
                continue;
            }

            /**
             * Exist in split point commit,
             * modified in one commit,
             * and deleted in another commit,
             * conflict.
             */
            if (inSplitPointCommit
                    && ((inCurrentCommit
                    && !currentBlobId.equals(splitPointBlobId)
                    && !inBranchCommit)
                    || (inBranchCommit
                    && !branchBlobId.equals(splitPointBlobId)
                    && !inCurrentCommit))) {
                conflictExists = true;
                conflictHelper(currentCommit, branchCommit, fileName);
                continue;
            }

            /**
             * Not exist in split point commit,
             * modified in current commit,
             * and modified in given branch commit in different ways,
             * conflict.
             */
            if (!inSplitPointCommit
                    && inCurrentCommit
                    && inBranchCommit
                    && !currentBlobId.equals(branchBlobId)) {
                conflictExists = true;
                conflictHelper(currentCommit, branchCommit, fileName);
                continue;
            }
        }

        if (stage.empty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        if (conflictExists) {
            System.out.println("Encountered a merge conflict.");
        }

        String currentBranchName = readObject(HEAD, String.class);
        String message = "Merged " + branchName + " into " + currentBranchName + ".";
        ArrayList<Commit> parents = new ArrayList<>();
        parents.add(currentCommit);
        parents.add(branchCommit);
        Commit commit = new Commit(message, parents, stage);
        writeCommitToFile(commit);
        writeCommitToBranch(commit);

        moveStagingToBlob();
        emptyStagingArea();
    }

    /**
     *
     */
    public void conflictHelper(Commit currentCommit, Commit branchCommit, String fileName) {
        Map<String, String> currentCommitBlobs = currentCommit.getBlobs();
        Map<String, String> branchCommitBlobs = branchCommit.getBlobs();
        File cwdFile = join(CWD, fileName);
        boolean inCurrentBlobs = currentCommitBlobs.containsKey(fileName);
        boolean inBranchBlobs = branchCommitBlobs.containsKey(fileName);
        String currentBlobContent = "";
        String branchBlobContent = "";
        if (inCurrentBlobs) {
            checkoutHelper(currentCommit, fileName);
            String currentBlobId = currentCommitBlobs.get(fileName);
            File currentBlobFile = join(BLOBS_DIR, currentBlobId);
            Blob currentBlob = readObject(currentBlobFile, Blob.class);
            byte[] currentBlobSerializedContent = currentBlob.getContent();
            writeContents(cwdFile, currentBlobSerializedContent);
            currentBlobContent = readObject(cwdFile, String.class);
        }
        if (inBranchBlobs) {
            checkoutHelper(branchCommit, fileName);
            String branchBlobId = branchCommitBlobs.get(fileName);
            File branchBlobFile = join(BLOBS_DIR, branchBlobId);
            Blob branchBlob = readObject(branchBlobFile, Blob.class);
            byte[] branchBlobSerializedContent = branchBlob.getContent();
            writeContents(cwdFile, branchBlobSerializedContent);
            branchBlobContent = readObject(cwdFile, String.class);
        }
        String conflictContent = "<<<<<<< HEAD\n"
                + currentBlobContent
                + "=======\n"
                + branchBlobContent
                + ">>>>>>>";
        writeContents(cwdFile, conflictContent);
        Blob conflictBlob = new Blob(cwdFile);
        currentCommit.addBlob(conflictBlob);
    }

    public void printLog(Commit commit) {
        String uid = commit.getId();
        String timestamp = commit.getFormattedTimestamp();
        String message = commit.getMessage();
        System.out.println("===");
        System.out.println("commit " + uid);
        if (commit.getParents().size() == 2) {
            String firstParent = commit.getParents().get(0);
            String secondParent = commit.getParents().get(1);
            String firstParentId = firstParent.substring(0, 7);
            String secondParentId = secondParent.substring(0, 7);
            System.out.println("Merge: "
                    + firstParentId
                    + " "
                    + secondParentId);
        }
        System.out.println("Date: " + timestamp);
        System.out.println(message);
        System.out.println();
    }

    /**
     * Check if the commit with the given id exists.
     */
    public boolean checkCommitExists(String commitId) {
        File commitFile = join(COMMITS_DIR, commitId);
        if (commitFile.exists()) {
            return true;
        }
        return false;
    }

    /**
     * Check if the branch with the given name exist.
     */
    public boolean checkBranchExists(String branchName) {
        File currentBranchHeadFile = join(HEADS_DIR, branchName);
        if (!currentBranchHeadFile.exists()) {
            return false;
        }
        return true;
    }

    /**
     * Check if the input branch name is current branch.
     */
    public boolean checkIsCurrentBranch(String branchName) {
        String currentBranchName = readObject(HEAD, String.class);
        if (currentBranchName.equals(branchName)) {
            return true;
        }
        return false;
    }

    /**
     * Check If a working file is untracked in the
     * current branch and would be overwritten
     * by the checkout.
     */
    public boolean checkUntrackedFiles(Commit commit) {
        Commit currentHeadCommit = getHead();
        Map<String, String> currentCommitMap = currentHeadCommit.getBlobs();
        Map<String, String> branchCommitMap = commit.getBlobs();

        List<String> cwdFileNames = plainFilenamesIn(CWD);
        for (String cwdFileName : cwdFileNames) {
            if (!currentCommitMap.containsKey(cwdFileName)
                    && branchCommitMap.containsKey(cwdFileName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Move everything in staging area to BLOB_DIR.
     */
    public void moveStagingToBlob() {
        List<String> blobIds = plainFilenamesIn(STAGING_DIR);
        List<File> blobFiles = new ArrayList<>();

        if (blobIds != null) {
            for (String blobId : blobIds) {
                File blobFile = join(STAGING_DIR, blobId);
                blobFiles.add(blobFile);
            }
        }

        for (File blobFile : blobFiles) {
            Blob blob = readObject(blobFile, Blob.class);
            File commitBlob = join(BLOBS_DIR, blob.getBlobId());
            writeObject(commitBlob, blob);
        }
    }

    /**
     * Empty the staging area.
     */
    public void emptyStagingArea() {
        List<String> blobIds = plainFilenamesIn(STAGING_DIR);
        List<File> blobFiles = new ArrayList<>();

        if (blobIds != null) {
            for (String blobId : blobIds) {
                File blobFile = join(STAGING_DIR, blobId);
                blobFiles.add(blobFile);
            }
        }

        for (File blobFile : blobFiles) {
            blobFile.delete();
        }
        writeObject(STAGE, new Stage());
    }


}
