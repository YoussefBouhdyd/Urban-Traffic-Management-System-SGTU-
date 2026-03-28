/**
 * ============================================================================
 * TYPES POLLUTION & BRUIT (Membre 1 - Kafka)
 * ============================================================================
 */

export interface PollutionData {
  zone_id: string;
  niveau_co2: number;
  timestamp: string;
}

export interface BruitData {
  zone_id: string;
  niveau_decibels: number;
  timestamp: string;
}

export interface PollutionAlerte {
  id: number;
  zone_id: string;
  niveau_co2: number;
  severity: 'NORMAL' | 'MEDIUM' | 'HIGH';
  timestamp: string;
  message: string;
}

export interface BruitAlerte {
  id: number;
  zone_id: string;
  niveau_decibels: number;
  severity: 'NORMAL' | 'MEDIUM' | 'HIGH';
  timestamp: string;
  message: string;
}

export type QualiteAirAlerte = PollutionAlerte | BruitAlerte;