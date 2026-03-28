'use client';

import React from 'react';
import { useFeuxStore } from '@/store/feux-store';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCircle, faClock } from '@fortawesome/free-solid-svg-icons';
import { motion } from 'framer-motion';

export default function IntersectionView() {
  const feux = useFeuxStore((state) => state.feux);

  const getFeuxByRoute = (routeId: string) => {
    return feux.find((f) => f.routeId === routeId);
  };

  const nord = getFeuxByRoute('nord');
  const sud = getFeuxByRoute('sud');
  const est = getFeuxByRoute('est');
  const ouest = getFeuxByRoute('ouest');

  const TrafficLight = ({ feu, position }: { feu: any; position: string }) => {
    if (!feu) return null;

    return (
      <motion.div
        initial={{ scale: 0 }}
        animate={{ scale: 1 }}
        className={`absolute ${position} flex flex-col items-center`}
      >
        <div className="bg-white rounded-lg shadow-lg p-4 border-2 border-gray-200">
          <div className="flex flex-col items-center space-y-2">
            <FontAwesomeIcon
              icon={faCircle}
              className={`w-8 h-8 ${
                feu.green ? 'text-green-500' : 'text-red-500'
              } animate-pulse-glow`}
            />
            <div className="text-center">
              <p className="text-sm font-semibold text-gray-900 capitalize">{feu.routeId}</p>
              <p className="text-xs text-gray-600">{feu.name}</p>
            </div>
            <div className="flex items-center space-x-1 text-xs text-gray-600">
              <FontAwesomeIcon icon={faClock} className="w-3 h-3" />
              <span className="font-mono">{feu.remaining}s</span>
            </div>
          </div>
        </div>
      </motion.div>
    );
  };

  return (
    <div className="card bg-gray-50">
      <h2 className="text-xl font-semibold text-gray-900 mb-6">Vue de l'Intersection</h2>

      {/* Intersection Diagram */}
      <div className="relative w-full h-96 bg-white rounded-lg border-2 border-gray-300 overflow-hidden">
        {/* Routes */}
        <div className="absolute inset-0 flex items-center justify-center">
          {/* Route Nord-Sud (verticale) */}
          <div className="absolute w-24 h-full bg-gray-300">
            <div className="absolute w-1 h-full left-1/2 transform -translate-x-1/2 border-l-2 border-dashed border-yellow-400" />
          </div>

          {/* Route Est-Ouest (horizontale) */}
          <div className="absolute h-24 w-full bg-gray-300">
            <div className="absolute h-1 w-full top-1/2 transform -translate-y-1/2 border-t-2 border-dashed border-yellow-400" />
          </div>

          {/* Centre intersection */}
          <div className="absolute w-24 h-24 bg-gray-400 rounded-lg flex items-center justify-center">
            <span className="text-white font-bold text-xs">INT-01</span>
          </div>
        </div>

        {/* Feux de circulation */}
        <TrafficLight feu={nord} position="top-8 left-1/2 transform -translate-x-1/2" />
        <TrafficLight feu={sud} position="bottom-8 left-1/2 transform -translate-x-1/2" />
        <TrafficLight feu={est} position="right-8 top-1/2 transform -translate-y-1/2" />
        <TrafficLight feu={ouest} position="left-8 top-1/2 transform -translate-y-1/2" />
      </div>

      {/* Légende */}
      <div className="mt-6 grid grid-cols-2 gap-4 text-sm">
        <div className="flex items-center space-x-2">
          <FontAwesomeIcon icon={faCircle} className="w-4 h-4 text-green-500" />
          <span className="text-gray-700">Feu vert - Circulation autorisée</span>
        </div>
        <div className="flex items-center space-x-2">
          <FontAwesomeIcon icon={faCircle} className="w-4 h-4 text-red-500" />
          <span className="text-gray-700">Feu rouge - Arrêt obligatoire</span>
        </div>
      </div>
    </div>
  );
}