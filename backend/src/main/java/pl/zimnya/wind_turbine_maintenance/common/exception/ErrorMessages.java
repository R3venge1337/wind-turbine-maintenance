package pl.zimnya.wind_turbine_maintenance.common.exception;

public class ErrorMessages {
    public static final String TURBINE_NOT_FOUND = "exception.turbine.not_found";
    public static final String TURBINE_SETTINGS_NOT_FOUND = "exception.turbine.settings.not_found";
    public static final String TURBINE_WITH_PRODUCT_ID_EXIST = "exception.turbine.product_id.exist";

    public static final String TURBINE_PRODUCT_ID_NOT_EMPTY = "exception.turbine.product_id_not_empty";
    public static final String TURBINE_PRODUCT_ID_SIZE = "exception.turbine.product_id_size_exceed";
    public static final String TURBINE_TYPE_CODE_REQUIRED = "exception.turbine.type_code_required";
    public static final String TURBINE_TYPE_CODE_PATTERN = "exception.turbine.type_code_pattern";
    public static final String TURBINE_LATITUDE_REQUIRED = "exception.turbine.latitude_required";
    public static final String TURBINE_LATITUDE_MIN = "exception.turbine.latitude_min";
    public static final String TURBINE_LATITUDE_MAX = "exception.turbine.latitude_max";

    public static final String TURBINE_LONGITUDE_REQUIRED = "exception.turbine.longitude_required";
    public static final String TURBINE_LONGITUDE_MIN = "exception.turbine.longitude_min";
    public static final String TURBINE_LONGITUDE_MAX = "exception.turbine.longitude_max";
    public static final String TURBINE_CITY_SIZE = "exception.turbine.city_size";
    public static final String TURBINE_CURRENT_TOOL_WEAR_MIN = "exception.turbine.current_tool_wear_min";

    public static final String WEATHER_WIND_SPEED_NOT_EMPTY = "exception.weather.wind_speed.not_empty";
    public static final String WEATHER_WIND_SPEED_MIN = "exception.weather.wind_speed.min";
    public static final String WEATHER_WIND_SPEED_MAX = "exception.weather.wind_speed.max";
    public static final String WEATHER_AIR_TEMP_NOT_EMPTY = "exception.weather.air_temp.not_empty";
    public static final String WEATHER_AIR_TEMP_MIN = "exception.weather.air_temp.min";
    public static final String WEATHER_AIR_TEMP_MAX = "exception.weather.air_temp.max";

    public static final String MEASUREMENT_NOT_FOUND = "exception.measurement.not_found";

    private ErrorMessages(){

    }

}
