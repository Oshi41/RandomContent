package dash.dashmode.utils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public enum NumberScope {
    Simple("%s", 1),
    Hundreds("%h", 100),
    Thousands("%sk", 1000),
    KiloThousands("%shk", 1000 * 100),
    Millions("%sm", 1000 * 1000),
    KiloMillions("%shm", 1000 * 1000 * 100),
    Billions("%sb", 1000 * 1000 * 1000);

    public final float min;
    private final String formatText;

    NumberScope(String formatText, float min) {
        this.formatText = formatText;
        this.min = min;
    }

    public static String toFormatText(int number) {
        NumberScope scope = getScope(number);

        String result = String.format(scope.formatText, ((int) Math.floor(number / scope.min)));
        return result;
    }

    public static NumberScope getScope(int number) {
        List<NumberScope> scopes = Arrays.stream(NumberScope.values()).collect(Collectors.toList());

        for (int i = 0; i < scopes.size(); i++) {
            NumberScope scope = scopes.get(i);
            if (scope.min > number) {
                return scopes.get(Math.max(0, i - 1));
            }
        }

        return Simple;
    }
}
