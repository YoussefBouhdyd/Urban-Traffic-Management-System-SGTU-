/**
 * ============================================================================
 * PALETTE DE COULEURS PROFESSIONNELLE
 * ============================================================================
 */

// Couleurs par service
export const SERVICE_COLORS = {
    POLLUTION: {
      light: '#C4B5FD',
      main: '#8B5CF6',
      dark: '#6D28D9',
    },
    BRUIT: {
      light: '#F9A8D4',
      main: '#EC4899',
      dark: '#BE185D',
    },
    FLUX: {
      light: '#93C5FD',
      main: '#3B82F6',
      dark: '#1D4ED8',
    },
    FEUX: {
      light: '#FCD34D',
      main: '#F59E0B',
      dark: '#D97706',
    },
    CAMERAS: {
      light: '#67E8F9',
      main: '#06B6D4',
      dark: '#0E7490',
    },
  } as const;
  
  // Couleurs par sévérité d'alerte
  export const SEVERITY_COLORS = {
    LOW: '#10B981',      // Vert
    MEDIUM: '#F59E0B',   // Orange
    HIGH: '#EF4444',     // Rouge
    CRITICAL: '#DC2626', // Rouge foncé
  } as const;
  
  // Couleurs par état
  export const STATUS_COLORS = {
    NORMAL: '#10B981',
    BUSY: '#F59E0B',
    CONGESTED: '#EF4444',
    INCIDENT: '#DC2626',
    RUNNING: '#10B981',
    STOPPED: '#6C757D',
    ERROR: '#EF4444',
  } as const;
  
  // Couleurs pour les feux
  export const TRAFFIC_LIGHT_COLORS = {
    GREEN: '#10B981',
    RED: '#EF4444',
    YELLOW: '#F59E0B',
  } as const;
  
  // Helper function pour obtenir une couleur par sévérité
  export const getSeverityColor = (severity: string): string => {
    const severityUpper = severity.toUpperCase();
    return SEVERITY_COLORS[severityUpper as keyof typeof SEVERITY_COLORS] || SEVERITY_COLORS.LOW;
  };
  
  // Helper function pour obtenir une couleur par état
  export const getStatusColor = (status: string): string => {
    const statusUpper = status.toUpperCase();
    return STATUS_COLORS[statusUpper as keyof typeof STATUS_COLORS] || STATUS_COLORS.NORMAL;
  };