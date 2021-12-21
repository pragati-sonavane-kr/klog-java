public class KlogObj {
    private String corelationId;
    private String division;
    private String store;
    private String payload;
    private String dateTime;
    private boolean valid;
    private String comment;
    private String status;

    public boolean isValid() {
        return valid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public String toString() {
        return "KlogObj{" +
                "corelationId='" + corelationId + '\'' +
                ", division='" + division + '\'' +
                ", store='" + store + '\'' +
                ", payload='" + payload + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", valid=" + valid +
                ", comment='" + comment + '\'' +
                '}';
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getCorelationId() {
        return corelationId;
    }

    public void setCorelationId(String corelationId) {
        this.corelationId = corelationId;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
