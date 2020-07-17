package snow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import snow.dependencies.MunicipalServices;
import snow.dependencies.SnowplowMalfunctioningException;
import snow.dependencies.WeatherForecastService;

import static org.mockito.Mockito.*;

public class SnowRescueServiceTest {

    private WeatherForecastService weatherForecastService;
    private MunicipalServices municipalServices;
    private SnowRescueService snowRescueService;

    @BeforeEach
    void setUp() {
        weatherForecastService = Mockito.mock(WeatherForecastService.class);
        municipalServices = Mockito.mock(MunicipalServices.class);
        snowRescueService = new SnowRescueService(weatherForecastService, municipalServices, null);
    }

    @Test
    void sends_sander_when_temperature_below_zero() {
        havingTemperature(-1);

        snowRescueService.checkForecastAndRescue();

        verify(municipalServices).sendSander();
    }

    @Test
    void do_not_send_sander_when_temperature_above_or_equal_zero() {
        havingTemperature(0);

        snowRescueService.checkForecastAndRescue();

        verify(municipalServices, Mockito.never()).sendSander();
    }

    @Test
    void sends_snowplow_when_snowing() {
        havingSnowfall(4);

        snowRescueService.checkForecastAndRescue();

        verify(municipalServices).sendSnowplow();
    }

    @Test
    void do_not_sends_snowplow_when_small_snow() {
        havingSnowfall(3);

        snowRescueService.checkForecastAndRescue();

        verify(municipalServices,never()).sendSnowplow();
    }

    @Test
    void sends_two_snowplow_when_a_lot_of_snowing() {
        havingSnowfall(6);

        snowRescueService.checkForecastAndRescue();

        verify(municipalServices,times(2)).sendSnowplow();
    }

    @Test
    void sends_second_snowplow_when_first_failed() {
        havingSnowfall(4);
        whenFirstSnowplowFails();

        snowRescueService.checkForecastAndRescue();

        verify(municipalServices, times(2)).sendSnowplow();
    }

    private void whenFirstSnowplowFails() {
        doThrow(SnowplowMalfunctioningException.class).doNothing().when(municipalServices).sendSnowplow();
    }

    private void havingTemperature(int temperature) {
        Mockito.when(weatherForecastService.getAverageTemperatureInCelsius()).thenReturn(temperature);
    }

    private void havingSnowfall(int snowAmount) {
        Mockito.when(weatherForecastService.getSnowFallHeightInMM()).thenReturn(snowAmount);
    }


}
