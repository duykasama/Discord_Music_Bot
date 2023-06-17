package test;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class SpotifyAPI {
    private static final String clientId = "ad0249c613804c7aab6cdf1626d7182b";
    private static final String clientSecret = "13d623c89508483da9db5200686d96f7";
    private static final URI redirectUri = SpotifyHttpManager.makeUri("https://example.com/spotify-redirect");
    private static final String code = "";

    private static final String ACCESS_TOKEN = "BQA5ZLGhjITNey9D-b6MMjdOckcaOZcQAZ6PF4zuTDFaxPyp8hdHWib_DyZaTWlsYYlI1clySuCww2C17F2zu5EdO7cbATh2j0Vldy6BjxJMrENoU8Bqrt-WYJOkguJ4bttyGrC7YQeTyPz39yPweKyX87fydF3cRol9pjPE3drModj4FVeM6j0mJzqGipRukqOzRIkpbRwB";

    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setAccessToken(ACCESS_TOKEN)
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setRedirectUri(redirectUri)
            .build();
    private static final AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code)
            .build();

    public static void authorizationCode_Sync() {
        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

            // Set access and refresh token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

            System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void authorizationCode_Async() {
        try {
            final CompletableFuture<AuthorizationCodeCredentials> authorizationCodeCredentialsFuture = authorizationCodeRequest.executeAsync();

            // Thread free to do other tasks...

            // Example Only. Never block in production code.
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeCredentialsFuture.join();

            // Set access and refresh token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

            System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());
        } catch (CompletionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
        } catch (CancellationException e) {
            System.out.println("Async operation cancelled.");
        }
    }

    public static String getAccessToken() {
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .build();

        ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials()
                .build();

        try {
            ClientCredentials clientCredentials = clientCredentialsRequest.execute();
            return clientCredentials.getAccessToken();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            // handle exception
            return null;
        }
    }


    public static void getTracks(){
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .build();

        ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();
        try {
            String accessToken = clientCredentialsRequest.execute().getAccessToken();
            spotifyApi.setAccessToken(accessToken);
            SearchTracksRequest searchTracksRequest = spotifyApi.searchTracks("Roses").build();
            Paging<Track> trackPaging = searchTracksRequest.execute();
            Track[] tracks = trackPaging.getItems();

            for (int i = 0; i < 2 && i < tracks.length; i++) {
                String trackId = tracks[i].getId();
                GetTrackRequest getTrackRequest = spotifyApi.getTrack(trackId).build();
                Track t = getTrackRequest.execute();
                System.out.println(t.getName());
                System.out.println(t.getArtists()[0].getName());
                System.out.println(t.getDurationMs()/1000000);
                System.out.println(t.getIsPlayable());
                System.out.println(t.getHref());
                System.out.println(t.getExternalUrls().toString());
            }
        }catch (IOException | SpotifyWebApiException | ParseException exception) {
            System.out.println("Error: " + exception.getMessage());
        }
    }
    public String getTrackId(String trackName) {
        String trackId = null;
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .build();

        ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();
        try {
            String accessToken = clientCredentialsRequest.execute().getAccessToken();
            spotifyApi.setAccessToken(accessToken);
            SearchTracksRequest searchTracksRequest = spotifyApi.searchTracks(trackName).build();
            Paging<Track> trackPaging = searchTracksRequest.execute();
            Track[] tracks = trackPaging.getItems();
            trackId = tracks[0].getId();
            GetTrackRequest getTrackRequest = spotifyApi.getTrack(trackId).build();
//            track = getTrackRequest.execute();

//            for (int i = 0; i < 3 && i < tracks.length; i++) {
////                System.out.println(tracks[i].getName());
//                String trackId = tracks[i].getId();
//                GetTrackRequest getTrackRequest = spotifyApi.getTrack(trackId).build();
//                Track t = getTrackRequest.execute();
//                System.out.println(t.getName());
//                System.out.println(t.getArtists()[0].getName());
//                System.out.println(t.getDurationMs()/1000000);
//                System.out.println(t.getIsPlayable());
//            }
        }catch (IOException | SpotifyWebApiException | ParseException exception) {
            System.out.println("Error: " + exception.getMessage());
        }
        return trackId;
    }

    public String getTrackUri(String trackName) {
        String trackUri = null;
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .build();

        ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();
        try {
            String accessToken = clientCredentialsRequest.execute().getAccessToken();
            spotifyApi.setAccessToken(accessToken);
            SearchTracksRequest searchTracksRequest = spotifyApi.searchTracks(trackName).build();
            Paging<Track> trackPaging = searchTracksRequest.execute();
            Track[] tracks = trackPaging.getItems();
            String trackId = tracks[0].getId();
            GetTrackRequest getTrackRequest = spotifyApi.getTrack(trackId).build();
            Track track = getTrackRequest.execute();
            trackUri = track.getUri();

//            for (int i = 0; i < 3 && i < tracks.length; i++) {
////                System.out.println(tracks[i].getName());
//                String trackId = tracks[i].getId();
//                GetTrackRequest getTrackRequest = spotifyApi.getTrack(trackId).build();
//                Track t = getTrackRequest.execute();
//                System.out.println(t.getName());
//                System.out.println(t.getArtists()[0].getName());
//                System.out.println(t.getDurationMs()/1000000);
//                System.out.println(t.getIsPlayable());
//            }
        }catch (IOException | SpotifyWebApiException | ParseException exception) {
            exception.printStackTrace();
        }
        return trackUri;
    }
}