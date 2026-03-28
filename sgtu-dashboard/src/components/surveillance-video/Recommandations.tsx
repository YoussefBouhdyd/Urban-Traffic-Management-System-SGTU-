'use client';

import React from 'react';
import { useCamerasStore } from '@/store/cameras-store';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faLightbulb } from '@fortawesome/free-solid-svg-icons';
import { motion } from 'framer-motion';

export default function Recommandations() {
  const recommendations = useCamerasStore((state) => state.recommendations);

  const getPriorityBadge = (priority: string) => {
    switch (priority) {
      case 'HIGH':
        return 'badge-danger';
      case 'MEDIUM':
        return 'badge-warning';
      default:
        return 'badge-info';
    }
  };

  const getPriorityLabel = (priority: string) => {
    switch (priority) {
      case 'HIGH':
        return 'Haute';
      case 'MEDIUM':
        return 'Moyenne';
      default:
        return 'Basse';
    }
  };

  return (
    <div className="card flex flex-col h-full">
      <h2 className="text-xl font-semibold text-gray-900 mb-4 flex-shrink-0">Recommandations Système</h2>

      <div className="space-y-3 overflow-y-auto max-h-[600px] pr-2 custom-scrollbar">
        {recommendations.length === 0 ? (
          <div className="text-center py-8 text-gray-500">
            <FontAwesomeIcon icon={faLightbulb} className="w-12 h-12 mb-2 opacity-30" />
            <p>Aucune recommandation pour le moment</p>
            <p className="text-sm mt-1">Le système fonctionne de manière optimale</p>
          </div>
        ) : (
          recommendations.map((rec, index) => (
            <motion.div
              key={rec.id}
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: index * 0.05 }}
              className="p-4 rounded-lg border border-gray-200 bg-white hover:shadow-md transition-shadow"
            >
              <div className="flex items-start space-x-3">
                <FontAwesomeIcon
                  icon={faLightbulb}
                  className={`w-5 h-5 mt-0.5 ${
                    rec.priority === 'HIGH'
                      ? 'text-red-500'
                      : rec.priority === 'MEDIUM'
                      ? 'text-yellow-500'
                      : 'text-blue-500'
                  }`}
                />
                <div className="flex-1">
                  <p className="font-medium text-gray-900">{rec.recommendation}</p>
                  {rec.reason && (
                    <p className="text-sm text-gray-600 mt-1">Raison : {rec.reason}</p>
                  )}
                  <div className="flex items-center space-x-3 mt-2">
                    <span className={`badge ${getPriorityBadge(rec.priority)}`}>
                      {getPriorityLabel(rec.priority)}
                    </span>
                    <span className="text-xs text-gray-500">
                      {new Date(rec.timestamp).toLocaleTimeString('fr-FR')}
                    </span>
                  </div>
                </div>
              </div>
            </motion.div>
          ))
        )}
      </div>
    </div>
  );
}