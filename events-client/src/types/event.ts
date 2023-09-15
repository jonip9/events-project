export interface EventNote {
  event_id: string;
  summary: string;
  descr: string;
  dtstamp: string;
  dtstart: string;
  dtend: string;
  duration: {
    years: number;
    months: number;
    days: number;
    hours: number;
    minutes: number;
  };
}

export interface EventNoteItem {
  event_id: string;
  name: string;
  description: string;
  timestamp: Date;
}
