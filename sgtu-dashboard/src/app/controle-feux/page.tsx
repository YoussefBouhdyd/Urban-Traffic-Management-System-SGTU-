'use client';

import React, { useEffect } from 'react';
import IntersectionView from '@/components/controle-feux/IntersectionView';
import FeuxControls from '@/components/controle-feux/FeuxControls';
import FeuxConfig from '@/components/controle-feux/FeuxConfig';
import { useFeuxStore } from '@/store/feux-store';
import { usePolling } from '@/hooks/usePolling';
import { serviceCentralClient } from '@/lib/api-client';
import { REFRESH_INTERVALS } from '@/config/api-config';
import LoadingSpinner from '@/components/shared/LoadingSpinner';

export default function ControleFeuxPage() {
  const feuxStore = useFeuxStore();
  const [loading, setLoading] = React.useState(true);

  const fetchFeux = async () => {
    try {
      const feux = await serviceCentralClient.getFeuxEtat();
      feuxStore.setFeux(feux);

      const config = await serviceCentralClient.getFeuxConfig();
      feuxStore.setConfig(config);
    } catch (error) {
      console.error('Erreur lors de la récupération des feux:', error);
    } finally {
      setLoading(false);
    }
  };

  usePolling(fetchFeux, REFRESH_INTERVALS.SERVICE_CENTRAL);

  useEffect(() => {
    fetchFeux();
  }, []);

  if (loading) {
    return <LoadingSpinner text="Chargement de l'état des feux..." />;
  }

  return (
    <div className="space-y-8">
      {/* En-tête */}
      <div>
        <h1 className="text-3xl font-bold text-gray-900 mb-2">Contrôle des Feux</h1>
        <p className="text-gray-600">
          Gestion en temps réel des feux de circulation de l'intersection principale
        </p>
      </div>

      {/* Vue de l'intersection */}
      <IntersectionView />

      {/* Configuration et Contrôles */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <FeuxConfig />
        <FeuxControls />
      </div>

      {/* État détaillé */}
      <div className="card">
        <h2 className="text-xl font-semibold text-gray-900 mb-4">État Détaillé des Feux</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          {feuxStore.feux.map((feu) => (
            <div
              key={feu.routeId}
              className={`p-4 rounded-lg border-2 ${
                feu.green
                  ? 'bg-green-50 border-green-300'
                  : 'bg-red-50 border-red-300'
              }`}
            >
              <div className="flex items-center justify-between mb-2">
                <h3 className="font-semibold text-gray-900 capitalize">{feu.routeId}</h3>
                <div
                  className={`w-4 h-4 rounded-full ${
                    feu.green ? 'bg-green-500' : 'bg-red-500'
                  } animate-pulse-glow`}
                />
              </div>
              <p className="text-sm text-gray-600 mb-2">{feu.name}</p>
              <div className="flex items-center justify-between text-xs">
                <span className="text-gray-500">Temps restant:</span>
                <span className="font-mono font-semibold">{feu.remaining}s</span>
              </div>
              <div className="flex items-center justify-between text-xs mt-1">
                <span className="text-gray-500">Segment:</span>
                <span className="font-medium">{feu.segment ? 'Nord/Sud' : 'Est/Ouest'}</span>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}