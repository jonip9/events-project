import EventForm from '../components/EventForm';
import { useGetEvents } from '../hooks/api';
import EventListing from '../components/EventListing';

export function Home() {
  const { isLoading, data, error } = useGetEvents();

  return (
    <>
      <main class="main">
        <div class="eventsHeader">
          <EventForm />
        </div>
        <div class="eventsContainer">
          <div class="sideMenu">
            temp
          </div>
          <div class="eventsList">
            <EventListing data={data} />
          </div>
        </div>
      </main>
    </>
  );
}
