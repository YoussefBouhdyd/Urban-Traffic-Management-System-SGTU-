'use client';

import React, { useEffect, useState } from 'react';
import StatCard from '@/components/shared/StatCard';
import LoadingSpinner from '@/components/shared/LoadingSpinner';
import {
  faSmog,
  faVolumeUp,
  faCar,
  faTrafficLight,
  faVideo,
  faBell,
} from '@fortawesome/free-solid-svg-icons';
import { usePollutionStore } from '@/store/pollution-store';
import { useFluxStore } from '@/store/flux-store';
import { useFeuxStore } from '@/store/feux-store';
import { useCamerasStore } from '@/store/cameras-store';
import { useAlerts } from '@/hooks/useAlerts';
import { usePolling } from '@/hooks/usePolling';
import { useKafka } from '@/hooks/useKafka';
import { serviceCentralClient, serviceCamerasClient } from '@/lib/api-client';
import { REFRESH_INTERVALS } from '@/config/api-config';
import { roundNumber } from '@/lib/utils';
import { motion } from 'framer-motion';

export default function HomePage() {
  const [loading, setLoading] = useState(true);

  // Stores
  const pollutionStore = usePollutionStore();
  const fluxStore = useFluxStore();
  const feuxStore = useFeuxStore();
  const camerasStore = useCamerasStore();
  const alertesStore = useAlerts();

  // ========================================
  // KAFKA - POLLUTION & BRUIT (Membre 1)
  // ========================================
  useKafka('pollution-topic', {
    onMessage: (data) => {
      pollutionStore.addPollutionData(data);
    },
    enabled: true,
  });

  useKafka('bruit-topic', {
    onMessage: (data) => {
      pollutionStore.addBruitData(data);
    },
    enabled: true,
  });

  // ========================================
  // SERVICE CENTRAL - FLUX (Membre 2)
  // ========================================
  const fetchFlux = async () => {
    try {
      const latest = await serviceCentralClient.getFluxLatest();
      fluxStore.setLatestFlux(latest);

      const alerts = await serviceCentralClient.getAlerts();
      fluxStore.setAlertes(alerts);
    } catch (error) {
      console.error('Erreur flux:', error);
    }
  };

  usePolling(fetchFlux, REFRESH_INTERVALS.SERVICE_CENTRAL);

  // ========================================
  // SERVICE CENTRAL - FEUX (Membre 2)
  // ========================================
  const fetchFeux = async () => {
    try {
      const feux = await serviceCentralClient.getFeuxEtat();
      feuxStore.setFeux(feux);

      const config = await serviceCentralClient.getFeuxConfig();
      feuxStore.setConfig(config);
    } catch (error) {
      console.error('Erreur feux:', error);
    }
  };

  usePolling(fetchFeux, REFRESH_INTERVALS.SERVICE_CENTRAL);

  // ========================================
  // SERVICE CAMÉRAS (Membre 3)
  // ========================================
  const fetchCameras = async () => {
    try {
      const trafficState = await serviceCamerasClient.getTrafficLatest();
      camerasStore.setTrafficState(trafficState);

      const alerts = await serviceCamerasClient.getAlerts();
      camerasStore.setAlerts(alerts);

      const recommendations = await serviceCamerasClient.getRecommendations();
      camerasStore.setRecommendations(recommendations);
    } catch (error) {
      console.error('Erreur caméras:', error);
    }
  };

  usePolling(fetchCameras, REFRESH_INTERVALS.SERVICE_CAMERAS);

  // ========================================
  // CHARGEMENT INITIAL
  // ========================================
  useEffect(() => {
    const loadData = async () => {
      try {
        await Promise.all([fetchFlux(), fetchFeux(), fetchCameras()]);
      } finally {
        setLoading(false);
      }
    };
    loadData();
  }, []);

  // ========================================
  // CALCULS DES STATISTIQUES
  // ========================================
  const getLatestPollution = () => {
    const zones = Object.keys(pollutionStore.pollutionData);
    if (zones.length === 0) return null;

    let total = 0;
    let count = 0;

    zones.forEach((zone) => {
      const data = pollutionStore.pollutionData[zone];
      if (data.length > 0) {
        total += data[data.length - 1].niveau_co2;
        count++;
      }
    });

    return count > 0 ? roundNumber(total / count) : null;
  };

  const getLatestBruit = () => {
    const zones = Object.keys(pollutionStore.bruitData);
    if (zones.length === 0) return null;

    let total = 0;
    let count = 0;

    zones.forEach((zone) => {
      const data = pollutionStore.bruitData[zone];
      if (data.length > 0) {
        total += data[data.length - 1].niveau_decibels;
        count++;
      }
    });

    return count > 0 ? roundNumber(total / count) : null;
  };

  const getTotalFlux = () => {
    const routes = Object.values(fluxStore.latestFlux);
    if (routes.length === 0) return null;
    return routes.reduce((sum, route) => sum + route.flux, 0);
  };

  const getFeuxActifs = () => {
    return feuxStore.feux.filter((f) => f.green).length;
  };

  const getTrafficStateLabel = () => {
    if (!camerasStore.trafficState) return 'Chargement...';
    return camerasStore.trafficState.trafficState;
  };

  const getPollutionSeverity = (): 'NORMAL' | 'MEDIUM' | 'HIGH' => {
    const niveau = getLatestPollution();
    if (!niveau) return 'NORMAL';
    if (niveau > 80) return 'HIGH';
    if (niveau > 50) return 'MEDIUM';
    return 'NORMAL';
  };

  const getBruitSeverity = (): 'NORMAL' | 'MEDIUM' | 'HIGH' => {
    const niveau = getLatestBruit();
    if (!niveau) return 'NORMAL';
    if (niveau > 85) return 'HIGH';
    if (niveau > 70) return 'MEDIUM';
    return 'NORMAL';
  };

  if (loading) {
    return <LoadingSpinner text="Chargement du système..." />;
  }

  return (
    <div className="space-y-8">
      {/* En-tête */}
      <div>
        <h1 className="text-3xl font-bold text-gray-900 mb-2">Vue d'ensemble</h1>
        <p className="text-gray-600">
          Surveillance en temps réel du système de gestion du trafic urbain
        </p>
      </div>

      {/* ========================================
          STATISTIQUES GLOBALES (6 Cards)
          ======================================== */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {/* POLLUTION (Membre 1) */}
        <StatCard
          title="Pollution Moyenne"
          value={getLatestPollution() !== null ? `${getLatestPollution()} µg/m³` : '--'}
          subtitle="Qualité de l'air"
          icon={faSmog}
          color="pollution"
          severity={getPollutionSeverity()}
        />

        {/* BRUIT (Membre 1) */}
        <StatCard
          title="Niveau Sonore Moyen"
          value={getLatestBruit() !== null ? `${getLatestBruit()} dB` : '--'}
          subtitle="Nuisances sonores"
          icon={faVolumeUp}
          color="bruit"
          severity={getBruitSeverity()}
        />


<StatCard
  title="Flux Total"
  value={getTotalFlux() !== null ? String(getTotalFlux()) : '--'}
  subtitle="Véhicules sur toutes les routes"
  icon={faCar}
  color="flux"
  severity={
    fluxStore.alertes.length > 0
      ? 'HIGH'
      : getTotalFlux() !== null && getTotalFlux()! > 100
      ? 'MEDIUM'
      : 'NORMAL'
  }
/>

        {/* FEUX (Membre 2) */}
        <StatCard
          title="Feux Actifs"
          value={`${getFeuxActifs()} / ${feuxStore.feux.length}`}
          subtitle="Intersections en vert"
          icon={faTrafficLight}
          color="feux"
        />

        {/* CAMÉRAS (Membre 3) */}
        <StatCard
          title="État du Trafic"
          value={getTrafficStateLabel()}
          subtitle="Analyse par caméras"
          icon={faVideo}
          color="cameras"
          severity={camerasStore.trafficState?.severity as any}
        />

        {/* ALERTES (Tous services) */}
        <StatCard
          title="Alertes Actives"
          value={alertesStore.alerts.filter((a) => a.status === 'ACTIVE').length}
          subtitle="Notifications du système"
          icon={faBell}
          color="flux"
          severity={
            alertesStore.alerts.some((a) => a.severity === 'CRITICAL' || a.severity === 'HIGH')
              ? 'HIGH'
              : 'NORMAL'
          }
        />
      </div>

      {/* ========================================
          ALERTES RÉCENTES
          ======================================== */}
      <div className="card">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-xl font-semibold text-gray-900">Alertes Récentes</h2>
          <span className="text-sm text-gray-500">
            {alertesStore.alerts.filter((a) => a.status === 'ACTIVE').length} actives
          </span>
        </div>

        <div className="space-y-3">
          {alertesStore.alerts
            .filter((a) => a.status === 'ACTIVE')
            .slice(0, 5)
            .map((alert) => (
              <motion.div
                key={alert.id}
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                className="flex items-start space-x-3 p-3 rounded-lg bg-gray-50 hover:bg-gray-100 transition-colors"
              >
                <div
                  className="w-2 h-2 rounded-full mt-2 flex-shrink-0"
                  style={{
                    backgroundColor:
                      alert.severity === 'HIGH' || alert.severity === 'CRITICAL'
                        ? '#EF4444'
                        : alert.severity === 'MEDIUM'
                        ? '#F59E0B'
                        : '#10B981',
                  }}
                />
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium text-gray-900">{alert.message}</p>
                  <div className="flex items-center space-x-2 mt-1">
                    <span className="text-xs text-gray-500">Source: {alert.source}</span>
                    <span className="text-xs text-gray-400">•</span>
                    <span className="text-xs text-gray-500">{alert.timestamp}</span>
                  </div>
                </div>
                <span
                  className={`badge flex-shrink-0 ${
                    alert.severity === 'HIGH' || alert.severity === 'CRITICAL'
                      ? 'badge-danger'
                      : alert.severity === 'MEDIUM'
                      ? 'badge-warning'
                      : 'badge-success'
                  }`}
                >
                  {alert.severity}
                </span>
              </motion.div>
            ))}

          {alertesStore.alerts.filter((a) => a.status === 'ACTIVE').length === 0 && (
            <div className="text-center py-8 text-gray-500">
              <p>Aucune alerte active</p>
              <p className="text-sm mt-1">Le système fonctionne normalement</p>
            </div>
          )}
        </div>
      </div>

      {/* ========================================
          APERÇU RAPIDE DES SERVICES
          ======================================== */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Qualité de l'Air */}
        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-3">Qualité de l'Air</h3>
          <div className="space-y-2">
            {Object.keys(pollutionStore.pollutionData).map((zone) => {
              const data = pollutionStore.pollutionData[zone];
              const latest = data[data.length - 1];
              return (
                <div key={zone} className="flex items-center justify-between text-sm">
                  <span className="text-gray-600">{zone}</span>
                  <span className="font-medium text-mono">
                    {roundNumber(latest.niveau_co2)} µg/m³
                  </span>
                </div>
              );
            })}
          </div>
        </div>

        {/* Flux Routier */}
        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-3">Flux Routier</h3>
          <div className="space-y-2">
            {Object.values(fluxStore.latestFlux).map((flux) => (
              <div key={flux.name} className="flex items-center justify-between text-sm">
                <span className="text-gray-600 capitalize">{flux.name}</span>
                <span className="font-medium text-mono">{flux.flux} véhicules</span>
              </div>
            ))}
          </div>
        </div>

        {/* Recommandations */}
        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-3">Recommandations</h3>
          <div className="space-y-2">
            {camerasStore.recommendations.slice(0, 3).map((rec) => (
              <div
                key={rec.id}
                className="text-sm p-2 rounded bg-blue-50 border border-blue-200"
              >
                <p className="text-blue-900 font-medium">{rec.recommendation}</p>
                <span
                  className={`badge mt-1 ${
                    rec.priority === 'HIGH'
                      ? 'badge-danger'
                      : rec.priority === 'MEDIUM'
                      ? 'badge-warning'
                      : 'badge-info'
                  }`}
                >
                  {rec.priority}
                </span>
              </div>
            ))}
            {camerasStore.recommendations.length === 0 && (
              <p className="text-sm text-gray-500 text-center py-4">
                Aucune recommandation pour le moment
              </p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}