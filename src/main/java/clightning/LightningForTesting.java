package clightning;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LightningForTesting implements AbstractLightningDaemon {

    private HashMap<String, String> jsonFiles = new HashMap();
    private ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module());

    public LightningForTesting() {
        try {
            loadJsonFiles();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void loadJsonFiles() throws URISyntaxException, IOException {
        URL url = LightningForTesting.class.getClassLoader().getResource(".");
        DirectoryStream<Path> paths = Files.newDirectoryStream(Paths.get(url.toURI()));
        for (Path path : paths) {
            String jsonFileName = normalizeFileName(path.getFileName().toString());
            if (!jsonFileName.isEmpty()) {
                FileInputStream in = new FileInputStream(path.toString());
                byte[] bytes = new byte[in.available()];
                in.read(bytes);
                jsonFiles.put(jsonFileName, new String(bytes));
            }
        }
    }

    private String getDataFileName(String method, Map<String, Object> params) {
        TreeMap<String, Object> orderedParams = new TreeMap();
        orderedParams.putAll(params);

        ArrayList<String> kvPairs = new ArrayList<String>();
        for (Map.Entry<String, Object> entry : orderedParams.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value != null) {
                kvPairs.add(key + "=" + value);
            }
        }
        StringBuilder sb = new StringBuilder();

        return sb
                .append(method)
                .append("(")
                .append(String.join(",", kvPairs))
                .append(")")
                .toString();
    }

    private String normalizeFileName(String fileName) {
        String regExp = "(\\w+)\\((([\\s,]*\\w+=[\\w-]+[\\s,]*)*)?\\)\\.json";
        Pattern r = Pattern.compile(regExp);
        Matcher m = r.matcher(fileName);
        if (m.find()) {
            String method = m.group(1);
            String[] pairs = m.group(2) == null ? new String[0] : m.group(2).split("\\s*,\\s*");
            HashMap<String, Object> params = new HashMap();
            for (String pair : pairs) {
                if (pair.isEmpty()) continue;
                int i = pair.indexOf("=");
                params.put(pair.substring(0, i), pair.substring(i + 1));
            }
            return getDataFileName(method, params);
        } else {
            return "";
        }
    }

    @Override
    public <T> T execute(String method, Map params, Class<T> valueType) throws IOException {
        String fileName = getDataFileName(method, params);
        return mapper.readValue(jsonFiles.get(fileName), valueType);
    }

    @Override
    public <T> T execute(String method, Class<T> valueType) throws IOException {
        return execute(method, new HashMap(), valueType);
    }
}
