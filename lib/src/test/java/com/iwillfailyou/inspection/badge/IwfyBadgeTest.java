package com.iwillfailyou.inspection.badge;

import com.iwillfailyou.inspection.InspectionException;
import com.iwillfailyou.inspections.nullfree.nulls.JavaNulls;
import org.junit.Assert;
import org.junit.Test;

public class IwfyBadgeTest {
    @Test
    public void failThreshold() {
        try {
            new IwfyBadge(
                new JavaNulls(
                    "class A {",
                    "  String name = null;",
                    "  String value = null;",
                    "  void run() {",
                    "     String a = null;",
                    "  }",
                    "}"
                ),
                2
            ).failIfRed();
            Assert.fail();
        } catch (InspectionException e) {
            // green
        }
    }
}