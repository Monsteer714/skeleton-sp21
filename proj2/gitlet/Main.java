package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if(args.length < 1){
            System.out.println("Please enter a command");
            System.exit(0);
        }
        String firstArg = args[0];
        Repository repo = new Repository();
        switch(firstArg) {
            case "init":
                repo.init();
                break;
            case "add":
                String fileName = args[1];
                repo.add(fileName);
                break;
            case "commit":
                if(args.length < 2){
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                String message = args[1];
                repo.commit(message);
                break;
            case "checkout":
                if(args.length < 2 || args.length > 4) {
                    break;
                } else if(args.length == 2) {
                    repo.checkoutBranch(args[1]);
                } else if(args.length == 3) {
                    repo.checkoutFile(args[2]);
                } else if(args.length == 4) {
                    repo.checkoutFile(args[1], args[3]);
                }
                break;
            case "log":
                repo.log();
                break;
            case "rm":
                repo.rm(args[1]);
                break;
            case "global-log":
                repo.globalLog();
                break;
            case "find":
                String commitMessage = args[1];
                repo.find(commitMessage);
                break;
            case "branch":
                String branchName = args[1];
                repo.branch(branchName);
                break;
            case "rm-branch":
                branchName = args[1];
                repo.rmBranch(branchName);
                break;

        }
    }
}
