import { useEffect, useMemo, useState } from 'preact/hooks';
import type { EventNote, EventNoteItem } from '../types/event';
import { partition } from '../utils';

interface EventListingProps {
  data: EventNote[];
}

export default function EventListing({ data }: EventListingProps) {
  const [datelimit, setDatelimit] = useState('');
  const [pagelimit, setPagelimit] = useState(10);
  const [page, setPage] = useState(0);

  useEffect(() => {
    const d = new Date();
    d.setMonth(d.getMonth() + 3);
    const date = d.toISOString().slice(0, 10);
    setDatelimit(date);
  }, []);

  const memoizedEvents = useMemo(() => {
    if (!data) return [];
    const allEvents = data
      .reduce<EventNoteItem[]>((arr, event) => {
        const end = new Date(datelimit);

        if (event.recur) {
          const curr = new Date(event.dtstart);
          const { years, months, days, hours, minutes } = event.recur;

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
        } else {
          arr.push({
            event_id: event.event_id,
            name: event.summary,
            description: event.descr,
            timestamp: new Date(event.dtstart)
          });
        }

        return arr;
      }, [])
      .sort((a, b) => a.timestamp.getTime() - b.timestamp.getTime())
      .map((item) => {
        return (
          <div key={`${item.event_id}${item.timestamp.getTime()}`}>
            <div>{item.name}</div>
            <div>{item.description}</div>
            <div>{item.timestamp.toString()}</div>
          </div>
        );
      });

    return partition(allEvents, pagelimit);
  }, [data, pagelimit, datelimit]);

  useMemo(() => {
    if (memoizedEvents.length > 0 && !memoizedEvents[page]) {
      setPage(memoizedEvents.length - 1);
    }
  }, [memoizedEvents, page]);

  function onDatelimitChange(e: Event) {
    const target = e.currentTarget as HTMLInputElement;
    setDatelimit(target.value);
  }

  function onPagelimitChange(e: Event) {
    const target = e.currentTarget as HTMLInputElement;
    setPagelimit(target.valueAsNumber);
  }

  function onNext() {
    if (page < memoizedEvents.length - 1) {
      setPage((state) => {
        return state + 1;
      });
    }
  }

  function onPrevious() {
    if (page > 0) {
      setPage((state) => {
        return state - 1;
      });
    }
  }

  return (
    <>
      <div>
        Limit:
        <input
          type="date"
          id="datelimit"
          name="datelimit"
          value={datelimit}
          onChange={onDatelimitChange}
        />
        Show:
        <input
          type="number"
          value={pagelimit}
          onChange={onPagelimitChange}
        />
        <button onClick={onPrevious}>
          Previous
        </button>
        Page: {page + 1}
        <button onClick={onNext}>
          Next
        </button>
      </div>
      <div>
        {memoizedEvents[page]}
      </div>
    </>
  );
}
