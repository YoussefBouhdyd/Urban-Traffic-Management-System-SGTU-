/**
 * ============================================================================
 * TYPES ALERTES GLOBALES (Fusion de tous les services)
 * ============================================================================
 */

export type AlertSource = 'POLLUTION' | 'BRUIT' | 'FLUX' | 'CAMERAS';
export type AlertSeverity = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
export type AlertStatus = 'ACTIVE' | 'RESOLVED';

export interface Alert {
  id: string;
  source: AlertSource;
  type: string;
  severity: AlertSeverity;
  message: string;
  timestamp: string;
  status: AlertStatus;
  metadata?: Record<string, any>;
}

export interface AlertFilters {
  sources: AlertSource[];
  severities: AlertSeverity[];
  statuses: AlertStatus[];
  dateFrom?: string;
  dateTo?: string;
}

export interface AlertStats {
  total: number;
  bySeverity: Record<AlertSeverity, number>;
  bySource: Record<AlertSource, number>;
  byStatus: Record<AlertStatus, number>;
}