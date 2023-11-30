export function partition<Type>(arr: Type[], step: number): Type[][] {
  const partitioned = [];
  const alen = arr.length;

  for (let i = 0; i <= alen; i = i + step) {
    partitioned.push(arr.slice(i, i + step));
  }

  return partitioned;
}
