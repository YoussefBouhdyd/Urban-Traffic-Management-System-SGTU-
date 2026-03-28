// =============================================================================
// Kafka Consumer — SGTU Dashboard
// Utilisé côté serveur (API Route Next.js) pour consommer les topics Kafka
// NOTE : KafkaJS ne fonctionne que dans un contexte Node.js (serveur)
// =============================================================================

import { Kafka, Consumer, KafkaMessage, logLevel } from 'kafkajs';
import { API_CONFIG } from '@/config/api-config';
import type { PollutionData, BruitData } from '@/types/pollution.types';

// --- Initialisation du client Kafka ---
const kafka = new Kafka({
  clientId: API_CONFIG.KAFKA.CLIENT_ID,
  brokers: [API_CONFIG.KAFKA.BROKER],
  logLevel: logLevel.ERROR,
  retry: {
    initialRetryTime: 100,
    retries: 3,
  },
});

// --- Consumer singleton ---
let consumer: Consumer | null = null;

export async function getKafkaConsumer(): Promise<Consumer> {
  if (!consumer) {
    consumer = kafka.consumer({ groupId: API_CONFIG.KAFKA.GROUP_ID });
  }
  return consumer;
}

// --- Parser des messages Kafka ---
export function parsePollutionMessage(message: KafkaMessage): PollutionData | null {
  try {
    if (!message.value) return null;
    return JSON.parse(message.value.toString()) as PollutionData;
  } catch {
    console.error('[Kafka] Erreur parsing message pollution');
    return null;
  }
}

export function parseBruitMessage(message: KafkaMessage): BruitData | null {
  try {
    if (!message.value) return null;
    return JSON.parse(message.value.toString()) as BruitData;
  } catch {
    console.error('[Kafka] Erreur parsing message bruit');
    return null;
  }
}

// --- Déconnexion propre ---
export async function disconnectKafkaConsumer(): Promise<void> {
  if (consumer) {
    await consumer.disconnect();
    consumer = null;
  }
}
