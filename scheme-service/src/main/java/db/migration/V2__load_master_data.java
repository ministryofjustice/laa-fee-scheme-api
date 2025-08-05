package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;

public class V2__load_master_data extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream("/db/csv/police_station_fees.csv")));
             PreparedStatement stmt = context.getConnection()
                     .prepareStatement("INSERT INTO master_data (id, name, type) VALUES (?, ?, ?)")) {

            String line;
            reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                stmt.setInt(1, Integer.parseInt(parts[0]));
                stmt.setString(2, parts[1]);
                stmt.setString(3, parts[2]);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }
}
