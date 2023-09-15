import { useGetEvents } from '../hooks/api';
import type { EventNote, EventNoteItem } from '../types/event';


}

export function Home() {
  const { isLoading, data, error } = useGetEvents();

  function generateEvents(event: EventNote): EventNoteItem[] {
    const curr = new Date(event.dtstart);
    const end = new Date(event.dtend);
    const { years, months, days, hours, minutes } = event.duration;
    const arr = [];

    while (curr <= end) {
      arr.push({
        event_id: event.event_id,
        name: event.summary,
        description: event.descr,
        timestamp: new Date(curr)
      });
      curr.setFullYear(curr.getFullYear() + years);
      curr.setMonth(curr.getMonth() + months);
      curr.setDate(curr.getDate() + days);
      curr.setHours(curr.getHours() + hours);
      curr.setMinutes(curr.getMinutes() + minutes);
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
                    );
                  })
              )}
            </div>
          </div>
        </div>
      </main>
    </>
  );
}
