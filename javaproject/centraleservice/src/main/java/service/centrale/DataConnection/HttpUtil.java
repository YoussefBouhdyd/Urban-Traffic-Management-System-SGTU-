package service.centrale.DataConnection;

public class HttpUtil {
    private static final java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
    //pour les autres services pour connecter avec jax rs
    public static void post(String url, String json) throws Exception {
        var request = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create(url))
            .header("Content-Type", "application/json")
            .POST(java.net.http.HttpRequest.BodyPublishers.ofString(json))
            .build();
        client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
    }
}