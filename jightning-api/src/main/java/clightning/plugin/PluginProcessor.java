package clightning.plugin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.google.auto.service.AutoService;
import lombok.Data;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.StandardLocation;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This is an annotation processor to handle annotation at source processing phase.
 * It will generate necessary information for plugin method signature. This information
 * whill help map from json request to the specific plugin method.
 */
@SupportedAnnotationTypes({
        "clightning.plugin.Command",
        "clightning.plugin.Subscribe",
        "clightning.plugin.Hook"
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class PluginProcessor extends AbstractProcessor {
    private Map<String, Map> plugins = new HashMap<>();
    private MetaStore metaStore;

    private void log(String msg) {
        FileWriter writer = null;
        try {
            File file = new File("/tmp/t.log");
            writer = new FileWriter(file, true);
            writer.write(msg);
            writer.write("\n");
            writer.flush();
        } catch (Exception e) {
        } finally {
            if (Objects.nonNull(writer)) {
                try {
                    writer.close();
                } catch (IOException e) {
                }
            }
        }
    }

    @SneakyThrows
    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        metaStore = MetaStore.load();
    }

    @SneakyThrows
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        log("size is: " + String.valueOf(annotations.size()));
        log("processingOver: " + String.valueOf(roundEnv.processingOver()));

        for (TypeElement annotation : annotations) {
            for (Element method : roundEnv.getElementsAnnotatedWith(annotation)) {
                TypeElement wrappingClass = (TypeElement) method.getEnclosingElement();
                PluginMetaInfo plugin = metaStore.getOrCreatePluginMetaInfo(wrappingClass.getQualifiedName().toString());
                plugin.addMethod(MethodSignature.from(annotation, (ExecutableElement) method));
            }
        }
        if (roundEnv.processingOver()) {
            metaStore.store(processingEnv.getFiler());
        }
        return true;
    }

    public enum MethodType {
        COMMAND,
        SUBSCRIBE,
        HOOK;

        public static MethodType from(TypeElement annotation) {
            String name = annotation.getQualifiedName().toString();
            if (name.equals(Command.class.getName())) {
                return COMMAND;
            } else if (name.equals(Subscribe.class.getName())) {
                return SUBSCRIBE;
            } else if (name.equals(Hook.class.getName())) {
                return HOOK;
            } else {
                throw new RuntimeException("unsupported method type: " + name);
            }
        }
    }

    @Data
    public static class MethodSignature {
        MethodType type;
        String method;
        String[] args;

        public MethodSignature() {
        }

        public MethodSignature(MethodType type, String method, String[] args) {
            this.type = type;
            this.method = method;
            this.args = args;
        }

        public static MethodSignature from(TypeElement annotation, ExecutableElement element) {
            MethodSignature signature = new MethodSignature();
            signature.type = MethodType.from(annotation);
            signature.method = element.getSimpleName().toString();
            signature.args = (String[]) element.getParameters()
                    .stream()
                    .map((e) -> e.getSimpleName().toString())
                    .collect(Collectors.toList())
                    .toArray(new String[]{});
            return signature;
        }

        public String argName(int pos) {
            return args[pos];
        }
    }

    @Data
    public static class PluginMetaInfo {
        String className;
        HashMap<String, MethodSignature> methods;

        public PluginMetaInfo() {
        }

        public PluginMetaInfo(String className) {
            this.className = className;
            methods = new HashMap<>();
            addMandatoryMethod();
        }

        private void addMandatoryMethod() {
            addMethod(new MethodSignature(MethodType.COMMAND, "getManifest", new String[]{}));
            addMethod(new MethodSignature(MethodType.COMMAND, "init", new String[]{"options", "configuration"}));
        }

        public MethodSignature get(String methodName) {
            return methods.get(methodName);
        }

        public void addMethod(MethodSignature signature) {
            methods.put(signature.method, signature);
        }
    }

    public static class MetaStore {
        private static final Logger logger = LoggerFactory.getLogger(MetaStore.class);

        static final String metaStoreFile = "pluginMetaInfos.json";
        static ObjectMapper mapper = new ObjectMapper();

        Map<String, PluginMetaInfo> plugins;

        public static MetaStore load() throws IOException {
            MetaStore metaStore = new MetaStore();
            InputStream in = null;
            try {
                in = MetaStore.class.getClassLoader().getResourceAsStream(metaStoreFile);
                metaStore.plugins = mapper.readValue(in, new TypeReference<Map<String, PluginMetaInfo>>() {
                });
            } catch (MismatchedInputException e) {
                logger.warn(metaStoreFile + " file not found while trying to load it", e);
                metaStore.plugins = new HashMap<>();
            } finally {
                if (Objects.nonNull(in)) {
                    in.close();
                }
            }
            return metaStore;
        }

        public void addPluginMetaInfo(PluginMetaInfo pluginMetaInfo) {
            plugins.put(pluginMetaInfo.className, pluginMetaInfo);
        }

        public PluginMetaInfo getOrCreatePluginMetaInfo(String classFqn) {
            PluginMetaInfo plugin = plugins.get(classFqn);
            if (Objects.isNull(plugin)) {
                plugin = new PluginMetaInfo(classFqn);
                addPluginMetaInfo(plugin);
            }
            return plugin;
        }

        /**
         * @param classFqn class fully qualified name
         * @return PluginMetaInfo instance or null if not found
         */
        public PluginMetaInfo getPluginMetaInfo(String classFqn) {
            return plugins.get(classFqn);
        }

        public void store(Filer filer) throws IOException {
            JavaFileManager.Location location = StandardLocation.CLASS_OUTPUT;
            OutputStream out = null;
            try {
                FileObject fileObj = filer.createResource(location, "", metaStoreFile);
                out = fileObj.openOutputStream();
                mapper.writeValue(out, plugins);
            } finally {
                if (Objects.nonNull(out)) {
                    out.close();
                }
            }

        }

        @SneakyThrows
        @Override
        public String toString() {
            return mapper.writeValueAsString(plugins);
        }
    }
}
