'use client';

import React, { useState, useEffect } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faBell, faSyncAlt, faClock } from '@fortawesome/free-solid-svg-icons';
import { format } from 'date-fns';
import { fr } from 'date-fns/locale';

export default function Header() {
  const [currentTime, setCurrentTime] = useState(new Date());
  const [lastUpdate, setLastUpdate] = useState(new Date());
  const [alertCount, setAlertCount] = useState(0);

  // Mettre à jour l'horloge chaque seconde
  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentTime(new Date());
    }, 1000);

    return () => clearInterval(timer);
  }, []);

  // Simuler la mise à jour des alertes (à remplacer par le vrai store)
  useEffect(() => {
    // TODO: Connecter au store d'alertes réel
    setAlertCount(3);
  }, []);

  const handleRefresh = () => {
    setLastUpdate(new Date());
    // TODO: Déclencher le rafraîchissement des données
  };

  return (
    <header className="header bg-white border-b border-gray-200 px-6 flex items-center justify-between sticky top-0 z-10">
      {/* Titre de la page */}
      <div>
        <h2 className="text-xl font-semibold text-gray-900">
          Système de Gestion du Trafic Urbain
        </h2>
        <p className="text-sm text-gray-500">Vue d'ensemble en temps réel</p>
      </div>

      {/* Actions */}
      <div className="flex items-center space-x-6">
        {/* Horloge */}
        <div className="flex items-center space-x-2 text-gray-600">
          <FontAwesomeIcon icon={faClock} className="w-4 h-4" />
          <span className="text-sm text-mono">
            {format(currentTime, 'HH:mm:ss', { locale: fr })}
          </span>
        </div>

        {/* Dernière mise à jour */}
        <div className="text-sm text-gray-500">
          Dernière MAJ: {format(lastUpdate, 'HH:mm:ss')}
        </div>

        {/* Bouton rafraîchir */}
        <button
          onClick={handleRefresh}
          className="p-2 rounded-lg hover:bg-gray-100 transition-colors"
          title="Rafraîchir les données"
        >
          <FontAwesomeIcon icon={faSyncAlt} className="w-4 h-4 text-gray-600" />
        </button>

        {/* Notifications */}
        <button className="relative p-2 rounded-lg hover:bg-gray-100 transition-colors">
          <FontAwesomeIcon icon={faBell} className="w-5 h-5 text-gray-600" />
          {alertCount > 0 && (
            <span className="absolute top-1 right-1 w-5 h-5 bg-red-500 text-white text-xs rounded-full flex items-center justify-center font-medium">
              {alertCount}
            </span>
          )}
        </button>
      </div>
    </header>
  );
}