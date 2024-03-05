package project.study.controller.image;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;

@RestController
@RequiredArgsConstructor
public class ImageController {

    @Value("${file.dir}")
    private String fileDir;

    @GetMapping(value = "/images/{fileUploadType}/{filename}", produces = {"image/png", "image/jpg", "image/jpeg"})
    public Resource downloadImage1(@PathVariable(name = "fileUploadType") String fileUploadType, @PathVariable(name = "filename") String filename) throws MalformedURLException {
        System.out.println("image1 filename = " + filename);
        FileUploadType type = FileUploadType.findDir(fileUploadType);
        return new UrlResource("file:" + getFullPath(filename, type));
    }
    @GetMapping("/images/{filename}")
    public Resource downloadImage2(@PathVariable(name = "filename") String filename) throws MalformedURLException {
        System.out.println("image2 filename = " + filename);
        return new UrlResource("file:" + getFullPath(filename, null));
    }

    private String getFullPath(String fileName, FileUploadType type) {
        StringBuilder sb = new StringBuilder(fileDir);

        if (type == null) {
            return sb.append("/").append(fileName).toString();
        }

        String folderName = type.getDir();
        sb.append(folderName).append("/").append(fileName);
        return sb.toString();
    }
}
