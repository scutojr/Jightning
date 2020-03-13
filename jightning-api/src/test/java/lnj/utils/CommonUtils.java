package lnj.utils;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CommonUtils {
    public static String getHostname() throws IOException {
        Process p = Runtime.getRuntime().exec("hostname");
        String rsp = IOUtils.toString(p.getInputStream(), "utf-8");
        if (rsp.contains("\n")) {
            return rsp.substring(0, rsp.length() - 1);
        } else {
            return rsp;
        }
    }

    public static DirectoryStream<Path> listClassPath() throws URISyntaxException, IOException {
        URL url = CommonUtils.class.getClassLoader().getResource(".");
        System.out.println(url);
        Path path = Paths.get(url.toURI());
        return Files.newDirectoryStream(path);
    }
}
