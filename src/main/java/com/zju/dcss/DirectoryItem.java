package com.zju.dcss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirectoryItem {
    private String fullPath;
    private String firstPath;
    private String parentPath;
    private boolean isFile = false;
    private Map<String, DirectoryItem> descendants = new HashMap<>(); //子目录（文件存储）<一级路径名或文件名,item>
    private List<String> position = new ArrayList<>();

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public String getFirstPath() {
        return firstPath;
    }

    public void setFirstPath(String firstPath) {
        this.firstPath = firstPath;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public List<String> getPosition() {
        return position;
    }

    public void setPosition(List<String> position) {
        this.position = position;
    }

    public Map<String, DirectoryItem> getDescendants() {
        return descendants;
    }

    public void setDescendants(Map<String, DirectoryItem> descendants) {
        this.descendants = descendants;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean file) {
        isFile = file;
    }
}
