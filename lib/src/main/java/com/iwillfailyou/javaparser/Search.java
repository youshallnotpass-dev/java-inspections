package com.iwillfailyou.javaparser;

import com.github.javaparser.ast.Node;
import com.iwillfailyou.inspection.InspectionException;

public interface Search<T extends Node> {
    T find() throws InspectionException;
}
