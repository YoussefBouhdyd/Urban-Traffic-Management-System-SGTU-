'use client';

import React from 'react';
import AlertesFilters from '@/components/alertes/AlertesFilters';
import AlertesList from '@/components/alertes/AlertesList';
import AlertesStats from '@/components/alertes/AlertesStats';
import { useAlerts } from '@/hooks/useAlerts';

export default function AlertesPage() {
  // Hook qui agrège toutes les alertes
  useAlerts();

  return (
    <div className="space-y-8">
      {/* En-tête */}
      <div>
        <h1 className="text-3xl font-bold text-gray-900 mb-2">Alertes & Rapports</h1>
        <p className="text-gray-600">
          Centralisation de toutes les alertes du système en temps réel
        </p>
      </div>

      {/* Statistiques */}
      <AlertesStats />

      {/* Filtres et Liste */}
      <div className="grid grid-cols-1 lg:grid-cols-4 gap-8">
        <div className="lg:col-span-1">
          <AlertesFilters />
        </div>
        <div className="lg:col-span-3">
          <AlertesList />
        </div>
      </div>
    </div>
  );
}