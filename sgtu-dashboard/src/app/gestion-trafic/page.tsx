'use client';

import React, { useEffect } from 'react';
import FluxRoutes from '@/components/gestion-trafic/FluxRoutes';
import FluxChart from '@/components/gestion-trafic/FluxChart';
import AlertesCongestion from '@/components/gestion-trafic/AlertesCongestion';
import { useFluxStore } from '@/store/flux-store';
import { usePolling } from '@/hooks/usePolling';
import { serviceCentralClient } from '@/lib/api-client';
import { REFRESH_INTERVALS } from '@/config/api-config';
import LoadingSpinner from '@/components/shared/LoadingSpinner';

export default function GestionTraficPage() {
  const fluxStore = useFluxStore();
  const [loading, setLoading] = React.useState(true);

  // Récupérer les données
  const fetchFlux = async () => {
    try {
      const latest = await serviceCentralClient.getFluxLatest();
      fluxStore.setLatestFlux(latest);

      const all = await serviceCentralClient.getFluxAll();
      fluxStore.setFluxData(all);

      const alerts = await serviceCentralClient.getAlerts();
      fluxStore.setAlertes(alerts);
    } catch (error) {
      console.error('Erreur lors de la récupération des données de flux:', error);
    } finally {
      setLoading(false);
    }
  };

  usePolling(fetchFlux, REFRESH_INTERVALS.SERVICE_CENTRAL);

  useEffect(() => {
    fetchFlux();
  }, []);

  if (loading) {
    return <LoadingSpinner text="Chargement des données de trafic..." />;
  }

  return (
    <div className="space-y-8">
      {/* En-tête */}
      <div>
        <h1 className="text-3xl font-bold text-gray-900 mb-2">Gestion du Trafic</h1>
        <p className="text-gray-600">
          Surveillance en temps réel du flux routier sur les 4 routes principales
        </p>
      </div>

      {/* Flux par route */}
      <div>
        <h2 className="text-xl font-semibold text-gray-900 mb-4">État Actuel du Trafic</h2>
        <FluxRoutes />
      </div>

      {/* Graphique d'évolution */}
      <div className="card">
        <h2 className="text-xl font-semibold text-gray-900 mb-4">Évolution du Flux</h2>
        <FluxChart />
      </div>

      {/* Alertes */}
      <AlertesCongestion />
    </div>
  );
}