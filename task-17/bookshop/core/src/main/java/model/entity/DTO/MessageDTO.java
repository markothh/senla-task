package model.entity.DTO;

public class MessageDTO {
    private final String code;
    private final String message;

    public MessageDTO(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "{\"code\": " + code +
                ",\n\"message\": " + message + "}";
    }
}
