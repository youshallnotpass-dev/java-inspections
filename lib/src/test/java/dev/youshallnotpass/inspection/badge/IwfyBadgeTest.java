package dev.youshallnotpass.inspection.badge;

import dev.youshallnotpass.inspection.InspectionException;
import dev.youshallnotpass.inspections.nullfree.nulls.JavaNulls;
import org.junit.Assert;
import org.junit.Test;

public final class IwfyBadgeTest {
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
        } catch (final InspectionException e) {
            // green
        }
    }
}