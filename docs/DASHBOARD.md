# Dashboard Application

## Overview

The SGTU Dashboard is a modern, real-time web application built with Next.js 14, React, and TypeScript that provides comprehensive visualization and management capabilities for the integrated traffic management system. It offers real-time monitoring of traffic conditions, environmental data, camera surveillance, traffic light control, and alert management through an intuitive user interface.

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                      SGTU Dashboard                              │
│                  (Next.js 14 + React 18)                        │
└─────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────┐
│                        Frontend Layer                            │
├──────────────────────────────────────────────────────────────────┤
│  ┌────────────────┐  ┌────────────────┐  ┌────────────────┐    │
│  │ Gestion        │  │ Surveillance   │  │ Qualité Air    │    │
│  │ Trafic         │  │ Vidéo          │  │                │    │
│  └────────────────┘  └────────────────┘  └────────────────┘    │
│  ┌────────────────┐  ┌────────────────┐  ┌────────────────┐    │
│  │ Contrôle       │  │ Alertes        │  │ Maps           │    │
│  │ Feux           │  │ Système        │  │ Integration    │    │
│  └────────────────┘  └────────────────┘  └────────────────┘    │
└──────────────────────────────────────────────────────────────────┘
                              │
┌──────────────────────────────────────────────────────────────────┐
│                      API Integration Layer                       │
├──────────────────────────────────────────────────────────────────┤
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │ Traffic API  │  │ Camera API   │  │ Pollution API│          │
│  │ Port 8083    │  │ Port 8083    │  │ Port 8080    │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
│  ┌──────────────┐  ┌──────────────┐                            │
│  │ Centrale API │  │ SOAP Services│                            │
│  │ Port 9999    │  │ Port 8080    │                            │
│  └──────────────┘  └──────────────┘                            │
└──────────────────────────────────────────────────────────────────┘
                              │
┌──────────────────────────────────────────────────────────────────┐
│                      State Management                            │
│                    (Zustand + React Query)                       │
└──────────────────────────────────────────────────────────────────┘
```

## Technology Stack

### Core Technologies

- **Framework:** Next.js 14.2.3 (App Router)
- **UI Library:** React 18
- **Language:** TypeScript 5
- **Styling:** Tailwind CSS 3.4.1
- **State Management:** Zustand
- **Data Fetching:** TanStack React Query (optional)
- **Charts:** Recharts / Chart.js
- **Maps:** React Leaflet / Mapbox GL
- **Icons:** Lucide React
- **HTTP Client:** Axios

### Development Tools

- **Package Manager:** npm / yarn / pnpm
- **Linter:** ESLint
- **Formatter:** Prettier (via eslint-config-next)
- **Build Tool:** Turbopack (Next.js built-in)

## Project Structure

```
sgtu-dashboard/
├── public/               # Static assets
├── src/
│   ├── app/              # Next.js App Router pages
│   │   ├── layout.tsx    # Root layout
│   │   ├── page.tsx      # Home page (dashboard overview)
│   │   ├── globals.css   # Global styles
│   │   ├── alertes/      # Alerts page
│   │   ├── controle-feux/    # Traffic light control
│   │   ├── gestion-trafic/   # Traffic management
│   │   ├── qualite-air/      # Air quality monitoring
│   │   └── surveillance-video/   # Camera surveillance
│   │
│   ├── components/       # React components
│   │   ├── layout/       # Layout components
│   │   │   ├── Header.tsx
│   │   │   ├── Sidebar.tsx
│   │   │   └── Navigation.tsx
│   │   ├── shared/       # Shared/common components
│   │   │   ├── Card.tsx
│   │   │   ├── Button.tsx
│   │   │   ├── Alert.tsx
│   │   │   └── LoadingSpinner.tsx
│   │   ├── gestion-trafic/
│   │   ├── controle-feux/
│   │   ├── surveillance-video/
│   │   ├── qualite-air/
│   │   └── alertes/
│   │
│   ├── config/           # Configuration files
│   │   └── api-config.ts     # API endpoints configuration
│   │
│   ├── constants/        # Application constants
│   │   ├── routes.ts     # Route definitions
│   │   ├── colors.ts     # Color schemes
│   │   └── thresholds.ts # Alert thresholds
│   │
│   ├── hooks/            # Custom React hooks
│   │   ├── useTrafficData.ts
│   │   ├── useCameraData.ts
│   │   ├── usePollutionData.ts
│   │   └── useWebSocket.ts
│   │
│   ├── lib/              # Utility libraries
│   │   ├── api.ts        # API client
│   │   ├── utils.ts      # Helper functions
│   │   └── formatting.ts # Data formatting
│   │
│   ├── store/            # State management
│   │   ├── trafficStore.ts
│   │   ├── alertStore.ts
│   │   └── userStore.ts
│   │
│   └── types/            # TypeScript type definitions
│       ├── traffic.ts
│       ├── camera.ts
│       ├── pollution.ts
│       └── alert.ts
│
├── package.json
├── next.config.js
├── tailwind.config.ts
├── tsconfig.json
└── README.md
```

## Key Features

### 1. Traffic Management Dashboard

**Route:** `/gestion-trafic`

**Features:**
- Real-time traffic flow visualization
- Vehicle count per route
- Average speed monitoring
- Congestion level indicators
- Historical traffic charts
- Route comparison analytics

**Components:**
- `TrafficOverview.tsx` - Summary cards
- `TrafficFlowChart.tsx` - Real-time flow visualization
- `RouteComparison.tsx` - Multi-route comparison
- `TrafficHistory.tsx` - Historical data charts

**API Integration:**
```typescript
// Fetch traffic data
const getTrafficData = async () => {
  const response = await fetch('http://localhost:9999/centrale/api/Flux');
  return await response.json();
};
```

### 2. Traffic Light Control

**Route:** `/controle-feux`

**Features:**
- Real-time traffic light status
- Manual light control
- Auto mode toggle
- Timing configuration
- Synchronized multi-intersection control
- Traffic light scheduling

**Components:**
- `TrafficLightGrid.tsx` - Light status display
- `LightController.tsx` - Manual control interface
- `AutoModeToggle.tsx` - Auto/manual switch
- `TimingConfig.tsx` - Duration settings

**Controls:**
```typescript
// Set traffic light
const setTrafficLight = async (routeId: string, color: string) => {
  await fetch(`http://localhost:9999/centrale/api/TrafficLights/${routeId}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ color, duration: 60 })
  });
};
```

### 3. Camera Surveillance

**Route:** `/surveillance-video`

**Features:**
- Live camera feed display
- Camera status monitoring
- Traffic analysis from cameras
- AI-powered recommendations
- Event timeline
- Camera health checks

**Components:**
- `CameraGrid.tsx` - Multi-camera view
- `CameraDetail.tsx` - Single camera detailed view
- `Recommandations.tsx` - AI recommendations (with scrollable list)
- `EventTimeline.tsx` - Recent events

**Styling Note:**
The `Recommandations.tsx` component uses custom scrolling:
```tsx
<div className="overflow-y-auto max-h-[600px] pr-2 custom-scrollbar">
  {recommendations.map(rec => ...)}
</div>
```

### 4. Air Quality Monitoring

**Route:** `/qualite-air`

**Features:**
- Real-time AQI display
- Pollutant level charts (PM2.5, PM10, NO2, CO, O3)
- Air quality alerts (with scrollable list)
- Historical trends
- Health recommendations
- Location-based filtering

**Components:**
- `AirQualityDashboard.tsx` - Main overview
- `PollutantChart.tsx` - Individual pollutant graphs
- `AQIGauge.tsx` - AQI indicator
- `AlertesQualiteAir.tsx` - Alert list (with scrollable container)
- `HealthAdvice.tsx` - Recommendations based on AQI

**Styling Note:**
The `AlertesQualiteAir.tsx` component uses the same scrolling pattern:
```tsx
<div className="overflow-y-auto max-h-[600px] pr-2 custom-scrollbar">
  {alerts.map(alert => ...)}
</div>
```

### 5. System Alerts

**Route:** `/alertes`

**Features:**
- Unified alert center
- Alert filtering (type, severity, status)
- Alert acknowledgment
- Alert resolution tracking
- Notification system
- Alert history

**Components:**
- `AlertsDashboard.tsx` - Overview
- `AlertsList.tsx` - Alert table (with scrollable list)
- `AlertFilters.tsx` - Filter controls
- `AlertDetails.tsx` - Detailed alert view

**Styling Note:**
The `AlertsList.tsx` component also uses custom scrolling:
```tsx
<div className="overflow-y-auto max-h-[600px] pr-2 custom-scrollbar">
  {alerts.map(alert => ...)}
</div>
```

### 6. Dashboard Overview

**Route:** `/` (home page)

**Features:**
- System-wide status summary
- Key metrics at a glance
- Recent alerts
- Quick actions
- System health indicators
- Real-time updates

**Components:**
- `DashboardSummary.tsx` - Overall stats
- `QuickActions.tsx` - Common actions
- `RecentActivity.tsx` - Activity feed
- `SystemHealth.tsx` - Health indicators

## API Configuration

### Configuration File

**Location:** `src/config/api-config.ts`

```typescript
export const API_CONFIG = {
  // Traffic Camera & Analysis Service
  CAMERA: {
    BASE_URL: 'http://localhost:8083/api',
    ENDPOINTS: {
      LATEST_TRAFFIC: '/traffic/latest',
      TRAFFIC_HISTORY: '/traffic/history',
      ALERTS: '/alerts',
      RECOMMENDATIONS: '/recommendations',
      CAMERA_STATUS: '/cameras/{cameraId}/status',
      ALL_CAMERAS: '/cameras'
    }
  },
  
  // Centrale Service
  CENTRALE: {
    BASE_URL: 'http://localhost:9999/centrale/api',
    ENDPOINTS: {
      FLUX: '/Flux',
      ROUTES: '/Routes',
      ROUTE_DETAIL: '/Routes/{routeId}',
      TRAFFIC_LIGHTS: '/TrafficLights/{routeId}',
      STATISTICS: '/Statistics',
      INCIDENTS: '/Incidents'
    }
  },
  
  // Pollution Service
  POLLUTION: {
    BASE_URL: 'http://localhost:8080/api/pollution',
    ENDPOINTS: {
      CURRENT: '/current',
      HISTORY: '/history',
      ALERTS: '/alerts',
      MEASUREMENTS: '/measurements',
      STATISTICS: '/statistics'
    }
  },
  
  // SOAP Services
  SOAP: {
    BASE_URL: 'http://localhost:8080/Services',
    SERVICES: {
      FEUX: '/ServiceFeux?wsdl',
      TRAFIC: '/ServiceTrafic?wsdl',
      CLIENT: '/ServiceClient?wsdl'
    }
  },
  
  // Refresh intervals (ms)
  REFRESH_INTERVALS: {
    TRAFFIC: 5000,
    CAMERA: 5000,
    POLLUTION: 10000,
    ALERTS: 3000
  }
};
```

## Custom Styling

### Custom Scrollbar

**Location:** `src/app/globals.css`

```css
/* Custom Scrollbar Utility */
.custom-scrollbar::-webkit-scrollbar {
  width: 8px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 10px;
}

.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: #555;
}

/* For Firefox */
.custom-scrollbar {
  scrollbar-width: thin;
  scrollbar-color: #888 #f1f1f1;
}
```

This is applied to components with long lists to prevent page-level scrolling:
- `Recommandations.tsx` (camera recommendations)
- `AlertsList.tsx` (alert table)
- `AlertesQualiteAir.tsx` (air quality alerts)

## State Management

### Zustand Store Example

**Location:** `src/store/trafficStore.ts`

```typescript
import { create } from 'zustand';

interface TrafficState {
  routes: Route[];
  selectedRoute: string | null;
  isLoading: boolean;
  error: string | null;
  setRoutes: (routes: Route[]) => void;
  selectRoute: (routeId: string) => void;
  fetchTrafficData: () => Promise<void>;
}

export const useTrafficStore = create<TrafficState>((set, get) => ({
  routes: [],
  selectedRoute: null,
  isLoading: false,
  error: null,
  
  setRoutes: (routes) => set({ routes }),
  
  selectRoute: (routeId) => set({ selectedRoute: routeId }),
  
  fetchTrafficData: async () => {
    set({ isLoading: true, error: null });
    try {
      const response = await fetch(API_CONFIG.CENTRALE.BASE_URL + '/Flux');
      const data = await response.json();
      set({ routes: data.routes, isLoading: false });
    } catch (error) {
      set({ error: error.message, isLoading: false });
    }
  }
}));
```

## Custom Hooks

### useTrafficData Hook

```typescript
import { useEffect, useState } from 'react';
import { API_CONFIG } from '@/config/api-config';

export const useTrafficData = (refreshInterval = 5000) => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  const fetchData = async () => {
    try {
      const response = await fetch(API_CONFIG.CENTRALE.BASE_URL + '/Flux');
      const result = await response.json();
      setData(result);
      setError(null);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };
  
  useEffect(() => {
    fetchData();
    const interval = setInterval(fetchData, refreshInterval);
    return () => clearInterval(interval);
  }, [refreshInterval]);
  
  return { data, loading, error, refetch: fetchData };
};
```

### usePollutionData Hook

```typescript
export const usePollutionData = (location?: string) => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  
  useEffect(() => {
    const fetchPollution = async () => {
      const url = location
        ? `${API_CONFIG.POLLUTION.BASE_URL}/current?location=${location}`
        : `${API_CONFIG.POLLUTION.BASE_URL}/current`;
      
      const response = await fetch(url);
      const result = await response.json();
      setData(result);
      setLoading(false);
    };
    
    fetchPollution();
    const interval = setInterval(fetchPollution, 10000);
    return () => clearInterval(interval);
  }, [location]);
  
  return { data, loading };
};
```

## Installation and Setup

### Prerequisites

- Node.js 18+ 
- npm, yarn, or pnpm
- Backend services running

### Installation

```bash
cd sgtu-dashboard

# Install dependencies
npm install
# or
yarn install
# or
pnpm install
```

### Configuration

1. Update API endpoints in `src/config/api-config.ts` if needed
2. Configure environment variables (create `.env.local`):

```env
NEXT_PUBLIC_API_CAMERA_URL=http://localhost:8083/api
NEXT_PUBLIC_API_CENTRALE_URL=http://localhost:9999/centrale/api
NEXT_PUBLIC_API_POLLUTION_URL=http://localhost:8080/api/pollution
NEXT_PUBLIC_REFRESH_INTERVAL=5000
```

### Running Development Server

```bash
npm run dev
# or
yarn dev
# or
pnpm dev
```

Dashboard will be available at: `http://localhost:3000`

### Building for Production

```bash
npm run build
npm run start
```

### Linting

```bash
npm run lint
```

## Deployment

### Production Build

```bash
# Build optimized production bundle
npm run build

# Start production server
npm run start
```

### Docker Deployment

Create `Dockerfile`:

```dockerfile
FROM node:18-alpine AS builder

WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM node:18-alpine AS runner

WORKDIR /app
COPY --from=builder /app/next.config.js ./
COPY --from=builder /app/public ./public
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/package.json ./package.json

EXPOSE 3000
CMD ["npm", "start"]
```

Build and run:
```bash
docker build -t sgtu-dashboard .
docker run -p 3000:3000 sgtu-dashboard
```

### Environment-Specific Configuration

For different environments:

**.env.development:**
```env
NEXT_PUBLIC_API_CAMERA_URL=http://localhost:8083/api
NEXT_PUBLIC_API_CENTRALE_URL=http://localhost:9999/centrale/api
```

**.env.production:**
```env
NEXT_PUBLIC_API_CAMERA_URL=https://api.production.com/camera
NEXT_PUBLIC_API_CENTRALE_URL=https://api.production.com/centrale
```

## Performance Optimization

### Code Splitting

Next.js automatically code-splits pages. For additional optimization:

```typescript
import dynamic from 'next/dynamic';

const HeavyComponent = dynamic(() => import('./HeavyComponent'), {
  loading: () => <LoadingSpinner />,
  ssr: false
});
```

### Image Optimization

Use Next.js Image component:

```typescript
import Image from 'next/image';

<Image 
  src="/camera-feed.jpg" 
  width={640} 
  height={480} 
  alt="Camera view"
  priority
/>
```

### API Response Caching

Implement SWR or React Query for smart caching:

```typescript
import useSWR from 'swr';

const { data, error } = useSWR('/api/traffic', fetcher, {
  refreshInterval: 5000,
  revalidateOnFocus: false
});
```

## Testing

### Unit Testing (Jest + React Testing Library)

```bash
npm install --save-dev jest @testing-library/react @testing-library/jest-dom
```

Example test:

```typescript
import { render, screen } from '@testing-library/react';
import TrafficOverview from './TrafficOverview';

test('renders traffic overview', () => {
  render(<TrafficOverview />);
  expect(screen.getByText('Traffic Overview')).toBeInTheDocument();
});
```

### E2E Testing (Playwright)

```bash
npm install --save-dev @playwright/test
npx playwright install
```

## Troubleshooting

### Common Issues

**Problem:** API calls fail with CORS errors
**Solution:** Ensure backend services have CORS enabled for `http://localhost:3000`

**Problem:** Components not updating in real-time
**Solution:** Check refresh intervals in `api-config.ts` and ensure hooks are properly configured

**Problem:** Scrollbar not visible on lists
**Solution:** Verify `custom-scrollbar` class is applied and `globals.css` is imported

**Problem:** Build fails with TypeScript errors
**Solution:** Run `npm run lint` and fix type errors

### Debug Mode

Enable debug logging:

```typescript
// src/lib/api.ts
const DEBUG = process.env.NODE_ENV === 'development';

if (DEBUG) {
  console.log('API Request:', url, options);
}
```

## Browser Support

- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

## Accessibility

The dashboard follows WCAG 2.1 AA standards:
- Semantic HTML
- ARIA labels
- Keyboard navigation
- Color contrast ratios
- Screen reader support

## Future Enhancements

- WebSocket support for real-time updates
- Mobile responsive design improvements
- Dark mode theme
- Multi-language support (i18n)
- Advanced filtering and search
- Customizable dashboard layouts
- Export functionality (PDF, CSV)
- User authentication and role-based access
