/**
 * ============================================================================
 * TYPES FLUX ROUTIER (Membre 2 - Service Central)
 * ============================================================================
 */

export interface FluxData {
  flux: number;
  name: string;
  timestamp: string;
}

export interface FluxAlerte {
  flux: number;
  name: string;
  timestamp: string;
}

export type RouteId = 'nord' | 'sud' | 'est' | 'ouest';

export interface RouteFlux {
  routeId: RouteId;
  name: string;
  flux: number;
  timestamp: string;
  level: 'NORMAL' | 'BUSY' | 'CONGESTED';
}