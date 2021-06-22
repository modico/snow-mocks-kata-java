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
		if(weatherForecastService.getAverageTemperatureInCelsius() < 0 ) {
			municipalServices.sendSander();
		}
		if (weatherForecastService.getSnowFallHeightInMM() > 10) {
			sendSnowplows(3);
		} else if (weatherForecastService.getSnowFallHeightInMM() > 5) {
			sendSnowplows(2);
		} else if (weatherForecastService.getSnowFallHeightInMM() > 3) {
			sendSnowplows(1);
		}
		if(weatherForecastService.getAverageTemperatureInCelsius() < -10 && weatherForecastService.getSnowFallHeightInMM() > 10) {
			pressService.sendWeatherAlert();
		}
	}

	private void sendSnowplows(int numberOfSnowplows) {
		for(int snowplow = 0; snowplow < numberOfSnowplows; snowplow++) {
			sendSnowplow();
		}

	}

	private void sendSnowplow() {
		try {
			municipalServices.sendSnowplow();
		} catch (SnowplowMalfunctioningException e) {
			municipalServices.sendSnowplow();
		}
	}

}