package com.jensonxu.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogTest {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(LogTest.class);

        logger.debug("This is a debug message");
        logger.info("This is an info message");
        logger.warn("This is a warning message");
        logger.error("This is an error message");
    }
}
