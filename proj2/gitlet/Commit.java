package gitlet;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.sha1;

public class Commit implements Serializable {

    /** The message of this Commit. */
    private String message;
    private Date timestamp;
    private List<String> parents;
    private Map<String, String> blobs; // 文件快照
    private String id;

    public Commit() {
        this.message = "initial commit";
        this.timestamp = new Date(0);
        this.parents = new LinkedList<>();
        this.blobs = new HashMap<String, String>();
        this.id = sha1(message, timestamp.toString()); // 生成哈希
    }

    public Commit(String message, List<Commit> parents, Stage stage) {
        this.message = message;
        this.timestamp = new Date(); // 当前时间
        this.parents = new ArrayList<>(2);

        for (Commit p : parents) {
            this.parents.add(p.getId());
        }

        this.blobs = parents.get(0).getBlobs();

        for (Map.Entry<String, String> item : stage.getAddedFiles().entrySet()) {
            String filename = item.getKey();
            String blobId = item.getValue();
            blobs.put(filename, blobId);
        }

        for (String fileName : stage.getRemovedFiles()) {
            blobs.remove(fileName);
        }

        this.id = sha1(message, timestamp.toString(), parents.toString(), blobs.toString());
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getFirstParentId() {
        if (parents.isEmpty()) {
            return null;
        }
        return parents.get(0);
    }

    public List<String> getParents() {
        return parents;
    }

    public Map<String, String> getBlobs() {
        return blobs;
    }

    public String getDateString() {
        // Thu Nov 9 20:00:05 2017 -0800
        DateFormat df = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        return df.format(timestamp);
    } // use this format

    public String getCommitAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append("===\n");
        sb.append("commit ").append(this.id).append("\n");

        if (parents.size() == 2) {
            sb.append("Merge: ")
                    .append(parents.get(0).substring(0, 7))
                    .append(" ")
                    .append(parents.get(1).substring(0, 7))
                    .append("\n");
        } // 合并提交的情况

        sb.append("Date: ").append(this.getDateString()).append("\n");
        sb.append(this.getMessage()).append("\n\n");
        return sb.toString();
    }
}
