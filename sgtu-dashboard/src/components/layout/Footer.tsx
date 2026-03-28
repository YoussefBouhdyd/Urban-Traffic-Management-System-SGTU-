'use client';

import React from 'react';

export default function Footer() {
  return (
    <footer className="footer bg-white border-t border-gray-200 px-6 flex items-center justify-between text-sm text-gray-600">
      <div>
        © {new Date().getFullYear()} SGTU Rabat - Système de Gestion du Trafic Urbain
      </div>
      <div className="flex items-center space-x-4">
        <span className="flex items-center space-x-2">
          <span className="w-2 h-2 bg-green-500 rounded-full animate-pulse"></span>
          <span>Tous les services actifs</span>
        </span>
      </div>
    </footer>
  );
}