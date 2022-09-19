package org.powertester.config;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

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
            System.out.println("TestEnv: " + testEnv);

//            Path path = Paths.get("src", "main", "resources", String.valueOf(testEnv).toLowerCase());
            String path = String.format("src/main/resources/%s", String.valueOf(testEnv).toLowerCase());
            File testEnvDir = new File(String.valueOf(path));
            System.out.println("directory path: " + testEnvDir.getAbsolutePath());
            for (File file : testEnvDir.listFiles()) {
//                Path envFilePath = Paths.get(String.valueOf(testEnv).toLowerCase(), file.getName());
//                Config childConfig = ConfigFactory.load(String.valueOf(envFilePath));
            Config childConfig = ConfigFactory.load(String.format("%s/%s", String.valueOf(testEnv).toLowerCase(), file.getName()));
                config = config.withFallback(childConfig);
            }

            return config;
        }catch(Exception exception){
            exception.printStackTrace();
            throw new IllegalStateException("not able to parse config properties");
        }
    }
}
