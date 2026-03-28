'use client';

import React, { useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faHandPaper, faCheckCircle } from '@fortawesome/free-solid-svg-icons';
import { serviceCentralClient } from '@/lib/api-client';
import { ROUTE_IDS } from '@/constants/routes';

export default function FeuxControls() {
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState<string | null>(null);

  const handleForceRoute = async (routeId: string, green: boolean) => {
    setLoading(true);
    setSuccess(null);

    try {
      await serviceCentralClient.forceFeuxRoute(routeId, {
        duration: 30,
        green,
      });
      setSuccess(`${routeId.toUpperCase()} forcé en ${green ? 'VERT' : 'ROUGE'}`);
      setTimeout(() => setSuccess(null), 3000);
    } catch (error) {
      console.error('Erreur lors du forçage du feu:', error);
      alert('Erreur lors du forçage du feu');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="card">
      <h2 className="text-xl font-semibold text-gray-900 mb-4">Contrôles Manuels</h2>

      {success && (
        <div className="mb-4 p-3 bg-green-50 border border-green-200 rounded-lg flex items-center space-x-2 text-green-800">
          <FontAwesomeIcon icon={faCheckCircle} className="w-5 h-5" />
          <span className="text-sm font-medium">{success}</span>
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {Object.values(ROUTE_IDS).map((routeId) => (
          <div key={routeId} className="p-4 bg-gray-50 rounded-lg border border-gray-200">
            <h3 className="font-semibold text-gray-900 mb-3 capitalize">{routeId}</h3>
            <div className="flex space-x-2">
              <button
                onClick={() => handleForceRoute(routeId, true)}
                disabled={loading}
                className="flex-1 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 active:bg-green-800 disabled:opacity-50 disabled:cursor-not-allowed transition-colors text-sm font-medium"
              >
                <FontAwesomeIcon icon={faHandPaper} className="mr-2" />
                Forcer Vert
              </button>
              <button
                onClick={() => handleForceRoute(routeId, false)}
                disabled={loading}
                className="flex-1 px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 active:bg-red-800 disabled:opacity-50 disabled:cursor-not-allowed transition-colors text-sm font-medium"
              >
                <FontAwesomeIcon icon={faHandPaper} className="mr-2" />
                Forcer Rouge
              </button>
            </div>
          </div>
        ))}
      </div>

      <div className="mt-4 p-3 bg-yellow-50 border border-yellow-200 rounded-lg">
        <p className="text-sm text-yellow-800">
          <strong>Note :</strong> Le forçage manuel d'un feu affecte l'ensemble du segment.
          Les feux du même segment changeront simultanément.
        </p>
      </div>
    </div>
  );
}