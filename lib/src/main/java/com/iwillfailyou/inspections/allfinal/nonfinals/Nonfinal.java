package com.iwillfailyou.inspections.allfinal.nonfinals;

import com.iwillfailyou.inspection.Violation;

public interface Nonfinal extends Violation {
    boolean isInterfaceMethodParam();
    boolean isLambdaParam();
    boolean isCatchParam();
}
