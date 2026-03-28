Interface simple de test des endpoints de centrale
==================================================

Contenu
-------
- index.html
- app.js

Utilisation conseillée
----------------------
Le plus simple est de servir ce dossier sur le meme Tomcat que `centraleservice`.

Exemple :
- deployer `centraleservice` sur `http://localhost:9999/centrale`
- servir ce dossier sur le meme Tomcat, par exemple :
  `http://localhost:9999/api-tester-webapp/`

Dans ce cas, la base URL par defaut :

http://localhost:9999/centrale/api

fonctionnera directement.

Remarque
--------
Si tu sers cette page sur un autre port ou en `file://`, le navigateur peut bloquer les appels a cause de CORS.

Endpoints couverts
------------------
- GET /Flux
- GET /Flux/latest
- GET /Flux/route/{name}
- GET /Flux/route/{name}/latest
- POST /Flux
- GET /Alert
- GET /Feux
- GET /Feux/etat
- GET /Feux/config
- POST /Feux/config
- POST /Feux/force/{name}
- POST /Feux/nom/{routeId}
- POST /Feux/maj
