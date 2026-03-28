'use client';

import { useEffect, useRef } from 'react';

/**
 * Hook pour effectuer un polling (appel répété) d'une fonction
 */
export function usePolling(callback: () => void, interval: number, enabled: boolean = true) {
  const savedCallback = useRef(callback);

  // Mettre à jour la référence si callback change
  useEffect(() => {
    savedCallback.current = callback;
  }, [callback]);

  useEffect(() => {
    if (!enabled) return;

    // Appeler immédiatement
    savedCallback.current();

    // Puis appeler à intervalle régulier
    const id = setInterval(() => {
      savedCallback.current();
    }, interval);

    return () => clearInterval(id);
  }, [interval, enabled]);
}