package ru.javawebinar.topjava.service.util;

import org.junit.rules.ExternalResource;
import org.junit.rules.Stopwatch;
import org.junit.runner.Description;
import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

public class WatcherTest {
    private static final Logger log = getLogger("result");
    private static final StringBuilder results = new StringBuilder();

    public final static Stopwatch STOPWATCH = new Stopwatch() {
        @Override
        protected void finished(long nanos, Description description) {
            String result = String.format("\n%-25s %7d", description.getMethodName(), TimeUnit.NANOSECONDS.toMillis(nanos));
            results.append(result);
            log.info(result + " ms\n");
        }
    };

    public final static ExternalResource EXTERNAL_RESOURCE = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            resetResult();
        }

        @Override
        protected void after() {
            printResult();
        }
    };

    private static void resetResult() {
        results.setLength(0);
    }

    private static void printResult() {
        log.info("\n---------------------------------" +
                "\nTest                 Duration, ms" +
                "\n---------------------------------" +
                results +
                "\n---------------------------------");
    }
}
