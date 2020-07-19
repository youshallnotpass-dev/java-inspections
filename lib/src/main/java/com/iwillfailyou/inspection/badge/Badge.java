package com.iwillfailyou.inspection.badge;

import com.iwillfailyou.inspection.InspectionException;

import java.net.URL;

public interface Badge {

    void send(URL url) throws InspectionException;

    void failIfRed() throws InspectionException;
}
