package stolexiy;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import stolexiy.database.Connector;

import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@ManagedBean(name = "params")
@SessionScoped
public class Param implements Serializable {
    private ArrayDeque<Point> points;
//    private List<Point> points;
    private String error;
    private Point newPoint;


    public Param() {
        setDefault();
    }

    public Point[] getPoints() {
        return (points.toArray(new Point[]{}));
    }

    public void submit() {
        long start = System.currentTimeMillis();
        newPoint.setNow(Timestamp.from(Instant.now()));
//        newPoint.setNow(ZonedDateTime.now(ZoneId.of("Europe/Moscow")));
        newPoint.setHit(Conditions.check(newPoint));
        long end = System.currentTimeMillis();
        newPoint.setLeadTime(start, end);
        newPoint.setLogin(FacesContext.getCurrentInstance().getExternalContext().getSessionId(false));
        add();
//        newPoint = newPoint.clone();
        newPoint = new Point();
    }

    public void add() {
        Transaction tx = null;
        Session session = Connector.getSessionFactory().openSession();
        try {
            tx = session.beginTransaction();
            session.save(newPoint);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        points.addFirst(newPoint);
        if (points.size() >= 10) {
            points.removeLast();
        }
    }

    public Point getNewPoint() {
        return newPoint;
    }

    public void setDefault() {
        points = new ArrayDeque<>(10);
        newPoint = new Point();
        error = "";
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "Param{" +
                "points=" + points +
                ", error='" + error + "'" +
                ", newPoint=" + newPoint +
                '}';
    }

    public String toJSON() {
        StringBuilder result = new StringBuilder("[");
        Iterator<Point> iterator = points.iterator();
        while (iterator.hasNext()) {
            result.append(iterator.next().toJSON());
            if (iterator.hasNext())
                result.append(",");
        }
        result.append("]");
        return result.toString();
    }
}
