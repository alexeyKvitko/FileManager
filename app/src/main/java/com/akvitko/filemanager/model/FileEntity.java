package com.akvitko.filemanager.model;

import com.akvitko.filemanager.AppUtils;

import java.util.Date;

/**
 * Created by alexey on 27.05.15.
 */
public class FileEntity implements Comparable<FileEntity> {

    /** File name */
    private String name;
    /**Full path with file name */
    private String path;
    /**Size directory or file in bytes */
    private long size;
    /**Create or last modify date of file */
    private Date modifyDate;
    /**Flag - it is folder or not */
    private boolean folder;
    /**Flag - has this folder  children or not */
    private boolean hasChild;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isFolder() {
        return folder;
    }

    public void setFolder(boolean folder) {
        this.folder = folder;
    }

    public boolean isHasChild() {
        return hasChild;
    }

    public void setHasChild(boolean hasChild) {
        this.hasChild = hasChild;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Name: [" + name + "], ")
          .append("Path: ["+path+"], ")
          .append("Size: [").append(size).append("], ")
          .append("Date: [").append( AppUtils.formatDate( modifyDate ) ).append("], ")
          .append("Is folder: [").append( folder ).append("], ")
          .append("Has child: [").append( hasChild ).append("]");

        return sb.toString();
    }

    @Override
    public int compareTo(FileEntity other) {
        return getName().toLowerCase().compareTo( other.getName().toLowerCase() );
    }
}
