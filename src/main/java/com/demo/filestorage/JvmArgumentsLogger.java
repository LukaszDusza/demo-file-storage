package com.demo.filestorage;

import java.lang.management.ManagementFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class JvmArgumentsLogger {

    @EventListener(ApplicationReadyEvent.class)
    public void logJvmArguments() {
        var jvmArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();

        System.out.println("=========================================");
        System.out.println("JVM arguments during application startup:");
        jvmArguments.forEach(System.out::println);
        System.out.println("-----------------------------------------");
        System.out.println("Java version: " + System.getProperty("java.version"));
        System.out.println("Java home: " + System.getProperty("java.home"));
        System.out.println("OS name: " + System.getProperty("os.name"));
        System.out.println("=========================================");
    }
}