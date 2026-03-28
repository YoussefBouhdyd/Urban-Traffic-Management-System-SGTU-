'use client';

import React from 'react';
import { useCamerasStore } from '@/store/cameras-store';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCar, faTachometerAlt, faExclamationTriangle } from '@fortawesome/free-solid-svg-icons';
import { getTrafficStateLabel, getSeverityLabel } from '@/lib/utils';
import { SEVERITY_COLORS, STATUS_COLORS } from '@/constants/colors';

export default function AnalyseTrafic() {
  const trafficState = useCamerasStore((state) => state.trafficState);

  if (!trafficState) {
    return (
      <div className="card">
        <h2 className="text-xl font-semibold text-gray-900 mb-4">Analyse du Trafic</h2>
        <div className="text-center py-8 text-gray-500">
          <p>Chargement de l'analyse...</p>
        </div>
      </div>
    );
  }

  const getStateColor = (state: string) => {
    return STATUS_COLORS[state.toUpperCase() as keyof typeof STATUS_COLORS] || STATUS_COLORS.NORMAL;
  };

  return (
    <div className="card">
      <h2 className="text-xl font-semibold text-gray-900 mb-4">Analyse du Trafic en Temps Réel</h2>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
        {/* État du trafic */}
        <div
          className="p-4 rounded-lg border-2"
          style={{
            borderColor: getStateColor(trafficState.trafficState),
            backgroundColor: `${getStateColor(trafficState.trafficState)}10`,
          }}
        >
          <p className="text-sm text-gray-600 mb-1">État du Trafic</p>
          <p className="text-2xl font-bold" style={{ color: getStateColor(trafficState.trafficState) }}>
            {getTrafficStateLabel(trafficState.trafficState)}
          </p>
        </div>

        {/* Sévérité */}
        <div
          className="p-4 rounded-lg border-2"
          style={{
            borderColor: SEVERITY_COLORS[trafficState.severity as keyof typeof SEVERITY_COLORS],
            backgroundColor: `${SEVERITY_COLORS[trafficState.severity as keyof typeof SEVERITY_COLORS]}10`,
          }}
        >
          <p className="text-sm text-gray-600 mb-1">Sévérité</p>
          <p
            className="text-2xl font-bold"
            style={{ color: SEVERITY_COLORS[trafficState.severity as keyof typeof SEVERITY_COLORS] }}
          >
            {getSeverityLabel(trafficState.severity)}
          </p>
        </div>

        {/* Intersection */}
        <div className="p-4 rounded-lg border-2 border-gray-200 bg-gray-50">
          <p className="text-sm text-gray-600 mb-1">Intersection</p>
          <p className="text-2xl font-bold text-gray-900">{trafficState.intersectionId}</p>
        </div>
      </div>

      {/* Recommandation */}
      {trafficState.recommendation && (
        <div className="p-4 rounded-lg bg-blue-50 border border-blue-200">
          <div className="flex items-start space-x-3">
            <FontAwesomeIcon icon={faExclamationTriangle} className="w-5 h-5 text-blue-600 mt-0.5" />
            <div>
              <p className="font-semibold text-blue-900 mb-1">Recommandation</p>
              <p className="text-sm text-blue-800">{trafficState.recommendation}</p>
            </div>
          </div>
        </div>
      )}

      {/* Timestamp */}
      <div className="mt-4 text-xs text-gray-500 text-right">
        Dernière analyse : {new Date(trafficState.timestamp).toLocaleString('fr-FR')}
      </div>
    </div>
  );
}