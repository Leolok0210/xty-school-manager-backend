package com.xiaotiyun.school.manager.basic.util;

import com.xiaotiyun.school.manager.config.FileConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Component
public class TempFileUtils {

    @Resource
    private FileConfig fileConfig;


    /**
     * 生成一个文件名
     * @return
     */
    public String getExportFileName(String preFix,String fileName){
        return fileConfig.getFilePrefix() + File.separator + preFix + fileName;
    }

    public String rootPath()
    {
        return fileConfig.getFilePrefix();
    }

    public File creteFolder(String folderName)
    {
        File folder = new File(fileConfig.getFilePrefix() + File.separator + folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }


    public void compressFolder(File folder, String parentFolder, ZipOutputStream zos){
        if (folder == null || folder.listFiles() == null || folder.listFiles().length == 0) {
            log.info("Source folder is empty.");
            return;
        }
        for (File file : folder.listFiles()) {

            if (file.isDirectory()) {
                compressFolder(file, parentFolder + "/" + file.getName(), zos);
            } else {
                FileInputStream fis = null;
                try{
                    fis = new FileInputStream(file);
                    ZipEntry zipEntry = new ZipEntry(parentFolder + "/" + file.getName());
                    zos.putNextEntry(zipEntry);

                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = fis.read(bytes)) >= 0) {
                        zos.write(bytes, 0, length);
                    }
                }catch (Exception e)
                {
                    log.error("Error while compressing file: " + file.getName(), e);
                }finally {
                    try {
                        if(fis != null)
                        {
                            fis.close();
                        }
                    }catch (Exception e)
                    {
                        log.error("Error while closing file input stream: " + file.getName(), e);
                    }
                }
            }

        }
    }

    public void deleteFolder(File folder) {
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File f : files) {
                    deleteFolder(f);
                }
            }
        }
        folder.delete();
    }

    public byte[] fileToBetyArray(String filePath)
    {
        FileInputStream fileInputStream = null;
        File file = new File(filePath);
        byte[] bFile = null;
        try {
            bFile = new byte[(int) file.length()];
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();
        } catch (Exception e) {
            log.error("Error converting file to byte array.");
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (bFile != null) {
                    bFile.clone();
                }
            } catch (IOException e) {
                log.error("Error closing file input stream.");
            }
        }
        return bFile;
    }
}
