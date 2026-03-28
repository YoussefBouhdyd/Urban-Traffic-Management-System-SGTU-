import { NextResponse } from 'next/server';
import { SERVICE_CENTRAL_CONFIG, SERVICE_CAMERAS_CONFIG, KAFKA_CONFIG } from '@/config/api-config';

/**
 * Health Check API - Vérifie l'état de tous les services
 */
export async function GET() {
  const health = {
    status: 'healthy',
    timestamp: new Date().toISOString(),
    services: {
      kafka: {
        status: 'unknown',
        broker: KAFKA_CONFIG.BROKER,
      },
      serviceCentral: {
        status: 'unknown',
        url: SERVICE_CENTRAL_CONFIG.BASE_URL,
      },
      serviceCameras: {
        status: 'unknown',
        url: SERVICE_CAMERAS_CONFIG.BASE_URL,
      },
    },
  };

  // Vérifier Service Central
  try {
    const response = await fetch(`${SERVICE_CENTRAL_CONFIG.BASE_URL}/Flux/latest`, {
      method: 'GET',
      signal: AbortSignal.timeout(5000),
    });
    health.services.serviceCentral.status = response.ok ? 'healthy' : 'unhealthy';
  } catch (error) {
    health.services.serviceCentral.status = 'unhealthy';
  }

  // Vérifier Service Caméras
  try {
    const response = await fetch(`${SERVICE_CAMERAS_CONFIG.BASE_URL}/api/traffic/latest`, {
      method: 'GET',
      signal: AbortSignal.timeout(5000),
    });
    health.services.serviceCameras.status = response.ok ? 'healthy' : 'unhealthy';
  } catch (error) {
    health.services.serviceCameras.status = 'unhealthy';
  }

  // Déterminer le statut global
  const allHealthy = Object.values(health.services).every((s) => s.status === 'healthy');
  health.status = allHealthy ? 'healthy' : 'degraded';

  return NextResponse.json(health);
}