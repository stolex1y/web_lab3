package stolexiy;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean
@ApplicationScoped
public class Conditions implements Serializable {

    private final ArrayList<Double> xValues = new ArrayList<>();
    private final ArrayList<Double> rValues = new ArrayList<>();
    private static final DecimalFormat decimalFormatter = new DecimalFormat("#.##");

    private final int yMin;
    private final int yMax;

    public Conditions() {
        yMin = -3;
        yMax = 3;

        for (double i = -2.0; i <= 1.5; i += 0.5) {
            xValues.add(i);
        }

        for (double i = 1.0; i <= 3.0; i += 0.5) {
            rValues.add(i);
        }
    }

    public int getYMax() {
        return yMax;
    }

    public int getYMin() {
        return yMin;
    }

    public List<String> getrValues() {
        return rValues.stream().map(decimalFormatter::format).collect(Collectors.toList());
    }

    public List<String> getxValues() {
        return xValues.stream().map(decimalFormatter::format).collect(Collectors.toList());
    }

    public static boolean check(Point point) {
        double X = point.getX();
        double Y = point.getY();
        double R = point.getR();

        if (X > 0) {
            if (Y > 0) {
                return false;
            } else {
                return (Y >= (X - R));
            }
        } else {
            if (Y <= 0) {
                return ((Y >= -R / 2) && (X >= -R));
            } else {
                double v = (R * R) - X * X;
                // Проверка ОДЗ
                if (v < 0)
                    return false;
                else
                    return (Y <= Math.sqrt(v));
            }
        }
    }
}
