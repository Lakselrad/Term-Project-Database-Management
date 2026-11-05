package uga.csx370.criticalhit.models;

import java.util.ArrayList;
import java.util.List;

/**
 * One choice in a dropdown, pill list or radio group.
 * Mustache has no if-equals logic, so the selected flag is computed in Java.
 */
public class Option {

    private static final String[] RATING_LABELS = { "", "Awful", "Meh", "Good", "Great", "Masterpiece" };

    private final String value;
    private final String label;
    private final boolean selected;

    public Option(String value, String label, boolean selected) {
        this.value = value;
        this.label = label;
        this.selected = selected;
    }

    /**
     * Builds options from a list of values. The first option has an empty value
     * and the given label (e.g. "All genres").
     */
    public static List<Option> listOf(List<String> values, String selectedValue, String emptyLabel) {
        String selected = selectedValue == null ? "" : selectedValue;
        List<Option> options = new ArrayList<>();
        options.add(new Option("", emptyLabel, selected.isEmpty()));
        for (String value : values) {
            options.add(new Option(value, value, value.equalsIgnoreCase(selected)));
        }
        return options;
    }

    /** Star rating radio buttons from 5 down to 1 (the CSS reverses them). */
    public static List<Option> ratingOptions(int selectedRating) {
        List<Option> options = new ArrayList<>();
        for (int stars = 5; stars >= 1; stars--) {
            options.add(new Option(String.valueOf(stars), RATING_LABELS[stars], stars == selectedRating));
        }
        return options;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    public boolean isSelected() {
        return selected;
    }
}
