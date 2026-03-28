'use client';

import React, { useEffect } from 'react';
import CameraStatus from '@/components/surveillance-video/CameraStatus';
import AnalyseTrafic from '@/components/surveillance-video/AnalyseTrafic';
import Recommandations from '@/components/surveillance-video/Recommandations';
import TrafficHistoryChart from '@/components/surveillance-video/TrafficHistoryChart';
import { useCamerasStore } from '@/store/cameras-store';
import { usePolling } from '@/hooks/usePolling';
import { serviceCamerasClient } from '@/lib/api-client';
import { REFRESH_INTERVALS } from '@/config/api-config';
import LoadingSpinner from '@/components/shared/LoadingSpinner';

export default function SurveillanceVideoPage() {
  const camerasStore = useCamerasStore();
  const [loading, setLoading] = React.useState(true);

  const fetchCameraData = async () => {
    try {
      const trafficState = await serviceCamerasClient.getTrafficLatest();
      camerasStore.setTrafficState(trafficState);

      const history = await serviceCamerasClient.getTrafficHistory();
      camerasStore.setTrafficHistory(history);

      const alerts = await serviceCamerasClient.getAlerts();
      camerasStore.setAlerts(alerts);

      const recommendations = await serviceCamerasClient.getRecommendations();
      camerasStore.setRecommendations(recommendations);

      // Charger le statut de la caméra principale
      const cameraStatus = await serviceCamerasClient.getCameraStatus('CAM-01');
      camerasStore.setCameraStatus(cameraStatus);
    } catch (error) {
      console.error('Erreur lors de la récupération des données caméras:', error);
    }
  };

  usePolling(fetchCameraData, REFRESH_INTERVALS.SERVICE_CAMERAS);

  useEffect(() => {
    fetchCameraData().then(() => setLoading(false));
  }, []);

  if (loading) {
    return <LoadingSpinner text="Chargement de l'analyse vidéo..." />;
  }

  return (
    <div className="space-y-8">
      {/* En-tête */}
      <div>
        <h1 className="text-3xl font-bold text-gray-900 mb-2">Surveillance Vidéo</h1>
        <p className="text-gray-600">
          Analyse intelligente du trafic par caméras et recommandations IA
        </p>
      </div>

      {/* Analyse actuelle */}
      <AnalyseTrafic />

      {/* Historique */}
      <div className="card">
        <h2 className="text-xl font-semibold text-gray-900 mb-4">Historique du Trafic</h2>
        <TrafficHistoryChart />
      </div>

      {/* Recommandations et Caméras */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <Recommandations />
        <CameraStatus />
      </div>
    </div>
  );
}