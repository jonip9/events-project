SET timezone TO 'UTC';
DROP TABLE IF EXISTS event;
CREATE TABLE event (
  event_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  summary TEXT NOT NULL,
  descr TEXT,
  dtstamp TIMESTAMP WITH TIME ZONE NOT NULL,
  dtstart TIMESTAMP WITH TIME ZONE,
  dtend TIMESTAMP WITH TIME ZONE,
  duration INTERVAL
);
INSERT INTO event (summary, descr, dtstamp, dtstart, dtend, duration) VALUES ('event1', 'my descr', '2023-05-01', '2023-05-13', '2023-05-15', 'P7D');
