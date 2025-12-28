package com.example.business.controller.admin;

import cn.hutool.core.util.IdUtil;
import com.aliyun.oss.OSS;
import com.example.common.core.UserContext;
import com.example.common.response.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@RestController
@RequestMapping("admin/file")
public class FileAdminController {
    private static final Logger log = LoggerFactory.getLogger(FileAdminController.class);

    @Autowired
    private OSS ossClient;
    
    @Value("${alibaba.cloud.oss.bucket}")
    private String bucket;
    
    @Value("${alibaba.cloud.oss.endpoint}")
    private String endpoint;

    @PostMapping(value = "upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            assert originalFilename != null;
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileType = detectFileType(Objects.requireNonNull(file.getContentType()));
            String objectName = UserContext.get() + "/" + fileType + "/" + IdUtil.getSnowflakeNextIdStr() + suffix;
            ossClient.putObject(bucket, objectName, file.getInputStream());
            String url = String.format("https://%s.%s/%s", bucket, endpoint, objectName);
            
            return R.ok(url);
        } catch (Exception e) {
            log.error("文件上传失败：{}", e.getMessage());
            return R.fail("上传失败");
        }
    }

    private String detectFileType(String contentType) {
        if (contentType.startsWith("video/")) {
            return "video";
        } else if (contentType.startsWith("image/")) {
            return "image";
        } else if (contentType.startsWith("audio/")) {
            return "audio";
        } else if (contentType.startsWith("application/pdf")) {
            return "pdf";
        } else if (contentType.startsWith("application/msword") || contentType.startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            return "word";
        } else {
            return "default";
        }
    }
}
