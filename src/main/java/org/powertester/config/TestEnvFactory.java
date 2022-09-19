package org.powertester.config;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;

public class TestEnvFactory {
    private static final TestEnvFactory UNIQUE_INSTANCE = new TestEnvFactory();

    private TestEnvFactory() {
        // Do not want anyone to call this constructor.
    }

    public static TestEnvFactory getInstance() {
        return UNIQUE_INSTANCE;
    }

    public Config getConfig() {
        // Standard config load behavior: https://github.com/lightbend/config#standard-behavior
        Config config = ConfigFactory.load();

        TestEnv testEnv = config.getEnum(TestEnv.class, "TEST_ENV");

        String testEnvDirPath = String.format("src/main/resources/%s", testEnv);
        File testEnvDir = new File(testEnvDirPath);
        for (File file : testEnvDir.listFiles()) {
            Config childConfig = ConfigFactory.load(String.format("%s/%s", testEnv, file.getName()));
            config = config.withFallback(childConfig);
        }

        return config;
    }
}
