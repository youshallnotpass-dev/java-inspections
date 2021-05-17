package dev.youshallnotpass.inspection;

public interface Violation {
    String description();

    boolean isSuppressed();
}
