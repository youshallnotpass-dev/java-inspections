package dev.youshallnotpass.plugin;

import java.net.URL;

public interface Urls {
    URL forInspection(Inspection inspection) throws YsnpException;
}
