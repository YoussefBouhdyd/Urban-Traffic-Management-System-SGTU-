'use client';

import React from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faChartLine,
  faSmog,
  faCar,
  faTrafficLight,
  faVideo,
  faBell,
  faCog,
} from '@fortawesome/free-solid-svg-icons';
import { NAVIGATION_ROUTES } from '@/constants/routes';

const iconMap: Record<string, any> = {
  'chart-line': faChartLine,
  'smog': faSmog,
  'car': faCar,
  'traffic-light': faTrafficLight,
  'video': faVideo,
  'bell': faBell,
  'cog': faCog,
};

export default function Sidebar() {
  const pathname = usePathname();

  return (
    <aside className="sidebar bg-white shadow-sidebar flex flex-col h-screen sticky top-0">
      {/* Logo & Titre */}
      <div className="px-6 py-5 border-b border-gray-200">
        <div className="flex items-center space-x-3">
          <div className="w-10 h-10 bg-gradient-to-br from-primary-500 to-primary-700 rounded-lg flex items-center justify-center">
            <span className="text-white font-bold text-xl">S</span>
          </div>
          <div>
            <h1 className="text-lg font-bold text-gray-900">SGTU</h1>
            <p className="text-xs text-gray-500">Rabat</p>
          </div>
        </div>
      </div>

      {/* Navigation */}
      <nav className="flex-1 px-4 py-6 overflow-y-auto">
        <ul className="space-y-2">
          {NAVIGATION_ROUTES.map((route) => {
            const isActive = pathname === route.path;
            const icon = iconMap[route.icon];

            return (
              <li key={route.path}>
                <Link
                  href={route.path}
                  className={`
                    flex items-center space-x-3 px-4 py-3 rounded-lg
                    transition-all duration-200
                    ${
                      isActive
                        ? 'bg-primary-50 text-primary-700 font-medium'
                        : 'text-gray-700 hover:bg-gray-50 hover:text-gray-900'
                    }
                  `}
                >
                  <FontAwesomeIcon
                    icon={icon}
                    className={`w-5 h-5 ${isActive ? 'text-primary-600' : 'text-gray-500'}`}
                  />
                  <div className="flex-1">
                    <div className="text-sm">{route.label}</div>
                    {isActive && (
                      <div className="text-xs text-primary-600 mt-0.5">{route.description}</div>
                    )}
                  </div>
                </Link>
              </li>
            );
          })}
        </ul>
      </nav>

      {/* Footer Sidebar */}
      <div className="px-4 py-4 border-t border-gray-200">
        <button className="flex items-center space-x-3 px-4 py-2 w-full rounded-lg text-gray-700 hover:bg-gray-50 transition-colors">
          <FontAwesomeIcon icon={faCog} className="w-5 h-5 text-gray-500" />
          <span className="text-sm">Paramètres</span>
        </button>
      </div>
    </aside>
  );
}