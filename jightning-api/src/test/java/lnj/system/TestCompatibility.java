package lnj.system;

import clightning.LightningAppKit;
import clightning.Network;
import clightning.apis.LightningClient;
import clightning.apis.annotations.ImplFor;
import clightning.apis.annotations.ParamTag;
import clightning.apis.optional.OptionalParams;
import com.google.common.collect.Sets;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TestCompatibility {
    private LightningAppKit appKit;
    private LightningClient client;
//
//    private List<String> whiteList = new ArrayList<>(Arrays.asList(
//            "newBench32Addr",
//            "newP2shSegwitAddr"
//    ));

    @Before
    public void setUp() {
        appKit = new LightningAppKit(Network.regtest);
        appKit.startAsync();
        appKit.awaitRunning();

        client = appKit.client();
    }

    public String help() throws IOException, InterruptedException {
        String cmd = "lightning-cli help";
        Process proc = Runtime.getRuntime().exec(cmd);
        int rc = proc.waitFor();
        if (rc != 0) {
            Assert.fail("command \"" + cmd + "\"" + " is failed with exit code " + rc);
        }
        return IOUtils.toString(proc.getInputStream(), "utf-8");
    }

    @Test
    public void test() throws IOException, InterruptedException {
        Pattern pattern = Pattern.compile("^\\w+");

        String helpDoc = help();
        String[] lines = helpDoc.split("\n");

        Set<RpcMethod> lndMethods = new HashSet<>();
        for (String line : lines) {
            if (line.startsWith("---")) {
                break;
            }
            if (pattern.matcher(line).find()) {
                String[] tokens = line.split(" ");
                lndMethods.add(new RpcMethod(tokens));
            }
        }

        Map<String, RpcMethod> implMethods = new HashMap();
        for (Method method : LightningClient.class.getMethods()) {
            String name = method.getName();
            List<String> args = new ArrayList<>();
            Set<String> kwargs = new HashSet<>();

//            if (whiteList.contains(name)) {
//                continue;
//            }

            RpcMethod m;
            ImplFor a = method.getAnnotation(ImplFor.class);
            if (Objects.nonNull(a)) {
                m = new RpcMethod(a.value());
            } else {
                for (Parameter param : method.getParameters()) {
                    if (param.getType().getSuperclass() == OptionalParams.class) {
                        for (Method mm : param.getType().getMethods()) {
                            if (mm.getName().startsWith("set") && mm.getParameters().length == 1) {
                                kwargs.add(mm.getName().substring(3));
                            }
                        }
                    } else {
                        ParamTag tag = param.getAnnotation(ParamTag.class);
                        if (Objects.isNull(tag)) {
                            args.add(param.getName());
                        } else {
                            String n = tag.alias().equals("") ? param.getName() : tag.alias();
                            if (tag.optional()) {
                                kwargs.add(n);
                            } else {
                                args.add(n);
                            }
                        }
                    }
                }
                m = new RpcMethod(name, args, kwargs);
            }
            if (!implMethods.containsKey(m.name) || implMethods.get(m.name).argCount() < m.argCount()) {
                implMethods.put(m.name, m);
            }
        }

        Set<RpcMethod> implM = new HashSet(implMethods.values());
        findDiff(lndMethods, implM);
        Assert.assertTrue(lndMethods.equals(implM));
    }

    private void findDiff(Set<RpcMethod> lndMethods, Set<RpcMethod> implMethods) {
        Set<RpcMethod> d1 = Sets.difference(lndMethods, implMethods);
        Set<RpcMethod> d2 = Sets.difference(implMethods, lndMethods);

        System.out.println("INFO: Method only found in lightning daemon:");
        for (RpcMethod m : d1) {
            System.out.println("    " + m);
        }

        System.out.println("=========================================");

        System.out.println("INFO: Methodonly implemented in the java code:");
        for (RpcMethod m : d2) {
            System.out.println("    " + m);
        }
    }

    private static class RpcMethod {
        String name;
        List<String> args = new ArrayList();
        Set<String> kwargs = new HashSet();

        public RpcMethod(String usage) {
            this(usage.split(" "));
        }

        public RpcMethod(String[] tokens) {
            name = tokens[0];
            for (int i = 1; i < tokens.length; i++) {
                String arg = tokens[i];
                if (arg.startsWith("[")) {
                    kwargs.add(arg.substring(1, arg.length() - 1));
                } else {
                    args.add(arg);
                }
            }
            normalizeAll();
        }

        public RpcMethod(String name, List<String> args, Set<String> kwargs) {
            this.name = name;
            this.args = args;
            this.kwargs = kwargs;
            normalizeAll();
        }

        private String normalize(String name) {
            return name.replaceAll("_|\\-", "").toLowerCase();
        }

        private void normalizeAll() {
            name = normalize(name);
            for (int i = 0; i < args.size(); i++) {
                args.set(i, normalize(args.get(i)));
            }
            Set<String> tmp = new HashSet<>();
            for (String k : kwargs) {
                tmp.add(normalize(k));
            }
            kwargs = tmp;
        }

        public int argCount() {
            return args.size() + kwargs.size();
        }

        public int hashCode() {
            return 1 + 31 * (name.hashCode() + args.hashCode() + kwargs.hashCode());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof RpcMethod) {
                return this == obj ? true : (name.equals(((RpcMethod) obj).name)
                        && argCount() == ((RpcMethod) obj).argCount()
                        && args.equals(((RpcMethod) obj).args)
                        && kwargs.equals(((RpcMethod) obj).kwargs)
                );
            } else {
                return false;
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(name + " ");
            sb.append(String.join(" ", args));

            if (kwargs.size() > 0) {
                sb.append(" ");
                sb.append(
                        kwargs.stream()
                                .map((e) -> "[" + e + "]")
                                .collect(Collectors.joining(" "))
                );
            }
            return sb.toString();
        }
    }
}
