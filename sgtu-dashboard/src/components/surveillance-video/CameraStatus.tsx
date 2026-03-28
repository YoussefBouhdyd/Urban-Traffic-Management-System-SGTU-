'use client';

import React from 'react';
import { useCamerasStore } from '@/store/cameras-store';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faVideo, faCheckCircle, faTimesCircle, faExclamationCircle } from '@fortawesome/free-solid-svg-icons';
import { formatRelativeTime } from '@/lib/utils';

export default function CameraStatus() {
  const cameraStatuses = useCamerasStore((state) => state.cameraStatuses);

  const cameras = Object.values(cameraStatuses);

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'RUNNING':
        return faCheckCircle;
      case 'STOPPED':
        return faTimesCircle;
      default:
        return faExclamationCircle;
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'RUNNING':
        return 'text-green-600 bg-green-50 border-green-200';
      case 'STOPPED':
        return 'text-gray-600 bg-gray-50 border-gray-200';
      default:
        return 'text-red-600 bg-red-50 border-red-200';
    }
  };

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'RUNNING':
        return 'badge-success';
      case 'STOPPED':
        return 'badge badge-secondary';
      default:
        return 'badge-danger';
    }
  };

  return (
    <div className="card">
      <h2 className="text-xl font-semibold text-gray-900 mb-4">État des Caméras</h2>

      {cameras.length === 0 ? (
        <div className="text-center py-8 text-gray-500">
          <FontAwesomeIcon icon={faVideo} className="w-12 h-12 mb-2 opacity-30" />
          <p>Aucune caméra configurée</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {cameras.map((camera) => (
            <div
              key={camera.cameraId}
              className={`p-4 rounded-lg border ${getStatusColor(camera.status)}`}
            >
              <div className="flex items-start justify-between mb-3">
                <div>
                  <h3 className="font-semibold">{camera.cameraId}</h3>
                  <p className="text-sm opacity-75">{camera.intersectionId}</p>
                </div>
                <FontAwesomeIcon icon={getStatusIcon(camera.status)} className="w-5 h-5" />
              </div>

              <div className="space-y-2">
                <div className="flex items-center justify-between text-sm">
                  <span className="opacity-75">Statut:</span>
                  <span className={`badge ${getStatusBadge(camera.status)}`}>
                    {camera.status}
                  </span>
                </div>
                <div className="flex items-center justify-between text-sm">
                  <span className="opacity-75">Dernière MAJ:</span>
                  <span className="font-mono text-xs">
                    {formatRelativeTime(camera.lastUpdate)}
                  </span>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}