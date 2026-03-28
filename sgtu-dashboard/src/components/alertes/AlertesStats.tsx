'use client';

import React, { useMemo } from 'react';
import { Doughnut, Bar } from 'react-chartjs-2';
import { Chart as ChartJS, ArcElement, Tooltip, Legend, CategoryScale, LinearScale, BarElement } from 'chart.js';
import { useAlertesStore } from '@/store/alertes-store';
import { SEVERITY_COLORS } from '@/constants/colors';

ChartJS.register(ArcElement, Tooltip, Legend, CategoryScale, LinearScale, BarElement);

export default function AlertesStats() {
  const { getStats } = useAlertesStore();
  const stats = getStats();

  const severityChartData = useMemo(() => {
    return {
      labels: ['Basse', 'Moyenne', 'Haute', 'Critique'],
      datasets: [
        {
          data: [
            stats.bySeverity.LOW,
            stats.bySeverity.MEDIUM,
            stats.bySeverity.HIGH,
            stats.bySeverity.CRITICAL,
          ],
          backgroundColor: [
            SEVERITY_COLORS.LOW,
            SEVERITY_COLORS.MEDIUM,
            SEVERITY_COLORS.HIGH,
            SEVERITY_COLORS.CRITICAL,
          ],
          borderWidth: 2,
          borderColor: '#fff',
        },
      ],
    };
  }, [stats]);

  const sourceChartData = useMemo(() => {
    return {
      labels: ['Pollution', 'Bruit', 'Flux', 'Caméras'],
      datasets: [
        {
          label: 'Alertes par source',
          data: [
            stats.bySource.POLLUTION,
            stats.bySource.BRUIT,
            stats.bySource.FLUX,
            stats.bySource.CAMERAS,
          ],
          backgroundColor: ['#8B5CF6', '#EC4899', '#3B82F6', '#06B6D4'],
        },
      ],
    };
  }, [stats]);

  const doughnutOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'bottom' as const,
        labels: { padding: 15, font: { size: 11 } },
      },
    },
  };

  const barOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false },
    },
    scales: {
      y: { beginAtZero: true, ticks: { stepSize: 1 } },
    },
  };

  return (
    <div className="card">
      <h2 className="text-xl font-semibold text-gray-900 mb-6">Statistiques des Alertes</h2>

      {/* Total */}
      <div className="mb-6 p-4 bg-gradient-to-r from-blue-50 to-blue-100 rounded-lg border border-blue-200">
        <p className="text-sm text-blue-700 mb-1">Total des Alertes</p>
        <p className="text-4xl font-bold text-blue-900">{stats.total}</p>
      </div>

      {/* Graphiques */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <h3 className="text-sm font-semibold text-gray-700 mb-3 text-center">Par Sévérité</h3>
          <div className="h-64">
            <Doughnut data={severityChartData} options={doughnutOptions} />
          </div>
        </div>

        <div>
          <h3 className="text-sm font-semibold text-gray-700 mb-3 text-center">Par Source</h3>
          <div className="h-64">
            <Bar data={sourceChartData} options={barOptions} />
          </div>
        </div>
      </div>

      {/* Détails */}
      <div className="mt-6 grid grid-cols-2 gap-4 text-sm">
        <div className="p-3 bg-green-50 rounded-lg">
          <p className="text-gray-600">Actives</p>
          <p className="text-2xl font-bold text-green-700">{stats.byStatus.ACTIVE}</p>
        </div>
        <div className="p-3 bg-gray-50 rounded-lg">
          <p className="text-gray-600">Résolues</p>
          <p className="text-2xl font-bold text-gray-700">{stats.byStatus.RESOLVED}</p>
        </div>
      </div>
    </div>
  );
}