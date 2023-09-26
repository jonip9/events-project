import { useCallback, useState } from "preact/hooks";

export function useOnFormInput() {
  const [formData, setFormData] = useState({
    summary: '',
    descr: '',
    dtstart: '',
    dtend: '',
    recur: {
      years: 0,
      months: 0,
      days: 0,
      hours: 0,
      minutes: 0
    }
  });

  const handleInput = useCallback((e: Event) => {
    const target = e.currentTarget as HTMLInputElement;
    const name = target.name;
    const value = target.value;
    const valueNum = target.valueAsNumber;

    if (target.className === 'recur-input') {
      setFormData((state) => ({
        ...state,
        recur: {
          ...state.recur,
          [name]: valueNum
        }
      }));
    } else {
      setFormData((state) => ({
        ...state,
        [name]: value
      }));
    }
  }, []);

  return { formData, handleInput };
}
