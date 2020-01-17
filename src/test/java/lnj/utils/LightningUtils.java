package lnj.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

public class LightningUtils {
    private static Map<String, String> idToHost;

    static {
        String file = "nodeIdToHost.json";
        InputStream is = LightningUtils.class.getClassLoader().getResourceAsStream(file);
        try {
            idToHost = new ObjectMapper().readValue(is, new TypeReference<Map<String, String>>(){});
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static String getHost(String nodeId) {
        return idToHost.get(nodeId);
    }
    public static void main(String[] args) {
        System.out.println(100);
    }
}