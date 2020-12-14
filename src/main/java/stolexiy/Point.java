package stolexiy;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;

@Entity
@Table(name = "RESULTS")
public class Point implements Serializable, Cloneable {
    @Id @GeneratedValue
    @Column(name="id")
    private long id;
    private String login;
    private double x;
    private double y;
    private double r;
    private boolean hit;
    @Column(name="lead_time")
    private long leadTime;
    @Column(name = "date")
    private Timestamp now;
    private static final DecimalFormat decimalFormatter = new DecimalFormat("#.##");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);

    /*public Point(int x, double y, int r, boolean hit, long leadTime, ZonedDateTime now) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
        this.leadTime = leadTime;
        this.now = now;
    }*/

    public Point() {
        x = 0.0;
        y = 0.0;
        r = 1.0;
        hit = false;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setLeadTime(long leadTime) {
        this.leadTime = leadTime;
    }

    public void setLeadTime(long start, long end) {
        leadTime = end - start;
    }

    public long getLeadTime() {
        return leadTime;
    }

    /*public void setNow(ZonedDateTime now) {
        this.now = now;
    }

    public ZonedDateTime getNow() {
        return now;
    }*/

    public void setNow(Timestamp now) {
        this.now = now;
    }

    public Timestamp getNow() {
        return now;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNowStr() {
        return dateFormatter.format(now.toLocalDateTime());
    }

    public String getHitStr() {
        return hit ? "Попадание" : "Промах";
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }

    public boolean getHit() {
        return hit;
    }

    public double getR() {
        return r;
    }

    public void setR(double R) {
        this.r = R;
    }

    public void setY(double Y) {
        this.y = Y;
    }

    public double getX() {
        return x;
    }

    public void setX(double X) {
        this.x = X;
    }

    public double getY() {
        return y;
    }

    public String numberToLocaleString(double n) {
        return decimalFormatter.format(n);
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + Math.round(x) +
                ", y=" + y +
                ", r=" + r +
                ", hit=" + hit +
                ", leadTime=" + leadTime +
                ", now=" + now +
                '}';
    }

    public String toTableRow() {
        return "<tr>" +
                "<td>" + dateFormatter.format(now.toLocalDateTime()) + "</td>" +
                "<td>" + leadTime + "</td>" +
                "<td>" + decimalFormatter.format(x) + "</td>" +
                "<td>" + decimalFormatter.format(y) + "</td>" +
                "<td>" + decimalFormatter.format(r) + "</td>" +
                "<td>" + (hit ? "Попадание" : "Промах") + "</td>" +
                "</tr>";
    }

    private String toNameValue(String name, String value) {
        return "\"" + name + "\": \"" + value + "\"";
    }

    public String toJSON() {
        return "{" +
                toNameValue("1now", dateFormatter.format(now.toLocalDateTime())) + "," +
                toNameValue("2leadTime", String.valueOf(leadTime)) + "," +
                toNameValue("3X", decimalFormatter.format(x)) + "," +
                toNameValue("4Y", decimalFormatter.format(y)) + "," +
                toNameValue("5R", decimalFormatter.format(r)) + "," +
                toNameValue("6hit", (hit ? "Попадание" : "Промах")) +
                "}";
    }

    @Override
    public Point clone() {
        Point clone = new Point();
        clone.x = this.x;
        clone.y = this.y;
        clone.r = this.r;
        return clone;
    }
}
