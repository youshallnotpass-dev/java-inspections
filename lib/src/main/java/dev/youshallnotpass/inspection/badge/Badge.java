package dev.youshallnotpass.inspection.badge;

import dev.youshallnotpass.inspection.InspectionException;

import java.net.URL;

public interface Badge {

    void send(URL url) throws InspectionException;

    void failIfRed() throws InspectionException;
}
