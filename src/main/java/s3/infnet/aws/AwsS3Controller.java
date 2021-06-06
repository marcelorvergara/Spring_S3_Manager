/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3.infnet.aws;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author marcelo
 */
@Controller
public class AwsS3Controller {

    @Autowired
    private AwsS3Service awsS3Service;

    private static final String bucket_name = "dr4s3bucket";

    @GetMapping("/")
    public String redirectHome() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String getHomePage(Model model) {
        List<String> allFileNameInBucket = awsS3Service.getAllFileNameInBucket(bucket_name);
        model.addAttribute("s3FileNames", allFileNameInBucket);
        return "home";
    }

    @PostMapping("/upload")
    public String uploadToS3(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        File file = File.createTempFile("tmp", "tmp");
        multipartFile.transferTo(file);
        awsS3Service.upload(file, multipartFile.getOriginalFilename(), bucket_name);
        return "redirect:/home";
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable("filename") String fileName) {
        ByteArrayOutputStream byteArrayOutputStream = awsS3Service.downloadFile(fileName, bucket_name);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(byteArrayOutputStream.toByteArray());
    }

    @GetMapping("/delete/{filename}")
    public String deleteFile(@PathVariable("filename") String fileName) {
        awsS3Service.deleteFromBucket(bucket_name, fileName);
        return "redirect:/home";
    }

}
