package lnj.utils;

import org.apache.commons.io.IOUtils;

import java.io.IOException;

public class CommonUtils {
    public static String getHostname() throws IOException {
        Process p = Runtime.getRuntime().exec("hostname");
        String rsp = IOUtils.toString(p.getInputStream(), "utf-8");
        if(rsp.contains("\n")) {
            return rsp.substring(0, rsp.length() - 1);
        } else {
            return rsp;
        }
    }
}
