import React from 'react';
import type { Metadata } from 'next';
import { Inter } from 'next/font/google';
import Sidebar from '@/components/layout/Sidebar';
import Header from '@/components/layout/Header';
import Footer from '@/components/layout/Footer';
import './globals.css';

// Configuration de la police Inter
const inter = Inter({ subsets: ['latin'] });

export const metadata: Metadata = {
  title: 'SGTU Rabat - Système de Gestion du Trafic Urbain',
  description: 'Plateforme de gestion intelligente du trafic urbain à Rabat',
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="fr">
      <body className={inter.className}>
        <div className="main-layout">
          <Sidebar />
          <Header />
          <main className="main-content">{children}</main>
          <Footer />
        </div>
      </body>
    </html>
  );
}