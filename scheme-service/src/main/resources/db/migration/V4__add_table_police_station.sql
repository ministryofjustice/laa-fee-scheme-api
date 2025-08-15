DROP TABLE IF EXISTS police_stations CASCADE;

CREATE TABLE IF NOT EXISTS police_stations (
    id SERIAL PRIMARY KEY,
    police_station_id          varchar NOT NULL UNIQUE,
    police_station_name        varchar NOT NULL,
    police_station_scheme_id   varchar NOT NULL,
    police_station_scheme_name varchar NOT NULL
);
