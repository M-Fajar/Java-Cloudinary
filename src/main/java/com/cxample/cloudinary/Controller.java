package com.cxample.cloudinary;

import com.cxample.cloudinary.Exception.FileStorageException;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.cloudinary.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class Controller {

    @PostMapping("/image")
    public Map getImage(@RequestParam MultipartFile file){
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "fajarimage",
                "api_key", "359649651775691",
                "api_secret", "5jpIDBEBtn7oBBAfr8_NSUYMYO4"));
        try{
            File newFile = convert(file);
            System.out.println("Sukses");
            Map uploadResult = cloudinary.uploader().upload(newFile, ObjectUtils.emptyMap());
            newFile.delete();
            return uploadResult;
        }catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println("Gagal Convert Image");
        }
        return null;
    }

    public static File convert(MultipartFile file) throws IOException {

        Path root = Paths.get("src\\main\\resources\\uploads\\");
        String name = storeFile(file,root);
        File convFile = new File(root+"/"+name);
        return convFile;
    }

    public static String storeFile(MultipartFile file,Path path){
        // Normalize file name


        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Name invalid " + fileName );
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = path.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }
}
