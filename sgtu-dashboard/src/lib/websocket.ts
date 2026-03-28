// =============================================================================
// WebSocket Client — Socket.io
// Connexion temps réel au Service Central pour les mises à jour de trafic
// =============================================================================

import { io, Socket } from 'socket.io-client';
import { API_CONFIG } from '@/config/api-config';

// --- Singletons par namespace ---
const sockets: Map<string, Socket> = new Map();

export function getSocket(namespace: string = ''): Socket {
  const url = `${API_CONFIG.WEBSOCKET.URL}${namespace}`;

  if (!sockets.has(namespace)) {
    const socket = io(url, {
      transports: ['websocket', 'polling'],
      reconnection: true,
      reconnectionAttempts: 5,
      reconnectionDelay: 2000,
      autoConnect: false,
    });

    socket.on('connect', () => {
      console.warn(`[WS] Connecté au namespace: ${namespace || '/'}`);
    });

    socket.on('disconnect', (reason) => {
      console.warn(`[WS] Déconnecté: ${reason}`);
    });

    socket.on('connect_error', (error) => {
      console.error(`[WS] Erreur de connexion: ${error.message}`);
    });

    sockets.set(namespace, socket);
  }

  return sockets.get(namespace)!;
}

export function connectSocket(namespace: string = ''): Socket {
  const socket = getSocket(namespace);
  if (!socket.connected) {
    socket.connect();
  }
  return socket;
}

export function disconnectSocket(namespace: string = ''): void {
  const socket = sockets.get(namespace);
  if (socket?.connected) {
    socket.disconnect();
    sockets.delete(namespace);
  }
}

export function disconnectAllSockets(): void {
  sockets.forEach((socket) => socket.disconnect());
  sockets.clear();
}
