package dev.youshallnotpass.javaparser;

import com.github.javaparser.ast.Node;
import dev.youshallnotpass.inspection.InspectionException;

public interface Search<T extends Node> {
    T find() throws InspectionException;
}
