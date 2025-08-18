package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");//我们的目录
    public static final File COMMITS_DIR = new File(GITLET_DIR, "commits");
    public static final File BLOBS_DIR = new File(GITLET_DIR, "blobs");//保存的文件内容
    public static final File BRANCHES_DIR = new File(GITLET_DIR, "branches");
    public static final File STAGING_FILE = join(GITLET_DIR, "staging");
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");
    public static File STAGE= join(GITLET_DIR, "stage");
    /* TODO: fill in the rest of this class. */
    static void init(){
        if(GITLET_DIR.exists()&&GITLET_DIR.isDirectory()){
            System.out.println("GITLET DIR already exists!");
            System.exit(0);
        }

        GITLET_DIR.mkdir();
        BLOBS_DIR.mkdir();
        BRANCHES_DIR.mkdir();
        COMMITS_DIR.mkdir();
        //new File(GITLET_DIR,"staging").mkdir();
       // File stagingDir=join(GITLET_DIR,"staging");
        //stagingDir.mkdir();

        writeObject(STAGING_FILE,new Stage());

        Commit initialCommit = new Commit();
        writeCommitToFile(initialCommit);
        String comId = initialCommit.getId();

        File commitFile = join(COMMITS_DIR, comId);
        writeObject(commitFile,initialCommit);
        File branchFile = join(BRANCHES_DIR, "master");
        writeContents(branchFile,comId);
        writeContents(HEAD_FILE,"master");//更新master到HEAD

    }
    static void add(String fileName){
        File file=join(CWD,fileName);
        if(!file.exists()){
            System.out.println("File does not exist");
            System.exit(0);
        }
        //创建文件
        Blob blob=new Blob(fileName,CWD);
        String blobId=blob.getId();

        Stage stage = new Stage();
        if(STAGING_FILE.exists()){
            stage=readObject(STAGING_FILE,Stage.class);
        }
        else {
            stage=new Stage();
        }

        if (!HEAD_FILE.exists()) {
            stage=new Stage();
        }
        String headBranch=readContentsAsString(HEAD_FILE);
        File branchFile = join(BRANCHES_DIR,headBranch);
        if (!branchFile.exists()) {
            System.out.println("Branch file not found: " + headBranch);
            System.exit(0);
        }

        String commitId=readContentsAsString(branchFile);
        Commit headCommit =readObject(join(COMMITS_DIR,commitId),Commit.class);//读取提交文件

        String headBlobId=headCommit.getBlobs().getOrDefault(fileName,"");
        String stageId=stage.getAddedFiles().getOrDefault(fileName,"");

        //检查文件
        if (blobId.equals(headBlobId)) {
            if (!blobId.equals(stageId)) {
                stage.removeFile(fileName);
                writeObject(STAGING_FILE, stage);
            }
        } else if (!blobId.equals(stageId)) {
            stage.addFile(fileName, blobId);
            writeObject(STAGING_FILE, stage);
        }

    }
    public static void commit(String message){
    if (Objects.equals(message, "")){
        System.out.println("Please enter a commit message.");
        System.exit(0);
    }
    File stagingFile=join(STAGING_FILE);
    if(!stagingFile.exists()){
        System.out.println("File does not exist");
        System.exit(0);
    }
    Commit head=getHead();
    List<Commit> parents=new ArrayList<>();
    parents.add(head);
    Stage stage=readObject(STAGING_FILE,Stage.class);
    if(stage.isEmpty()){
        System.out.println("Stage is empty");
        System.exit(0);
    }
    Commit newCommit=new Commit(message,parents,stage);
    writeCommitToFile(newCommit);
    //更新当前分支
    String headBranch=readContentsAsString(HEAD_FILE);
    File branchFile = join(BRANCHES_DIR,headBranch);
    writeContents(branchFile,newCommit.getId());
    //清空暂存区
    stage=new Stage();
    writeObject(STAGING_FILE,stage);
    }
    public static void remove(String fileName){

    }
    private static void writeCommitToFile(Commit commit) {
        File file = join(COMMITS_DIR, commit.getId());
        writeObject(file, commit);
    }
    private static Commit getHead(){
        String branchName=readContentsAsString(HEAD_FILE);
        if(!HEAD_FILE.exists()){
            System.out.println("File does not exist: " + HEAD_FILE);
            System.exit(0);
        }

        File branchFile = join(BRANCHES_DIR,branchName);
        if (!branchFile.exists()) {
            System.out.println("Branch file not found: " + branchName);
            System.exit(0);
        }
        String commitId=readContentsAsString(branchFile);
        File commitFile=join(COMMITS_DIR,commitId);
        if (!commitFile.exists()) {
            System.out.println("Commit file not found: " + commitId);
            System.exit(0);
        }

        return readObject(commitFile,Commit.class);
    }
}
