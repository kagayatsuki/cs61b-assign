package gitlet;

import java.io.File;
import java.util.*;

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
    public static final int UID_LENGTH =40;
    /* TODO: fill in the rest of this class. */
    static void init(){
        if(GITLET_DIR.exists()&&GITLET_DIR.isDirectory()){
            System.out.println("A Gitlet version-control system already exists in the current directory.");
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
        //创建文件并写入文件夹
        //将文件内容序列化存储到 .gitlet/blobs/<SHA-1 ID>
        Blob blob=new Blob(fileName,CWD);//本地文件
        String blobId=blob.getId();
        File blobFile = join(BLOBS_DIR, blobId);
        if(!blobFile.exists()){
            writeObject(blobFile, blob);
        }
        //将文件名和 blob SHA-1 ID 添加到暂存区的 `addedFiles` ,如果文件已暂存，覆盖之前的暂存内容
        Commit head=getHeadCommit();
        Stage stage = readObject(STAGING_FILE,Stage.class);
        String headBlobId=head.getBlobs().getOrDefault(fileName,"");
        //如果文件内容与当前提交（HEAD 指向的提交）中的版本相同，移除其暂存状态（从 `addedFiles` 和 `removedFiles` 中移除）
        if (headBlobId != null && headBlobId.equals(blobId)) {
            // 从暂存区移除
            stage.getAddedFiles().remove(fileName);
            stage.getRemovedFiles().remove(fileName);
        } else {
            // 否则，把新 blob 添加到暂存区
            stage.addFile(fileName, blobId);
            // 如果之前标记为删除，则取消删除状态
            stage.getRemovedFiles().remove(fileName);
        }
            writeObject(STAGING_FILE,stage);
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
    Commit head=getHeadCommit();
    List<Commit> parents=new ArrayList<>();
    parents.add(head);
    Stage stage=readObject(STAGING_FILE,Stage.class);
    if(stage.isEmpty()){
        System.out.println("No changes added to the commit.");
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
        File removedFile=join(CWD,fileName);

        Commit headCommit=getHeadCommit();
        Stage stage=readObject(STAGING_FILE,Stage.class);
        //首先检查文件
        boolean stagedForAdd = stage.getAddedFiles().containsKey(fileName);
        boolean trackedInHead = headCommit.getBlobs().containsKey(fileName);
        if(!stagedForAdd && !trackedInHead){
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        //先获取再移除
        if(stagedForAdd){
            stage.getAddedFiles().remove(fileName);
        }
        //区别是下面的会把removedfiles添加，上面的不会
        if(trackedInHead){
            stage.removeFile(fileName);
            if(removedFile.exists()){
                Utils.restrictedDelete(removedFile);
            }//安全删除
        }
        writeObject(STAGING_FILE,stage);

    }
    public static void log(){
        StringBuilder log=new StringBuilder();
        Commit headCommit=getHeadCommit();
        while(headCommit!=null){
            log.append(headCommit.getCommitAsString());
            if (headCommit.getParents() == null || headCommit.getParents().isEmpty()) {
                break;
            }
            headCommit=getCommitFromId(headCommit.getFirstParentId());
        }
        System.out.println(log);
    }
    //和上面的逻辑一样
    public void globalLog(){
        StringBuilder log=new StringBuilder();
        List<String>files=plainFilenamesIn(COMMITS_DIR);
        for(String fileName:files){
            Commit commit=getCommitFromId(fileName);
            log.append(commit.getCommitAsString());
        }
        System.out.println(log);
    }


    //checkout -- [file name]
    public void checkoutFileFromHead(String fileName) {
        Commit headCommit=getHeadCommit();
        writeBlobFromCommit(fileName, headCommit);
    }
    //checkout [commit id] -- [file name]
    public void checkoutFileFromCommitId(String commitId, String fileName) {
        commitId=getFullCommitId(commitId);
        Commit headCommit=getCommitFromId(commitId);
        if(headCommit==null){
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        writeBlobFromCommit(fileName, headCommit);
    }

    private void writeBlobFromCommit(String fileName, Commit headCommit) {
        String blobId=headCommit.getBlobs().getOrDefault(fileName,"");
        if(blobId.equals("")){
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        Blob blob=getBlobFromId(blobId);
        File file=join(CWD,blob.getFileName());
        writeContents(file,blob.getContent());
    }

    //checkout [branch name]
    public void checkoutBranch(String branchName) {
        File BranchFile=join(BRANCHES_DIR,branchName);
        if(!BranchFile.exists()){
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        String currentBranch=readContentsAsString(HEAD_FILE);

        if(branchName.equals(currentBranch)){
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        //两个分支
        String commitId=readContentsAsString(join(BRANCHES_DIR,currentBranch));
        Commit currentCommit=getCommitFromId(commitId);
        String targetCommitId=readContentsAsString(BranchFile);
        selectFilesAndDelete(targetCommitId, currentCommit);
        //切换分支
        writeContents(HEAD_FILE,branchName);
    }

    public void branch(String branchName) {
        File branchFile=join(BRANCHES_DIR,branchName);
        if(branchFile.exists()){
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        String currentBranch=readContentsAsString(HEAD_FILE);
        File currentBranchFile=join(BRANCHES_DIR,currentBranch);
        String headCommitId=readContentsAsString(currentBranchFile);
        writeContents(branchFile,headCommitId);
    }
    public void reBranch(String branchName) {
        File branchFile=join(BRANCHES_DIR,branchName);
        if (!branchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        String headCommitName=readContentsAsString(HEAD_FILE);
        if(headCommitName.equals(branchName)){
            System.out.println("Cannot remove the current branch.");
        }
        branchFile.delete();
    }
    public void reset(String commitId) {
        commitId=getFullCommitId(commitId);
        File file=join(COMMITS_DIR,commitId);
        if(!file.exists()){
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        //branch->commit
        String currentBranch=readContentsAsString(HEAD_FILE);
        Commit currentCommit=getCommitFromBranch(currentBranch);
        selectFilesAndDelete(commitId, currentCommit);
        //注意这里和上面不一样的
        File branchFile=join(BRANCHES_DIR,currentBranch);
        writeContents(branchFile,commitId);

    }
    //未跟踪的文件：1.存在在CWD里面。2.文件不在当前提交或stage区域
    private List<String> getUntrackedFiles(){
        List<String> untrackedFiles=new ArrayList<>();
        List<String> stageFiles=readObject(STAGING_FILE,Stage.class).getStagedFilename();
        Set<String> headFileLists=getHeadCommit().getBlobs().keySet();
        for(String headFile:plainFilenamesIn(CWD)){
            if(!headFileLists.contains(headFile)&&stageFiles.contains(headFile)){
                untrackedFiles.add(headFile);
            }
        }
        Collections.sort(untrackedFiles);
        return untrackedFiles;
    }
    private void selectFilesAndDelete(String commitId, Commit currentCommit) {
        commitId = getFullCommitId(commitId);
        Commit targetCommit = getCommitFromId(commitId);
        //最难的一步，检查文件，使用map,如果原来的目录里面有新目录没有的文件，抛出错误
        List<String> cwdFiles = plainFilenamesIn(CWD);
        Map<String,String> currBlobs = currentCommit.getBlobs();
        Map<String,String> targetBlobs = targetCommit.getBlobs();
        for(String cwdFile:cwdFiles){
            if(!currBlobs.containsKey(cwdFile)&&targetBlobs.containsKey(cwdFile)){
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        //写入目标文件，注意blobId的处理以及加入的判断条件
        for(String file:targetBlobs.keySet()){
            String blobId = targetBlobs.get(file);
            Blob blob = getBlobFromId(blobId);
            File file1 = join(CWD,file);
            if(file1.exists()) {
                file1.delete();
            }
            if (blob != null) {
                writeContents(file1, (Object) blob.getContent());
            }
        }
        //删除当前分支跟踪但目标分支不跟踪的文件
        for(String file:currBlobs.keySet()){
            if(!targetBlobs.containsKey(file)){
                File file1 = join(CWD,file);
                if(file1.exists()){
                    file1.delete();
                }
            }
        }
        //清除暂存区，记住！！！
        Stage stage = new Stage();
        writeObject(STAGING_FILE,stage);
    }

    public void find(String message){
        StringBuilder sb = new StringBuilder();
        List<String> Files = plainFilenamesIn(COMMITS_DIR);
        for(String file:Files){
            Commit commit = getCommitFromId(file);
            if(commit.getMessage().contains(message)){
                sb.append(commit.getId()+"\n");
            }
        }
        if(sb.length() == 0) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
        System.out.println(sb);
    }

    public void status() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Branches ===\n");
        String headBranch = readContentsAsString(HEAD_FILE);
        //先遍历，打印
        List<String> branches = plainFilenamesIn(BRANCHES_DIR);
        Collections.sort(branches);
        for(String branch:branches){
            if(branch.equals(headBranch)){
                sb.append("*"+branch+"\n");
            }
            else {
                sb.append(branch+"\n");
            }
        }
        sb.append("\n");
        //    private Map<String, String> addedFiles; 文件名 -> blob SHA-1 ID，一一对应
        //    private List<String> removedFiles;
        sb.append("=== Staged Files ===\n");
        Stage stage = readObject(STAGING_FILE,Stage.class);
        //注意这里要按字典序
        List<String>files = new ArrayList<>(stage.getAddedFiles().keySet());
        Collections.sort(files);
        for(String file:files){
            sb.append(file+"\n");
        }
        sb.append("\n");

        sb.append("=== Removed Files ===\n");
        List<String>result = stage.getRemovedFiles();
        Collections.sort(result);
        for (String file:result){
            sb.append(file+"\n");
        }
        sb.append("\n");
        //跟踪但被修改或删除（即当前提交里有但内容不同或已删除，且未暂存）
        sb.append("=== Modifications Not Staged For Commit ===\n");
        List<String>cwdFiles = plainFilenamesIn(CWD);
        List<String> targetFiles = new ArrayList<>();
        //当前commit
        Commit currentCommit = getCommitFromId(readContentsAsString(join(BRANCHES_DIR,headBranch)));
        Map<String,String> currBlobs = currentCommit.getBlobs();
        List<String> untrackedFiles = new ArrayList<>();//result2
        Set<String> stagedAddFiles  =  stage.getAddedFiles().keySet();
        List<String> stagedRmFiles = stage.getRemovedFiles();
        List<String> modifiedFiles = new ArrayList<>();//result
        List<String> stagedFiles = stage.getStagedFilename();
        Set<String> headFiles = getHeadCommit().getBlobs().keySet();
        Set<String>allFiles =new HashSet<>();
        allFiles.addAll(headFiles);
        allFiles.addAll(stagedFiles);
        allFiles.addAll(cwdFiles);
        for(String file:allFiles) {
            if(cwdFiles.contains(file)){
                if(stagedFiles.contains(file)||(headFiles.contains(file)&&!stagedFiles.contains(file))){
                    modifiedFiles.add(file+" (deleted)");
                }
            }else {
                //use three blobIds
                String blobId = new Blob(file,CWD).getId();
                String sBlobId=stage.getAddedFiles().getOrDefault(file,"");
                String hBlobId=currentCommit.getBlobs().getOrDefault(file,"");
                if(hBlobId != "" && sBlobId == "" && !blobId.equals(hBlobId) || (sBlobId != "" && !blobId.equals(sBlobId))) {
                    modifiedFiles.add(file+" (modified)");
                }
            }
        }
            Collections.sort(targetFiles);
            for (String s : targetFiles) {
                sb.append(s+"\n");
            }
            sb.append("\n");
            //工作目录中存在但未跟踪（不在当前提交或暂存区）的文件
        sb.append("=== Untracked Files ===\n");

        for (String file : cwdFiles) {
            if (!stagedFiles.contains(file)&&!headFiles.contains(file)) {
                untrackedFiles.add(file);
            }
        }
        Collections.sort(untrackedFiles);
        for (String s:untrackedFiles){
            sb.append(s+"\n");
        }
        sb.append("\n");

        System.out.println(sb);
    }

    public void merge(String branchName){
        Stage stage=readObject(STAGING_FILE,Stage.class);
        //invalid
        if(!stage.isEmpty()){
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        File branchFile=join(BRANCHES_DIR,branchName);
        if(!branchFile.exists()){
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        String headBranchName=readContentsAsString(HEAD_FILE);
        if(headBranchName.equals(branchName)){
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        //find split point
        String currCommitId=getCommitFromBranch(headBranchName).getId();
        String targetCommitId=getCommitFromBranch(branchName).getId();
        String splitPoint=findSplitPointDfs(currCommitId,targetCommitId);
        //split point 是当前分支
        if(splitPoint.equals(currCommitId)){
            checkoutBranch(branchName);
            System.out.println("Current branch fast-forwarded.");
            return;
        }
        //split point 是目标分支
        if (splitPoint.equals(targetCommitId)){
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        //check untracked
        List<String>untrackedFiles = getUntrackedFiles();
        List<String> cwdFiles = plainFilenamesIn(CWD);
        Set<String> targetFiles = getCommitFromBranch(branchName).getBlobs().keySet();
        for(String file:cwdFiles){
            if(untrackedFiles.contains(file)&&!targetFiles.contains(file)){
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                return;
            }
        }
        mergeWithLCA(splitPoint,targetCommitId,currCommitId,headBranchName,branchName);

    }
    /**
    * Below are some functions assist me in programming.
    * */
    private void mergeWithLCA(String splitPoint, String targetCommitId,String currCommitId,String curBranch,String targetBranch){
        //get all the files first
        Commit splitCommit=getCommitFromId(splitPoint);
        Commit targetCommit=getCommitFromId(targetCommitId);
        Commit currCommit=getCommitFromId(currCommitId);
        Set<String> allFiles =new HashSet<>();
        boolean hasConflict=false;
        allFiles.addAll(splitCommit.getBlobs().keySet());
        allFiles.addAll(targetCommit.getBlobs().keySet());
        allFiles.addAll(currCommit.getBlobs().keySet());

        for(String file:allFiles){
            String splitBlobId=splitCommit.getBlobs().get(file);
            String targetBlobId=targetCommit.getBlobs().get(file);
            String currBlobId=currCommit.getBlobs().get(file);
            //判断三个状态
            boolean inSplit=(splitBlobId!=null);
            boolean inTarget=(targetBlobId!=null);
            boolean inCurr=(currBlobId!=null);
            //获得三个存储
            Blob splitBlob=(splitBlobId==null?null:getBlobFromId(splitBlobId));
            Blob targetBlob=(targetBlobId==null?null:getBlobFromId(targetBlobId));
            Blob currBlob=(currBlobId==null?null:getBlobFromId(currBlobId));
            //1.目标分支修改、当前分支未修改的文件：检出并暂存,判断是否修改即判断blobId是否相同
            if(inSplit&&splitBlobId.equals(currBlobId)&&!splitBlobId.equals(targetBlobId)&&inTarget){
                File fileObj=join(CWD, file);
                writeContents(fileObj,targetBlob.getContent());
                add(file);
            }
            //2.当前分支修改、目标分支未修改的文件：保持不变
            else if(inSplit&&!splitBlobId.equals(currBlobId)&&splitBlobId.equals(targetBlobId)&&inCurr){
                continue;
            }
            //3.两分支相同修改：保持不变
            else if(inSplit&&inCurr&&inTarget&&!splitBlobId.equals(currBlobId)&&!splitBlobId.equals(targetBlobId)&&currBlobId.equals(targetBlobId)){
                continue;
            }
            //4.仅当前分支有新文件：保持不变
            else if(!inTarget&&inCurr&&!inSplit){
                continue;
            }
            //5.仅目标分支有新文件：检出并暂存
            else if(inTarget&&!inSplit&&!inCurr){
                File fileObj=join(CWD, file);
                writeContents(fileObj,targetBlob.getContent());
                add(file);
            }
            //6.split point 存在、当前分支未修改、目标分支移除：移除并取消跟踪
            else if (inSplit&&splitBlobId.equals(currBlobId)&&!inTarget){
                remove(file);
            }
            //7.split point 存在、目标分支未修改、当前分支移除：保持移除
            else if (inSplit&&splitBlobId.equals(targetBlobId)&&!inCurr){
                continue;
            }
            //8.两分支不同修改（包括删除）：生成冲突文件
            else if (inSplit&&!splitBlobId.equals(currBlobId)&&!splitBlobId.equals(targetBlobId)&&!currBlobId.equals(targetBlobId)) {
                hasConflict = true;
                File conflictFile = join(CWD, file);
                StringBuilder conflictFileContent = new StringBuilder();
                conflictFileContent.append("<<<<<<< HEAD\n");
                if (currBlob != null) {
                    conflictFileContent.append(currBlob.getContentAsString());
                }
                conflictFileContent.append("=======\n");
                if (targetBlob != null) {
                    conflictFileContent.append(targetBlob.getContentAsString());
                }
                conflictFileContent.append(">>>>>>>\n");
                writeContents(conflictFile, conflictFileContent.toString().getBytes());
                add(file);
            }
        }
        //创建合并提交
        String mergeMessage="Merged "+targetBranch+" into "+curBranch+".";
        List<Commit> mergedCommits=new ArrayList<>();
        mergedCommits.add(getCommitFromBranch(curBranch));
        mergedCommits.add(getCommitFromBranch(targetBranch));
        Commit mergedCommit=new Commit(mergeMessage,mergedCommits,readObject(STAGING_FILE,Stage.class));
        if(hasConflict){
            System.out.println("Encountered a merge conflict.");
        }
        writeCommitToFile(mergedCommit);

        //更新指针
        File currBranchFile=join(BRANCHES_DIR,curBranch);
        writeContents(currBranchFile,mergedCommit.getId());
        Stage stage=new Stage();
        writeObject(STAGING_FILE,stage);



    }
    private void dfs(String commitId,Set<String>ancestors){
        //如果回溯到了尽头（即已经到达自身）
        if(ancestors.contains(commitId)||commitId==null){
            return;
        }
        ancestors.add(commitId);
        Commit commit=getCommitFromId(commitId);
        for(String parent:commit.getParents()){
            dfs(parent,ancestors);
        }//一个或者两个的情况

    }

    private Set<String> getAncestors(String commitId){
        Set<String> ancestors=new HashSet<>();
        dfs(commitId,ancestors);
        return ancestors;
    }

    private String findSplitPointDfs(String commitIdA,String commitIdB){
        Set<String> ancestorsA=getAncestors(commitIdA);
        return dfsFind(commitIdB,ancestorsA);
    }
    private String dfsFind(String commitId,Set<String>targetSet){
         if(commitId==null){
             return null;
         }
         if(targetSet.contains(commitId)){
             return commitId;
         }
         Commit commit=getCommitFromId(commitId);
         for(String parent:commit.getParents()){
             String result = dfsFind(parent,targetSet);
             if(result!=null){
                 return result;
             }
         }
         return null;
    }
    private static Commit getCommitFromBranch(String branchName){
        File branchFile=join(BRANCHES_DIR,branchName);
        if(!branchFile.exists()){
            System.out.println("No branch with that id exists.");
            System.exit(0);
        }
        String commitId=readContentsAsString(branchFile);
        File commitFile=join(COMMITS_DIR,commitId);
        if(!commitFile.exists()){
            System.out.println("No commit with that id does not exist.");
            System.exit(0);
        }
        return readObject(commitFile,Commit.class);

    }
    private static void writeCommitToFile(Commit commit) {
        File file = join(COMMITS_DIR, commit.getId());
        writeObject(file, commit);
    }
    private static Commit getHeadCommit() {
        String branchName=readContentsAsString(HEAD_FILE);
        return getCommitFromBranch(branchName);
    }
    private static Commit getCommitFromId(String commitId){
        File commitFile=join(COMMITS_DIR,commitId);
        if(!commitFile.exists()||commitId==null){
            return null;
        }
        return readObject(commitFile,Commit.class);
    }
    private static Blob getBlobFromId(String blobId){
        File blobFile=join(BLOBS_DIR,blobId);
        if(!blobFile.exists()||blobId==null){
            return null;
        }
        return readObject(blobFile,Blob.class);
    }
    public void checkEqual(String arg, String s) {
        if (!s.equals(arg)) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    private String getFullCommitId(String commitId){
        if(commitId.length()==UID_LENGTH){
            return commitId;
        }
        //注意这里要先把目录序列提出来，而不是在下面for-each里面使用commitsFile.list()
        String[] files = COMMITS_DIR.list();
        if(files==null){
            System.out.println("No commit files found.");
            System.exit(0);
        }
        //File commitFile=join(COMMITS_DIR,commitId);
        String fullCommitId=null;
        int count=0;
        for(String fileName : files){
            if(fileName.startsWith(commitId)){
                fullCommitId=fileName;
                count++;
            }
        }
        if(count==0){
            throw new IllegalArgumentException("No commit with that id exists.");
        }
        if(count>=2){
            throw new IllegalArgumentException("Too many commit ids.");
        }
        return fullCommitId;
    }
    public void checkCommand(int expect,int length) {
        if(expect!=length){
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
    public boolean checkIsInit(){
        File init=join(CWD,".gitlet");
        return init.exists()&&init.isDirectory();
    }
    public void checkInit(){
        if(!checkIsInit()){
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

}
