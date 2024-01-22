package dev.huskuraft.effortless.api.platform;

import java.nio.file.Path;

public interface Platform {

    String getLoaderName();

    String getLoaderVersion();

    String getGameVersion();

    Path getGameDir();

    Path getConfigDir();

    Environment getEnvironment();

    boolean isDevelopment();

    enum OperatingSystem {
        LINUX,
        SOLARIS,
        WINDOWS,
        MACOS,
        UNKNOWN
    }

    enum Environment {
        CLIENT,
        SERVER
    }

}
