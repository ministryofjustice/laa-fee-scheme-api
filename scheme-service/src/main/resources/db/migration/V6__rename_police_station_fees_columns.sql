ALTER TABLE police_station_fees
    RENAME COLUMN police_station_code TO ps_scheme_id;

ALTER TABLE police_station_fees
    RENAME COLUMN police_station_name TO ps_scheme_name;

ALTER TABLE police_stations
    RENAME COLUMN police_station_scheme_id TO ps_scheme_id;

ALTER TABLE police_stations
    RENAME COLUMN police_station_scheme_name TO ps_scheme_name;
