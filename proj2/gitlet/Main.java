package gitlet;
import java.io.File;
import java.util.*;

import static gitlet.Repository.*;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {
private static final Set<String> Operands=new HashSet<>(Set.of( "init", "add", "commit", "rm", "log", "global-log", "find",
        "status", "checkout", "branch", "rm-branch", "reset", "merge"));
    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?

        if(args.length==0){
            System.out.println(" Please enter a command.");
            System.exit(0);
        }
        Repository repo = new Repository();
        String firstArg = args[0];
        if(!Operands.contains(firstArg)){
            System.out.println("No command with that name exists.");
            System.exit(0);
        }
        //如果用户输入的作数数量或格式错误的命令
        //如果用户输入的命令需要位于初始化的 Gitlet 工作目录（即包含 .gitlet 子目录的目录）中，但不在该目录中
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                init();
                break;
            case "add":
                if (args.length < 2) {
                    System.out.println("Please specify a file to add.");
                    System.exit(0);}
                add(args[1]);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                if (args.length < 2) {
                    System.out.println("Please specify a file to commit.");
                    System.exit(0);

                }
                commit(args[1]);
                break;
            case "rm":
                if (args.length < 2) {
                    System.out.println("Please specify a file to remove.");
                    System.exit(0);
                    }
                remove(args[1]);
                break;
            case "log":
                log();
                break;
            case "checkout":
                if (args.length < 2||args.length > 4) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }//2-4
                if (args.length == 2) {
                    // java gitlet.Main checkout [branch name]
                    repo.checkoutBranch(args[1]);
                } else if (args.length == 3) {
                    // java gitlet.Main checkout -- [file name]
                    repo.checkEqual(args[1], "--");
                    repo.checkoutFileFromHead(args[2]);
                } else if (args.length== 4) {
                    // java gitlet.Main checkout [commit id] -- [file name]
                    repo.checkEqual(args[2], "--");
                    repo.checkoutFileFromCommitId(args[1], args[3]);
                }
                break;
            default:
                System.out.println("Unknown command.");
                System.exit(0);
        }
    }
}

