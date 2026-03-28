import { type ClassValue, clsx } from 'clsx';
import { format, formatDistanceToNow } from 'date-fns';
import { fr } from 'date-fns/locale';

/**
 * Combine les classes CSS avec clsx
 */
export function cn(...inputs: ClassValue[]) {
  return clsx(inputs);
}

/**
 * Formater une date en français
 */
export function formatDate(date: string | Date, formatStr: string = 'PPpp'): string {
  const d = typeof date === 'string' ? new Date(date) : date;
  return format(d, formatStr, { locale: fr });
}

/**
 * Formater une date relative ("il y a 5 minutes")
 */
export function formatRelativeTime(date: string | Date): string {
  const d = typeof date === 'string' ? new Date(date) : date;
  return formatDistanceToNow(d, { addSuffix: true, locale: fr });
}

/**
 * Arrondir un nombre avec décimales
 */
export function roundNumber(num: number, decimals: number = 1): number {
  return Math.round(num * Math.pow(10, decimals)) / Math.pow(10, decimals);
}

/**
 * Obtenir la couleur selon la sévérité
 */
export function getSeverityColor(severity: string): string {
  const colors: Record<string, string> = {
    LOW: '#10B981',
    NORMAL: '#10B981',
    MEDIUM: '#F59E0B',
    BUSY: '#F59E0B',
    HIGH: '#EF4444',
    CRITICAL: '#DC2626',
    CONGESTED: '#EF4444',
  };
  return colors[severity.toUpperCase()] || colors.LOW;
}

/**
 * Obtenir le label en français pour la sévérité
 */
export function getSeverityLabel(severity: string): string {
  const labels: Record<string, string> = {
    LOW: 'Faible',
    NORMAL: 'Normal',
    MEDIUM: 'Moyen',
    BUSY: 'Chargé',
    HIGH: 'Élevé',
    CRITICAL: 'Critique',
    CONGESTED: 'Congestionné',
    INCIDENT: 'Incident',
  };
  return labels[severity.toUpperCase()] || severity;
}

/**
 * Obtenir le label en français pour l'état du trafic
 */
export function getTrafficStateLabel(state: string): string {
  const labels: Record<string, string> = {
    NORMAL: 'Normal',
    BUSY: 'Chargé',
    CONGESTED: 'Congestionné',
    INCIDENT: 'Incident',
  };
  return labels[state.toUpperCase()] || state;
}

/**
 * Générer un ID unique
 */
export function generateId(): string {
  return `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
}

/**
 * Calculer le pourcentage de changement
 */
export function calculatePercentageChange(current: number, previous: number): number {
  if (previous === 0) return 0;
  return ((current - previous) / previous) * 100;
}