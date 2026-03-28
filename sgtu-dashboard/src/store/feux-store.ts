import { create } from 'zustand';
import { FeuxState, FeuxConfig } from '@/types/feux.types';

interface FeuxStore {
  feux: FeuxState[];
  config: FeuxConfig | null;
  
  setFeux: (feux: FeuxState[]) => void;
  setConfig: (config: FeuxConfig) => void;
  
  getFeuxByRoute: (routeId: string) => FeuxState | null;
}

export const useFeuxStore = create<FeuxStore>((set, get) => ({
  feux: [],
  config: null,

  setFeux: (feux) => set({ feux }),
  setConfig: (config) => set({ config }),

  getFeuxByRoute: (routeId) => {
    const state = get();
    return state.feux.find((f) => f.routeId === routeId) || null;
  },
}));