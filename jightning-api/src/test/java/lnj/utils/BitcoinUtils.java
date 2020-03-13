package lnj.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

public class BitcoinUtils {
    private static String exec(String cmd) throws IOException {
        Process proc = Runtime.getRuntime().exec(cmd);
        String response = IOUtils.toString(proc.getInputStream(), "utf-8");
        return response;
    }

    public static void confirm() throws IOException {
        generateToAddress(100, getNewAddress());
    }

    public static void generateToAddress(int blockCnt, String address) throws IOException {
        String cmd = "bitcoin-cli -regtest generatetoaddress " + blockCnt + " " + address;
        exec(cmd);
    }

    public static String getNewAddress() throws IOException {
        String cmd = "bitcoin-cli -regtest getnewaddress";
        String addr = exec(cmd);
        return addr.substring(0, addr.length() - 1);
    }

    public static JsonNode decodeRawTransaction(String hex) throws IOException {
        String cmd = "bitcoin-cli -regtest decoderawtransaction " + hex;
        return new ObjectMapper().readTree(exec(cmd));
    }
}
