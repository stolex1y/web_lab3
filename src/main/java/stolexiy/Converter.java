package stolexiy;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.*;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@FacesConverter("stolexiy.DoubleConverter")
public class Converter implements javax.faces.convert.Converter<Double> {
    @Override
    public Double getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        Pattern pattern = Pattern.compile("^[+-]?\\d+[.,]?\\d*$");
        Matcher matcher = pattern.matcher(s);
        if (!matcher.matches()) {
            FacesMessage message = Messages.getMessage("errorMsg",
                    "javax.faces.converter.NumberConverter.PATTERN", new Object[]{s, "-1,2"});
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ConverterException(message);
        }
        return Double.parseDouble(s.replaceAll(",", "."));
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Double aDouble) {
        DecimalFormat decimalFormatter = new DecimalFormat("#.##");
        return decimalFormatter.format(aDouble);
    }
}
