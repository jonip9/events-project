import { useOnFormInput } from '../hooks/util';

export default function EventForm() {
  const { formData, handleInput } = useOnFormInput();

  async function onSubmit(e: Event) {
    e.preventDefault();
    try {
      const datesInUTC = {
        ...formData,
        dtstamp: new Date().toISOString(),
        dtstart: new Date(formData.dtstart).toISOString(),
        dtend: new Date(formData.dtend).toISOString()
      };

      await fetch('http://localhost:3000/api/events', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(datesInUTC)
      });
    } catch (e) {
      console.log(e);
    }
  }

  return (
    <form class="event-form" onSubmit={onSubmit}>
      <div>
        <label for="summary">Summary</label>
        <input
          type="text"
          id="summary"
          name="summary"
          value={formData.summary}
          onInput={handleInput}
        />
      </div>
      <div class="form-textarea">
        <label for="descr">Description</label>
        <textarea
          type="text"
          id="descr"
          name="descr"
          value={formData.descr}
          onInput={handleInput}
        />
      </div>
      <div>
        <label for="dtstart">Start Date</label>
        <input
          type="datetime-local"
          id="dtstart"
          name="dtstart"
          value={formData.dtstart}
          onChange={handleInput}
        />
      </div>
      <div>
        <label for="dtend">End Date</label>
        <input
          type="datetime-local"
          id="dtend"
          name="dtend"
          value={formData.dtend}
          onChange={handleInput}
        />
      </div>
      <div>
        <label>Repeat every</label>
        <div class="recur-input-wrapper">
          <div>
            <input
              class="recur-input"
              type="number"
              name="years"
              min="0"
              value={formData.recur.years}
              onChange={handleInput}
            />
            <label for="years">years</label>
          </div>
          <div>
            <input
              class="recur-input"
              type="number"
              name="months"
              min="0"
              value={formData.recur.months}
              onChange={handleInput}
            />
            <label for="months">months</label>
          </div>
          <div>
            <input
              class="recur-input"
              type="number"
              name="days"
              min="0"
              value={formData.recur.days}
              onChange={handleInput}
            />
            <label for="days">days</label>
          </div>
          <div>
            <input
              class="recur-input"
              type="number"
              name="hours"
              min="0"
              value={formData.recur.hours}
              onChange={handleInput}
            />
            <label for="hours">hours</label>
          </div>
          <div>
            <input
              class="recur-input"
              type="number"
              name="minutes"
              min="0"
              value={formData.recur.minutes}
              onChange={handleInput}
            />
            <label for="minutes">minutes</label>
          </div>
        </div>
      </div>
      <button type="submit">Create</button>
    </form>
  );
}
