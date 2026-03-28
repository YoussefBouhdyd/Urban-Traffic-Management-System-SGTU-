'use client';

import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IconDefinition } from '@fortawesome/fontawesome-svg-core';
import { motion } from 'framer-motion';

interface StatCardProps {
  title: string;
  value: string | number;
  subtitle?: string;
  icon: IconDefinition;
  color: 'pollution' | 'bruit' | 'flux' | 'feux' | 'cameras';
  severity?: 'NORMAL' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  trend?: {
    value: number;
    isPositive: boolean;
  };
}

const colorClasses = {
  pollution: {
    bg: 'bg-pollution-light/20',
    icon: 'text-pollution',
    border: 'border-pollution-light',
  },
  bruit: {
    bg: 'bg-bruit-light/20',
    icon: 'text-bruit',
    border: 'border-bruit-light',
  },
  flux: {
    bg: 'bg-flux-light/20',
    icon: 'text-flux',
    border: 'border-flux-light',
  },
  feux: {
    bg: 'bg-feux-light/20',
    icon: 'text-feux',
    border: 'border-feux-light',
  },
  cameras: {
    bg: 'bg-cameras-light/20',
    icon: 'text-cameras',
    border: 'border-cameras-light',
  },
};

const severityBadges = {
  NORMAL: 'badge-success',
  MEDIUM: 'badge-warning',
  HIGH: 'badge-danger',
  CRITICAL: 'badge-danger',
};

const severityLabels = {
  NORMAL: 'Normal',
  MEDIUM: 'Moyen',
  HIGH: 'Élevé',
  CRITICAL: 'Critique',
};

export default function StatCard({
  title,
  value,
  subtitle,
  icon,
  color,
  severity,
  trend,
}: StatCardProps) {
  const colors = colorClasses[color];

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3 }}
      className="card card-hover border-l-4 relative overflow-hidden"
      style={{ borderLeftColor: `var(--tw-${color})` }}
    >
      {/* Icône de fond */}
      <div className={`absolute top-0 right-0 -mt-4 -mr-4 ${colors.bg} rounded-full w-24 h-24 opacity-50`} />

      <div className="relative">
        {/* Header */}
        <div className="flex items-start justify-between mb-4">
          <div className="flex-1">
            <p className="text-sm font-medium text-gray-600 mb-1">{title}</p>
            <div className="flex items-baseline space-x-2">
              <h3 className="text-3xl font-bold text-gray-900 text-mono">{value}</h3>
              {trend && (
                <span
                  className={`text-sm font-medium ${
                    trend.isPositive ? 'text-green-600' : 'text-red-600'
                  }`}
                >
                  {trend.isPositive ? '↑' : '↓'} {Math.abs(trend.value)}%
                </span>
              )}
            </div>
            {subtitle && <p className="text-xs text-gray-500 mt-1">{subtitle}</p>}
          </div>

          {/* Icône */}
          <div className={`${colors.bg} ${colors.border} border-2 rounded-lg p-3`}>
            <FontAwesomeIcon icon={icon} className={`w-6 h-6 ${colors.icon}`} />
          </div>
        </div>

        {/* Badge sévérité */}
        {severity && (
          <div className="mt-3 pt-3 border-t border-gray-100">
            <span className={`badge ${severityBadges[severity]}`}>
              {severityLabels[severity]}
            </span>
          </div>
        )}
      </div>
    </motion.div>
  );
}