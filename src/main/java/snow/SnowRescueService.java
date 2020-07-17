package snow;

import snow.dependencies.MunicipalServices;
import snow.dependencies.PressService;
import snow.dependencies.SnowplowMalfunctioningException;
import snow.dependencies.WeatherForecastService;

public class SnowRescueService {

    private final WeatherForecastService weatherForecastService;
    private final MunicipalServices municipalServices;
    private final PressService pressService;

    public SnowRescueService(WeatherForecastService weatherForecastService, MunicipalServices municipalServices, PressService pressService) {
        this.weatherForecastService = weatherForecastService;
        this.municipalServices = municipalServices;
        this.pressService = pressService;
    }

    public void checkForecastAndRescue() {
        if (weatherForecastService.getAverageTemperatureInCelsius() < 0) {
            municipalServices.sendSander();
        }

        if (weatherForecastService.getSnowFallHeightInMM() > 3) {
            tryToSendSnowplow();
        }

        if (weatherForecastService.getSnowFallHeightInMM() > 5) {
            tryToSendSnowplow();
        }

    }

    private void tryToSendSnowplow() {
        try {
            municipalServices.sendSnowplow();
        } catch (SnowplowMalfunctioningException e) {
            municipalServices.sendSnowplow();
        }
    }

}
