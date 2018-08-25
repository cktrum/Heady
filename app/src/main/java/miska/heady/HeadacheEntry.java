package miska.heady;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HeadacheEntry implements java.io.Serializable {
    private int id = -1;
    private Date startTime;
    private Date endTime = null;
    private int intensity;
    private Side side;
    private PainType painType;
    private String notes;
    private SideEffect sideEffect = SideEffect.NONE;
    private ArrayList<CounterMeasure> counterMeasures;

    public HeadacheEntry(Date startTime, int intensity, Side side, PainType painType) {
        this.startTime = startTime;
        this.intensity = intensity;
        this.side = side;
        this.painType = painType;
        this.counterMeasures = new ArrayList<CounterMeasure>();
    }

    public HeadacheEntry(int id, Date startTime, Date endTime, int intensity, Side side, PainType painType,
                         String notes, SideEffect sideEffect) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intensity = intensity;
        this.side = side;
        this.painType = painType;
        this.notes = notes;
        this.sideEffect = sideEffect;
        this.counterMeasures = new ArrayList<CounterMeasure>();
    }

    public boolean hasID() {
        return this.id != -1;
    }

    public int getID() {
        return this.id;
    }

    public void setStartTime(Date startTime) { this.startTime = startTime; }

    public long getStartTime() {
        return this.startTime.getTime();
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public boolean hasEndTime() {
        return this.endTime != null;
    }

    public long getEndTime() {
        return this.endTime.getTime();
    }

    public void setIntensity(int intensity) { this.intensity = intensity; }

    public int getIntensity() {
        return this.intensity;
    }

    public void setSide(Side side) { this.side = side; }

    public Side getSide() {
        return this.side;
    }

    public void setPainType(PainType type) { this.painType = type; }

    public PainType getPainType() {
        return this.painType;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getNotes() {
        return this.notes;
    }

    public boolean hasSideEffect() {
        return this.sideEffect != null;
    }

    public void setSideEffect(SideEffect sideEffect) {
        this.sideEffect = sideEffect;
    }

    public SideEffect getSideEffect() {
        return this.sideEffect;
    }

    public void addCounterMeasure(CounterMeasure measure) {
        this.counterMeasures.add(measure);
    }

    public void setCounterMeasures(ArrayList<CounterMeasure> measures) {
        this.counterMeasures = measures;
    }

    public ArrayList<CounterMeasure> getCounterMeasures() {
        return this.counterMeasures;
    }

    public String toString() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.UK);
        StringBuilder builder = new StringBuilder();

        builder.append(dateFormatter.format(startTime));
        builder.append(",");
        builder.append(timeFormatter.format(startTime));
        builder.append(",");
        if (endTime != null) {
            builder.append(timeFormatter.format(endTime));
        }
        builder.append(",");
        builder.append(intensity);
        builder.append(",");
        builder.append(side.toString());
        builder.append(",");
        builder.append(painType.toString());
        builder.append(",");
        builder.append(sideEffect.toString());
        builder.append(",");
        String notesWithoutLineBreak = notes.replace("\n", ";");
        builder.append(notesWithoutLineBreak);
        builder.append(",");

        for (int i = 0; i < counterMeasures.size(); i++) {
            CounterMeasure measure = counterMeasures.get(i);
            builder.append(measure.getName());
            builder.append(",");
            builder.append(timeFormatter.format(measure.getTimeOfIntake()));
            if (i < counterMeasures.size() - 1) {
                builder.append(",");
            }
        }

        return builder.toString();
    }
}
