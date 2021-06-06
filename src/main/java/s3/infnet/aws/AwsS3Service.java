/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3.infnet.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author marcelo
 */
@Service
public class AwsS3Service {

    @Autowired
    private AmazonS3 amazonS3Client;

    @Value("${aws.AccessId}")
    private String awsAccessKey;

    @Value("${aws.Secret}")
    private String awsSecretKey;

    public boolean upload(File file, String filename, String bucketName) {
        try {
            amazonS3Client.putObject(new PutObjectRequest(bucketName, filename, file).withCannedAcl(CannedAccessControlList.PublicRead));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<String> getAllFileNameInBucket(String bucketName) {
        List<String> bucketList = new ArrayList<>();
        ObjectListing objectListing = amazonS3Client.listObjects(bucketName);
        List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();
        for (S3ObjectSummary objectSummary : objectSummaries) {
            bucketList.add(objectSummary.getKey());
        }
        return bucketList;
    }

    public ByteArrayOutputStream downloadFile(String fileName, String bucketName) {
        try {

            S3Object s3object = amazonS3Client.getObject(new GetObjectRequest(bucketName, fileName));

            InputStream is = s3object.getObjectContent();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len;
            byte[] buffer = new byte[4096];
            while ((len = is.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, len);
            }
            return baos;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteFromBucket(String bucketName, String fileName) {
        try {
            amazonS3Client.deleteObject(bucketName, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
