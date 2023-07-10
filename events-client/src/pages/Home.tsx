import { useState, useEffect } from 'preact/hooks';

interface Event {
  event_id: number;
  summary: string;
  descr: string;
  dtstamp: string;
  dtstart: string;
  dtend: string;
  duration: string;
}

interface EventItem {
  event_id: number;
  name: string;
  description: string;
  timestamp: Date;
}

export function Home() {
  const [eventData, setEventData] = useState<Event[]>([]);

  useEffect(() => {
    setEventData([
      {
        event_id: 0,
        summary: 'Event 1',
        descr: 'My description',
        dtstamp: '2023-08-24T10:00:00',
        dtstart: '2023-08-24T10:00:00',
        dtend: '2023-12-24T00:00:00',
        duration: '1 months 7 days'
      },
      {
        event_id: 1,
        summary: 'Event 2',
        descr: 'Another description',
        dtstamp: '2023-10-14T14:30:00',
        dtstart: '2023-10-14T14:30:00',
        dtend: '2024-01-14T00:00:00',
        duration: '2 days'
      }
    ])
  }, []);

  function getInterval(interval: string[], start: Date, end: Date): number {
    if (interval.length === 0) return end.getTime() - start.getTime();
    const kind = interval.pop();
    const amount = Number.parseInt(interval.pop());

    switch (kind) {
      case 'days':
        end.setDate(end.getDate() + amount);
        break;
      case 'months':
        end.setMonth(end.getMonth() + amount);
        break;
      case 'years':
        end.setFullYear(end.getFullYear() + amount);
        break;
    }

    return getInterval(interval, start, end);
  }

  function generateEvents(event: Event, ival: number, offset: number): EventItem[] {
    let curr = new Date(event.dtstart);
    const end = new Date(event.dtend);
    const arr = [];

    while (curr <= end) {
      const tstamp = new Date(curr);
      tstamp.setMinutes(tstamp.getMinutes() - (offset - tstamp.getTimezoneOffset()));
      arr.push({
        event_id: event.event_id,
        name: event.summary,
        description: event.descr,
        timestamp: tstamp
      });

      curr = new Date(curr.getTime() + ival);
    }

    return arr;
  }

  function combineEvents(events: Event[]): EventItem[] {
    const d = new Date();
    const allEvents = events
          .map((item) => {
            const ival = getInterval(
              item.duration.split(' '),
              d,
              new Date()
            );
            return generateEvents(item, ival, d.getTimezoneOffset());
          })
          .flat()
          .sort((a, b) => a.timestamp.getTime() - b.timestamp.getTime());

    return allEvents;
  }

  return (
    <>
      <main class="main">
        <div class="eventsHeader">
          temp
        </div>
        <div class="eventsContainer">
          <div class="sideMenu">
            temp
          </div>
          <div class="eventsList">
            <div>
              {eventData.length > 0 && (
                combineEvents(eventData)
                  .map((item) => {
                    return (
                      <div key={item.event_id + item.timestamp.getTime()}>
                        <div>{item.name}</div>
                        <div>{item.description}</div>
                        <div>{item.timestamp.toString()}</div>
                      </div>
                    )
                  })
              )}
            </div>
          </div>
        </div>
      </main>
    </>
  );
}
