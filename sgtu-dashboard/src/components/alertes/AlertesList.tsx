'use client';

import React from 'react';
import { useAlertesStore } from '@/store/alertes-store';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faExclamationTriangle,
  faExclamationCircle,
  faInfoCircle,
  faCheckCircle,
} from '@fortawesome/free-solid-svg-icons';
import { motion } from 'framer-motion';
import { formatRelativeTime } from '@/lib/utils';

export default function AlertesList() {
  const { getFilteredAlerts } = useAlertesStore();
  const alerts = getFilteredAlerts();

  const getIcon = (severity: string) => {
    switch (severity) {
      case 'CRITICAL':
      case 'HIGH':
        return faExclamationTriangle;
      case 'MEDIUM':
        return faExclamationCircle;
      case 'LOW':
        return faInfoCircle;
      default:
        return faCheckCircle;
    }
  };

  const getColor = (severity: string) => {
    switch (severity) {
      case 'CRITICAL':
      case 'HIGH':
        return 'text-red-600 bg-red-50 border-red-200';
      case 'MEDIUM':
        return 'text-yellow-600 bg-yellow-50 border-yellow-200';
      case 'LOW':
        return 'text-blue-600 bg-blue-50 border-blue-200';
      default:
        return 'text-green-600 bg-green-50 border-green-200';
    }
  };

  const getSourceBadge = (source: string) => {
    const colors: Record<string, string> = {
      POLLUTION: 'bg-purple-100 text-purple-800',
      BRUIT: 'bg-pink-100 text-pink-800',
      FLUX: 'bg-blue-100 text-blue-800',
      CAMERAS: 'bg-cyan-100 text-cyan-800',
    };
    return colors[source] || 'bg-gray-100 text-gray-800';
  };

  return (
    <div className="card flex flex-col h-full">
      <div className="flex items-center justify-between mb-4 flex-shrink-0">
        <h2 className="text-xl font-semibold text-gray-900">
          Liste des Alertes ({alerts.length})
        </h2>
      </div>

      <div className="space-y-3 overflow-y-auto max-h-[600px] pr-2 custom-scrollbar">
        {alerts.length === 0 ? (
          <div className="text-center py-12 text-gray-500">
            <FontAwesomeIcon icon={faCheckCircle} className="w-16 h-16 mb-3 text-green-500" />
            <p className="text-lg font-medium">Aucune alerte correspondante</p>
            <p className="text-sm mt-1">Modifiez les filtres pour voir plus d'alertes</p>
          </div>
        ) : (
          alerts.map((alert, index) => (
            <motion.div
              key={alert.id}
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: index * 0.03 }}
              className={`flex items-start space-x-4 p-4 rounded-lg border ${getColor(
                alert.severity
              )}`}
            >
              <FontAwesomeIcon icon={getIcon(alert.severity)} className="w-5 h-5 mt-1 flex-shrink-0" />

              <div className="flex-1 min-w-0">
                <div className="flex items-start justify-between mb-2">
                  <p className="font-medium">{alert.message}</p>
                  <span className={`badge ${getSourceBadge(alert.source)} ml-2 flex-shrink-0`}>
                    {alert.source}
                  </span>
                </div>

                <div className="flex flex-wrap items-center gap-3 text-xs">
                  <span className="opacity-75">Type: {alert.type}</span>
                  <span className="opacity-50">•</span>
                  <span className="opacity-75">{formatRelativeTime(alert.timestamp)}</span>
                  <span className="opacity-50">•</span>
                  <span
                    className={`badge ${
                      alert.status === 'ACTIVE' ? 'badge-danger' : 'badge-success'
                    }`}
                  >
                    {alert.status}
                  </span>
                  <span className="opacity-50">•</span>
                  <span
                    className={`badge ${
                      alert.severity === 'CRITICAL' || alert.severity === 'HIGH'
                        ? 'badge-danger'
                        : alert.severity === 'MEDIUM'
                        ? 'badge-warning'
                        : 'badge-info'
                    }`}
                  >
                    {alert.severity}
                  </span>
                </div>
              </div>
            </motion.div>
          ))
        )}
      </div>
    </div>
  );
}