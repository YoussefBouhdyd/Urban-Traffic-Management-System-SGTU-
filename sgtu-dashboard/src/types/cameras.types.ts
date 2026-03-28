/**
 * ============================================================================
 * TYPES CAMÉRAS & ANALYSE (Membre 3 - Service Caméras)
 * ============================================================================
 */

export type TrafficStateType = 'NORMAL' | 'BUSY' | 'CONGESTED' | 'INCIDENT';
export type SeverityType = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
export type CameraStatusType = 'RUNNING' | 'STOPPED' | 'ERROR';

export interface TrafficState {
  intersectionId: string;
  timestamp: string;
  trafficState: TrafficStateType;
  severity: SeverityType;
  recommendation: string;
}

export interface TrafficHistory {
  id: number;
  intersectionId: string;
  timestamp: string;
  trafficState: TrafficStateType;
  severity: SeverityType;
  vehicleCount: number;
  averageSpeed: number;
  recommendation: string;
}

export interface CameraAlert {
  id: number;
  type: 'CONGESTION' | 'INCIDENT' | 'CAMERA_OFFLINE' | 'SYSTEM_WARNING';
  severity: SeverityType;
  message: string;
  timestamp: string;
  status: 'ACTIVE' | 'RESOLVED';
}

export interface Recommendation {
  id: number;
  intersectionId: string;
  timestamp: string;
  recommendation: string;
  reason?: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH';
}

export interface CameraStatus {
  cameraId: string;
  intersectionId: string;
  status: CameraStatusType;
  lastUpdate: string;
}