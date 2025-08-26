package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Stage implements Serializable {
    private Map<String, String> addedFiles; // 文件名 -> blob SHA-1 ID，一一对应
    private List<String> removedFiles; // 待移除的文件名
    public Stage() {
        addedFiles = new HashMap<>();
        removedFiles = new ArrayList<>();
    }
    public void addFile(String file,String fileId) {
        addedFiles.put(file, fileId);
        removedFiles.remove(file);
    }
    public boolean isEmpty() {
        return addedFiles.isEmpty() && removedFiles.isEmpty();
    }
    public void removeFile(String file) {
        removedFiles.add(file);
        addedFiles.remove(file);
    }
    public Map<String,String> getAddedFiles() {
        return addedFiles;
    }
    public List<String> getRemovedFiles() {
        return removedFiles;
    }
    public void clear() {
        addedFiles.clear();
        removedFiles.clear();
    }
    public ArrayList<String> getStagedFilename() {
        ArrayList<String> res = new ArrayList<>();
        res.addAll(addedFiles.keySet());
        res.addAll(removedFiles);
        return res;
    }
}
