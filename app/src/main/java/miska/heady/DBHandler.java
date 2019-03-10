package miska.heady;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;

public class DBHandler extends SQLiteOpenHelper {
    private static final String databaseName = "HeadacheDB";
    private static final int databaseVersion = 3;

    public DBHandler(Context context) {
        super(context, databaseName, null, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createHeadacheTableCommand = "CREATE TABLE Headache (" +
                "ID INTEGER PRIMARY KEY, StartDate INTEGER, EndDate INTEGER, " +
                "Intensity INTEGER, Side VARCHAR(10), Type VARCHAR(20), SideEffect VARCHAR(20)," +
                "Notes VARCHAR(300))";
        String createCounterMeasureTableCommand = "CREATE TABLE CounterMeasure (" +
                "ID INTEGER PRIMARY KEY, Name VARCHAR(30), Time INTEGER, UNIQUE (Name, Time))";
        String createMappingTableCommand = "CREATE TABLE Mapping (HeadacheID INT, " +
                "CounterMeasureID INT, FOREIGN KEY (HeadacheID) REFERENCES Headache(ID), " +
                "FOREIGN KEY (CounterMeasureID) REFERENCES CounterMeasure(ID), " +
                "PRIMARY KEY (HeadacheID, CounterMeasureID))";
        String createSettingsTableCommand = "CREATE TABLE Setting (NumberEntries INTEGER, " +
                "FromDate INTEGER, PRIMARY KEY (NumberEntries, FromDate))";

        db.execSQL(createHeadacheTableCommand);
        db.execSQL(createCounterMeasureTableCommand);
        db.execSQL(createMappingTableCommand);
        db.execSQL(createSettingsTableCommand);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Headache");
        db.execSQL("DROP TABLE IF EXISTS CounterMeasure");
        db.execSQL("DROP TABLE IF EXISTS Mapping");
        db.execSQL("DROP TABLE IF EXISTS Setting");
        onCreate(db);
    }

    public void addOrUpdateHeadacheEntry(HeadacheEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues headacheValues = fillValues(entry);
        long headacheID = db.insertWithOnConflict("Headache", null, headacheValues,
                SQLiteDatabase.CONFLICT_REPLACE);

        db.delete("Mapping", "HeadacheID=?",
                new String[] {String.valueOf(entry.getID())});

        ArrayList<CounterMeasure> counterMeasures = entry.getCounterMeasures();
        for (int i = 0; i < counterMeasures.size(); i++) {
            CounterMeasure counterMeasure = counterMeasures.get(i);
            ContentValues counterMeasureValues = new ContentValues();
            counterMeasureValues.put("Name", counterMeasure.getName().toString());
            counterMeasureValues.put("Time", counterMeasure.getTimeOfIntake());

            long counterMeasureID = db.insertWithOnConflict("CounterMeasure", null,
                    counterMeasureValues, SQLiteDatabase.CONFLICT_REPLACE);

            ContentValues mappingValues = new ContentValues();
            mappingValues.put("HeadacheID", headacheID);
            mappingValues.put("CounterMeasureID", counterMeasureID);

            db.insertWithOnConflict("Mapping", null, mappingValues,
                    SQLiteDatabase.CONFLICT_REPLACE);
        }

        db.close();
    }

    public void saveSettings(Setting settings) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues settingValues = new ContentValues();
        settingValues.put("NumberEntries", settings.getNumberOfEntries());
        settingValues.put("FromDate", settings.getFromDate());

        db.insertWithOnConflict("Setting", null, settingValues,
                SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public int getNumberOfEntries() {
        int result = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Setting", new String[] {"NumberEntries", "FromDate"},
                null,null, null, null, null);
        if (cursor.moveToFirst()) {
            result = cursor.getInt(0);
        }

        return result;
    }

    private ArrayList<CounterMeasure> retrieveCounterMeasures(int headacheID) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<CounterMeasure> measures = new ArrayList<CounterMeasure>();

        String query = "SELECT cm.Name, cm.Time FROM CounterMeasure cm " +
                "JOIN Mapping m ON m.CounterMeasureID = cm.ID " +
                "JOIN Headache h ON h.ID = m.HeadacheID WHERE m.HeadacheID=?";
        Cursor cursor = db.rawQuery(query, new String[] {String.valueOf(headacheID)});

        if (cursor.moveToFirst()) {
            do {
                String nameString = cursor.getString(0);
                CounterMeasureName name = CounterMeasureName.fromString(nameString);
                Date time = new Date(cursor.getLong(1));
                CounterMeasure measure = new CounterMeasure(name, time);
                measures.add(measure);
            } while (cursor.moveToNext());
        }

        db.close();
        return measures;
    }

    public ArrayList<HeadacheEntry> getLatestHeadacheEntries(int limit) {
        String limitString = null;
        if (limit > 0) {
            limitString = String.valueOf(limit);
        }

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<HeadacheEntry> entries = new ArrayList<HeadacheEntry>();

        Cursor cursor = db.query("Headache", new String[] {"ID", "StartDate", "EndDate",
                        "Intensity", "Side", "Type", "Notes", "SideEffect"}, null,
                null, null, null, "StartDate DESC", limitString);
        if (cursor.moveToFirst()) {
            do {
                int entry_id = cursor.getInt(0);
                long startDateStamp = cursor.getLong(1);
                Date startDate = new Date(startDateStamp);
                long endDateStamp = cursor.getLong(2);
                Date endDate = null;
                if (endDateStamp != 0) {
                    endDate = new Date(endDateStamp);
                }
                int intensity = cursor.getInt(3);
                String sideString = cursor.getString(4);
                Side side = Side.fromString(sideString);
                String typeString = cursor.getString(5);
                PainType type = PainType.fromString(typeString);
                String notes = cursor.getString(6);
                String sideEffectString = cursor.getString(7);
                SideEffect sideEffect = SideEffect.fromString(sideEffectString);
                HeadacheEntry entry = new HeadacheEntry(entry_id, startDate, endDate, intensity,
                        side, type, notes, sideEffect);
                ArrayList<CounterMeasure> measures = this.retrieveCounterMeasures(entry_id);
                entry.setCounterMeasures(measures);

                entries.add(entry);
            } while (cursor.moveToNext());
        }

        db.close();
        return entries;
    }

    public void deleteEntry(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Mapping", "HeadacheID=?", new String[] {String.valueOf(id)});
        db.delete("Headache", "ID=?", new String[] {String.valueOf(id)});
        db.close();
    }

    private ContentValues fillValues(HeadacheEntry entry) {
        ContentValues headacheValues = new ContentValues();

        headacheValues.put("StartDate", entry.getStartTime());
        headacheValues.put("Intensity", entry.getIntensity());
        headacheValues.put("Side", entry.getSide().toString());
        headacheValues.put("Type", entry.getPainType().toString());
        headacheValues.put("Notes", entry.getNotes());
        if (entry.hasID()) {
            headacheValues.put("ID", entry.getID());
        }
        if (entry.hasEndTime()) {
            headacheValues.put("EndDate", entry.getEndTime());
        }
        if (entry.hasSideEffect()) {
            headacheValues.put("SideEffect", entry.getSideEffect().toString());
        }

        return headacheValues;
    }

}
