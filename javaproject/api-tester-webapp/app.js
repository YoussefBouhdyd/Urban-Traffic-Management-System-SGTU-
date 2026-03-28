const DEFAULT_BASE_URL = "http://localhost:9999/centrale/api";
const STORAGE_KEY = "centrale_api_base_url";

const baseUrlInput = document.getElementById("baseUrl");
const responsePanel = document.getElementById("responsePanel");
const responseMeta = document.getElementById("responseMeta");

function loadBaseUrl() {
  baseUrlInput.value = localStorage.getItem(STORAGE_KEY) || DEFAULT_BASE_URL;
}

function saveBaseUrl() {
  localStorage.setItem(STORAGE_KEY, normalizeBaseUrl(baseUrlInput.value));
}

function normalizeBaseUrl(url) {
  return (url || DEFAULT_BASE_URL).trim().replace(/\/+$/, "");
}

function getBaseUrl() {
  return normalizeBaseUrl(baseUrlInput.value);
}

function nowTimestamp() {
  return new Date().toISOString().slice(0, 19).replace("T", " ");
}

function renderResponse(meta, payload) {
  responseMeta.textContent = meta;
  responsePanel.textContent = typeof payload === "string"
    ? payload
    : JSON.stringify(payload, null, 2);
}

async function callApi(method, path, body) {
  const url = `${getBaseUrl()}${path}`;
  const options = { method, headers: {} };

  if (body !== undefined) {
    options.headers["Content-Type"] = "application/json";
    options.body = JSON.stringify(body);
  }

  try {
    const response = await fetch(url, options);
    const text = await response.text();
    let data = text;

    try {
      data = text ? JSON.parse(text) : {};
    } catch (_) {
      // keep raw text
    }

    renderResponse(`${method} ${url} -> ${response.status} ${response.statusText}`, data);
  } catch (error) {
    renderResponse(`${method} ${url} -> erreur`, {
      message: error.message,
      conseil: "Si cette page est servie sur un autre port que Tomcat, il peut y avoir un probleme CORS."
    });
  }
}

function boolValue(selectId) {
  return document.getElementById(selectId).value === "true";
}

function routeValue(selectId) {
  return document.getElementById(selectId).value;
}

function initDefaults() {
  document.getElementById("postFluxTimestamp").value = nowTimestamp();
  document.getElementById("majPayload").value = JSON.stringify([
    { name: "Av Ibn Rochd", segment: true, remaining: 20 },
    { name: "Av Ibn Rochd", segment: true, remaining: 20 },
    { name: "Av Ma El Aynayne", segment: false, remaining: 20 },
    { name: "Av Ma El Aynayne", segment: false, remaining: 20 }
  ], null, 2);
}

document.getElementById("saveBaseUrl").addEventListener("click", () => {
  saveBaseUrl();
  renderResponse("Configuration", { baseUrl: getBaseUrl(), status: "sauvegardee" });
});

document.getElementById("clearResponse").addEventListener("click", () => {
  responseMeta.textContent = "Aucune requete executee.";
  responsePanel.textContent = "Pret pour tester l'API.";
});

document.getElementById("setNowTimestamp").addEventListener("click", () => {
  document.getElementById("postFluxTimestamp").value = nowTimestamp();
});

document.querySelectorAll("[data-action]").forEach((button) => {
  button.addEventListener("click", async () => {
    const action = button.dataset.action;

    if (action === "get-flux-all") {
      return callApi("GET", "/Flux");
    }
    if (action === "get-flux-latest") {
      return callApi("GET", "/Flux/latest");
    }
    if (action === "get-flux-route") {
      return callApi("GET", `/Flux/route/${routeValue("routeFlux")}`);
    }
    if (action === "get-flux-route-latest") {
      return callApi("GET", `/Flux/route/${routeValue("routeFlux")}/latest`);
    }
    if (action === "post-flux") {
      return callApi("POST", "/Flux", {
        flux: Number(document.getElementById("postFluxValue").value),
        name: routeValue("postFluxRoute"),
        timestamp: document.getElementById("postFluxTimestamp").value.trim()
      });
    }
    if (action === "get-alert") {
      return callApi("GET", "/Alert");
    }
    if (action === "get-feux") {
      return callApi("GET", "/Feux");
    }
    if (action === "get-feux-etat") {
      return callApi("GET", "/Feux/etat");
    }
    if (action === "get-feux-config") {
      return callApi("GET", "/Feux/config");
    }
    if (action === "post-feux-config") {
      return callApi("POST", "/Feux/config", {
        duration: Number(document.getElementById("configDuration").value),
        segmentGreen: boolValue("configSegmentGreen")
      });
    }
    if (action === "post-feux-force") {
      return callApi("POST", `/Feux/force/${routeValue("forceRoute")}`, {
        duration: Number(document.getElementById("forceDuration").value),
        green: boolValue("forceGreen")
      });
    }
    if (action === "post-feux-nom") {
      return callApi("POST", `/Feux/nom/${routeValue("renameRoute")}`, {
        name: document.getElementById("renameValue").value.trim()
      });
    }
    if (action === "post-feux-maj") {
      try {
        const body = JSON.parse(document.getElementById("majPayload").value);
        return callApi("POST", "/Feux/maj", body);
      } catch (error) {
        return renderResponse("Validation JSON", {
          message: "Le corps JSON de /Feux/maj n'est pas valide.",
          details: error.message
        });
      }
    }
  });
});

loadBaseUrl();
initDefaults();
