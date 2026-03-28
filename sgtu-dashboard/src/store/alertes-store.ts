import { create } from 'zustand';
import { Alert, AlertFilters, AlertStats } from '@/types/alertes.types';

interface AlertesStore {
  alerts: Alert[];
  filters: AlertFilters;
  
  setAlerts: (alerts: Alert[]) => void;
  setFilters: (filters: Partial<AlertFilters>) => void;
  
  getFilteredAlerts: () => Alert[];
  getStats: () => AlertStats;
}

const defaultFilters: AlertFilters = {
  sources: [],
  severities: [],
  statuses: ['ACTIVE'],
};

export const useAlertesStore = create<AlertesStore>((set, get) => ({
  alerts: [],
  filters: defaultFilters,

  setAlerts: (alerts) => set({ alerts }),
  
  setFilters: (newFilters) =>
    set((state) => ({
      filters: { ...state.filters, ...newFilters },
    })),

  getFilteredAlerts: () => {
    const { alerts, filters } = get();
    
    return alerts.filter((alert) => {
      if (filters.sources.length > 0 && !filters.sources.includes(alert.source)) {
        return false;
      }
      if (filters.severities.length > 0 && !filters.severities.includes(alert.severity)) {
        return false;
      }
      if (filters.statuses.length > 0 && !filters.statuses.includes(alert.status)) {
        return false;
      }
      return true;
    });
  },

  getStats: () => {
    const alerts = get().alerts;
    
    const stats: AlertStats = {
      total: alerts.length,
      bySeverity: { LOW: 0, MEDIUM: 0, HIGH: 0, CRITICAL: 0 },
      bySource: { POLLUTION: 0, BRUIT: 0, FLUX: 0, CAMERAS: 0 },
      byStatus: { ACTIVE: 0, RESOLVED: 0 },
    };

    alerts.forEach((alert) => {
      stats.bySeverity[alert.severity]++;
      stats.bySource[alert.source]++;
      stats.byStatus[alert.status]++;
    });

    return stats;
  },
}));