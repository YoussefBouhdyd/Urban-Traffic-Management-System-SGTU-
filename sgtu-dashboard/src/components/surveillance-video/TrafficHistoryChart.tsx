'use client';

import React, { useMemo } from 'react';
import { Bar } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';
import { useCamerasStore } from '@/store/cameras-store';
import { format } from 'date-fns';
import { STATUS_COLORS } from '@/constants/colors';

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

export default function TrafficHistoryChart() {
  const trafficHistory = useCamerasStore((state) => state.trafficHistory);

  const chartData = useMemo(() => {
    if (trafficHistory.length === 0) {
      return { labels: [], datasets: [] };
    }

    const labels = trafficHistory.map((h) => format(new Date(h.timestamp), 'HH:mm'));
    const vehicleCounts = trafficHistory.map((h) => h.vehicleCount);
    const speeds = trafficHistory.map((h) => h.averageSpeed);

    const backgroundColors = trafficHistory.map((h) => {
      const color = STATUS_COLORS[h.trafficState as keyof typeof STATUS_COLORS] || STATUS_COLORS.NORMAL;
      return `${color}80`;
    });

    return {
      labels,
      datasets: [
        {
          label: 'Nombre de véhicules',
          data: vehicleCounts,
          backgroundColor: backgroundColors,
          borderColor: backgroundColors.map((c) => c.replace('80', '')),
          borderWidth: 2,
        },
      ],
    };
  }, [trafficHistory]);

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: true,
        position: 'top' as const,
      },
      tooltip: {
        callbacks: {
          label: (context: any) => {
            const index = context.dataIndex;
            const history = trafficHistory[index];
            return [
              `Véhicules: ${context.parsed.y}`,
              `Vitesse moy: ${history.averageSpeed} km/h`,
              `État: ${history.trafficState}`,
            ];
          },
        },
      },
    },
    scales: {
      x: {
        grid: { color: 'rgba(0, 0, 0, 0.05)' },
      },
      y: {
        beginAtZero: true,
        grid: { color: 'rgba(0, 0, 0, 0.05)' },
      },
    },
  };

  return (
    <div className="h-80">
      {trafficHistory.length === 0 ? (
        <div className="flex items-center justify-center h-full text-gray-500">
          <p>Aucune donnée historique disponible</p>
        </div>
      ) : (
        <Bar data={chartData} options={options} />
      )}
    </div>
  );
}