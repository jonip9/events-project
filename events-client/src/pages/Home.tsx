import { useGetEvents } from '../hooks/api';

interface EventNote {
  event_id: string;
  summary: string;
  descr: string;
  dtstamp: string;
  dtstart: string;
  dtend: string;
  duration: number;
}

interface EventNoteItem {
  event_id: string;
  name: string;
  description: string;
  timestamp: Date;
}

export function Home() {
  const { isLoading, data, error } = useGetEvents();

  function generateEvents(event: EventNote): EventNoteItem[] {
    const d = new Date();
    let curr = new Date(event.dtstart);
    const end = new Date(event.dtend);
    const arr = [];

    while (curr <= end) {
      const tstamp = new Date(curr);

      tstamp.setMinutes(tstamp.getMinutes() - (d.getTimezoneOffset() - tstamp.getTimezoneOffset()));
      arr.push({
        event_id: event.event_id,
        name: event.summary,
        description: event.descr,
        timestamp: tstamp
      });
      curr.setTime(curr.getTime() + (event.duration * 1000))
    }

    return arr;
  }

  function combineEvents(events: EventNote[]): EventNoteItem[] {
    const allEvents = events
      .map((item) => {
        return generateEvents(item);
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
              {data && (
                combineEvents(data)
                  .map((item) => {
                    return (
                      <div key={`${item.event_id}${item.timestamp.getTime()}`}>
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
