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
                repo.checkCommand(args.length,2);
                add(args[1]);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                repo.checkCommand(args.length,2);
                commit(args[1]);
                break;
            case "rm":
                repo.checkCommand(args.length,2);
                remove(args[1]);
                break;
            case "log":
                repo.checkCommand(args.length,1);
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
            case "branch":
                repo.checkCommand(args.length,2);
                repo.branch(args[1]);
                break;
            case "rm-branch":
                repo.checkCommand(args.length,2);
                repo.reBranch(args[1]);
                break;
            case "reset":
                repo.checkCommand(args.length,2);
                repo.reset(args[1]);
                break;
            case "global-log":
                repo.checkCommand(args.length,1);
                repo.globalLog();
                break;
            case "find":
                repo.checkCommand(args.length,2);
                repo.find(args[1]);
                break;
            case "status":
                repo.checkCommand(args.length,1);
                repo.status();
                break;
            case "merge":
                repo.checkCommand(args.length,2);
                repo.merge(args[1]);
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }
}

