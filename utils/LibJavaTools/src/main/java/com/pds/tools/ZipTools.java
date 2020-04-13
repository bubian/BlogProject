package com.pds.tools;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;

public class ZipTools {

    public static String zipEncrypt(String src, String passWd) {
        return zipEncrypt(src, null, passWd,false);
    }

    public static String zipEncrypt(String src, String dest,String passWd) {
        return zipEncrypt(src, dest, passWd,false);
    }

    public static String zipEncrypt(String src, String dest, String passWd,boolean isCreateDir) {
        ZipParameters parameters = new ZipParameters();
        // 压缩方式
        parameters.setCompressionMethod(CompressionMethod.DEFLATE);
        // 压缩级别
        parameters.setCompressionLevel(CompressionLevel.NORMAL);
        ZipFile zipFile;

        File srcFile = new File(src);
        dest = buildDestinationZipFilePath(srcFile, dest);

        if (!StringTools.isEmpty(passWd)) {
            parameters.setEncryptFiles(true);
            parameters.setEncryptionMethod(EncryptionMethod.AES);
            parameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
            zipFile = new ZipFile(dest, passWd.toCharArray());
        } else {
            zipFile = new ZipFile(dest);
        }
        try {
            if (srcFile.isDirectory()) {
                // 如果不创建目录的话,将直接把给定目录下的文件压缩到压缩文件,即没有目录结构
                if (!isCreateDir) {
                    File[] subFiles = srcFile.listFiles();
                    ArrayList<File> temp = new ArrayList<>();
                    Collections.addAll(temp, subFiles);
                    zipFile.addFiles(temp, parameters);
                    return dest;
                }
                zipFile.addFolder(srcFile, parameters);
            } else {
                zipFile.addFile(srcFile, parameters);
            }
            return dest;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static File[] unZipEncrypt(File zipFile, String dest, String passWd) throws ZipException {
        ZipFile zFile = new ZipFile(zipFile);
        zFile.setCharset(Charset.forName("GBK"));
        if (!zFile.isValidZipFile()) {
            throw new ZipException("压缩文件不合法,可能被损坏.");
        }
        File destDir = new File(dest);
        if (destDir.isDirectory() && !destDir.exists()) {
            destDir.mkdir();
        }
        if (zFile.isEncrypted()) {
            zFile.setPassword(passWd.toCharArray());
        }
        zFile.extractAll(dest);

        List<FileHeader> headerList = zFile.getFileHeaders();
        List<File> extractedFileList = new ArrayList<>();
        for (FileHeader fileHeader : headerList) {
            if (!fileHeader.isDirectory()) {
                extractedFileList.add(new File(destDir, fileHeader.getFileName()));
            }
        }
        File[] extractedFiles = new File[extractedFileList.size()];
        extractedFileList.toArray(extractedFiles);
        return extractedFiles;
    }


    private static String buildDestinationZipFilePath(File srcFile, String dest) {
        if (StringTools.isEmpty(dest)) {
            if (srcFile.isDirectory()) {
                dest = srcFile.getParent() + File.separator + srcFile.getName() + ".zip";
            } else {
                String fileAllName = srcFile.getName();
                String fileName = fileAllName.substring(0, srcFile.getName().lastIndexOf("."));
                dest = srcFile.getParent() + File.separator + fileName + ".zip";
            }
        } else {
            // 在指定路径不存在的情况下将其创建出来
            createDestDirectoryIfNecessary(dest);
            if (dest.endsWith(File.separator)) {
                String fileName;
                if (srcFile.isDirectory()) {
                    fileName = srcFile.getName();
                } else {
                    fileName = srcFile.getName()
                            .substring(0, srcFile.getName().lastIndexOf("."));
                }
                dest += fileName + ".zip";
            }
        }
        return dest;
    }

    private static void createDestDirectoryIfNecessary(String destParam) {
        File destDir;
        if (destParam.endsWith(File.separator)) {
            destDir = new File(destParam);
        } else {
            destDir = new File(destParam.substring(0, destParam.lastIndexOf(File.separator)));
        }
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
    }
}
