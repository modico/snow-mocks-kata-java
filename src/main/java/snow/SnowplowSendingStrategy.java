package snow;

public class SnowplowSendingStrategy {
    public int getNumberOfSnowplows(int snowfall) {
        if (snowfall > 10) {
            return 3;
        } else if (snowfall > 5) {
            return 2;
        } else if (snowfall > 3) {
            return 1;
        } else {
            return 0;
        }
    }
}
