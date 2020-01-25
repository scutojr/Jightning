package lnj;

import clightning.AbstractLightningDaemon;
import clightning.utils.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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

    /**
     *
     mapper.convertValue(params, new TypeReference<Map<String, Object>>() {})
     * @param node
     * @return
     */
    private Map<String, Object> safeJsNodeToMap(JsonNode node) {
        Map<String, Object> map = new HashMap<>();
        Iterator<String> it = node.fieldNames();
        while (it.hasNext()) {
            String field = it.next();
            JsonNode value = node.get(field);
            if (value.isTextual()) {
                map.put(field, value.asText()); // avoid extra quote from the toString() of textual json node
            } else {
                map.put(field, value);
            }
        }
        return map;
    }

    private void loadJsonFiles() throws URISyntaxException, IOException {
        URL url = LightningForTesting.class.getClassLoader().getResource("./api_data");
        DirectoryStream<Path> paths = Files.newDirectoryStream(Paths.get(url.toURI()));
        for (Path path : paths) {
            String fileName = path.getFileName().toString();
            if (fileName.endsWith(".json")) {
                String method = fileName.replace(".json", "");
                JsonNode content = mapper.readTree(path.toFile());
                for (JsonNode node : content) {
                    JsonNode params = node.get("params");
                    JsonNode response = node.get("response");
                    String nameAsId = getDataFileName(
                            method,
                            safeJsNodeToMap(params)
                    );
                    jsonFiles.put(nameAsId, response.toString());
                }
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
        String regExp = "(\\w+)\\((([\\s,]*\\w+=[\\w-\\.]+[\\s,]*)*)?\\)\\.json";
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
    public <T> T execute(String method, Map params, Class<T> valueType) {
        String fileName = getDataFileName(method, params);
        String content = jsonFiles.get(fileName);
        if (valueType == String.class)
            return (T) content;
        else
            return JsonUtil.convert(content, valueType);
    }

    @Override
    public <T> T execute(String method, Class<T> valueType) {
        return execute(method, new HashMap(), valueType);
    }
}
