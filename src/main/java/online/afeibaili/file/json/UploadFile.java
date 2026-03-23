package online.afeibaili.file.json;

import java.util.List;

/**
 * 文件上传模块
 *
 * @author AfeiBaili
 * @version 2026/3/23 12:45
 */

public class UploadFile {
    Boolean isOpen;
    Long maxFileSize;
    List<NamePath> pathList;

    public Long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(Long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public Boolean getIsOpen() {
        return isOpen;
    }

    public void setOpen(Boolean open) {
        isOpen = open;
    }

    public List<NamePath> getPathList() {
        return pathList;
    }

    public void setPathList(List<NamePath> pathList) {
        this.pathList = pathList;
    }

    public static class NamePath {
        String name;
        String path;

        public NamePath(String name, String path) {
            this.name = name;
            this.path = path;
        }

        public NamePath() {
        }

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
    }

}