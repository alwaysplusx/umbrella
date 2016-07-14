package com.harmony.umbrella.fs;

import java.util.List;

/**
 * @author wuxii@foxmail.com
 */
public interface FileGroup {

    Long getGroupId();

    String getGroupName();

    List<FileItem> getFileItems();

}
