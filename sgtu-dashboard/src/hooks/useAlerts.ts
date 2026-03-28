'use client';

import { useEffect } from 'react';
import { useAlertesStore } from '@/store/alertes-store';
import { usePollutionStore } from '@/store/pollution-store';
import { useFluxStore } from '@/store/flux-store';
import { useCamerasStore } from '@/store/cameras-store';

/**
 * Hook pour agréger toutes les alertes de tous les services
 */
export function useAlerts() {
  const alertesStore = useAlertesStore();
  const pollutionStore = usePollutionStore();
  const fluxStore = useFluxStore();
  const camerasStore = useCamerasStore();

  useEffect(() => {
    // Agréger les alertes de pollution
    const pollutionAlerts = pollutionStore.alertes.map(alert => ({
      id: `pollution-${alert.id}`,
      source: 'POLLUTION' as const,
      type: 'POLLUTION_LEVEL',
      severity: alert.severity as any,
      message: alert.message,
      timestamp: alert.timestamp,
      status: 'ACTIVE' as const,
      metadata: { zone_id: alert.zone_id, niveau: alert.niveau_co2 },
    }));

    // Agréger les alertes de bruit
    const bruitAlerts = pollutionStore.alertesBruit.map(alert => ({
      id: `bruit-${alert.id}`,
      source: 'BRUIT' as const,
      type: 'NOISE_LEVEL',
      severity: alert.severity as any,
      message: alert.message,
      timestamp: alert.timestamp,
      status: 'ACTIVE' as const,
      metadata: { zone_id: alert.zone_id, niveau: alert.niveau_decibels },
    }));

    // Agréger les alertes de flux
    const fluxAlerts = fluxStore.alertes.map((alert, index) => ({
      id: `flux-${index}`,
      source: 'FLUX' as const,
      type: 'CONGESTION',
      severity: 'HIGH' as const,
      message: `Congestion détectée sur ${alert.name} (${alert.flux} véhicules)`,
      timestamp: alert.timestamp,
      status: 'ACTIVE' as const,
      metadata: { route: alert.name, flux: alert.flux },
    }));

    // Agréger les alertes caméras
    const cameraAlerts = camerasStore.alerts.map(alert => ({
      id: `camera-${alert.id}`,
      source: 'CAMERAS' as const,
      type: alert.type,
      severity: alert.severity as any,
      message: alert.message,
      timestamp: alert.timestamp,
      status: alert.status as any,
      metadata: {},
    }));

    // Mettre à jour le store d'alertes global
    alertesStore.setAlerts([
      ...pollutionAlerts,
      ...bruitAlerts,
      ...fluxAlerts,
      ...cameraAlerts,
    ]);
  }, [
    pollutionStore.alertes,
    pollutionStore.alertesBruit,
    fluxStore.alertes,
    camerasStore.alerts,
  ]);

  return alertesStore;
}