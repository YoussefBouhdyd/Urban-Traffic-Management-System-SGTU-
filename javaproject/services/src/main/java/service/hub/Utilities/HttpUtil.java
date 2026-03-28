package service.hub.Utilities;


public class HttpUtil {
    private static final java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();

    public static String post(String url, String json) throws Exception {
        var request = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create(url))
            .header("Content-Type", "application/json")
            .POST(java.net.http.HttpRequest.BodyPublishers.ofString(json))
            .build();

        var response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
        return response.statusCode() + " - " + response.body();
    }
}