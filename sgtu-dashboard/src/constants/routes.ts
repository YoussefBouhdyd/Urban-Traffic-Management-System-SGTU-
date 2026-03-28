/**
 * ============================================================================
 * CONSTANTES DES ROUTES DE NAVIGATION
 * ============================================================================
 */

export const NAVIGATION_ROUTES = [
    {
      path: '/',
      label: 'Accueil',
      icon: 'chart-line',
      description: 'Vue d\'ensemble du système',
    },
    {
      path: '/qualite-air',
      label: 'Qualité de l\'Air',
      icon: 'smog',
      description: 'Pollution et niveaux sonores',
    },
    {
      path: '/gestion-trafic',
      label: 'Gestion du Trafic',
      icon: 'car',
      description: 'Flux routier et congestion',
    },
    {
      path: '/controle-feux',
      label: 'Contrôle des Feux',
      icon: 'traffic-light',
      description: 'Feux de circulation',
    },
    {
      path: '/surveillance-video',
      label: 'Surveillance Vidéo',
      icon: 'video',
      description: 'Caméras et analyse IA',
    },
    {
      path: '/alertes',
      label: 'Alertes & Rapports',
      icon: 'bell',
      description: 'Toutes les alertes actives',
    },
  ] as const;
  
  export const ROUTE_IDS = {
    NORD: 'nord',
    SUD: 'sud',
    EST: 'est',
    OUEST: 'ouest',
  } as const;