'use client';

import React, { useState, useEffect } from 'react';
import { useFeuxStore } from '@/store/feux-store';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCog, faSave } from '@fortawesome/free-solid-svg-icons';
import { serviceCentralClient } from '@/lib/api-client';

export default function FeuxConfig() {
  const config = useFeuxStore((state) => state.config);
  const [duration, setDuration] = useState(10);
  const [segmentGreen, setSegmentGreen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    if (config) {
      setDuration(config.duration);
      setSegmentGreen(config.segmentGreen);
    }
  }, [config]);

  const handleSave = async () => {
    setLoading(true);
    setSuccess(false);

    try {
      await serviceCentralClient.updateFeuxConfig({
        duration,
        segmentGreen,
      });
      setSuccess(true);
      setTimeout(() => setSuccess(false), 3000);
    } catch (error) {
      console.error('Erreur lors de la mise à jour de la configuration:', error);
      alert('Erreur lors de la mise à jour');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="card">
      <div className="flex items-center space-x-2 mb-4">
        <FontAwesomeIcon icon={faCog} className="w-5 h-5 text-gray-600" />
        <h2 className="text-xl font-semibold text-gray-900">Configuration Globale</h2>
      </div>

      {success && (
        <div className="mb-4 p-3 bg-green-50 border border-green-200 rounded-lg text-green-800 text-sm">
          Configuration mise à jour avec succès !
        </div>
      )}

      <div className="space-y-6">
        {/* Durée du cycle */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Durée du cycle (secondes)
          </label>
          <div className="flex items-center space-x-4">
            <input
              type="range"
              min="5"
              max="60"
              step="5"
              value={duration}
              onChange={(e) => setDuration(parseInt(e.target.value))}
              className="flex-1"
            />
            <span className="text-2xl font-bold text-mono text-gray-900 w-16 text-center">
              {duration}s
            </span>
          </div>
          <p className="text-xs text-gray-500 mt-1">
            Temps pendant lequel un segment reste vert avant de passer au rouge
          </p>
        </div>

        {/* Segment actif */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Segment actuellement vert
          </label>
          <div className="grid grid-cols-2 gap-3">
            <button
              onClick={() => setSegmentGreen(true)}
              className={`p-4 rounded-lg border-2 transition-all ${
                segmentGreen
                  ? 'border-green-500 bg-green-50 text-green-900'
                  : 'border-gray-200 bg-white text-gray-600 hover:border-gray-300'
              }`}
            >
              <p className="font-semibold">Nord / Sud</p>
              <p className="text-xs mt-1">Av Ibn Rochd</p>
            </button>
            <button
              onClick={() => setSegmentGreen(false)}
              className={`p-4 rounded-lg border-2 transition-all ${
                !segmentGreen
                  ? 'border-green-500 bg-green-50 text-green-900'
                  : 'border-gray-200 bg-white text-gray-600 hover:border-gray-300'
              }`}
            >
              <p className="font-semibold">Est / Ouest</p>
              <p className="text-xs mt-1">Av Ma El Aynayne</p>
            </button>
          </div>
        </div>

        {/* Bouton Sauvegarder */}
        <button
          onClick={handleSave}
          disabled={loading}
          className="w-full btn-primary flex items-center justify-center space-x-2"
        >
          <FontAwesomeIcon icon={faSave} />
          <span>{loading ? 'Enregistrement...' : 'Enregistrer la configuration'}</span>
        </button>
      </div>
    </div>
  );
}