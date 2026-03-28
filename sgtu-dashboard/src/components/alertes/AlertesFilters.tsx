'use client';

import React from 'react';
import { useAlertesStore } from '@/store/alertes-store';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faFilter } from '@fortawesome/free-solid-svg-icons';

export default function AlertesFilters() {
  const { filters, setFilters } = useAlertesStore();

  const sources = ['POLLUTION', 'BRUIT', 'FLUX', 'CAMERAS'];
  const severities = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'];
  const statuses = ['ACTIVE', 'RESOLVED'];

  const toggleSource = (source: any) => {
    const newSources = filters.sources.includes(source)
      ? filters.sources.filter((s) => s !== source)
      : [...filters.sources, source];
    setFilters({ sources: newSources });
  };

  const toggleSeverity = (severity: any) => {
    const newSeverities = filters.severities.includes(severity)
      ? filters.severities.filter((s) => s !== severity)
      : [...filters.severities, severity];
    setFilters({ severities: newSeverities });
  };

  const toggleStatus = (status: any) => {
    const newStatuses = filters.statuses.includes(status)
      ? filters.statuses.filter((s) => s !== status)
      : [...filters.statuses, status];
    setFilters({ statuses: newStatuses });
  };

  const resetFilters = () => {
    setFilters({
      sources: [],
      severities: [],
      statuses: ['ACTIVE'],
    });
  };

  return (
    <div className="card">
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center space-x-2">
          <FontAwesomeIcon icon={faFilter} className="w-5 h-5 text-gray-600" />
          <h2 className="text-xl font-semibold text-gray-900">Filtres</h2>
        </div>
        <button onClick={resetFilters} className="text-sm text-blue-600 hover:text-blue-700">
          Réinitialiser
        </button>
      </div>

      <div className="space-y-4">
        {/* Sources */}
        <div>
          <p className="text-sm font-medium text-gray-700 mb-2">Source</p>
          <div className="flex flex-wrap gap-2">
            {sources.map((source) => (
              <button
                key={source}
                onClick={() => toggleSource(source)}
                className={`px-3 py-1 rounded-full text-sm transition-colors ${
                  filters.sources.includes(source as any)
                    ? 'bg-blue-600 text-white'
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
              >
                {source}
              </button>
            ))}
          </div>
        </div>

        {/* Sévérités */}
        <div>
          <p className="text-sm font-medium text-gray-700 mb-2">Sévérité</p>
          <div className="flex flex-wrap gap-2">
            {severities.map((severity) => (
              <button
                key={severity}
                onClick={() => toggleSeverity(severity)}
                className={`px-3 py-1 rounded-full text-sm transition-colors ${
                  filters.severities.includes(severity as any)
                    ? severity === 'CRITICAL' || severity === 'HIGH'
                      ? 'bg-red-600 text-white'
                      : severity === 'MEDIUM'
                      ? 'bg-yellow-600 text-white'
                      : 'bg-green-600 text-white'
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
              >
                {severity}
              </button>
            ))}
          </div>
        </div>

        {/* Statuts */}
        <div>
          <p className="text-sm font-medium text-gray-700 mb-2">Statut</p>
          <div className="flex flex-wrap gap-2">
            {statuses.map((status) => (
              <button
                key={status}
                onClick={() => toggleStatus(status)}
                className={`px-3 py-1 rounded-full text-sm transition-colors ${
                  filters.statuses.includes(status as any)
                    ? 'bg-blue-600 text-white'
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
              >
                {status}
              </button>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}