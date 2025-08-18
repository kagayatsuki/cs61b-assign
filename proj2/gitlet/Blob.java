package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.*;

public class Blob implements Serializable {
    private String fileName;
    private byte[] content;
    private String Id;
    public Blob(String fileName, File CWD){
        this.fileName = fileName;
        File file=join(CWD,fileName);
        if(file.exists()){
            this.content = readContents(file);
            this.Id=sha1(fileName,content);
        }
        else{
            this.content = null;
            this.Id=sha1(fileName);
        }
    }
    public boolean exists(){
        return this.content!=null;
    }
    public String getFileName(){
        return this.fileName;
    }
    public byte[] getContent(){
        return this.content;
    }
    public String getId(){
        return this.Id;
    }
    public String getContentAsString(){
        return new String(this.content);
    }
}
