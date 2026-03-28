/**
 * ============================================================================
 * CONFIGURATION CENTRALISÉE DES APIs ET SERVICES
 * ============================================================================
 * 
 * ⚠️ LA PERSONNE QUI FAIT L'INTÉGRATION MODIFIE UNIQUEMENT CE FICHIER !
 * 
 * Ce fichier contient TOUTES les URLs et configurations des 3 services :
 * - Membre 1 : Pollution + Bruit (Kafka)
 * - Membre 2 : Flux + Feux (Service Central REST)
 * - Membre 3 : Caméras + Analyse (Service Caméras REST)
 */

// =============================================================================
// KAFKA (Membre 1 - Pollution + Bruit)
// =============================================================================
export const KAFKA_CONFIG = {
  BROKER: process.env.NEXT_PUBLIC_KAFKA_BROKER || 'localhost:9092',
  TOPICS: {
    POLLUTION: process.env.NEXT_PUBLIC_KAFKA_TOPIC_POLLUTION || 'pollution-topic',
    BRUIT: process.env.NEXT_PUBLIC_KAFKA_TOPIC_BRUIT || 'bruit-topic',
  },
};

// =============================================================================
// SERVICE CENTRAL (Membre 2 - Flux + Feux)
// URL CONFIRMÉE : http://localhost:9999/centrale/api ✅
// =============================================================================
export const SERVICE_CENTRAL_CONFIG = {
  BASE_URL: process.env.NEXT_PUBLIC_SERVICE_CENTRAL_URL || 'http://localhost:9999/centrale/api',
  
  ENDPOINTS: {
    // Flux routier
    FLUX_ALL: '/Flux',
    FLUX_LATEST: '/Flux/latest',
    FLUX_ROUTE: (name: string) => `/Flux/route/${name}`,
    FLUX_ROUTE_LATEST: (name: string) => `/Flux/route/${name}/latest`,
    
    // Alertes de congestion
    ALERT: '/Alert',
    
    // Feux de circulation
    FEUX_ETAT: '/Feux/etat',
    FEUX_CONFIG: '/Feux/config',
    FEUX_FORCE: (name: string) => `/Feux/force/${name}`,
    FEUX_NOM: (routeId: string) => `/Feux/nom/${routeId}`,
    FEUX_MAJ: '/Feux/maj',
  },
};

// =============================================================================
// SERVICE CAMÉRAS (Membre 3 - Analyse Trafic)
// ✅ PORT CONFIGURÉ: 8083
// =============================================================================
export const SERVICE_CAMERAS_CONFIG = {
  BASE_URL: process.env.NEXT_PUBLIC_SERVICE_CAMERAS_URL || 'http://localhost:8083',
  
  ENDPOINTS: {
    // Analyse du trafic
    TRAFFIC_LATEST: '/api/traffic/latest',
    TRAFFIC_HISTORY: '/api/traffic/history',
    
    // Alertes caméras
    ALERTS: '/api/alerts',
    
    // Recommandations IA
    RECOMMENDATIONS: '/api/recommendations',
    
    // État des caméras
    CAMERA_STATUS: (cameraId: string) => `/api/cameras/${cameraId}/status`,
  },
};

// =============================================================================
// INTERVALLES DE RAFRAÎCHISSEMENT
// =============================================================================
export const REFRESH_INTERVALS = {
  KAFKA_REALTIME: parseInt(process.env.NEXT_PUBLIC_REFRESH_KAFKA || '1000'),
  SERVICE_CENTRAL: parseInt(process.env.NEXT_PUBLIC_REFRESH_SERVICE_CENTRAL || '5000'),
  SERVICE_CAMERAS: parseInt(process.env.NEXT_PUBLIC_REFRESH_SERVICE_CAMERAS || '5000'),
  ALERTS: parseInt(process.env.NEXT_PUBLIC_REFRESH_ALERTS || '3000'),
};

// =============================================================================
// ROUTES DE NAVIGATION
// =============================================================================
export const ROUTES = {
  HOME: '/',
  QUALITE_AIR: '/qualite-air',
  GESTION_TRAFIC: '/gestion-trafic',
  CONTROLE_FEUX: '/controle-feux',
  SURVEILLANCE_VIDEO: '/surveillance-video',
  ALERTES: '/alertes',
};

// =============================================================================
// HELPER FUNCTIONS
// =============================================================================
export const getFullUrl = (baseUrl: string, endpoint: string): string => {
  const cleanBase = baseUrl.endsWith('/') ? baseUrl.slice(0, -1) : baseUrl;
  const cleanEndpoint = endpoint.startsWith('/') ? endpoint : `/${endpoint}`;
  return `${cleanBase}${cleanEndpoint}`;
};