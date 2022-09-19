package org.powertester.config;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class TestEnvFactory {
    private static final TestEnvFactory UNIQUE_INSTANCE = new TestEnvFactory();
    private Config config;

    private TestEnvFactory() {
        config = setConfig();
    }

    public static TestEnvFactory getInstance() {
        return UNIQUE_INSTANCE;
    }

    public Config getConfig() {
        return config;
    }

    private Config setConfig() {
        try{
            log.info("setConfig called");
            // Standard config load behavior: https://github.com/lightbend/config#standard-behavior
            config = ConfigFactory.load();

            TestEnv testEnv = config.getEnum(TestEnv.class, "TEST_ENV");

            String path = String.format("src/main/resources/%s", testEnv);
            log.info("path: {}", path);
            File testEnvDir = new File(String.valueOf(path));
            for (File file : testEnvDir.listFiles()) {
                Config childConfig = ConfigFactory.load(String.format("%s/%s", testEnv, file.getName()));
                config = config.withFallback(childConfig);
            }

            return config;
        }catch(Exception exception){
            exception.printStackTrace();
            throw new IllegalStateException("Could not parse config");
        }
    }
}
