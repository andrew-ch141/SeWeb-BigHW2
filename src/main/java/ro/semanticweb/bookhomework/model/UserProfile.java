package ro.semanticweb.bookhomework.model;

import java.util.ArrayList;
import java.util.List;

public class UserProfile {

    private String id;
    private String name;
    private String readingLevel;
    private List<String> preferredThemes = new ArrayList<>();

    public UserProfile() {
    }

    public UserProfile(String id, String name, String readingLevel, List<String> preferredThemes) {
        this.id = id;
        this.name = name;
        this.readingLevel = readingLevel;
        this.preferredThemes = new ArrayList<>(preferredThemes);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getReadingLevel() {
        return readingLevel;
    }

    public List<String> getPreferredThemes() {
        return preferredThemes;
    }
}
