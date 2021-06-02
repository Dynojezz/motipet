package de.dynomedia.motipet;

public class FitnessValue {

    private String day, steps, distance, calories;

    public FitnessValue(String currentDay, String currentSteps, String currentDistance, String currentCalories) {

        this.day = currentDay;
        this.steps = currentSteps;
        this.distance = currentDistance;
        this.calories = currentCalories;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }
}
