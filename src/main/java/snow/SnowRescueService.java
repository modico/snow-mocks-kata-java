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
		if(isSanderToBeSent()) {
			municipalServices.sendSander();
		}
		sendSnowplows(getNumberOfSnowplowsAccordingToSnowfall());
		if(isPressServiceToBeInvolved()) {
			pressService.sendWeatherAlert();
		}
	}

	private int getNumberOfSnowplowsAccordingToSnowfall() {
		return new SnowplowSendingStrategy().getNumberOfSnowplows(weatherForecastService.getSnowFallHeightInMM());
	}

	private boolean isSanderToBeSent() {
		return weatherForecastService.getAverageTemperatureInCelsius() < 0;
	}

	private boolean isPressServiceToBeInvolved() {
		return weatherForecastService.getAverageTemperatureInCelsius() < -10 && weatherForecastService.getSnowFallHeightInMM() > 10;
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