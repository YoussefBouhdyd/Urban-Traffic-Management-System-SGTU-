'use client';

import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faExclamationTriangle } from '@fortawesome/free-solid-svg-icons';

interface ErrorBoundaryProps {
  error?: Error;
  reset?: () => void;
  message?: string;
}

export default function ErrorBoundary({ error, reset, message }: ErrorBoundaryProps) {
  return (
    <div className="flex flex-col items-center justify-center p-12 bg-red-50 rounded-lg border border-red-200">
      <FontAwesomeIcon icon={faExclamationTriangle} className="w-12 h-12 text-red-500 mb-4" />
      <h3 className="text-lg font-semibold text-red-900 mb-2">Une erreur est survenue</h3>
      <p className="text-sm text-red-700 mb-4 text-center max-w-md">
        {message || error?.message || 'Impossible de charger les données'}
      </p>
      {reset && (
        <button onClick={reset} className="btn-primary">
          Réessayer
        </button>
      )}
    </div>
  );
}