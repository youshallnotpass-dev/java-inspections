package com.iwillfailyou.javaparser;

import com.github.javaparser.ast.Node;
import java.util.Optional;

public interface Search<T extends Node> {
    Optional<T> find();
}
