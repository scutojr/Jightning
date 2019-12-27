package lnj.runner;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import lnj.testCase.TestLightningDaemon;

public class SimpleRunner {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestLightningDaemon.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
    }
}
