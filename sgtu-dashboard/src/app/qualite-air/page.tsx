'use client';

import React from 'react';
import PollutionChart from '@/components/qualite-air/PollutionChart';
import BruitChart from '@/components/qualite-air/BruitChart';
import AlertesQualiteAir from '@/components/qualite-air/AlertesQualiteAir';
import StatCard from '@/components/shared/StatCard';
import { faSmog, faVolumeUp } from '@fortawesome/free-solid-svg-icons';
import { usePollutionStore } from '@/store/pollution-store';
import { useKafka } from '@/hooks/useKafka';
import { roundNumber } from '@/lib/utils';
import { POLLUTION_THRESHOLDS, BRUIT_THRESHOLDS, getPollutionLevel, getBruitLevel } from '@/constants/thresholds';

export default function QualiteAirPage() {
  const pollutionStore = usePollutionStore();

  // Connexion Kafka
  useKafka('pollution-topic', {
    onMessage: (data) => pollutionStore.addPollutionData(data),
  });

  useKafka('bruit-topic', {
    onMessage: (data) => pollutionStore.addBruitData(data),
  });

  // Calculer les moyennes
  const getAveragePollution = () => {
    const zones = Object.keys(pollutionStore.pollutionData);
    if (zones.length === 0) return 0;

    let total = 0;
    let count = 0;

    zones.forEach((zone) => {
      const data = pollutionStore.pollutionData[zone];
      if (data.length > 0) {
        total += data[data.length - 1].niveau_co2;
        count++;
      }
    });

    return count > 0 ? total / count : 0;
  };

  const getAverageBruit = () => {
    const zones = Object.keys(pollutionStore.bruitData);
    if (zones.length === 0) return 0;

    let total = 0;
    let count = 0;

    zones.forEach((zone) => {
      const data = pollutionStore.bruitData[zone];
      if (data.length > 0) {
        total += data[data.length - 1].niveau_decibels;
        count++;
      }
    });

    return count > 0 ? total / count : 0;
  };

  return (
    <div className="space-y-8">
      {/* En-tête */}
      <div>
        <h1 className="text-3xl font-bold text-gray-900 mb-2">Qualité de l'Air</h1>
        <p className="text-gray-600">
          Surveillance en temps réel de la pollution atmosphérique et des niveaux sonores
        </p>
      </div>

      {/* Statistiques */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <StatCard
          title="Pollution Moyenne"
          value={`${roundNumber(getAveragePollution())} µg/m³`}
          subtitle="Toutes zones confondues"
          icon={faSmog}
          color="pollution"
          severity={getPollutionLevel(getAveragePollution())}
        />
        <StatCard
          title="Bruit Moyen"
          value={`${roundNumber(getAverageBruit())} dB`}
          subtitle="Toutes zones confondues"
          icon={faVolumeUp}
          color="bruit"
          severity={getBruitLevel(getAverageBruit())}
        />
      </div>

      {/* Graphiques */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="card">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">Pollution Atmosphérique</h2>
          <PollutionChart />
          <div className="mt-4 grid grid-cols-3 gap-4 text-sm">
            <div>
              <p className="text-gray-600">Seuil Normal</p>
              <p className="font-medium text-green-600">&lt; {POLLUTION_THRESHOLDS.NORMAL_MAX} µg/m³</p>
            </div>
            <div>
              <p className="text-gray-600">Seuil Moyen</p>
              <p className="font-medium text-yellow-600">{POLLUTION_THRESHOLDS.NORMAL_MAX}-{POLLUTION_THRESHOLDS.MEDIUM_MAX} µg/m³</p>
            </div>
            <div>
              <p className="text-gray-600">Seuil Critique</p>
              <p className="font-medium text-red-600">&gt; {POLLUTION_THRESHOLDS.MEDIUM_MAX} µg/m³</p>
            </div>
          </div>
        </div>

        <div className="card">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">Niveaux Sonores</h2>
          <BruitChart />
          <div className="mt-4 grid grid-cols-3 gap-4 text-sm">
            <div>
              <p className="text-gray-600">Seuil Normal</p>
              <p className="font-medium text-green-600">&lt; {BRUIT_THRESHOLDS.NORMAL_MAX} dB</p>
            </div>
            <div>
              <p className="text-gray-600">Seuil Moyen</p>
              <p className="font-medium text-yellow-600">{BRUIT_THRESHOLDS.NORMAL_MAX}-{BRUIT_THRESHOLDS.MEDIUM_MAX} dB</p>
            </div>
            <div>
              <p className="text-gray-600">Seuil Critique</p>
              <p className="font-medium text-red-600">&gt; {BRUIT_THRESHOLDS.MEDIUM_MAX} dB</p>
            </div>
          </div>
        </div>
      </div>

      {/* Détails par zone */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="card">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">Pollution par Zone</h2>
          <div className="space-y-3">
            {Object.keys(pollutionStore.pollutionData).map((zone) => {
              const data = pollutionStore.pollutionData[zone];
              const latest = data[data.length - 1];
              const level = getPollutionLevel(latest?.niveau_co2 || 0);

              return (
                <div key={zone} className="flex items-center justify-between p-3 rounded-lg bg-gray-50">
                  <div>
                    <p className="font-medium text-gray-900">{zone}</p>
                    <p className="text-sm text-gray-500">{data.length} mesures</p>
                  </div>
                  <div className="text-right">
                    <p className="text-2xl font-bold text-mono">{roundNumber(latest?.niveau_co2 || 0)} µg/m³</p>
                    <span className={`badge ${level === 'HIGH' ? 'badge-danger' : level === 'MEDIUM' ? 'badge-warning' : 'badge-success'}`}>
                      {level}
                    </span>
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        <div className="card">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">Bruit par Zone</h2>
          <div className="space-y-3">
            {Object.keys(pollutionStore.bruitData).map((zone) => {
              const data = pollutionStore.bruitData[zone];
              const latest = data[data.length - 1];
              const level = getBruitLevel(latest?.niveau_decibels || 0);

              return (
                <div key={zone} className="flex items-center justify-between p-3 rounded-lg bg-gray-50">
                  <div>
                    <p className="font-medium text-gray-900">{zone}</p>
                    <p className="text-sm text-gray-500">{data.length} mesures</p>
                  </div>
                  <div className="text-right">
                    <p className="text-2xl font-bold text-mono">{roundNumber(latest?.niveau_decibels || 0)} dB</p>
                    <span className={`badge ${level === 'HIGH' ? 'badge-danger' : level === 'MEDIUM' ? 'badge-warning' : 'badge-success'}`}>
                      {level}
                    </span>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </div>

      {/* Alertes */}
      <AlertesQualiteAir />
    </div>
  );
}