/**
 * ============================================================================
 * TYPES FEUX DE CIRCULATION (Membre 2 - Service Central)
 * ============================================================================
 */

export interface FeuxState {
  remaining: number;
  segment: boolean;
  name: string;
  routeId: string;
  green: boolean;
}

export interface FeuxConfig {
  duration: number;
  segmentGreen: boolean;
}

export interface FeuxForceRequest {
  duration: number;
  green: boolean;
}

export interface FeuxNomRequest {
  name: string;
}

export interface IntersectionFeux {
  nord: FeuxState;
  sud: FeuxState;
  est: FeuxState;
  ouest: FeuxState;
}