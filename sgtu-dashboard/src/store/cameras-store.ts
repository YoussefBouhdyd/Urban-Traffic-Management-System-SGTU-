import { create } from 'zustand';
import {
  TrafficState,
  TrafficHistory,
  CameraAlert,
  Recommendation,
  CameraStatus,
} from '@/types/cameras.types';

interface CamerasStore {
  trafficState: TrafficState | null;
  trafficHistory: TrafficHistory[];
  alerts: CameraAlert[];
  recommendations: Recommendation[];
  cameraStatuses: Record<string, CameraStatus>;
  
  setTrafficState: (state: TrafficState) => void;
  setTrafficHistory: (history: TrafficHistory[]) => void;
  setAlerts: (alerts: CameraAlert[]) => void;
  setRecommendations: (recs: Recommendation[]) => void;
  setCameraStatus: (status: CameraStatus) => void;
}

export const useCamerasStore = create<CamerasStore>((set) => ({
  trafficState: null,
  trafficHistory: [],
  alerts: [],
  recommendations: [],
  cameraStatuses: {},

  setTrafficState: (state) => set({ trafficState: state }),
  setTrafficHistory: (history) => set({ trafficHistory: history }),
  setAlerts: (alerts) => set({ alerts }),
  setRecommendations: (recs) => set({ recommendations: recs }),
  
  setCameraStatus: (status) =>
    set((state) => ({
      cameraStatuses: {
        ...state.cameraStatuses,
        [status.cameraId]: status,
      },
    })),
}));