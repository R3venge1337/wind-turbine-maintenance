package pl.zimnya.wind_turbine_maintenance.common.exception;

import java.util.UUID;

public class AlreadyExistException extends RuntimeException {

    public AlreadyExistException(final String message) {
        super(message);
    }

    public AlreadyExistException(final UUID uuid) {
        super(String.format("Entity already exist by uuid: %s", uuid));
    }

    public AlreadyExistException(final String message, final Object... args) {
        super(String.format(message, args));
    }

}