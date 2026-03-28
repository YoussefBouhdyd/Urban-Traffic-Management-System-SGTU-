'use client';

import { useEffect, useRef } from 'react';

interface UseKafkaOptions {
  onMessage: (data: any) => void;
  enabled?: boolean;
}

/**
 * Hook pour se connecter au WebSocket Kafka
 * (Sera connecté à l'API route /api/kafka)
 */
export function useKafka(topic: string, options: UseKafkaOptions) {
  const { onMessage, enabled = true } = options;
  const wsRef = useRef<WebSocket | null>(null);
  const onMessageRef = useRef(onMessage);

  // Keep callback ref updated
  useEffect(() => {
    onMessageRef.current = onMessage;
  }, [onMessage]);

  useEffect(() => {
    if (!enabled) return;

    console.log(`[useKafka] Connexion au topic: ${topic}`);

    // Liste des zones pour simulation
    const zones = ['ZONE-01', 'ZONE-02', 'ZONE-03', 'ZONE-04'];
    let zoneIndex = 0;

    // Simulation de données (à remplacer par WebSocket réel)
    const simulateData = () => {
      const currentZone = zones[zoneIndex % zones.length];
      zoneIndex++;

      if (topic === 'pollution-topic') {
        onMessageRef.current({
          zone_id: currentZone,
          niveau_co2: 30 + Math.random() * 70,
          timestamp: new Date().toISOString(),
        });
      } else if (topic === 'bruit-topic') {
        onMessageRef.current({
          zone_id: currentZone,
          niveau_decibels: 50 + Math.random() * 50,
          timestamp: new Date().toISOString(),
        });
      }
    };

    // Générer une donnée initiale après un court délai
    const initialTimeout = setTimeout(simulateData, 100);

    // Continuer à simuler les données toutes les 3 secondes
    const interval = setInterval(simulateData, 3000);

    return () => {
      clearTimeout(initialTimeout);
      clearInterval(interval);
      if (wsRef.current) {
        wsRef.current.close();
      }
    };
  }, [topic, enabled]);
}