/**
 * ============================================================================
 * SEUILS D'ALERTE DU SYSTÈME
 * ============================================================================
 */

// Seuils pollution (µg/m³)
export const POLLUTION_THRESHOLDS = {
    NORMAL_MAX: 50,
    MEDIUM_MAX: 80,
    // Au-dessus de 80 = CRITIQUE
  } as const;
  
  // Seuils bruit (dB)
  export const BRUIT_THRESHOLDS = {
    NORMAL_MAX: 70,
    MEDIUM_MAX: 85,
    // Au-dessus de 85 = CRITIQUE
  } as const;
  
  // Seuils flux routier (nombre de véhicules)
  export const FLUX_THRESHOLDS = {
    NORMAL_MAX: 30,
    BUSY_MAX: 100,
    // Au-dessus de 100 = CONGESTION
  } as const;
  
  // Helper functions
  export const getPollutionLevel = (niveau: number): 'NORMAL' | 'MEDIUM' | 'HIGH' => {
    if (niveau < POLLUTION_THRESHOLDS.NORMAL_MAX) return 'NORMAL';
    if (niveau < POLLUTION_THRESHOLDS.MEDIUM_MAX) return 'MEDIUM';
    return 'HIGH';
  };
  
  export const getBruitLevel = (niveau: number): 'NORMAL' | 'MEDIUM' | 'HIGH' => {
    if (niveau < BRUIT_THRESHOLDS.NORMAL_MAX) return 'NORMAL';
    if (niveau < BRUIT_THRESHOLDS.MEDIUM_MAX) return 'MEDIUM';
    return 'HIGH';
  };
  
  export const getFluxLevel = (flux: number): 'NORMAL' | 'BUSY' | 'CONGESTED' => {
    if (flux < FLUX_THRESHOLDS.NORMAL_MAX) return 'NORMAL';
    if (flux < FLUX_THRESHOLDS.BUSY_MAX) return 'BUSY';
    return 'CONGESTED';
  };