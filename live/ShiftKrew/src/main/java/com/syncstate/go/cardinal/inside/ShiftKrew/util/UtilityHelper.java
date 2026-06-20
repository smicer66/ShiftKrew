package com.syncstate.go.cardinal.inside.ShiftKrew.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class UtilityHelper {

    private final static Logger log = LoggerFactory.getLogger(UtilityHelper.class);
    private static final List<String> contentTypes = Arrays.asList("text/csv");


    public static String uploadDataSourceFile(MultipartFile file, String fileDestinationPath, int refNoLength) throws IOException {
        byte[] fileBytes = file.getBytes();
        String pathName = file.getOriginalFilename();
        String newFileName = RandomStringUtils.randomAlphanumeric(16);
        String newFileNameExt = pathName.substring(pathName.lastIndexOf(".") + 1);
        File destinationFile = new File(fileDestinationPath + File.separator + newFileName + "." + newFileNameExt);
        BufferedOutputStream is = new BufferedOutputStream(new FileOutputStream(destinationFile));
        is.write(fileBytes);
        is.close();

        return newFileName + "." + newFileNameExt;
    }



}
