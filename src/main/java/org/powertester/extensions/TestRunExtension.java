package org.powertester.extensions;

import static org.powertester.factories.DateFactory.getDateAsString;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.MDC;

@Slf4j
public class TestRunExtension
    implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {
  AtomicBoolean isTestRunStarted = new AtomicBoolean(false);

  Path testRunReportPath =
      Paths.get("target", "test-run-reports", getDateAsString("yyyy-MM-dd/HH-mm"));

  @Override
  public void beforeAll(ExtensionContext context) {
    try {
      if (isTestRunStarted.compareAndSet(false, true)) {
        // Set MDC context
        MDC.put("testContext", "One Time Setup");

        log.info("Run this section only once at the beginning of the whole test run.");
        context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL).put("TestRunExtension", this);

        // Add your database connection pool setup here.
        // DBConnectionPool.getInstance().setup();

        // Create test run repository here.
        Files.createDirectories(testRunReportPath);
      }
    } catch (Exception e) {
      log.error("Exception in TestRunExtension: {}", e);
      log.error(
          "⚠ Terminating Test run since tests depend on the setup done in TestRunExtension class");
      System.exit(1);
    }
  }

  @Override
  public void close() {
    log.info("Run this section only once at the end of the whole test run.");

    // Close your database connection pool here.
    // DBConnectionPool.getInstance().close();

    // Clear MDC after test
    MDC.clear();
  }
}
