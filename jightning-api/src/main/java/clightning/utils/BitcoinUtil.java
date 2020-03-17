package clightning.utils;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;

import java.math.BigInteger;

public class BitcoinUtil {
    public static Transaction decodeTransaction(NetworkParameters networkParameters, String tx) {
        byte[] data = new BigInteger(tx, 16).toByteArray();
        return new Transaction(networkParameters, data);
    }
}
