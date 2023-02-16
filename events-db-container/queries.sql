DROP TABLE IF EXISTS event;
CREATE TABLE event (
  event_id text PRIMARY KEY,
  name text NOT NULL,
  description text,
  start_date timestamp,
  duration interval,
  repeat interval
);
