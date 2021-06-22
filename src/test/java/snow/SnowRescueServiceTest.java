package snow;

import org.junit.jupiter.api.Test;
import snow.dependencies.MunicipalServices;
import snow.dependencies.PressService;
import snow.dependencies.SnowplowMalfunctioningException;
import snow.dependencies.WeatherForecastService;

import static org.mockito.Mockito.*;

public class SnowRescueServiceTest {

    private WeatherForecastService weatherForecastService;
    private MunicipalServices municipalServices;
    private PressService pressService;


    @Test
    public void sends_sander_when_temperature_is_bellow_zero() {
        weatherForecastService = mock(WeatherForecastService.class);
        when(weatherForecastService.getAverageTemperatureInCelsius()).thenReturn(-1);
        municipalServices = mock(MunicipalServices.class);
        new SnowRescueService(weatherForecastService,municipalServices,pressService).checkForecastAndRescue();

        verify(municipalServices).sendSander();
    }

    @Test
    public void sends_snowplow_when_snowfall_is_over_three_mm() {
        weatherForecastService = mock(WeatherForecastService.class);
        when(weatherForecastService.getSnowFallHeightInMM()).thenReturn(4);
        municipalServices = mock(MunicipalServices.class);
        new SnowRescueService(weatherForecastService,municipalServices,pressService).checkForecastAndRescue();

        verify(municipalServices).sendSnowplow();
    }

    @Test
    public void sends_another_snowplow_in_case_of_first_snowplow_malfunction() {
        weatherForecastService = mock(WeatherForecastService.class);
        when(weatherForecastService.getSnowFallHeightInMM()).thenReturn(4);
        municipalServices = mock(MunicipalServices.class);
        doThrow(new SnowplowMalfunctioningException()).when(municipalServices).sendSnowplow();

        try {
            new SnowRescueService(weatherForecastService,municipalServices,pressService).checkForecastAndRescue();
        } catch (Exception ignore) {}

        verify(municipalServices, times(2)).sendSnowplow();
    }

    @Test
    public void sends_two_snowplows_when_snowfall_is_over_five_mm() {
        weatherForecastService = mock(WeatherForecastService.class);
        when(weatherForecastService.getSnowFallHeightInMM()).thenReturn(6);
        municipalServices = mock(MunicipalServices.class);
        new SnowRescueService(weatherForecastService,municipalServices,pressService).checkForecastAndRescue();

        verify(municipalServices,times(2)).sendSnowplow();
    }

    @Test
    public void sends_three_snowplows_sander_and_notify_press_when_temperature_is_below_minusTen_and_snowfall_is_over_10_mm() {
        weatherForecastService = mock(WeatherForecastService.class);
        when(weatherForecastService.getSnowFallHeightInMM()).thenReturn(11);
        when(weatherForecastService.getAverageTemperatureInCelsius()).thenReturn(-11);
        municipalServices = mock(MunicipalServices.class);
        pressService = mock(PressService.class);
        new SnowRescueService(weatherForecastService,municipalServices,pressService).checkForecastAndRescue();

        verify(municipalServices).sendSander();
        verify(municipalServices,times(3)).sendSnowplow();
        verify(pressService).sendWeatherAlert();
    }

}