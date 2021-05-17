package dev.youshallnotpass.inspections.allfinal.nonfinals;

import dev.youshallnotpass.inspection.Violation;

public interface Nonfinal extends Violation {
    boolean isInterfaceMethodParam();
    boolean isLambdaParam();
    boolean isCatchParam();
}
