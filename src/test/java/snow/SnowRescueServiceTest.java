package snow;

import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void setUp() {
        weatherForecastService = mock(WeatherForecastService.class);
        municipalServices = mock(MunicipalServices.class);
        pressService = mock(PressService.class);
    }

    @Test
    public void sends_sander_when_temperature_is_bellow_zero() {
        givenTemperature(-1);

        new SnowRescueService(weatherForecastService, municipalServices,pressService).checkForecastAndRescue();

        verify(municipalServices).sendSander();
    }

    @Test
    public void sends_snowplow_when_snowfall_is_over_three_mm() {
        givenSnowfall(4);

        new SnowRescueService(weatherForecastService, municipalServices,pressService).checkForecastAndRescue();

        verify(municipalServices).sendSnowplow();
    }

    @Test
    public void sends_another_snowplow_in_case_of_first_snowplow_malfunction() {
        givenSnowfall(4);
        doThrow(new SnowplowMalfunctioningException()).doNothing().when(municipalServices).sendSnowplow();

        new SnowRescueService(weatherForecastService, municipalServices,pressService).checkForecastAndRescue();

        verify(municipalServices, times(2)).sendSnowplow();
    }

    @Test
    public void sends_two_snowplows_when_snowfall_is_over_five_mm() {
        givenSnowfall(6);

        new SnowRescueService(weatherForecastService, municipalServices,pressService).checkForecastAndRescue();

        verify(municipalServices,times(2)).sendSnowplow();
    }

    @Test
    public void sends_three_snowplows_sander_and_notify_press_when_temperature_is_below_minusTen_and_snowfall_is_over_10_mm() {
        givenSnowfall(11);
        givenTemperature(-11);

        new SnowRescueService(weatherForecastService, municipalServices,pressService).checkForecastAndRescue();

        verify(municipalServices).sendSander();
        verify(municipalServices,times(3)).sendSnowplow();
        verify(pressService).sendWeatherAlert();
    }

    private void givenTemperature(int temperature) {
        when(weatherForecastService.getAverageTemperatureInCelsius()).thenReturn(temperature);
    }

    private void givenSnowfall(int snowfall) {
        when(weatherForecastService.getSnowFallHeightInMM()).thenReturn(snowfall);
    }

}