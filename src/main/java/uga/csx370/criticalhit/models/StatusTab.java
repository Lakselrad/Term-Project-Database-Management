package uga.csx370.criticalhit.models;

/**
 * A filter tab on the library page, e.g. "Playing (3)".
 */
public class StatusTab {

    private final String value;
    private final String label;
    private final int count;
    private final boolean active;

    public StatusTab(String value, String label, int count, boolean active) {
        this.value = value;
        this.label = label;
        this.count = count;
        this.active = active;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    public int getCount() {
        return count;
    }

    public boolean isActive() {
        return active;
    }
}
