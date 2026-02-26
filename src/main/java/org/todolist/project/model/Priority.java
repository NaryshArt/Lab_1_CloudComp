package org.todolist.project.model;

public enum Priority {
    LOW(0, "Низкий"),
    MEDIUM(1, "Средний"),
    HIGH(2, "Высокий");

    private final int value;
    private final String displayName;

    Priority(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public int getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }
}