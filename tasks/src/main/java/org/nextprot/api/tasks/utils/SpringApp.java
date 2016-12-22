package org.nextprot.api.tasks.utils;

public interface SpringApp {

    SpringConfig getSpringConfig();

    default void start() {

        getSpringConfig().startApplicationContext();
    }

    default void stop() {

        getSpringConfig().stopApplicationContext();
    }
}
