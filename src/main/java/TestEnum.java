public enum TestEnum {
    USB("usb"), VGA("vga"), HDMI("hdmi");

    private final String code;

    TestEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
