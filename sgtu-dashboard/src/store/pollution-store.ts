import { create } from 'zustand';
import { PollutionData, BruitData, PollutionAlerte, BruitAlerte } from '@/types/pollution.types';

interface PollutionStore {
  // Données pollution
  pollutionData: Record<string, PollutionData[]>;
  addPollutionData: (data: PollutionData) => void;
  
  // Données bruit
  bruitData: Record<string, BruitData[]>;
  addBruitData: (data: BruitData) => void;
  
  // Alertes
  alertes: PollutionAlerte[];
  alertesBruit: BruitAlerte[];
  addAlerte: (alerte: PollutionAlerte) => void;
  addAlerteBruit: (alerte: BruitAlerte) => void;
  
  // Utilitaires
  clearOldData: () => void;
}

const MAX_DATA_POINTS = 50; // Garder les 50 dernières mesures

export const usePollutionStore = create<PollutionStore>((set) => ({
  pollutionData: {},
  bruitData: {},
  alertes: [],
  alertesBruit: [],

  addPollutionData: (data) =>
    set((state) => {
      const zoneData = state.pollutionData[data.zone_id] || [];
      const newData = [...zoneData, data].slice(-MAX_DATA_POINTS);

      // Générer une alerte si niveau élevé
      const alertes = [...state.alertes];
      if (data.niveau_co2 > 80) {
        alertes.push({
          id: Date.now(),
          zone_id: data.zone_id,
          niveau_co2: data.niveau_co2,
          severity: 'HIGH',
          timestamp: data.timestamp,
          message: `Pollution critique détectée à ${data.zone_id}: ${data.niveau_co2.toFixed(1)} µg/m³`,
        });
      } else if (data.niveau_co2 > 50) {
        alertes.push({
          id: Date.now(),
          zone_id: data.zone_id,
          niveau_co2: data.niveau_co2,
          severity: 'MEDIUM',
          timestamp: data.timestamp,
          message: `Pollution moyenne détectée à ${data.zone_id}: ${data.niveau_co2.toFixed(1)} µg/m³`,
        });
      }

      return {
        pollutionData: {
          ...state.pollutionData,
          [data.zone_id]: newData,
        },
        alertes: alertes.slice(-10), // Garder les 10 dernières alertes
      };
    }),

  addBruitData: (data) =>
    set((state) => {
      const zoneData = state.bruitData[data.zone_id] || [];
      const newData = [...zoneData, data].slice(-MAX_DATA_POINTS);

      // Générer une alerte si niveau élevé
      const alertesBruit = [...state.alertesBruit];
      if (data.niveau_decibels > 85) {
        alertesBruit.push({
          id: Date.now(),
          zone_id: data.zone_id,
          niveau_decibels: data.niveau_decibels,
          severity: 'HIGH',
          timestamp: data.timestamp,
          message: `Niveau sonore critique détecté à ${data.zone_id}: ${data.niveau_decibels.toFixed(1)} dB`,
        });
      } else if (data.niveau_decibels > 70) {
        alertesBruit.push({
          id: Date.now(),
          zone_id: data.zone_id,
          niveau_decibels: data.niveau_decibels,
          severity: 'MEDIUM',
          timestamp: data.timestamp,
          message: `Niveau sonore moyen détecté à ${data.zone_id}: ${data.niveau_decibels.toFixed(1)} dB`,
        });
      }

      return {
        bruitData: {
          ...state.bruitData,
          [data.zone_id]: newData,
        },
        alertesBruit: alertesBruit.slice(-10),
      };
    }),

  addAlerte: (alerte) =>
    set((state) => ({
      alertes: [...state.alertes, alerte].slice(-10),
    })),

  addAlerteBruit: (alerte) =>
    set((state) => ({
      alertesBruit: [...state.alertesBruit, alerte].slice(-10),
    })),

  clearOldData: () =>
    set((state) => {
      const oneHourAgo = Date.now() - 60 * 60 * 1000;
      
      const cleanData = (data: Record<string, any[]>) => {
        const cleaned: Record<string, any[]> = {};
        Object.keys(data).forEach((zone) => {
          cleaned[zone] = data[zone].filter(
            (item) => new Date(item.timestamp).getTime() > oneHourAgo
          );
        });
        return cleaned;
      };

      return {
        pollutionData: cleanData(state.pollutionData),
        bruitData: cleanData(state.bruitData),
      };
    }),
}));