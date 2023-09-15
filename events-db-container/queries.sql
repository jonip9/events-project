SET timezone TO 'UTC';
DROP TABLE IF EXISTS event;
CREATE TABLE event (
  event_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  summary TEXT NOT NULL,
  descr TEXT,
  dtstamp TIMESTAMP WITH TIME ZONE NOT NULL,
  dtstart TIMESTAMP WITH TIME ZONE,
  dtend TIMESTAMP WITH TIME ZONE,
  recur INTERVAL
);
INSERT INTO event (summary, descr, dtstamp, dtstart, dtend, recur) VALUES ('event1', 'my descr', '2023-05-01T13:00:00Z', '2023-05-13T13:00:00Z', '2023-08-13T13:00:00Z', '7 days');
