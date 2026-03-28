'use client';

import React from 'react';
import { useFluxStore } from '@/store/flux-store';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faExclamationTriangle, faCheckCircle } from '@fortawesome/free-solid-svg-icons';
import { motion } from 'framer-motion';

export default function AlertesCongestion() {
  const alertes = useFluxStore((state) => state.alertes);

  return (
    <div className="card">
      <h2 className="text-xl font-semibold text-gray-900 mb-4">Alertes de Congestion</h2>

      <div className="space-y-3">
        {alertes.length === 0 ? (
          <div className="text-center py-8 text-gray-500">
            <FontAwesomeIcon icon={faCheckCircle} className="w-12 h-12 mb-2 text-green-500" />
            <p>Aucune congestion détectée</p>
            <p className="text-sm mt-1">Le trafic est fluide sur toutes les routes</p>
          </div>
        ) : (
          alertes.map((alerte, index) => (
            <motion.div
              key={`${alerte.name}-${alerte.timestamp}`}
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: index * 0.05 }}
              className="flex items-start space-x-3 p-4 rounded-lg border bg-red-50 border-red-200 text-red-900"
            >
              <FontAwesomeIcon icon={faExclamationTriangle} className="w-5 h-5 mt-0.5" />
              <div className="flex-1">
                <p className="font-medium">
                  Congestion détectée sur {alerte.name}
                </p>
                <p className="text-sm opacity-75 mt-1">
                  Flux: {alerte.flux} véhicules • {new Date(alerte.timestamp).toLocaleTimeString('fr-FR')}
                </p>
              </div>
              <span className="badge badge-danger">HAUTE</span>
            </motion.div>
          ))
        )}
      </div>
    </div>
  );
}