'use client';

import React from 'react';
import { useFluxStore } from '@/store/flux-store';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCar } from '@fortawesome/free-solid-svg-icons';
import { getFluxLevel } from '@/constants/thresholds';

export default function FluxRoutes() {
  const latestFlux = useFluxStore((state) => state.latestFlux);

  // Convertir l'objet en tableau
  const routes = Object.values(latestFlux);

  const getStatusColor = (flux: number) => {
    const level = getFluxLevel(flux);
    switch (level) {
      case 'CONGESTED':
        return 'bg-red-50 border-red-200 text-red-900';
      case 'BUSY':
        return 'bg-yellow-50 border-yellow-200 text-yellow-900';
      default:
        return 'bg-green-50 border-green-200 text-green-900';
    }
  };

  const getStatusBadge = (flux: number) => {
    const level = getFluxLevel(flux);
    switch (level) {
      case 'CONGESTED':
        return 'badge-danger';
      case 'BUSY':
        return 'badge-warning';
      default:
        return 'badge-success';
    }
  };

  // Si pas de données
  if (!routes || routes.length === 0) {
    return (
      <div className="card">
        <div className="text-center py-8 text-gray-500">
          <FontAwesomeIcon icon={faCar} className="w-12 h-12 mb-2 opacity-30" />
          <p>Chargement des données de flux...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
      {routes.map((route) => (
        <div
          key={route.name}
          className={`card border-l-4 ${getStatusColor(route.flux)}`}
        >
          <div className="flex items-start justify-between mb-3">
            <div>
              <p className="text-sm font-medium opacity-75 capitalize">{route.name}</p>
              <h3 className="text-3xl font-bold text-mono mt-1">{route.flux}</h3>
              <p className="text-xs opacity-60 mt-1">véhicules</p>
            </div>
            <FontAwesomeIcon icon={faCar} className="w-8 h-8 opacity-30" />
          </div>

          <div className="flex items-center justify-between pt-3 border-t border-current/10">
            <span className={`badge ${getStatusBadge(route.flux)}`}>
              {getFluxLevel(route.flux)}
            </span>
            <span className="text-xs opacity-60">
              {new Date(route.timestamp).toLocaleTimeString('fr-FR')}
            </span>
          </div>
        </div>
      ))}
    </div>
  );
}