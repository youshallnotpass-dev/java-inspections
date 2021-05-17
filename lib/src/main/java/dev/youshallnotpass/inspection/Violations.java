package dev.youshallnotpass.inspection;

import java.util.List;

public interface Violations<T extends Violation> {
    List<T> asList() throws InspectionException;
}
