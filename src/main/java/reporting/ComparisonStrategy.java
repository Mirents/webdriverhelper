package reporting;

public enum ComparisonStrategy {

    ONE_TO_ONE("One to one direct comparison (Both images of same size)"),
    SUB_IMAGE("Sub image comparison (One image of larger size)"),
    ERROR("Error in image comparison");

    private String value;

    ComparisonStrategy(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
