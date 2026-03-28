'use client';

import React, { useMemo } from 'react';
import { Line } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler,
} from 'chart.js';
import { usePollutionStore } from '@/store/pollution-store';
import { format } from 'date-fns';
import { SERVICE_COLORS } from '@/constants/colors';

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend, Filler);

export default function PollutionChart() {
  const pollutionData = usePollutionStore((state) => state.pollutionData);

  const chartData = useMemo(() => {
    const zones = Object.keys(pollutionData);
    if (zones.length === 0) {
      return {
        labels: [],
        datasets: [],
      };
    }

    // Utiliser les timestamps de la première zone comme labels
    const firstZone = zones[0];
    const labels = pollutionData[firstZone]?.map((d) =>
      format(new Date(d.timestamp), 'HH:mm:ss')
    ) || [];

    const datasets = zones.map((zone, index) => {
      const colors = [SERVICE_COLORS.POLLUTION.main, '#A78BFA', '#C4B5FD'];
      return {
        label: zone,
        data: pollutionData[zone]?.map((d) => d.niveau_co2) || [],
        borderColor: colors[index % colors.length],
        backgroundColor: `${colors[index % colors.length]}20`,
        borderWidth: 2,
        fill: true,
        tension: 0.4,
        pointRadius: 3,
        pointHoverRadius: 5,
      };
    });

    return { labels, datasets };
  }, [pollutionData]);

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'top' as const,
        labels: {
          usePointStyle: true,
          padding: 15,
          font: { size: 12, family: 'Inter' },
        },
      },
      tooltip: {
        mode: 'index' as const,
        intersect: false,
        backgroundColor: 'rgba(0, 0, 0, 0.8)',
        padding: 12,
        titleFont: { size: 13, family: 'Inter' },
        bodyFont: { size: 12, family: 'JetBrains Mono' },
        callbacks: {
          label: (context: any) => {
            return `${context.dataset.label}: ${context.parsed.y.toFixed(1)} µg/m³`;
          },
        },
      },
    },
    scales: {
      x: {
        grid: { color: 'rgba(0, 0, 0, 0.05)' },
        ticks: { font: { size: 11, family: 'JetBrains Mono' } },
      },
      y: {
        beginAtZero: true,
        grid: { color: 'rgba(0, 0, 0, 0.05)' },
        ticks: {
          font: { size: 11, family: 'JetBrains Mono' },
          callback: (value: any) => `${value} µg/m³`,
        },
      },
    },
  };

  return (
    <div className="h-80">
      <Line data={chartData} options={options} />
    </div>
  );
}