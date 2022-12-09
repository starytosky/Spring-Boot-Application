package test;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
@Slf4j
public class createFolderTest {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get("D:\\VC\\1\\2\\");
        Path pathCreate = Files.createDirectories(path);
        log.info("文件夹" + pathCreate);
    }
}
