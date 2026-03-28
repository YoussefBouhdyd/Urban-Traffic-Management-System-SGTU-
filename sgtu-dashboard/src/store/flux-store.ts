import { create } from 'zustand';
import { FluxData, FluxAlerte, RouteFlux } from '@/types/flux.types';

interface FluxStore {
  fluxData: Record<string, FluxData[]>;
  latestFlux: Record<string, FluxData>;
  alertes: FluxAlerte[];
  
  setFluxData: (data: FluxData[]) => void;
  setLatestFlux: (data: FluxData[]) => void;
  setAlertes: (alertes: FluxAlerte[]) => void;
  
  getRouteFlux: (routeName: string) => RouteFlux | null;
}

export const useFluxStore = create<FluxStore>((set, get) => ({
  fluxData: {},
  latestFlux: {},
  alertes: [],

  setFluxData: (data) =>
    set((state) => {
      const newFluxData: Record<string, FluxData[]> = {};
      
      data.forEach((flux) => {
        if (!newFluxData[flux.name]) {
          newFluxData[flux.name] = [];
        }
        newFluxData[flux.name].push(flux);
      });

      return { fluxData: newFluxData };
    }),

  setLatestFlux: (data) =>
    set(() => {
      const latest: Record<string, FluxData> = {};
      data.forEach((flux) => {
        latest[flux.name] = flux;
      });
      return { latestFlux: latest };
    }),

  setAlertes: (alertes) => set({ alertes }),

  getRouteFlux: (routeName) => {
    const state = get();
    const flux = state.latestFlux[routeName];
    
    if (!flux) return null;

    let level: 'NORMAL' | 'BUSY' | 'CONGESTED' = 'NORMAL';
    if (flux.flux >= 100) level = 'CONGESTED';
    else if (flux.flux >= 30) level = 'BUSY';

    return {
      routeId: routeName as any,
      name: routeName,
      flux: flux.flux,
      timestamp: flux.timestamp,
      level,
    };
  },
}));