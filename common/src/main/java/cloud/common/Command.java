package cloud.common;

public enum Command {
    ADD("ADD"),
    UPGRADE("UPG"),
    DOWNLOAD("DWN"),
    DELETE("DEL"),
    AUTH("ATH");

    private String tag;

    Command(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

}
