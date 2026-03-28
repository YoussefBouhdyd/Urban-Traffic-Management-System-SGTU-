import axios, { AxiosInstance } from 'axios';
import {
  SERVICE_CENTRAL_CONFIG,
  SERVICE_CAMERAS_CONFIG,
  getFullUrl,
} from '@/config/api-config';

/**
 * ============================================================================
 * CLIENT HTTP POUR SERVICE CENTRAL (Membre 2)
 * ============================================================================
 */
class ServiceCentralClient {
  private client: AxiosInstance;

  constructor() {
    this.client = axios.create({
      baseURL: SERVICE_CENTRAL_CONFIG.BASE_URL,
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json',
      },
    });
  }

  // FLUX
  async getFluxAll() {
    const response = await this.client.get(SERVICE_CENTRAL_CONFIG.ENDPOINTS.FLUX_ALL);
    return response.data;
  }

  async getFluxLatest() {
    const response = await this.client.get(SERVICE_CENTRAL_CONFIG.ENDPOINTS.FLUX_LATEST);
    return response.data;
  }

  async getFluxRoute(name: string) {
    const response = await this.client.get(SERVICE_CENTRAL_CONFIG.ENDPOINTS.FLUX_ROUTE(name));
    return response.data;
  }

  async getFluxRouteLatest(name: string) {
    const response = await this.client.get(
      SERVICE_CENTRAL_CONFIG.ENDPOINTS.FLUX_ROUTE_LATEST(name)
    );
    return response.data;
  }

  // ALERTES
  async getAlerts() {
    const response = await this.client.get(SERVICE_CENTRAL_CONFIG.ENDPOINTS.ALERT);
    return response.data;
  }

  // FEUX
  async getFeuxEtat() {
    const response = await this.client.get(SERVICE_CENTRAL_CONFIG.ENDPOINTS.FEUX_ETAT);
    return response.data;
  }

  async getFeuxConfig() {
    const response = await this.client.get(SERVICE_CENTRAL_CONFIG.ENDPOINTS.FEUX_CONFIG);
    return response.data;
  }

  async updateFeuxConfig(config: { duration: number; segmentGreen: boolean }) {
    const response = await this.client.post(SERVICE_CENTRAL_CONFIG.ENDPOINTS.FEUX_CONFIG, config);
    return response.data;
  }

  async forceFeuxRoute(name: string, data: { duration: number; green: boolean }) {
    const response = await this.client.post(
      SERVICE_CENTRAL_CONFIG.ENDPOINTS.FEUX_FORCE(name),
      data
    );
    return response.data;
  }

  async updateFeuxNom(routeId: string, name: string) {
    const response = await this.client.post(SERVICE_CENTRAL_CONFIG.ENDPOINTS.FEUX_NOM(routeId), {
      name,
    });
    return response.data;
  }
}

/**
 * ============================================================================
 * CLIENT HTTP POUR SERVICE CAMÉRAS (Membre 3)
 * ============================================================================
 */
class ServiceCamerasClient {
  private client: AxiosInstance;

  constructor() {
    this.client = axios.create({
      baseURL: SERVICE_CAMERAS_CONFIG.BASE_URL,
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json',
      },
    });
  }

  // ANALYSE DU TRAFIC
  async getTrafficLatest() {
    const response = await this.client.get(SERVICE_CAMERAS_CONFIG.ENDPOINTS.TRAFFIC_LATEST);
    return response.data;
  }

  async getTrafficHistory(params?: { from?: string; to?: string }) {
    const response = await this.client.get(SERVICE_CAMERAS_CONFIG.ENDPOINTS.TRAFFIC_HISTORY, {
      params,
    });
    return response.data;
  }

  // ALERTES
  async getAlerts() {
    const response = await this.client.get(SERVICE_CAMERAS_CONFIG.ENDPOINTS.ALERTS);
    return response.data;
  }

  // RECOMMANDATIONS
  async getRecommendations() {
    const response = await this.client.get(SERVICE_CAMERAS_CONFIG.ENDPOINTS.RECOMMENDATIONS);
    return response.data;
  }

  // CAMÉRAS
  async getCameraStatus(cameraId: string) {
    const response = await this.client.get(
      SERVICE_CAMERAS_CONFIG.ENDPOINTS.CAMERA_STATUS(cameraId)
    );
    return response.data;
  }
}

// Exports
export const serviceCentralClient = new ServiceCentralClient();
export const serviceCamerasClient = new ServiceCamerasClient();