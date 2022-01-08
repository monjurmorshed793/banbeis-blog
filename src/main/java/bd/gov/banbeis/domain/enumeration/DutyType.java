package bd.gov.banbeis.domain.enumeration;

/**
 * The DutyType enumeration.
 */
public enum DutyType {
    MAIN("Main"),
    ADDITIONAL("Additional");

    private final String value;

    DutyType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
