import { useState, useEffect } from 'preact/hooks';

export function useGetEvents(id?: string) {
  const [isLoading, setIsLoading] = useState(true);
  const [data, setData] = useState();
  const [error, setError] = useState();
  const base = 'http://localhost:3000/api/events';

  useEffect(() => {
    const abortController = new AbortController();
    const url = id
      ? `${base}/${id}`
      : base;
    const props: RequestInit = {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      },
      signal: abortController.signal
    };

    const getData = async () => {
      try {
        setIsLoading(true);
        const res = await fetch(url, props);
        const json = await res.json();
        setData(json);
      } catch (e) {
        setError(e);
      } finally {
        setIsLoading(false);
      }
    };
    getData();

    return () => {
      abortController.abort();
    };
  }, [id]);

  return { isLoading, data, error };
}
