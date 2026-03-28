'use client';

import React, { useMemo } from 'react';
import { Line } from 'react-chartjs-2';
import { useFluxStore } from '@/store/flux-store';
import { format } from 'date-fns';

export default function FluxChart() {
  const fluxData = useFluxStore((state) => state.fluxData);

  const chartData = useMemo(() => {
    const routes = Object.keys(fluxData);
    if (routes.length === 0) {
      return { labels: [], datasets: [] };
    }

    const firstRoute = routes[0];
    const labels = fluxData[firstRoute]?.map((d) =>
      format(new Date(d.timestamp), 'HH:mm')
    ) || [];

    const colors = ['#3B82F6', '#10B981', '#F59E0B', '#EF4444'];

    const datasets = routes.map((route, index) => ({
      label: route.charAt(0).toUpperCase() + route.slice(1),
      data: fluxData[route]?.map((d) => d.flux) || [],
      borderColor: colors[index % colors.length],
      backgroundColor: `${colors[index % colors.length]}20`,
      borderWidth: 2,
      fill: false,
      tension: 0.4,
      pointRadius: 3,
      pointHoverRadius: 5,
    }));

    return { labels, datasets };
  }, [fluxData]);

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
        callbacks: {
          label: (context: any) => {
            return `${context.dataset.label}: ${context.parsed.y} véhicules`;
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
          callback: (value: any) => `${value}`,
        },
      },
    },
  };

  return (
    <div className="h-96">
      <Line data={chartData} options={options} />
    </div>
  );
}