'use client';

import React from 'react';
import { usePollutionStore } from '@/store/pollution-store';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faExclamationTriangle, faExclamationCircle, faCheckCircle } from '@fortawesome/free-solid-svg-icons';
import { motion } from 'framer-motion';

export default function AlertesQualiteAir() {
  const alertes = usePollutionStore((state) => state.alertes);
  const alertesBruit = usePollutionStore((state) => state.alertesBruit);

  const allAlertes = [...alertes, ...alertesBruit].sort(
    (a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime()
  );

  const getIcon = (severity: string) => {
    switch (severity) {
      case 'HIGH':
        return faExclamationTriangle;
      case 'MEDIUM':
        return faExclamationCircle;
      default:
        return faCheckCircle;
    }
  };

  const getColor = (severity: string) => {
    switch (severity) {
      case 'HIGH':
        return 'text-red-600 bg-red-50 border-red-200';
      case 'MEDIUM':
        return 'text-yellow-600 bg-yellow-50 border-yellow-200';
      default:
        return 'text-green-600 bg-green-50 border-green-200';
    }
  };

  return (
    <div className="card flex flex-col h-full">
      <h2 className="text-xl font-semibold text-gray-900 mb-4 flex-shrink-0">Alertes Qualité de l'Air</h2>

      <div className="space-y-3 overflow-y-auto max-h-[600px] pr-2 custom-scrollbar">
        {allAlertes.length === 0 ? (
          <div className="text-center py-8 text-gray-500">
            <FontAwesomeIcon icon={faCheckCircle} className="w-12 h-12 mb-2 text-green-500" />
            <p>Aucune alerte</p>
            <p className="text-sm mt-1">La qualité de l'air est bonne</p>
          </div>
        ) : (
          allAlertes.map((alerte, index) => (
            <motion.div
              key={alerte.id}
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: index * 0.05 }}
              className={`flex items-start space-x-3 p-4 rounded-lg border ${getColor(
                alerte.severity
              )}`}
            >
              <FontAwesomeIcon icon={getIcon(alerte.severity)} className="w-5 h-5 mt-0.5" />
              <div className="flex-1">
                <p className="font-medium">{alerte.message}</p>
                <p className="text-sm opacity-75 mt-1">
                  Zone: {alerte.zone_id} • {new Date(alerte.timestamp).toLocaleTimeString('fr-FR')}
                </p>
              </div>
              <span className="badge badge-danger text-xs">{alerte.severity}</span>
            </motion.div>
          ))
        )}
      </div>
    </div>
  );
}