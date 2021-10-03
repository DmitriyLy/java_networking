public interface TestInterface {

    String getMessage();

    default String getCachedMessage() {
        return "";
    }

    static String processCachedMessage(String input) {
        return input;
    }

}
